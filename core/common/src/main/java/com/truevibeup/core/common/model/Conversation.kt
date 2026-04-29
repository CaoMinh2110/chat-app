package com.truevibeup.core.common.model

import com.google.gson.annotations.SerializedName

data class Conversation(
    val id: Long = 0,
    @SerializedName("other_user") val otherUser: User = User(),
    @SerializedName("last_message") val lastMessage: Message? = null,
    @SerializedName("unread_count") val unreadCount: Int = 0,
    @SerializedName("last_activity_at") val lastActivityAt: String? = null,
    @SerializedName("is_e2e_enabled") val isE2eEnabled: Boolean = false,
    @SerializedName("message_ttl") val messageTtl: Int? = null,
)

data class CursorMeta(
    val before: String? = null,
    @SerializedName("has_more") val hasMore: Boolean = false,
)
