package com.truevibeup.core.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.truevibeup.core.common.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")
private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

@Singleton
class SecureStorage(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
) {
    private val appPrefs = context.appDataStore
    private val userPrefs = context.userDataStore

    companion object {
        // User session keys
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val CURRENT_USER = stringPreferencesKey("current_user")
        val IS_GUEST_MODE = booleanPreferencesKey("is_guest_mode")

        // App settings keys
        val LANGUAGE = stringPreferencesKey("language")
        val DEVICE_ID = stringPreferencesKey("device_id")
        val DEVICE_PASSWORD = stringPreferencesKey("device_password")
    }

    // --- User Session Methods ---

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        userPrefs.edit { prefs ->
            prefs[ACCESS_TOKEN] = accessToken
            prefs[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun getAccessToken(): String? =
        userPrefs.data.map { it[ACCESS_TOKEN] }.firstOrNull()

    suspend fun getRefreshToken(): String? =
        userPrefs.data.map { it[REFRESH_TOKEN] }.firstOrNull()

    suspend fun saveUser(user: User) {
        userPrefs.edit { prefs ->
            prefs[CURRENT_USER] = gson.toJson(user)
        }
    }

    suspend fun getUser(): User? {
        val json = userPrefs.data.map { it[CURRENT_USER] }.firstOrNull() ?: return null
        return try { gson.fromJson(json, User::class.java) } catch (e: Exception) { null }
    }

    suspend fun setIsGuestMode(isGuest: Boolean) {
        userPrefs.edit { prefs ->
            prefs[IS_GUEST_MODE] = isGuest
        }
    }

    suspend fun isGuestMode(): Boolean =
        userPrefs.data.map { it[IS_GUEST_MODE] ?: false }.firstOrNull() ?: false

    suspend fun isLoggedIn(): Boolean = getAccessToken() != null

    /**
     * Clears only user-specific session data (tokens, profile, guest flag).
     * Keeps app settings like language and device credentials.
     */
    suspend fun clearAll() {
        userPrefs.edit { it.clear() }
    }

    // --- App Settings Methods ---

    suspend fun getLanguage(): String? =
        appPrefs.data.map { it[LANGUAGE] }.firstOrNull()

    suspend fun setLanguage(code: String) {
        appPrefs.edit { it[LANGUAGE] = code }
    }

    suspend fun saveDeviceCredentials(deviceId: String, devicePassword: String) {
        appPrefs.edit { prefs ->
            prefs[DEVICE_ID] = deviceId
            prefs[DEVICE_PASSWORD] = devicePassword
        }
    }

    suspend fun getDeviceCredentials(): Pair<String, String>? {
        val id = appPrefs.data.map { it[DEVICE_ID] }.firstOrNull() ?: return null
        val pass = appPrefs.data.map { it[DEVICE_PASSWORD] }.firstOrNull() ?: return null
        return Pair(id, pass)
    }
}
