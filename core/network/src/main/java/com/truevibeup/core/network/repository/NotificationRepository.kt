package com.truevibeup.core.network.repository

import com.truevibeup.core.common.api.ApiResult
import com.truevibeup.core.common.model.AppNotification
import com.truevibeup.core.network.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(private val api: ApiService) {

    suspend fun getNotifications(before: String? = null): ApiResult<List<AppNotification>> = try {
        val resp = api.getNotifications(before = before)
        if (resp.isSuccessful) ApiResult.Success(resp.body()?.data ?: emptyList())
        else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun markAllRead(): ApiResult<Unit> = try {
        val resp = api.markAllNotificationsRead()
        if (resp.isSuccessful) ApiResult.Success(Unit) else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }
}
