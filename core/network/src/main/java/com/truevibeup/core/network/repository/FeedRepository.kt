package com.truevibeup.core.network.repository

import com.truevibeup.core.common.api.ApiResult
import com.truevibeup.core.common.model.Comment
import com.truevibeup.core.common.model.CreatePostRequest
import com.truevibeup.core.common.model.Post
import com.truevibeup.core.network.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(private val api: ApiService) {

    suspend fun getFeed(page: Int = 1, type: String = "all"): ApiResult<List<Post>> = try {
        val resp = api.getFeed(page = page, type = type)
        if (resp.isSuccessful) ApiResult.Success(resp.body()?.data ?: emptyList())
        else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun getUserPosts(uuid: String, page: Int = 1, limit: Int = 20): ApiResult<List<Post>> = try {
        val resp = api.getUserPosts(uuid, page, limit)
        if (resp.isSuccessful) ApiResult.Success(resp.body()?.data ?: emptyList())
        else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun getPost(id: Long): ApiResult<Post> = try {
        val resp = api.getPost(id)
        val post = resp.body()
        if (resp.isSuccessful && post != null) ApiResult.Success(post)
        else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun createPost(content: String?, images: List<String> = emptyList()): ApiResult<Post> = try {
        val resp = api.createPost(CreatePostRequest(content, images))
        val post = resp.body()
        if (resp.isSuccessful && post != null) ApiResult.Success(post)
        else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun likePost(id: Long): ApiResult<Unit> = try {
        val resp = api.likePost(id)
        if (resp.isSuccessful) ApiResult.Success(Unit) else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun unlikePost(id: Long): ApiResult<Unit> = try {
        val resp = api.unlikePost(id)
        if (resp.isSuccessful) ApiResult.Success(Unit) else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun deletePost(id: Long): ApiResult<Unit> = try {
        val resp = api.deletePost(id)
        if (resp.isSuccessful) ApiResult.Success(Unit) else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun getComments(postId: Long): ApiResult<List<Comment>> = try {
        val resp = api.getComments(postId)
        if (resp.isSuccessful) ApiResult.Success(resp.body()?.data ?: emptyList())
        else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun addComment(postId: Long, content: String, parentId: Long? = null): ApiResult<Comment> = try {
        val body = buildMap<String, Any> {
            put("content", content)
            parentId?.let { put("parent_id", it) }
        }
        val resp = api.addComment(postId, body)
        val comment = resp.body()
        if (resp.isSuccessful && comment != null) ApiResult.Success(comment)
        else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun follow(userId: String): ApiResult<Unit> = try {
        val resp = api.follow(userId)
        if (resp.isSuccessful) ApiResult.Success(Unit) else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun unfollow(userId: String): ApiResult<Unit> = try {
        val resp = api.unfollow(userId)
        if (resp.isSuccessful) ApiResult.Success(Unit) else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }
}
