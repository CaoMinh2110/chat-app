package com.truevibeup.core.common.model

import com.google.gson.annotations.SerializedName

data class AppNotification(
    val id: Long = 0,
    val type: String = "",
    val actor: User = User(),
    @SerializedName("reference_id") val referenceId: Long = 0,
    @SerializedName("entity_id") val entityId: Long? = null, // Có thể giữ lại nếu API trả về cả hai
    @SerializedName("is_read") val isRead: Boolean = false,
    @SerializedName("created_at") val createdAt: String = "",
)

data class PaginationMeta(
    val page: Int = 1,
    val limit: Int = 20,
    val total: Int = 0,
    @SerializedName("total_pages") val totalPages: Int = 1,
)
