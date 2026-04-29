package com.truevibeup.core.network.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.truevibeup.core.network.BuildConfig
import com.truevibeup.core.storage.SecureStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "UploadRepository"

data class UploadResult(
    val success: Boolean,
    val url: String? = null,
    val error: String? = null
)

@Singleton
class UploadRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val secureStorage: SecureStorage
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val PROJECT_NAME = "truevibeup"
    private val BASE_URL = BuildConfig.IMG_URL
    
    // Admin token (same as web app) - used for upload since server expects it
    private val ADMIN_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjoiYWRtaW5hZG1pbiIsInVzZXJJZCI6IjEiLCJ1c2VyR3JvdXBzSWQiOjEsImlhdCI6MTc2NDcyODIzMSwiZXhwIjoyMDgwMDg4MjMxfQ.FQM1dxtwWU0RdBJLtMdYG36gsM-74JN-1kAN20dmIwY"

    suspend fun uploadImage(
        uri: Uri,
        fileName: String? = null,
        mimeType: String? = null
    ): UploadResult = withContext(Dispatchers.IO) {
        try {
            // Step 1: Read file content
            Log.d(TAG, "Step 1️⃣  Reading file content...")
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri) 
                ?: return@withContext UploadResult(false, error = "Failed to open input stream")
            
            val bytes = inputStream.readBytes()
            val finalFileName = fileName ?: getFileName(uri) ?: "photo.jpg"
            val finalMimeType = mimeType ?: contentResolver.getType(uri) ?: "image/jpeg"
            Log.d(TAG, "File: $finalFileName (${finalMimeType}), Size: ${bytes.size} bytes")

            // Step 2: Build multipart request
            Log.d(TAG, "Step 2️⃣  Building multipart request...")
            val requestFile = bytes.toRequestBody(finalMimeType.toMediaTypeOrNull(), 0, bytes.size)
            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", finalFileName, requestFile)
                .build()

            // Step 3: Send upload request with admin token
            Log.d(TAG, "Step 3️⃣  Sending upload request...")
            val uploadUrl = "${BASE_URL}/files/uploadHandler/"
            Log.d(TAG, "Upload URL: $uploadUrl")

            val request = Request.Builder()
                .url(uploadUrl)
                .addHeader("x-auth-key", ADMIN_TOKEN)
                .addHeader("x-auth-project", PROJECT_NAME)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d(TAG, "Response code: ${response.code}")
            Log.d(TAG, "Response body: $responseBody")

            // Step 4: Parse response
            Log.d(TAG, "Step 4️⃣  Parsing response...")
            if (response.isSuccessful && responseBody != null) {
                try {
                    val json = JSONObject(responseBody)
                    val path = json.optString("path")
                    if (path.isNotEmpty()) {
                        val publicUrl = "${BASE_URL}/get/images/${PROJECT_NAME}/$path"
                        Log.d(TAG, "✅ Upload successful!")
                        Log.d(TAG, "Public URL: $publicUrl")
                        return@withContext UploadResult(true, url = publicUrl)
                    } else {
                        val errorMsg = json.optString("message", "No path in response")
                        Log.e(TAG, "Response error: $errorMsg")
                        return@withContext UploadResult(false, error = errorMsg)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse JSON response", e)
                    return@withContext UploadResult(false, error = "Invalid response format: ${e.message}")
                }
            } else {
                val errorMsg = "Upload failed: ${response.code}"
                Log.e(TAG, "$errorMsg - $responseBody")
                
                // Parse error details from server
                if (responseBody != null) {
                    try {
                        val errorJson = JSONObject(responseBody)
                        val serverMessage = errorJson.optJSONObject("error")?.optString("message", "Unknown error") 
                            ?: errorJson.optString("message", "Unknown error")
                        
                        Log.e(TAG, "Server error details: $serverMessage")
                        
                        val userFriendlyError = when (response.code) {
                            500 -> "Upload failed: Server error. Please try again later."
                            400 -> "Upload failed: Invalid request. Please try again."
                            401, 403 -> "Upload failed: Authentication error."
                            else -> errorMsg
                        }
                        
                        return@withContext UploadResult(false, error = userFriendlyError)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse error response", e)
                    }
                }
                
                return@withContext UploadResult(false, error = errorMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Upload exception", e)
            UploadResult(false, error = e.message ?: "Unknown error")
        }
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) name = it.getString(index)
            }
        }
        return name
    }
}
