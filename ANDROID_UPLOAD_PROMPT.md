# Android Upload Implementation Prompt

## Tổng quan Upload Mechanism
Ứng dụng cần upload files (images, audio, video) lên một CDN storage.

## Thông tin CDN Storage

### Endpoint
- **Base URL:** Lấy từ `BuildConfig.IMG_URL` (được định nghĩa trong `build.gradle`)
- **Upload Endpoint:** `{BASE_URL}/files/uploadHandler/`
- **Example:** `https://example.com/files/uploadHandler/`

### Authentication Headers
```json
{
  "x-auth-key": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjoiYWRtaW5hZG1pbiIsInVzZXJJZCI6IjEiLCJ1c2VyR3JvdXBzSWQiOjEsImlhdCI6MTc2NDcyODIzMSwiZXhwIjoyMDgwMDg4MjMxfQ.FQM1dxtwWU0RdBJLtMdYG36gsM-74JN-1kAN20dmIwY",
  "x-auth-project": "truevibeup"
}
```

⚠️ **TÍNH BẢO MẬT:** Token này cần được lấy động từ Backend hoặc Server, không được hardcode trong client.

### Content-Type
- Header tự động set là `multipart/form-data` khi gửi file

## Upload Request Format

### Multipart Body
```
Content-Disposition: form-data; name="file"; filename="{fileName}"
Content-Type: {mimeType}

{file binary data}
```

### File Types & MIME Types

| Type | Extension | MIME Type | Used In |
|------|-----------|-----------|---------|
| Image | jpg/jpeg/png | image/jpeg | Profile avatar, chat images |
| Audio | m4a/mp3 | audio/m4a | Voice messages |
| Video | mp4 | video/mp4 | Video messages |

### Default File Names (nếu không có)
- Image: `photo.jpg`
- Audio: `voice-message.m4a`
- Video: `video.mp4`

## API Response Format

### Success Response (200 OK)
```json
{
  "path": "relative/path/to/file.jpg"
}
```

### Error Response (4xx/5xx)
```json
{
  "message": "Error description"
}
```

## Public URL Construction

Sau khi upload thành công, convert `path` từ response thành public URL:

```
Public URL = {BASE_URL}/get/images/{project}/{path}
Example: https://example.com/get/images/truevibeup/relative/path/to/file.jpg
```

## Upload Functions Cần Implement

### 1. Single Image Upload
```
Function: uploadImage()
Input:
  - uri: String (file path hoặc content URI)
  - fileName: String (optional)
  - mimeType: String (optional)
Output:
  - Success: {success: true, url: String}
  - Error: {success: false, error: String}
```

### 2. Single Audio Upload
```
Function: uploadAudio()
Input:
  - uri: String
  - fileName: String (optional)
  - mimeType: String (optional)
Output:
  - Success: {success: true, url: String}
  - Error: {success: false, error: String}
```

### 3. Batch Image Upload
```
Function: uploadImages()
Input:
  - List<UploadAsset> (multiple URIs)
Output:
  - Success: List<String> (public URLs)
  - Throws Exception if any upload fails
```

## Key Implementation Details

### URI Handling
- Accept both `file://` and `content://` schemes
- App folder files: `/data/data/{package}/files/{fileName}` format

### Error Handling
- Network timeout: ~15 seconds
- Retry logic: Optional (failed uploads should bubble up to UI)
- Validation: Check file exists before upload

### File Reading
- Use Android's built-in file APIs (File class, URI, etc.)
- Handle permission requests (READ_EXTERNAL_STORAGE)

### Progress Tracking (Optional)
- Implement progress callback for large files
- Useful for UI feedback during upload

## Use Cases

1. **Chat Image Upload** - User sends image in chat
2. **Profile Avatar Update** - User uploads/changes profile picture
3. **Onboarding Avatar** - User selects avatar during registration
4. **Voice Messages** - User records and uploads audio
5. **Video Messages** - User uploads video files

## Network Configuration

- **Request Type:** POST
- **Content-Type:** multipart/form-data (auto-set)
- **Timeout:** 15 seconds
- **Retry:** Handle client-side or server-side
- **Authentication:** Via x-auth-key and x-auth-project headers

## Dependencies Required
- OkHttp or HttpURLConnection (for HTTP requests)
- RequestBody for multipart
- File handling utilities
- JSON parsing (gson or kotlinx.serialization)

## Testing Scenarios
- ✅ Upload valid image/audio/video
- ✅ Upload with missing file
- ✅ Network timeout handling
- ✅ Invalid file path
- ✅ Large file upload (> 10MB)
- ✅ Concurrent uploads (batch)
- ✅ Response parsing errors
