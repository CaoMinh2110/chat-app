package com.truevibeup.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truevibeup.core.common.api.ApiResult
import com.truevibeup.core.common.model.User
import com.truevibeup.core.network.repository.FeedRepository
import com.truevibeup.core.network.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FollowState(
    val username: String = "",
    val followers: List<User> = emptyList(),
    val following: List<User> = emptyList(),
    val followersQuery: String = "",
    val followingQuery: String = "",
    val isLoadingUser: Boolean = false,
    val isLoadingFollowers: Boolean = false,
    val isLoadingFollowing: Boolean = false,
)

@OptIn(FlowPreview::class)
@HiltViewModel
class FollowViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val feedRepository: FeedRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FollowState())
    val state: StateFlow<FollowState> = _state

    private val _followersQuery = MutableStateFlow("")
    private val _followingQuery = MutableStateFlow("")

    private var userId: String = ""

    fun init(userId: String) {
        if (this.userId == userId) return
        this.userId = userId
        loadUser(userId)
        loadFollowers()
        loadFollowing()

        viewModelScope.launch {
            _followersQuery.debounce(300).distinctUntilChanged().collect { q ->
                _state.value = _state.value.copy(followersQuery = q)
                loadFollowers(q)
            }
        }
        viewModelScope.launch {
            _followingQuery.debounce(300).distinctUntilChanged().collect { q ->
                _state.value = _state.value.copy(followingQuery = q)
                loadFollowing(q)
            }
        }
    }

    fun onFollowersQueryChange(q: String) {
        _followersQuery.value = q
    }

    fun onFollowingQueryChange(q: String) {
        _followingQuery.value = q
    }

    private fun loadUser(userId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingUser = true)
            when (val r = searchRepository.getUser(userId)) {
                is ApiResult.Success -> _state.value = _state.value.copy(username = r.data.name, isLoadingUser = false)
                is ApiResult.Error -> _state.value = _state.value.copy(isLoadingUser = false)
            }
        }
    }

    private fun loadFollowers(query: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingFollowers = true)
            when (val r = searchRepository.getFollowers(userId, query?.takeIf { it.isNotBlank() })) {
                is ApiResult.Success -> _state.value = _state.value.copy(followers = r.data, isLoadingFollowers = false)
                is ApiResult.Error -> _state.value = _state.value.copy(isLoadingFollowers = false)
            }
        }
    }

    private fun loadFollowing(query: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingFollowing = true)
            when (val r = searchRepository.getFollowing(userId, query?.takeIf { it.isNotBlank() })) {
                is ApiResult.Success -> _state.value = _state.value.copy(following = r.data, isLoadingFollowing = false)
                is ApiResult.Error -> _state.value = _state.value.copy(isLoadingFollowing = false)
            }
        }
    }

    fun follow(targetId: String) {
        val previousState = _state.value
        updateFollowState(targetId, isFollowing = true)
        
        viewModelScope.launch {
            val result = feedRepository.follow(targetId)
            if (result is ApiResult.Error) {
                _state.value = previousState
            }
        }
    }

    fun unfollow(targetId: String) {
        val previousState = _state.value
        updateFollowState(targetId, isFollowing = false)
        
        viewModelScope.launch {
            val result = feedRepository.unfollow(targetId)
            if (result is ApiResult.Error) {
                _state.value = previousState
            }
        }
    }

    private fun updateFollowState(targetId: String, isFollowing: Boolean) {
        _state.value = _state.value.copy(
            followers = _state.value.followers.map { if (it.id == targetId) it.copy(isFollowing = isFollowing) else it },
            following = _state.value.following.map { if (it.id == targetId) it.copy(isFollowing = isFollowing) else it },
        )
    }
}
