package com.truevibeup.core.common.model

data class Post(
    val id: Long = 0,
    val author: User = User(),
    val content: String? = null,
    val images: List<String> = emptyList(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLiked: Boolean = false,
    val createdAt: String = "",
)

data class Comment(
    val id: Long = 0,
    val author: User = User(),
    val content: String = "",
    val parentId: Long? = null,
    val replies: List<Comment> = emptyList(),
    val createdAt: String = "",
)

data class CreatePostRequest(
    val content: String?,
    val images: List<String> = emptyList(),
)
