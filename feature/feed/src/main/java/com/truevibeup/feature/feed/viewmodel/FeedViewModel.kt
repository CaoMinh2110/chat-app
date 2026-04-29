package com.truevibeup.feature.feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truevibeup.core.common.api.ApiResult
import com.truevibeup.core.common.model.Post
import com.truevibeup.core.network.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val error: String? = null,
    val selectedType: String = "all"
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: FeedRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FeedState())
    val state: StateFlow<FeedState> = _state

    private var currentPage = 1

    init {
        loadFeed()
    }

    fun loadFeed(refresh: Boolean = false) {
        if (refresh) currentPage = 1
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = repository.getFeed(page = currentPage, type = _state.value.selectedType)) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(
                        posts = if (refresh) result.data else _state.value.posts + result.data,
                        isLoading = false,
                        hasMore = result.data.size >= 10
                    )
                }
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun onTypeSelected(type: String) {
        if (_state.value.selectedType == type) return
        _state.value = _state.value.copy(selectedType = type, posts = emptyList())
        loadFeed(refresh = true)
    }

    fun loadMore() {
        if (_state.value.isLoadingMore || !_state.value.hasMore) return
        currentPage++
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingMore = true)
            when (val result = repository.getFeed(page = currentPage, type = _state.value.selectedType)) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(
                        posts = _state.value.posts + result.data,
                        isLoadingMore = false,
                        hasMore = result.data.size >= 10
                    )
                }
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(isLoadingMore = false)
                }
            }
        }
    }

    fun likePost(postId: Long) {
        viewModelScope.launch {
            repository.likePost(postId)
            _state.value = _state.value.copy(
                posts = _state.value.posts.map {
                    if (it.id == postId) it.copy(isLiked = true, likesCount = it.likesCount + 1) else it
                }
            )
        }
    }

    fun unlikePost(postId: Long) {
        viewModelScope.launch {
            repository.unlikePost(postId)
            _state.value = _state.value.copy(
                posts = _state.value.posts.map {
                    if (it.id == postId) it.copy(isLiked = false, likesCount = (it.likesCount - 1).coerceAtLeast(0)) else it
                }
            )
        }
    }

    fun followUser(userId: String) {
        viewModelScope.launch {
            repository.follow(userId)
            _state.value = _state.value.copy(
                posts = _state.value.posts.map {
                    if (it.author.id == userId) it.copy(author = it.author.copy(isFollowing = true)) else it
                }
            )
        }
    }

    fun unfollowUser(userId: String) {
        viewModelScope.launch {
            repository.unfollow(userId)
            _state.value = _state.value.copy(
                posts = _state.value.posts.map {
                    if (it.author.id == userId) it.copy(author = it.author.copy(isFollowing = false)) else it
                }
            )
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            repository.deletePost(postId)
            _state.value = _state.value.copy(
                posts = _state.value.posts.filter { it.id != postId }
            )
        }
    }
}
