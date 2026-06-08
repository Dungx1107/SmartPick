# Chương 2: Công nghệ sử dụng

Dựa trên phân tích thực tế từ `build.gradle.kts` và cấu trúc mã nguồn của dự án SmartPick, các công nghệ sau đã được áp dụng:

## 2.1. Kiến trúc tổng thể
- **Kiến trúc ứng dụng:** Mobile Client-Server.
- **Mô hình phát triển:** Native Android Development.
- **Pattern:** MVVM (Model-View-ViewModel) kết hợp với các nguyên lý của Clean Architecture (Data, Domain, UI layers).
- **Cơ chế giao tiếp:** REST API (thông qua Ktor/OkHttp) và Realtime Data (thông qua Supabase Realtime).

## 2.2. Frontend (Mobile App)
- **Ngôn ngữ:** Kotlin 2.0 (JVM 17).
- **UI Framework:** Jetpack Compose (Declarative UI) với Material Design 3.
- **Navigation:** Jetpack Navigation Compose (Type-safe routes).
- **Dependency Injection:** Hilt (Dagger) để quản lý vòng đời của các đối tượng.
- **Reactive Programming:** Kotlin Coroutines & Flow (StateFlow, SharedFlow).
- **Image/Video Loading:** Coil (hỗ trợ cả Video Frame decoding).
- **Media Player:** Media3 ExoPlayer để phát video review.
- **Local Storage:** Jetpack DataStore (Preferences) để lưu trạng thái người dùng và session.

## 2.3. Backend & Database (BaaS)
- **Nền tảng chính:** **Supabase** (Backend-as-a-Service).
- **Database:** PostgreSQL (với Postgrest API tự động).
- **Authentication:** Supabase Auth (hỗ trợ Email/Password và tích hợp Google ID Service).
- **Storage:** Supabase Storage để lưu trữ hình ảnh và video bài đăng.
- **Realtime:** Supabase Realtime (WebSockets) để cập nhật thông báo và tương tác tức thì.

## 2.4. Trí tuệ nhân tạo (External Services)
- **Generative AI:** Google Gemini AI SDK (`generativeai:0.7.0`).
    - *Vai trò:* Chatbot tư vấn sản phẩm và kiểm duyệt văn bản.
- **Content Moderation:** Sightengine API.
    - *Vai trò:* Nhận diện nội dung hình ảnh/video nhạy cảm, vi phạm chính sách.

## 2.5. Cloud Services & DevOps
- **Push Notification:** Firebase Cloud Messaging (FCM).
- **Google Services:** Google Play Services Auth (cho Google Sign-In).

## 2.6. Testing & Quality Assurance
- **Unit Testing:** JUnit 4, MockK.
- **Flow Testing:** Turbine.
- **Dependency Injection Testing:** Hilt Testing.
- **Mocking Web Server:** OkHttp MockWebServer (cho network testing).

## 2.7. Bảng tổng hợp các thư viện quan trọng

| Thư viện | Vai trò | Lý do lựa chọn |
| :--- | :--- | :--- |
| `io.github.jan-tennert.supabase` | Core SDK | Đồng bộ hóa dữ liệu và xác thực nhanh chóng. |
| `io.ktor:ktor-client` | Network Engine | Nhẹ, hiệu năng cao và tương thích tốt với Kotlin Multiplatform. |
| `androidx.compose.material3` | Giao diện | Cung cấp các thành phần UI hiện đại, chuẩn Google. |
| `com.google.ai.client.generativeai` | AI Integration | Tận dụng sức mạnh của mô hình Gemini 1.5 Flash (nhanh và rẻ). |
| `org.jetbrains.kotlinx:kotlinx-serialization` | JSON Parsing | Type-safe, tích hợp sâu với ngôn ngữ Kotlin. |

## 2.8. Kết luận chương
Hệ thống tận dụng các công nghệ tiên tiến nhất trong hệ sinh thái Android hiện nay. Việc sử dụng Supabase giúp giảm tải việc xây dựng Server truyền thống, trong khi Jetpack Compose và Hilt đảm bảo mã nguồn dễ bảo trì và mở rộng trong tương lai.
