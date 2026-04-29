package com.truevibeup.feature.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truevibeup.core.common.api.ApiResult
import com.truevibeup.core.common.model.Conversation
import com.truevibeup.core.common.model.User
import com.truevibeup.core.network.repository.AuthRepository
import com.truevibeup.core.network.repository.ChatRepository
import com.truevibeup.core.network.repository.SearchRepository
import com.truevibeup.core.network.socket.SocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConversationsState(
    val conversations: List<Conversation> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val following: List<User> = emptyList(),
    val isFollowingLoading: Boolean = false,
    val currentUser: User? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val searchRepository: SearchRepository,
    private val authRepository: AuthRepository,
    private val socketManager: SocketManager,
) : ViewModel() {

    private val _state = MutableStateFlow(ConversationsState())
    val state: StateFlow<ConversationsState> = _state

    init {
        loadConversations()
        observeNewMessages()
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            _state.value = _state.value.copy(currentUser = user)
            user?.id?.let { loadFollowing() }
        }
    }

    fun loadConversations(refresh: Boolean = false) {
        viewModelScope.launch {
            if (refresh) _state.value = _state.value.copy(isRefreshing = true)
            else _state.value = _state.value.copy(isLoading = true)

            when (val result = chatRepository.getConversations()) {
                is ApiResult.Success -> _state.value = _state.value.copy(
                    conversations = result.data, isLoading = false, isRefreshing = false,
                )

                is ApiResult.Error -> _state.value = _state.value.copy(
                    isLoading = false, isRefreshing = false, error = result.message,
                )

                else -> {}
            }
        }
    }

    fun loadFollowing(query: String? = null) {
        val userId = _state.value.currentUser?.id ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isFollowingLoading = true)
            when (val result = searchRepository.getFollowing(userId, query)) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(
                        following = result.data,
                        isFollowingLoading = false
                    )
                }
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(isFollowingLoading = false)
                }
            }
        }
    }

    private fun observeNewMessages() {
        viewModelScope.launch {
            socketManager.newMessage.collect { message ->
                val convId = message.conversationId
                val updated = _state.value.conversations.map { conv ->
                    if (conv.id == convId) conv.copy(
                        lastMessage = message,
                        unreadCount = conv.unreadCount + 1,
                    ) else conv
                }.sortedByDescending { it.lastActivityAt }
                _state.value = _state.value.copy(conversations = updated)
            }
        }
    }

    fun getOrCreateConversation(uuid: String, onResult: (Long) -> Unit) {
        viewModelScope.launch {
            when (val result = chatRepository.getOrCreateConversation(uuid)) {
                is ApiResult.Success -> onResult(result.data.id)
                else -> {}
            }
        }
    }

    fun deleteConversation(id: Long) {
        viewModelScope.launch {
            chatRepository.deleteConversation(id)
            _state.value = _state.value.copy(
                conversations = _state.value.conversations.filter { it.id != id }
            )
        }
    }
}
