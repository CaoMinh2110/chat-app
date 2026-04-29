package com.truevibeup.core.network.repository

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.truevibeup.core.common.api.ApiResult
import com.truevibeup.core.common.model.User
import com.truevibeup.core.network.api.ApiService
import com.truevibeup.core.storage.SecureStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "AuthRepository"
@Singleton
class AuthRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val api: ApiService,
    private val secureStorage: SecureStorage,
) {
    @SuppressLint("HardwareIds")
    private suspend fun getOrCreateDeviceCredentials(): Triple<String, String, String> {
        val saved = secureStorage.getDeviceCredentials()
        val deviceId = if (saved != null) {
            saved.first
        } else {
            val id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            id
        }

        val password = deviceId + "pw-truevibeup"
        val email = "${deviceId.replace("-", "").substring(0, 16)}@device.app"
        
        if (saved == null) {
            secureStorage.saveDeviceCredentials(deviceId, password)
        }
        
        return Triple(email, password, deviceId)
    }

    suspend fun deviceRegister(name: String, gender: String, birthday: String?): ApiResult<User> {
        return try {
            val (email, password, _) = getOrCreateDeviceCredentials()
            val body = mutableMapOf(
                "name" to name,
                "email" to email,
                "password" to password,
                "gender" to gender
            )
            birthday?.let { body["birthday"] = it }

            val resp = api.deviceRegister(body)
            val tokens = resp.body()
            if (resp.isSuccessful && tokens != null) {
                secureStorage.saveTokens(tokens.accessToken, tokens.refreshToken)
                secureStorage.setIsGuestMode(true)
                val userResp = api.getMe()
                val user = userResp.body()
                if (userResp.isSuccessful && user != null) {
                    secureStorage.saveUser(user)
                    ApiResult.Success(user)
                } else ApiResult.Error("Failed to fetch user")
            } else ApiResult.Error(resp.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun emailRegister(email: String, password: String, name: String, gender: String, birthday: String?): ApiResult<User> {
        return try {
            val body = mutableMapOf(
                "name" to name,
                "email" to email,
                "password" to password,
                "gender" to gender
            )
            birthday?.takeIf { it.isNotEmpty() }?.let { body["birthday"] = it }

            val resp = api.deviceRegister(body)
            val tokens = resp.body()
            if (resp.isSuccessful && tokens != null) {
                secureStorage.saveTokens(tokens.accessToken, tokens.refreshToken)
                val userResp = api.getMe()
                val user = userResp.body()
                if (userResp.isSuccessful && user != null) {
                    secureStorage.saveUser(user)
                    ApiResult.Success(user)
                } else ApiResult.Error("Failed to fetch user")
            } else {
                val errorBody = resp.errorBody()?.string()
                val message = try {
                    org.json.JSONObject(errorBody ?: "").optString("message", resp.message() ?: "Registration failed")
                } catch (e: Exception) { resp.message() ?: "Registration failed" }
                ApiResult.Error(message)
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun emailLogin(email: String, password: String): ApiResult<User> {
        return try {
            val body = mapOf("email" to email, "password" to password)
            val resp = api.deviceLogin(body)
            val tokens = resp.body()
            if (resp.isSuccessful && tokens != null) {
                secureStorage.saveTokens(tokens.accessToken, tokens.refreshToken)
                val userResp = api.getMe()
                val user = userResp.body()
                if (userResp.isSuccessful && user != null) {
                    secureStorage.saveUser(user)
                    ApiResult.Success(user)
                } else ApiResult.Error("Failed to fetch user")
            } else {
                val errorBody = resp.errorBody()?.string()
                val message = try {
                    org.json.JSONObject(errorBody ?: "").optString("message", resp.message() ?: "Login failed")
                } catch (e: Exception) { resp.message() ?: "Login failed" }
                ApiResult.Error(message)
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun deviceLogin(): ApiResult<User> {
        return try {
            val (email, password, _) = getOrCreateDeviceCredentials()
            val body = mapOf("email" to email, "password" to password)
            val resp = api.deviceLogin(body)
            val tokens = resp.body()
            if (resp.isSuccessful && tokens != null) {
                secureStorage.saveTokens(tokens.accessToken, tokens.refreshToken)
                secureStorage.setIsGuestMode(true)
                val userResp = api.getMe()
                val user = userResp.body()
                if (userResp.isSuccessful && user != null) {
                    secureStorage.saveUser(user)
                    ApiResult.Success(user)
                } else ApiResult.Error("Failed to fetch user")
            } else ApiResult.Error(resp.message())
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun logout() {
        try { api.logout() } catch (_: Exception) {}
        secureStorage.clearAll()
    }

    suspend fun getCurrentUser(): User? = secureStorage.getUser()

    suspend fun isGuestMode(): Boolean = secureStorage.isGuestMode()

    suspend fun refreshCurrentUser(): ApiResult<User> {
        return try {
            val resp = api.getMe()
            val user = resp.body()
            if (resp.isSuccessful && user != null) {
                secureStorage.saveUser(user)
                ApiResult.Success(user)
            } else ApiResult.Error(resp.message())
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "Unknown error")
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun updateProfile(params: Map<String, Any?>): ApiResult<User> {
        return try {
            val resp = api.updateMe(params)

            if (resp.isSuccessful) {
                Log.d(TAG, "Profile updated successfully, fetching user data")

                when (val meResult = refreshCurrentUser()) {
                    is ApiResult.Success -> ApiResult.Success(meResult.data)
                    is ApiResult.Error -> ApiResult.Error("Profile updated but failed to fetch profile: ${meResult.message}")
                }
            } else ApiResult.Error(resp.message())
        } catch (e: Exception) {
            Log.d(TAG, e.message ?: "Unknown error")
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun updateAvatar(url: String): ApiResult<User> {
        return try {
            val resp = api.updateAvatar(mapOf("avatar_url" to url))

            if (resp.isSuccessful) {
                Log.d(TAG, "Avatar updated successfully, fetching user data")

                return when (val meResult = refreshCurrentUser()) {
                    is ApiResult.Success -> ApiResult.Success(meResult.data)
                    is ApiResult.Error -> ApiResult.Error("Avatar updated but failed to fetch profile: ${meResult.message}")
                }
            } else {
                val errorBody = resp.errorBody()?.string()
                val message = when {
                    resp.code() == 401 -> "Authentication failed. Please login again."
                    resp.code() == 403 -> "You don't have permission to update your avatar."
                    resp.code() == 400 -> "Invalid request. Please check the image URL."
                    resp.code() == 500 -> "Server error. Please try again later."
                    !errorBody.isNullOrEmpty() -> try {
                        val errorJson = JSONObject(errorBody)
                        errorJson.optString("message", resp.message() ?: "Unknown error")
                    } catch (e: Exception) {
                        resp.message() ?: "Failed to update avatar: $e"
                    }
                    else -> resp.message() ?: "Failed to update avatar"
                }
                ApiResult.Error(message)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating avatar", e)
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }
}
