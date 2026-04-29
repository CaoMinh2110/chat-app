package com.truevibeup.core.network.api

import com.truevibeup.core.storage.SecureStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AuthInterceptor(private val secureStorage: SecureStorage) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { secureStorage.getAccessToken() }
        val request = if (token != null) {
            chain.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}

class TokenAuthenticator(
    private val secureStorage: SecureStorage,
    private val apiServiceProvider: () -> ApiService,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = runBlocking { secureStorage.getRefreshToken() } ?: return null

        synchronized(this) {
            val currentToken = runBlocking { secureStorage.getAccessToken() }
            val authHeader = response.request.header("Authorization")
            
            // Nếu token hiện tại trong storage khác với token đã gửi (đã được refresh bởi thread khác)
            if (authHeader != "Bearer $currentToken") {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            // Thực hiện refresh token
            val newTokens = try {
                runBlocking {
                    apiServiceProvider().refreshToken(mapOf("refresh_token" to refreshToken)).body()
                }
            } catch (e: Exception) {
                null
            }

            return if (newTokens != null) {
                runBlocking {
                    secureStorage.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                }
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${newTokens.accessToken}")
                    .build()
            } else {
                runBlocking { secureStorage.clearAll() }
                null
            }
        }
    }
}

fun createOkHttpClient(
    secureStorage: SecureStorage,
    apiServiceProvider: () -> ApiService,
): OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor(secureStorage))
    .authenticator(TokenAuthenticator(secureStorage, apiServiceProvider))
    .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

fun createApiService(baseUrl: String, okHttpClient: OkHttpClient, gson: com.google.gson.Gson): ApiService =
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(ApiService::class.java)
