package com.truevibeup.core.network.repository

import com.truevibeup.core.common.api.ApiResult
import com.truevibeup.core.common.model.City
import com.truevibeup.core.common.model.Country
import com.truevibeup.core.common.model.Conversation
import com.truevibeup.core.common.model.User
import com.truevibeup.core.network.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(private val api: ApiService) {

    suspend fun searchUsers(
        page: Int = 1, query: String? = null, filter: String = "all", country: String? = null,
        ageMin: Int? = null, ageMax: Int? = null, gender: String? = null,
        lat: Double? = null, lng: Double? = null,
    ): ApiResult<List<User>> = try {
        val resp = api.searchUsers(page = page, query = query, filter = filter, country = country,
            ageMin = ageMin, ageMax = ageMax, gender = gender, lat = lat, lng = lng)
        if (resp.isSuccessful) ApiResult.Success(resp.body()?.data ?: emptyList())
        else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun getUser(uuid: String): ApiResult<User> = try {
        val resp = api.getUser(uuid)
        val user = resp.body()
        if (resp.isSuccessful && user != null) ApiResult.Success(user)
        else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun getFollowers(uuid: String, query: String? = null): ApiResult<List<User>> = try {
        val resp = api.getFollowers(uuid, query)
        if (resp.isSuccessful) ApiResult.Success(resp.body()?.data ?: emptyList())
        else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun getFollowing(uuid: String, query: String? = null): ApiResult<List<User>> = try {
        val resp = api.getFollowing(uuid, query)
        if (resp.isSuccessful) ApiResult.Success(resp.body()?.data ?: emptyList())
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

    suspend fun getOrCreateConversation(uuid: String): ApiResult<Conversation> = try {
        val resp = api.getOrCreateConversation(uuid)
        val conversation = resp.body()
        if (resp.isSuccessful && conversation != null) ApiResult.Success(conversation)
        else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun getCountries(): ApiResult<List<Country>> = try {
        val resp = api.getCountries()
        if (resp.isSuccessful) ApiResult.Success(resp.body() ?: emptyList())
        else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }

    suspend fun getCities(code: String): ApiResult<List<City>> = try {
        val resp = api.getCities(code)
        if (resp.isSuccessful) ApiResult.Success(resp.body() ?: emptyList())
        else ApiResult.Error(resp.message())
    } catch (e: Exception) { ApiResult.Error(e.message ?: "Unknown error") }
}
