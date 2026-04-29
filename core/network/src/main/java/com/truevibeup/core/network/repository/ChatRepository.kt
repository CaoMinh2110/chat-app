package com.truevibeup.core.network.repository

import com.truevibeup.core.common.api.ApiResult
import com.truevibeup.core.common.model.Conversation
import com.truevibeup.core.common.model.Message
import com.truevibeup.core.network.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(private val api: ApiService) {

    suspend fun getConversations(before: String? = null): ApiResult<List<Conversation>> = try {
        val resp = api.getConversations(before = before)
        if (resp.isSuccessful) ApiResult.Success(resp.body()?.data ?: emptyList())
        else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun getConversation(id: Long): ApiResult<Conversation> = try {
        val resp = api.getConversation(id)
        val conv = resp.body()
        if (resp.isSuccessful && conv != null) ApiResult.Success(conv)
        else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun getOrCreateConversation(uuid: String): ApiResult<Conversation> = try {
        val resp = api.getOrCreateConversation(uuid)
        val conv = resp.body()
        if (resp.isSuccessful && conv != null) ApiResult.Success(conv)
        else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun getMessages(conversationId: Long, before: String? = null): ApiResult<List<Message>> = try {
        val resp = api.getMessages(conversationId, before = before)
        if (resp.isSuccessful) ApiResult.Success(resp.body()?.data ?: emptyList())
        else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun deleteConversation(id: Long): ApiResult<Unit> = try {
        val resp = api.deleteConversation(id)
        if (resp.isSuccessful) ApiResult.Success(Unit) else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }
}
