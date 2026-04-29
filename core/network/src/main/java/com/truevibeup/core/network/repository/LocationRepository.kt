package com.truevibeup.core.network.repository

import com.truevibeup.core.common.api.ApiResult
import com.truevibeup.core.common.model.City
import com.truevibeup.core.common.model.Country
import com.truevibeup.core.network.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getCountries(): ApiResult<List<Country>> {
        return try {
            val resp = api.getCountries()
            val body = resp.body()
            if (resp.isSuccessful && body != null) {
                ApiResult.Success(body)
            } else ApiResult.Error(resp.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getCities(countryCode: String): ApiResult<List<City>> {
        return try {
            val resp = api.getCities(countryCode)
            val body = resp.body()
            if (resp.isSuccessful && body != null) {
                ApiResult.Success(body)
            } else ApiResult.Error(resp.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }
}
