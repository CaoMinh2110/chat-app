package com.truevibeup.core.network.util

import android.content.Context
import android.util.Log
import com.truevibeup.core.network.BuildConfig
import com.truevibeup.core.storage.SecureStorage
import kotlinx.coroutines.runBlocking

object UploadDiagnostics {
    private const val TAG = "UploadDiagnostics"

    /**
     * Run diagnostic checks for upload functionality
     * Returns a detailed report of what's working and what's not
     */
    fun performDiagnostics(context: Context, secureStorage: SecureStorage): DiagnosticReport {
        val report = DiagnosticReport()

        // 1. Check if user is logged in
        val isLoggedIn = runBlocking {
            secureStorage.isLoggedIn()
        }
        report.isLoggedIn = isLoggedIn
        Log.d(TAG, "✓ User logged in: $isLoggedIn")

        // 2. Check if access token exists
        val accessToken = runBlocking {
            secureStorage.getAccessToken()
        }
        report.accessTokenExists = accessToken != null
        report.accessToken = accessToken?.take(20)?.let { "$it..." }
        Log.d(TAG, "✓ Access token exists: ${report.accessTokenExists}")

        // 3. Check if device credentials exist
        val deviceCreds = runBlocking {
            secureStorage.getDeviceCredentials()
        }
        report.deviceCredsExists = deviceCreds != null
        Log.d(TAG, "✓ Device credentials exist: ${report.deviceCredsExists}")

        // 4. Check BuildConfig values
        report.baseUrl = BuildConfig.IMG_URL
        report.apiBaseUrl = BuildConfig.BASE_URL
        Log.d(TAG, "✓ Base URL: ${report.baseUrl}")
        Log.d(TAG, "✓ API Base URL: ${report.apiBaseUrl}")

        // 5. Check network connectivity
        report.isNetworkAvailable = isNetworkAvailable(context)
        Log.d(TAG, "✓ Network available: ${report.isNetworkAvailable}")

        return report
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        return try {
            val connectivityManager =
                context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE)
                    as android.net.ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork
            activeNetwork != null
        } catch (e: Exception) {
            Log.e(TAG, "Error checking network", e)
            false
        }
    }

    data class DiagnosticReport(
        var isLoggedIn: Boolean = false,
        var accessTokenExists: Boolean = false,
        var accessToken: String? = null,
        var deviceCredsExists: Boolean = false,
        var baseUrl: String = "",
        var apiBaseUrl: String = "",
        var isNetworkAvailable: Boolean = false
    ) {
        fun toLogString(): String = """
            ═══════════════════════════════════════
            UPLOAD DIAGNOSTICS REPORT
            ═══════════════════════════════════════
            Logged In: $isLoggedIn
            Access Token: ${if (accessTokenExists) accessToken else "NOT FOUND"}
            Device Credentials: $deviceCredsExists
            Network Available: $isNetworkAvailable
            Base URL: $baseUrl
            API Base URL: $apiBaseUrl
            ═══════════════════════════════════════
        """.trimIndent()

        fun isReadyForUpload(): Boolean {
            return isLoggedIn && accessTokenExists && isNetworkAvailable
        }
    }
}
