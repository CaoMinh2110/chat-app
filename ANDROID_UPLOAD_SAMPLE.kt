// Sample Kotlin Implementation for Android Upload

// ActivityUploadImage.kt
class UploadImageActivity : AppCompatActivity() {
    private val uploadManager = UploadManager(this)
    
    fun pickAndUploadImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_CODE)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == REQUEST_IMAGE_CODE && resultCode == RESULT_OK) {
            val imageUri = data?.data ?: return
            val fileName = getFileNameFromUri(imageUri)
            val mimeType = contentResolver.getType(imageUri)
            
            lifecycleScope.launch {
                val result = uploadManager.uploadImage(
                    uri = imageUri.toString(),
                    fileName = fileName,
                    mimeType = mimeType
                )
                
                if (result.success) {
                    // Use result.url for backend
                    updateProfileImage(result.url)
                } else {
                    showError(result.error)
                }
            }
        }
    }
}

// UploadManager.kt
class UploadManager(private val context: Context) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .build()
    
    suspend fun uploadImage(
        uri: String,
        fileName: String? = null,
        mimeType: String? = null
    ): UploadResult = withContext(Dispatchers.IO) {
        try {
            val file = getFileFromUri(uri)
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    fileName ?: "photo.jpg",
                    file.asRequestBody(mimeType?.toMediaType() ?: "image/jpeg".toMediaType())
                )
                .build()
            
            val request = Request.Builder()
                .url("${BuildConfig.IMG_URL}/files/uploadHandler/")
                .addHeader("x-auth-key", CDN_TOKEN)
                .addHeader("x-auth-project", "truevibeup")
                .post(requestBody)
                .build()
            
            val response = client.newCall(request).execute()
            
            return@withContext if (response.isSuccessful) {
                val json = JSONObject(response.body?.string() ?: "{}")
                val path = json.optString("path")
                if (path.isEmpty()) {
                    UploadResult(false, json.optString("message", "No path in response"))
                } else {
                    val url = "${BuildConfig.IMG_URL}/get/images/truevibeup/$path"
                    UploadResult(true, url)
                }
            } else {
                UploadResult(false, "Upload failed: ${response.code}")
            }
        } catch (e: Exception) {
            UploadResult(false, e.message ?: "Unknown error")
        }
    }
    
    private fun getFileFromUri(uri: String): File {
        return File(uri.removePrefix("file://"))
    }
}

// Data Classes
data class UploadResult(
    val success: Boolean,
    val urlOrError: String
) {
    val url: String? get() = if (success) urlOrError else null
    val error: String? get() = if (!success) urlOrError else null
}

// BuildConfig should include:
// IMG_URL = "https://example.com"
