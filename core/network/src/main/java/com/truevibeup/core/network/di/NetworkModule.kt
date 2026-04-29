package com.truevibeup.core.network.di

import com.google.gson.Gson
import com.truevibeup.core.network.api.ApiService
import com.truevibeup.core.network.api.createApiService
import com.truevibeup.core.network.api.createOkHttpClient
import com.truevibeup.core.storage.SecureStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(secureStorage: SecureStorage, gson: Gson): ApiService {
        // SECURITY: Base URL loaded from local.properties via BuildConfig
        // NEVER hardcode API endpoints in source code!
        val baseUrl = com.truevibeup.core.network.BuildConfig.API_BASE_URL
        var service: ApiService? = null
        val client = createOkHttpClient(secureStorage) { 
            service ?: throw IllegalStateException("ApiService not yet initialized")
        }
        service = createApiService(baseUrl, client, gson)
        return service
    }
}
