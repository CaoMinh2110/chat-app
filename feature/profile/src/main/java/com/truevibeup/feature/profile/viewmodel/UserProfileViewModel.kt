package com.truevibeup.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truevibeup.core.common.api.ApiResult
import com.truevibeup.core.common.model.Post
import com.truevibeup.core.common.model.User
import com.truevibeup.core.network.repository.ChatRepository
import com.truevibeup.core.network.repository.FeedRepository
import com.truevibeup.core.network.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserProfileState(
    val user: User? = null,
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val feedRepository: FeedRepository,
    private val chatRepository: ChatRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(UserProfileState())
    val state: StateFlow<UserProfileState> = _state

    private var currentPage = 1

    fun loadUser(uuid: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            currentPage = 1
            
            val userResult = searchRepository.getUser(uuid)
            val postsResult = feedRepository.getUserPosts(uuid, page = currentPage)
            
            _state.value = _state.value.copy(
                isLoading = false,
                user = (userResult as? ApiResult.Success)?.data,
                posts = (postsResult as? ApiResult.Success)?.data ?: emptyList(),
                hasMore = ((postsResult as? ApiResult.Success)?.data?.size ?: 0) >= 20,
                error = (userResult as? ApiResult.Error)?.message ?: (postsResult as? ApiResult.Error)?.message
            )
        }
    }

    fun loadMorePosts() {
        val user = _state.value.user
        if (_state.value.isLoadingMore || !_state.value.hasMore || user == null) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingMore = true)
            when (val result = feedRepository.getUserPosts(user.id, page = ++currentPage)) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(
                        posts = _state.value.posts + result.data,
                        isLoadingMore = false,
                        hasMore = result.data.size >= 20
                    )
                }
                is ApiResult.Error -> {
                    _state.value = _state.value.copy(isLoadingMore = false)
                }
            }
        }
    }

    fun follow(userId: String) {
        viewModelScope.launch {
            searchRepository.follow(userId)
            _state.value = _state.value.copy(
                user = _state.value.user?.copy(
                    isFollowing = true,
                    followersCount = (_state.value.user?.followersCount ?: 0) + 1,
                )
            )
        }
    }

    fun unfollow(userId: String) {
        viewModelScope.launch {
            searchRepository.unfollow(userId)
            _state.value = _state.value.copy(
                user = _state.value.user?.copy(
                    isFollowing = false,
                    followersCount = maxOf(0, (_state.value.user?.followersCount ?: 1) - 1),
                )
            )
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

    fun likePost(postId: Long) {
        viewModelScope.launch {
            feedRepository.likePost(postId)
            _state.value = _state.value.copy(
                posts = _state.value.posts.map { p ->
                    if (p.id == postId) p.copy(isLiked = true, likesCount = p.likesCount + 1) else p
                }
            )
        }
    }

    fun unlikePost(postId: Long) {
        viewModelScope.launch {
            feedRepository.unlikePost(postId)
            _state.value = _state.value.copy(
                posts = _state.value.posts.map { p ->
                    if (p.id == postId) p.copy(
                        isLiked = false,
                        likesCount = maxOf(0, p.likesCount - 1)
                    ) else p
                }
            )
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            feedRepository.deletePost(postId)
            _state.value = _state.value.copy(posts = _state.value.posts.filter { it.id != postId })
        }
    }
}
