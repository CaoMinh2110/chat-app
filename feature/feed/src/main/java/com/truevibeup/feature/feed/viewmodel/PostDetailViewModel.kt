package com.truevibeup.feature.feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truevibeup.core.common.AppConstants.MAX_COMMENT_LEVEL
import com.truevibeup.core.common.api.ApiResult
import com.truevibeup.core.common.model.Comment
import com.truevibeup.core.common.model.Post
import com.truevibeup.core.common.model.User
import com.truevibeup.core.network.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FlatComment(
    val id: Long,
    val author: User,
    val content: String,
    val parentId: Long?,
    val depth: Int,
    val replyCount: Int,
    val isExpanded: Boolean,
    val isLastSibling: Boolean,
    val createdAt: String,
)

data class PostDetailState(
    val post: Post? = null,
    val flatComments: List<FlatComment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val replyingToCommentId: Long? = null,
)

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(PostDetailState())
    val state: StateFlow<PostDetailState> = _state

    private var rootItems: List<FlatComment> = emptyList()
    private var repliesByParent: Map<Long, List<FlatComment>> = emptyMap()
    private val expandedIds = mutableSetOf<Long>()

    // ---------------------------
    // BUILD VISIBLE LIST
    // ---------------------------
    private fun buildFlatList(): List<FlatComment> {
        val result = mutableListOf<FlatComment>()

        rootItems.forEach { root ->
            val isExpanded = root.id in expandedIds

            result.add(root.copy(isExpanded = isExpanded))

            if (isExpanded) {
                repliesByParent[root.id]?.let { result.addAll(it) }
            }
        }

        return result
    }

    // ---------------------------
    // FLATTEN TREE (FIX DEPTH HERE)
    // ---------------------------
    private fun flattenFromTree(comments: List<Comment>) {

        val roots = mutableListOf<FlatComment>()
        val replyMap = mutableMapOf<Long, MutableList<FlatComment>>()

        fun traverse(
            items: List<Comment>,
            parentId: Long?,
            depth: Int
        ) {
            items.forEachIndexed { index, c ->

                val flat = FlatComment(
                    id = c.id,
                    author = c.author,
                    content = c.content,
                    parentId = parentId,
                    depth = depth,
                    replyCount = c.replies.size,
                    isExpanded = false,
                    isLastSibling = index == items.lastIndex,
                    createdAt = c.createdAt,
                )

                if (depth == 0) {
                    roots.add(flat)
                } else {
                    replyMap.getOrPut(parentId!!) { mutableListOf() }.add(flat)
                }

                val nextDepth = depth + 1

                if (nextDepth < MAX_COMMENT_LEVEL) {
                    traverse(c.replies, c.id, nextDepth)
                }
            }
        }

        traverse(comments, null, 0)

        rootItems = roots
        repliesByParent = replyMap
    }

    // ---------------------------
    // LOAD
    // ---------------------------
    fun load(postId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val postResult = feedRepository.getPost(postId)
            val commentsResult = feedRepository.getComments(postId)

            val post = (postResult as? ApiResult.Success)?.data
            val rawData = (commentsResult as? ApiResult.Success)?.data ?: emptyList()

            flattenFromTree(rawData.filter { it.parentId == null })

            _state.value = _state.value.copy(
                post = post,
                flatComments = buildFlatList(),
                isLoading = false,
            )
        }
    }

    // ---------------------------
    // TOGGLE REPLIES
    // ---------------------------
    fun toggleReplies(commentId: Long) {
        if (commentId in expandedIds) {
            expandedIds.remove(commentId)
        } else {
            expandedIds.add(commentId)
        }

        _state.value = _state.value.copy(
            flatComments = buildFlatList()
        )
    }

    // ---------------------------
    // ADD COMMENT (ROOT)
    // ---------------------------
    fun addComment(content: String) {
        val post = _state.value.post ?: return

        viewModelScope.launch {
            when (val result = feedRepository.addComment(post.id, content)) {
                is ApiResult.Success -> {
                    val c = result.data

                    val newRoot = FlatComment(
                        id = c.id,
                        author = c.author,
                        content = c.content,
                        parentId = null,
                        depth = 0,
                        replyCount = 0,
                        isExpanded = false,
                        isLastSibling = false,
                        createdAt = c.createdAt,
                    )

                    rootItems = rootItems + newRoot

                    _state.value = _state.value.copy(
                        flatComments = buildFlatList(),
                        post = post.copy(
                            commentsCount = post.commentsCount + 1
                        )
                    )
                }
                else -> Unit
            }
        }
    }

    // ---------------------------
    // REPLY
    // ---------------------------
    fun setReplyingTo(commentId: Long?) {
        _state.value = _state.value.copy(replyingToCommentId = commentId)
    }

    fun addReply(content: String) {
        val post = _state.value.post ?: return
        val parentId = _state.value.replyingToCommentId ?: return

        viewModelScope.launch {
            when (val result = feedRepository.addComment(post.id, content, parentId)) {
                is ApiResult.Success -> {
                    val r = result.data

                    val existing = repliesByParent[parentId] ?: emptyList()

                    val parentDepth = rootItems.find { it.id == parentId }?.depth ?: 0

                    val newReply = FlatComment(
                        id = r.id,
                        author = r.author,
                        content = r.content,
                        parentId = parentId,
                        depth = parentDepth + 1,
                        replyCount = 0,
                        isExpanded = false,
                        isLastSibling = true,
                        createdAt = r.createdAt,
                    )

                    val updated = if (existing.isEmpty()) {
                        listOf(newReply)
                    } else {
                        existing.dropLast(1) +
                                existing.last().copy(isLastSibling = false) +
                                newReply
                    }

                    repliesByParent = repliesByParent + (parentId to updated)

                    rootItems = rootItems.map {
                        if (it.id == parentId) {
                            it.copy(replyCount = updated.size)
                        } else it
                    }

                    expandedIds.add(parentId)

                    _state.value = _state.value.copy(
                        flatComments = buildFlatList(),
                        replyingToCommentId = null,
                        post = post.copy(
                            commentsCount = post.commentsCount + 1
                        )
                    )
                }
                else -> Unit
            }
        }
    }

    // ---------------------------
    // OTHER ACTIONS (unchanged)
    // ---------------------------
    fun like() {
        val post = _state.value.post ?: return
        viewModelScope.launch {
            feedRepository.likePost(post.id)
            _state.value = _state.value.copy(
                post = post.copy(
                    isLiked = true,
                    likesCount = post.likesCount + 1
                )
            )
        }
    }

    fun unlike() {
        val post = _state.value.post ?: return
        viewModelScope.launch {
            feedRepository.unlikePost(post.id)
            _state.value = _state.value.copy(
                post = post.copy(
                    isLiked = false,
                    likesCount = maxOf(0, post.likesCount - 1)
                )
            )
        }
    }

    fun follow(userId: String) {
        viewModelScope.launch {
            feedRepository.follow(userId)

            val post = _state.value.post ?: return@launch

            if (post.author.id == userId) {
                _state.value = _state.value.copy(
                    post = post.copy(
                        author = post.author.copy(isFollowing = true)
                    )
                )
            }
        }
    }

    fun unfollow(userId: String) {
        viewModelScope.launch {
            feedRepository.unfollow(userId)

            val post = _state.value.post ?: return@launch

            if (post.author.id == userId) {
                _state.value = _state.value.copy(
                    post = post.copy(
                        author = post.author.copy(isFollowing = false)
                    )
                )
            }
        }
    }

    fun delete() {
        val post = _state.value.post ?: return
        viewModelScope.launch {
            feedRepository.deletePost(post.id)
        }
    }
}