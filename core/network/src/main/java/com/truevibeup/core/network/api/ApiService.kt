package com.truevibeup.core.network.api

import com.truevibeup.core.common.api.ListResponse
import com.truevibeup.core.common.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("auth/social")
    suspend fun socialLogin(@Body body: Map<String, String>): Response<AuthTokens>

    @POST("auth/register")
    suspend fun deviceRegister(@Body body: Map<String, String>): Response<AuthTokens>

    @POST("auth/login")
    suspend fun deviceLogin(@Body body: Map<String, String>): Response<AuthTokens>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body body: Map<String, String>): Response<AuthTokens>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    // Users
    @GET("users/me")
    suspend fun getMe(): Response<User>

    @PUT("users/me")
    suspend fun updateMe(@Body body: Map<String, @JvmSuppressWildcards Any?>): Response<Unit>

    @PUT("users/me/avatar")
    suspend fun updateAvatar(@Body body: Map<String, String>): Response<Unit>

    @PUT("users/me/photos")
    suspend fun updatePhotos(@Body body: Map<String, List<String>>): Response<Unit>

    @GET("users/me/badges")
    suspend fun getBadges(): Response<Badges>

    @GET("users/{uuid}")
    suspend fun getUser(@Path("uuid") uuid: String): Response<User>

    @GET("users/{uuid}/posts")
    suspend fun getUserPosts(
        @Path("uuid") uuid: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
    ): Response<ListResponse<Post>>

    @GET("users/{uuid}/followers")
    suspend fun getFollowers(
        @Path("uuid") uuid: String,
        @Query("q") query: String? = null,
        @Query("page") page: Int = 1,
    ): Response<ListResponse<User>>

    @GET("users/{uuid}/following")
    suspend fun getFollowing(
        @Path("uuid") uuid: String,
        @Query("q") query: String? = null,
        @Query("page") page: Int = 1,
    ): Response<ListResponse<User>>

    @POST("follows/{userId}")
    suspend fun follow(@Path("userId") userId: String): Response<Unit>

    @DELETE("follows/{userId}")
    suspend fun unfollow(@Path("userId") userId: String): Response<Unit>

    // Feed & Posts
    @GET("feed")
    suspend fun getFeed(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("type") type: String = "all",
    ): Response<ListResponse<Post>>

    @GET("posts/{id}")
    suspend fun getPost(@Path("id") id: Long): Response<Post>

    @POST("posts")
    suspend fun createPost(@Body body: CreatePostRequest): Response<Post>

    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: Long): Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likePost(@Path("id") id: Long): Response<Unit>

    @DELETE("posts/{id}/likes")
    suspend fun unlikePost(@Path("id") id: Long): Response<Unit>

    @GET("posts/{id}/comments")
    suspend fun getComments(@Path("id") id: Long): Response<ListResponse<Comment>>

    @POST("posts/{id}/comments")
    suspend fun addComment(
        @Path("id") id: Long,
        @Body body: Map<String, @JvmSuppressWildcards Any>,
    ): Response<Comment>

    // Search / Users
    @GET("users")
    suspend fun searchUsers(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("filter") filter: String = "all",
        @Query("q") query: String? = null,
        @Query("country") country: String? = null,
        @Query("age_min") ageMin: Int? = null,
        @Query("age_max") ageMax: Int? = null,
        @Query("gender") gender: String? = null,
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
    ): Response<ListResponse<User>>

    // Messages / Chat
    @GET("messages/conversations")
    suspend fun getConversations(
        @Query("before") before: String? = null,
        @Query("limit") limit: Int = 20,
    ): Response<ListResponse<Conversation>>

    @GET("messages/conversations/{id}")
    suspend fun getConversation(@Path("id") id: Long): Response<Conversation>

    @GET("messages/conversations/{id}/messages")
    suspend fun getMessages(
        @Path("id") id: Long,
        @Query("before") before: String? = null,
        @Query("limit") limit: Int = 30,
    ): Response<ListResponse<Message>>

    @GET("messages/conversations/with/{uuid}")
    suspend fun getOrCreateConversation(@Path("uuid") uuid: String): Response<Conversation>

    @DELETE("messages/conversations/{id}")
    suspend fun deleteConversation(@Path("id") id: Long): Response<Unit>

    // Notifications
    @GET("notifications")
    suspend fun getNotifications(
        @Query("before") before: String? = null,
        @Query("limit") limit: Int = 20,
    ): Response<ListResponse<AppNotification>>

    @PUT("notifications/read-all")
    suspend fun markAllNotificationsRead(): Response<Unit>

    // Locations
    @GET("locations/countries")
    suspend fun getCountries(): Response<List<Country>>

    @GET("locations/countries/{code}/cities")
    suspend fun getCities(@Path("code") code: String): Response<List<City>>
}
