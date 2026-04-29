# API Documentation Summary - Chat App Project

## 📍 Files Liên Quan đến API Documentation

### 1. **Documentation Files** (Markdown)

- `ANDROID_UPLOAD_PROMPT.md` - Chi tiết về upload endpoint, format request/response
- `ANDROID_UPLOAD_USAGE_GUIDE.md` - Hướng dẫn sử dụng prompt cho developers
- `ANDROID_UPLOAD_SAMPLE.kt` - Code sample cho upload implementation
- `ARCHITECTURE.md` - Kiến trúc multi-module
- `MODULE_GUIDE.md` - Hướng dẫn module development
- `MIGRATION_GUIDE.md` - Migration guide từ single-module sang multi-module
- `MULTI_MODULE_SETUP.md` - Setup hướng dẫn cho multi-module

### 2. **API Service Files** (Kotlin)

- `core/network/src/main/java/com/truevibeup/core/network/api/ApiService.kt` - REST API endpoints definition
- `core/network/src/main/java/com/truevibeup/core/network/api/ApiClient.kt` - API client configuration
- `core/network/src/main/java/com/truevibeup/core/network/repository/UploadRepository.kt` - Upload implementation

### 3. **JSON Configuration Files**

- `app/google-services.json` - Firebase configuration (not pure API docs)

### 4. **API Folder Structure**

```
core/network/src/main/java/com/truevibeup/core/network/
├── api/
│   ├── ApiService.kt          # Main API interface
│   ├── ApiClient.kt           # HTTP client setup
│   └── (other API configs)
├── repository/
│   ├── UploadRepository.kt    # Upload handling
│   └── (other repository files)
├── socket/
│   └── (Socket.IO files)
├── NetworkConstants.kt         # Network constants
└── di/
    └── (Dependency injection modules)
```

---

## 📤 UPLOAD ENDPOINT - Chi Tiết Lengkap

### **Endpoint Information**

| Property          | Value                                      |
| ----------------- | ------------------------------------------ |
| **HTTP Method**   | POST                                       |
| **Base URL**      | `{BuildConfig.IMG_URL}`                    |
| **Endpoint Path** | `/files/uploadHandler/`                    |
| **Full URL**      | `{BASE_URL}/files/uploadHandler/`          |
| **Example**       | `https://example.com/files/uploadHandler/` |

### **Request Headers**

```json
{
  "Authorization": "Bearer {accessToken}",
  "x-auth-project": "truevibeup",
  "Content-Type": "multipart/form-data"
}
```

**Notes:**

- `Authorization`: Lấy từ `SecureStorage.getAccessToken()`
- `x-auth-project`: Hardcoded là "truevibeup" (project name)
- `Content-Type`: Tự động set khi gửi multipart form

### **Request Body (Multipart Form)**

```
POST /files/uploadHandler/ HTTP/1.1
Host: {BASE_URL}
Authorization: Bearer {accessToken}
x-auth-project: truevibeup
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary

------WebKitFormBoundary
Content-Disposition: form-data; name="file"; filename="{fileName}"
Content-Type: {mimeType}

{file binary data}
------WebKitFormBoundary--
```

### **Supported File Types**

| Type  | Extension      | MIME Type             | Usage                       |
| ----- | -------------- | --------------------- | --------------------------- |
| Image | jpg, jpeg, png | image/jpeg, image/png | Profile avatar, chat images |
| Audio | m4a, mp3       | audio/m4a, audio/mp3  | Voice messages              |
| Video | mp4            | video/mp4             | Video messages              |

### **Response Format**

#### ✅ Success Response (HTTP 200)

```json
{
  "path": "relative/path/to/file.jpg"
}
```

#### ❌ Error Response (HTTP 4xx/5xx)

```json
{
  "message": "Error description"
}
```

### **Public URL Construction**

Sau khi upload thành công, convert `path` từ response thành public URL:

```
Public URL = {BASE_URL}/get/images/{PROJECT_NAME}/{path}
Example: https://example.com/get/images/truevibeup/relative/path/to/file.jpg
```

---

## 🔧 Implementation Details

### **UploadRepository Class**

**Location:** `core/network/src/main/java/com/truevibeup/core/network/repository/UploadRepository.kt`

**Configuration:**

```kotlin
private val client = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .writeTimeout(15, TimeUnit.SECONDS)
    .readTimeout(15, TimeUnit.SECONDS)
    .build()

private val PROJECT_NAME = "truevibeup"
private val BASE_URL = BuildConfig.IMG_URL
```

**Key Functions:**

- `uploadImage(uri: Uri, fileName: String?, mimeType: String?): UploadResult`
- `uploadAudio(uri: Uri, fileName: String?, mimeType: String?): UploadResult`
- `uploadImages(uris: List<Uri>): List<UploadResult>`

**Return Type:**

```kotlin
data class UploadResult(
    val success: Boolean,
    val url: String? = null,
    val error: String? = null
)
```

### **Upload Process Flow**

1. Read file bytes từ Uri
2. Validate file exists
3. Get MIME type và file name
4. Build multipart request body
5. Get access token từ SecureStorage
6. Send POST request với headers
7. Parse JSON response
8. Construct public URL
9. Return UploadResult

---

## 📋 REST API Endpoints (ApiService.kt)

### **Authentication**

- `POST /auth/social` - Social login
- `POST /auth/register` - Device register
- `POST /auth/login` - Device login
- `POST /auth/refresh` - Refresh token
- `POST /auth/logout` - Logout

### **Users**

- `GET /users/me` - Get current user
- `PUT /users/me` - Update user info
- `PUT /users/me/avatar` - Update avatar
- `PUT /users/me/photos` - Update photos
- `GET /users/me/badges` - Get badges
- `GET /users/{uuid}` - Get user by UUID
- `POST /follows/{userId}` - Follow user
- `DELETE /follows/{userId}` - Unfollow user

### **Feed & Posts**

- `GET /feed` - Get feed (paginated, with filters)
- `GET /posts/{id}` - Get post details
- `POST /posts` - Create post
- `DELETE /posts/{id}` - Delete post
- `POST /posts/{id}/likes` - Like post
- `DELETE /posts/{id}/likes` - Unlike post
- `GET /posts/{id}/comments` - Get comments
- `POST /posts/{id}/comments` - Add comment

### **Search/Users**

- `GET /users` - Search/filter users (với nhiều query params như age, location, etc.)

### **Messages/Chat**

- `GET /messages/conversations` - Get conversations list
- `GET /messages/conversations/{id}` - Get conversation details
- `GET /messages/conversations/{id}/messages` - Get messages in conversation
- `GET /messages/conversations/with/{uuid}` - Get or create conversation
- `DELETE /messages/conversations/{id}` - Delete conversation

### **Notifications**

- `GET /notifications` - Get notifications
- `PUT /notifications/read-all` - Mark all as read

### **Locations**

- `GET /locations/countries` - Get countries
- `GET /locations/countries/{code}/cities` - Get cities

---

## ⚠️ Security & Important Notes

1. **Token Management:**
   - ❌ KHÔNG hardcode token trong source code
   - ✅ Lấy token động từ `SecureStorage.getAccessToken()`
   - ✅ Token được refresh qua `/auth/refresh` endpoint

2. **Timeouts:**
   - Connection timeout: 15 seconds
   - Write timeout: 15 seconds
   - Read timeout: 15 seconds

3. **Error Handling:**
   - Check HTTP response code
   - Parse JSON error message
   - Handle network exceptions

4. **File Validation:**
   - Validate file exists trước upload
   - Check file size (nếu có limit)
   - Verify MIME type

---

## 📚 Related Documentation Files

1. **ANDROID_UPLOAD_PROMPT.md** - Complete prompt cho AI developers
2. **ANDROID_UPLOAD_SAMPLE.kt** - Code implementation example
3. **ANDROID_UPLOAD_USAGE_GUIDE.md** - How to use the prompt
4. **ARCHITECTURE.md** - Project architecture overview
5. **MULTI_MODULE_SETUP.md** - Module setup guide

---

## 🔍 File Locations Summary

```
d:\Android\Projects\chat-app\
├── ANDROID_UPLOAD_PROMPT.md              ← Upload API spec
├── ANDROID_UPLOAD_SAMPLE.kt              ← Code sample
├── ANDROID_UPLOAD_USAGE_GUIDE.md         ← Usage guide
├── ARCHITECTURE.md                        ← Architecture docs
├── MULTI_MODULE_SETUP.md                 ← Module setup
├── MODULE_GUIDE.md                        ← Module rules
├── MIGRATION_GUIDE.md                     ← Migration docs
└── core/network/src/main/java/com/truevibeup/core/network/
    ├── api/ApiService.kt                 ← All REST endpoints
    ├── api/ApiClient.kt                  ← HTTP client config
    └── repository/UploadRepository.kt     ← Upload implementation
```

---

**Generated:** April 7, 2026
**Project:** TrueVibeUp Chat App (Multi-Module Architecture)
