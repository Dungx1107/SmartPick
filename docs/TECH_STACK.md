# Phân tích Công nghệ & Thư viện (Tech Stack)

Dự án SmartPick được xây dựng trên nền tảng công nghệ Android hiện đại nhất, tập trung vào hiệu suất, khả năng mở rộng và trải nghiệm người dùng mượt mà.

## 1. Ngôn ngữ & Nền tảng

- **Kotlin 2.0:** Ngôn ngữ lập trình chính, sử dụng K2 Compiler cho tốc độ build nhanh hơn và các tính năng ngôn ngữ mới nhất.
- **Android SDK:** Compile SDK 36 (Android 15), Min SDK 24.
- **Gradle Kotlin DSL:** Quản lý build script bằng Kotlin giúp đồng bộ và kiểm tra lỗi ngay khi viết script.

## 2. UI Framework (Jetpack Compose)

- **Jetpack Compose 1.7+:** Framework xây dựng giao diện khai báo hiện đại.
- **Material Design 3:** Sử dụng bộ linh kiện UI mới nhất, hỗ trợ Dynamic Color.
- **Compose Navigation:** Quản lý luồng di chuyển Type-safe giữa các màn hình.

## 3. Backend & Data (Supabase)

SmartPick sử dụng **Supabase** (Open-source Firebase Alternative):

- **Postgrest-kt:** Thao tác với PostgreSQL database qua Kotlin DSL an toàn.
- **GoTrue-kt:** Quản lý định danh (Email/Password, Google Sign-In).
- **Storage-kt:** Lưu trữ file media (ảnh/video).
- **Realtime-kt:** Đồng bộ dữ liệu tức thì (Thông báo, Giỏ hàng, Tương tác).

## 4. Kiến trúc & Dependency Injection

- **Clean Architecture:** Tách biệt rõ ràng Presentation, Domain và Data.
- **MVVM:** Model-View-ViewModel quản lý trạng thái UI bền vững.
- **Dagger Hilt:** Framework Dependency Injection chính thức từ Google, giúp quản lý vòng đời dependencies dễ dàng.

## 5. Xử lý Media & Mạng

- **Ktor Client:** Engine mạng hiệu năng cao được sử dụng bởi Supabase SDK.
- **OkHttp 4:** Sử dụng cho các custom API service (ModerationService).
- **Coil (Compose):** Load ảnh tối ưu, hỗ trợ caching và giải mã video frame.
- **Media3 ExoPlayer:** Phát video mượt mà trong Feed.

## 6. Trí tuệ nhân tạo (AI)

- **Google Gemini 1.5 Flash:** Sử dụng làm trợ lý ảo tư vấn sản phẩm và kiểm duyệt văn bản tự động (toxic detection, teencode analysis).
- **Sightengine API:** Dịch vụ nhận diện hình ảnh chuyên sâu để chặn nội dung nhạy cảm, bạo lực.

## 7. Các thư viện bổ trợ

- **Kotlinx Serialization:** Serializer chính thức của Kotlin, nhanh và an toàn kiểu.
- **Kotlinx Datetime:** Xử lý thời gian chuẩn xác theo ISO 8601.
- **DataStore:** Lưu trữ các cấu hình nhỏ (Settings, User Preferences) thay thế SharedPreferences.

---
Sự kết hợp này giúp SmartPick trở thành một ứng dụng mạnh mẽ, bảo mật và dễ dàng bảo trì.
