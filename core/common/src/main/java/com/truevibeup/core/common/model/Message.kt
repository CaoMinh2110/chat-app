package com.truevibeup.core.common.model

import com.google.gson.annotations.SerializedName

data class Message(
    val id: Long = 0,
    @SerializedName("conversation_id") val conversationId: Long = 0,
    val sender: User = User(),
    val type: String = "text",
    val content: String? = null,
    @SerializedName("media_url") val mediaUrl: String? = null,
    val duration: Int? = null,
    @SerializedName("is_read") val isRead: Boolean = false,
    @SerializedName("read_at") val readAt: String? = null,
    @SerializedName("is_deleted") val isDeleted: Boolean = false,
    @SerializedName("created_at") val createdAt: String = "",
)
