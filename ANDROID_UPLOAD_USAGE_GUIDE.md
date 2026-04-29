# Hướng dẫn sử dụng Upload Prompt cho Android Studio

## 📋 Cách sử dụng

### Với Android Studio + GitHub Copilot
1. Mở file `.instructions.md` hoặc `.copilot-instructions.md` trong root project
2. Copy nội dung từ `ANDROID_UPLOAD_PROMPT.md` vào file instructions
3. Khi tạo class upload, GitHub Copilot sẽ tham khảo prompt này

### Với Cursor AI hoặc CodeGPT
1. Mở Cursor/CodeGPT chat panel
2. Paste prompt từ `ANDROID_UPLOAD_PROMPT.md`
3. Yêu cầu: "Hãy implement UploadManager class theo prompt này"

### Với Manual Development
1. Tham khảo `ANDROID_UPLOAD_SAMPLE.kt` cho cấu trúc code
2. Đọc `ANDROID_UPLOAD_PROMPT.md` để hiểu chi tiết từng yêu cầu
3. Implement theo step-by-step

## 🎯 Nội dung Prompt bao gồm

### 1. **Overview** (Tư duy chung)
- Mục đích của upload
- Loại files cần hỗ trợ

### 2. **Technical Specs** (Chi tiết kỹ thuật)
- CDN endpoint URL
- Authentication headers
- Content-type requirements

### 3. **Request/Response Format** (API Contract)
- Cách gửi request (multipart)
- Cách parse response
- Error handling

### 4. **Functions to Implement** (Scope công việc)
- Danh sách functions cần viết
- Input/output của mỗi function
- Kết quả mong đợi

### 5. **Implementation Details** (Hướng dẫn chi tiết)
- URI handling
- Error cases
- Optional features (progress tracking)

### 6. **Use Cases** (Bối cảnh thực tế)
- Nơi nào dùng upload
- Business logic liên quan

### 7. **Testing Scenarios** (QA checklist)
- Test cases cần cover
- Edge cases

## ⚠️ Điểm quan trọng khi viết Prompt

Khi ai đó implement từ prompt này, cần chắc:

✅ **Luôn có:**
- [ ] Error handling (network, file not found, timeout)
- [ ] Authentication headers (x-auth-key, x-auth-project)
- [ ] URL construction logic (convert response path → public URL)
- [ ] Support for multiple file types (image, audio, video)
- [ ] Timeout mechanism (15 seconds)

❌ **Tránh:**
- [ ] Hardcode token trong source code
- [ ] Không validate file exists before upload
- [ ] Quên handle response errors
- [ ] Không support cancellation

## 📝 Cách mở rộng Prompt

Nếu sau này cần thêm tính năng:

1. **Thêm progress tracking:**
   - Thêm section "Progress Tracking" trong prompt
   - Chi tiết callback interface
   - Use case example

2. **Thêm retry logic:**
   - Define retry strategy (exponential backoff)
   - Max retry count
   - Which errors are retryable

3. **Thêm compression:**
   - Image compression trước upload
   - Quality settings
   - Size limits

4. **Batch upload optimization:**
   - Concurrent upload count
   - Queue management
   - Cancel batch operation

## 🔍 Verification Checklist

Sau khi developer implement, kiểm tra:

- [ ] Tất cả functions đã implement (uploadImage, uploadAudio, uploadImages)
- [ ] Authentication headers gửi đúng
- [ ] Public URL format đúng
- [ ] Error messages rõ ràng
- [ ] Timeout được implement
- [ ] File permissions handled (Android 6.0+)
- [ ] Unit tests cover basic cases
- [ ] Concurrent uploads không conflict
