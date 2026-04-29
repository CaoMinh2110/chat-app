package com.truevibeup.core.common.api

import com.truevibeup.core.common.model.CursorMeta
import com.truevibeup.core.common.model.PaginationMeta

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int = -1) : ApiResult<Nothing>()
}

data class ApiResponse<T>(
    val data: T? = null,
    val message: String? = null,
    val success: Boolean = false,
)

data class ListResponse<T>(
    val data: List<T> = emptyList(),
    val meta: PaginationMeta? = null,
    val cursor: CursorMeta? = null,
    val message: String? = null,
)
