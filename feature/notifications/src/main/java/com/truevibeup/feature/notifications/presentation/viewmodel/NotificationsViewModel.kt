package com.truevibeup.feature.notifications.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truevibeup.core.common.api.ApiResult
import com.truevibeup.core.common.model.AppNotification
import com.truevibeup.core.network.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsState(
    val notifications: List<AppNotification> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val hasMore: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: NotificationRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = _state

    init {
        load()
    }

    fun load(refresh: Boolean = false) {
        viewModelScope.launch {
            if (refresh) _state.value = _state.value.copy(isRefreshing = true)
            else _state.value = _state.value.copy(isLoading = true)
            when (val result = repository.getNotifications()) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(
                        notifications = result.data,
                        unreadCount = result.data.count { !it.isRead },
                        isLoading = false,
                        isRefreshing = false,
                        hasMore = result.data.size >= 20,
                    )
                }
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun markAsRead(notificationId: Long) {
        viewModelScope.launch {
            // Backend only has markAllRead, so we use it here.
            // Even though we clicked one notification, we mark all as read to sync with server.
            repository.markAllRead()
            _state.value = _state.value.copy(
                notifications = _state.value.notifications.map { it.copy(isRead = true) },
                unreadCount = 0
            )
        }
    }

    fun loadMore() {
        if (_state.value.isLoading || !_state.value.hasMore) return
        viewModelScope.launch {
            val oldest = _state.value.notifications.lastOrNull()?.createdAt
            when (val result = repository.getNotifications(before = oldest)) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(
                        notifications = _state.value.notifications + result.data,
                        hasMore = result.data.size >= 20,
                    )
                }
                else -> {}
            }
        }
    }
}
