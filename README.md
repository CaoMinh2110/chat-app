# 💬 ChatBox - Real-time Messaging App

**ChatBox** là ứng dụng nhắn tin real-time xây dựng với **Jetpack Compose** và **Multi-Module Architecture**, hỗ trợ các tính năng hiện đại như tìm kiếm người dùng, thông báo FCM, và gửi ảnh/media.

## ✨ Tính Năng Chính

### 1. **Xác Thực & Đăng Nhập**

- Đăng ký tài khoản mới
- Đăng nhập với email/mật khẩu
- Quên mật khẩu (reset via email)
- Lưu trữ token an toàn với SecureStorage

### 2. **Nhắn Tin Real-time**

- Chat 1-1 giữa người dùng
- Hỗ trợ tin nhắn text/hình ảnh/media
- Xem tình trạng "đang gõ" (typing indicator)
- Xem trạng thái "đã xem" (read receipts)
- Ghi âm & gửi voice messages

### 3. **Tìm Kiếm & Khám Phá**

- Tìm kiếm người dùng theo tên/username
- Xem danh sách bạn bè
- Khám phá người dùng mới (Discovery)

### 4. **Thông Báo**

- Push notification via Firebase Cloud Messaging (FCM)
- Thông báo tin nhắn mới
- Thông báo theo dõi/kết bạn

### 5. **Hồ Sơ Người Dùng**

- Xem & chỉnh sửa hồ sơ cá nhân
- Tải ảnh đại diện
- Cập nhật trạng thái (bio)

### 6. **Feed & News**

- Xem feed từ các bạn bè
- Tạo bài viết mới
- Like & bình luận

---

## 🏗️ Kiến Trúc & Công Nghệ

### Kiến Trúc

```
Multi-Module + Clean Architecture
├── app/                      # Application entry point
├── core/                      # Core libraries (reusable)
│   ├── common/               # Constants, extensions, utilities
│   ├── ui/                   # Shared UI components, theme
│   ├── network/              # Retrofit, Socket.IO, API layer
│   ├── storage/              # DataStore, SecureStorage, cache
│   └── di/                   # Shared dependency injection
└── feature/                   # Feature modules (isolated)
    ├── auth/                 # Authentication screens
    ├── chat/                 # Chat screens & messaging
    ├── home/                 # Chat list & home
    ├── profile/              # User profile
    ├── search/               # Search functionality
    ├── feed/                 # News feed
    └── notifications/        # Notification handling
```

### Tech Stack

| Lĩnh Vực            | Công Nghệ                               |
| ------------------- | --------------------------------------- |
| **UI**              | Jetpack Compose, Material Design 3      |
| **Architecture**    | MVVM + Clean Architecture, Multi-Module |
| **Async**           | Kotlin Coroutines, Flow, LiveData       |
| **Networking**      | Retrofit 2, OkHttp, Socket.IO           |
| **Database**        | Room Database, Firebase Firestore       |
| **Real-time**       | Socket.IO WebSocket                     |
| **Storage**         | SecureStorage (encrypted), DataStore    |
| **Authentication**  | JWT Token, Refresh Token rotation       |
| **DI**              | Hilt (Dagger 2 wrapper)                 |
| **Media**           | Firebase Storage, Glide/Coil            |
| **Push**            | Firebase Cloud Messaging (FCM)          |
| **Version Control** | Git, GitHub                             |

---

## 📱 Screenshots (Structure)

```
┌─────────────────┐
│   AUTH FLOW     │ → Login/Register/Forgot Password
├─────────────────┤
│   HOME SCREEN   │ → Chat List, Conversations
├─────────────────┤
│   CHAT DETAIL   │ → Messages, Typing indicator
├─────────────────┤
│   PROFILE       │ → User info, Edit Profile
├─────────────────┤
│   SEARCH        │ → Find Users
├─────────────────┤
│   FEED          │ → Social News Feed
└─────────────────┘
```

---

## 🚀 Hướng Dẫn Setup & Chạy

### Yêu Cầu Hệ Thống

- Android Studio Flamingo (2022.2.1) trở lên
- JDK 11+
- Gradle 8.0+
- Android SDK: Min 24 (Android 7.0), Target 34 (Android 14)

### Bước 1: Clone Repository

```bash
git clone https://github.com/CaoMinh2110/chatbox-app.git
cd chatbox-app
```

### Bước 2: Cấu Hình Build

Tạo file `local.properties` trong root directory:

```properties
sdk.dir=/path/to/android/sdk

# API Configuration
API_BASE_URL=https://your.endpoint

# Socket URL
SOCKET_URL=https://your.endpoint
```

### Bước 3: Sync Dependencies

```bash
./gradlew clean build
```

### Bước 4: Cấu Hình Firebase (Nếu Cần)

1. Vào [Firebase Console](https://console.firebase.google.com/)
2. Tạo dự án hoặc chọn dự án hiện có
3. Download `google-services.json`
4. Đặt vào `app/google-services.json`

### Bước 5: Chạy Ứng Dụng

```bash
# Chạy trên emulator
./gradlew installDebug

# Hoặc từ Android Studio
# Shift + F10 (Windows/Linux) hoặc Cmd + R (macOS)
```

---

## 🎯 Cách Sử Dụng Ứng Dụng

### 1. **Tạo Tài Khoản**

- Nhấn "Sign Up"
- Nhập email, password
- Xác nhận email
- Tạo profile (username, avatar)

### 2. **Đăng Nhập**

- Nhập email & mật khẩu
- Hoặc "Forgot Password" để reset

### 3. **Tìm & Kết Bạn**

- Tab "Search" → Tìm kiếm người dùng
- Nhấn "Add Friend" để kết bạn
- Chờ người kia chấp nhận

### 4. **Chat**

- Tab "Home" → Chọn conversation
- Gõ tin nhắn hoặc gửi ảnh
- Xem "đã xem", "đang gõ" indicator

### 5. **Feed & Bài Viết**

- Tab "Feed" → Xem feed bạn bè
- "New Post" → Tạo bài viết mới
- Like & comment bài viết

### 6. **Profile**

- Tab "Profile" → Xem hồ sơ
- "Edit" → Chỉnh sửa thông tin
- Upload ảnh đại diện

---

## 🛠️ Phát Triển & Mở Rộng

### Thêm Feature Mới

#### Option 1: Thêm vào Feature Hiện Tại

```kotlin
// Vào feature/chat/src/main/java/com/truevibeup/feature/chat/
// presentation/screen/ChatScreen.kt → Thêm UI
// presentation/viewmodel/ChatViewModel.kt → Logic
```

#### Option 2: Tạo Feature Module Mới

```bash
# Tạo cấu trúc mới
mkdir -p feature/comment/src/main/{java/com/truevibeup/feature/comment,res}
```

### Quy Trình Git

```bash
# Tạo branch
git checkout -b feature/your-feature

# Commit thường xuyên
git commit -m "feat: add new feature"
git commit -m "fix: resolve bug"

# Push
git push origin feature/your-feature

# Tạo Pull Request
```

### Code Style

```kotlin
// Sử dụng Kotlin idioms
val isValid = data.isNotEmpty() && user.isAuthenticated

// Naming conventions
val userViewModel: UserViewModel = ...
fun fetchUserProfile(): Flow<User> = ...
class ChatRepository : Repository { ... }

// Coroutines Best Practices
viewModelScope.launch {
    try {
        val result = repository.loadData()
        _uiState.emit(result)
    } catch (e: Exception) {
        _error.emit(e.message)
    }
}
```

---

## 📊 API Endpoints (Examples)

### Authentication

```
POST   /auth/register       - Đăng ký
POST   /auth/login          - Đăng nhập
POST   /auth/refresh-token  - Làm mới token
POST   /auth/logout         - Đăng xuất
```

### Users

```
GET    /users/{id}          - Lấy info người dùng
PUT    /users/{id}          - Cập nhật profile
GET    /users/search?q=...  - Tìm kiếm người dùng
```

### Chat & Messages

```
GET    /conversations       - Danh sách conversation
GET    /conversations/{id}/messages  - Lấy messages
POST   /messages            - Gửi tin nhắn
PUT    /messages/{id}       - Chỉnh sửa tin nhắn
```

### Socket.IO Events

```javascript
// Client → Server
emit('chat:send', { conversationId, content })
emit('chat:typing', { conversationId, isTyping })

// Server → Client
on('chat:message', (message) => {...})
on('chat:typing', (data) => {...})
on('notification:new', () => {...})
```

---

## 🐛 Troubleshooting

### ❌ Lỗi: "API_BASE_URL not configured"

**Giải pháp**:

1. Kiểm tra `local.properties` có `API_BASE_URL`
2. Rebuild project: `./gradlew clean build`

### ❌ Lỗi: "Socket connection refused"

**Giải pháp**:

1. Kiểm tra server có chạy
2. Kiểm tra firewall/network rules
3. Certificate pinning có kích hoạt?

### ❌ Lỗi: "JWT Token expired"

**Giải pháp**:

1. Tự động refresh token
2. Hoặc yêu cầu user đăng nhập lại
3. Kiểm tra `TokenAuthenticator` trong `ApiClient.kt`

### ❌ Lỗi: "Compose rendering error"

**Giải pháp**:

1. Rebuild: `./gradlew clean build`
2. Invalidate caches: Android Studio > File > Invalidate Caches
3. Update Compose version

---

## 📚 Tài Liệu Tham Khảo

- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- [Multi-Module Architecture](https://developer.android.com/topic/modularization)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Socket.IO Android Client](https://github.com/socketio/socket.io-client-java)
- [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

---

## 👥 Đóng Góp

Chúng tôi chào đón các đóng góp! Vui lòng:

1. Fork repository
2. Tạo branch feature: `git checkout -b feature/AmazingFeature`
3. Commit changes: `git commit -m 'Add AmazingFeature'`
4. Push to branch: `git push origin feature/AmazingFeature`
5. Open Pull Request

---

## 📄 License

Project này được phát triển cho mục đích giáo dục & portfolio.

---

## 👤 Tác Giả

**Khuất Cao Minh**

- GitHub: [@CaoMinh2110](https://github.com/CaoMinh2110)
- Email: khuatcaominh.2110@gmail.com
- Vị trí: Thanh Xuân, Hà Nội

---

## 🙏 Cảm Ơn

- [Google Developers](https://developer.android.com)
- [Square (Retrofit, OkHttp)](https://square.github.io)
- [Socket.IO](https://socket.io)
- [Firebase](https://firebase.google.com)
- [Jetpack Architecture](https://developer.android.com/jetpack)

---

**Cập nhật lần cuối**: 29/04/2026  
**Phiên bản**: 1.0.0  
**Status**: ✅ Production Ready
