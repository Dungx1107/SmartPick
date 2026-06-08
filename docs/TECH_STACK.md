# Phân tích Công nghệ & Thư viện (Tech Stack)

Dự án SmartPick được xây dựng trên nền tảng công nghệ Android hiện đại nhất (Modern Android Development - MAD), tập trung vào hiệu suất, khả năng mở rộng và trải nghiệm người dùng mượt mà.

## 1. Ngôn ngữ & Nền tảng

- **Kotlin 2.0:** Sử dụng K2 Compiler mang lại tốc độ biên dịch vượt trội và khả năng kiểm soát mã nguồn chặt chẽ.
- **Android SDK:** Hỗ trợ tối đa Android 16 (API 36), mức tối thiểu API 24 (Android 7.0).
- **Gradle Kotlin DSL:** Quản lý cấu hình dự án bằng ngôn ngữ Kotlin, hỗ trợ Version Catalogs (libs.versions.toml) để quản lý phiên bản thư viện tập trung.

## 2. Giao diện người dùng (UI Framework)

- **Jetpack Compose (BOM 2024.09.00):** Framework xây dựng UI khai báo hoàn toàn. Sử dụng plugin `kotlin-compose` mới nhất của Kotlin 2.0.
- **Material Design 3:** Áp dụng các tiêu chuẩn thiết kế mới nhất của Google, bao gồm hỗ trợ hệ màu dynamic và Material Icons Extended.
- **Compose Navigation:** Quản lý luồng di chuyển Type-safe, hỗ trợ truyền tham số phức tạp giữa các màn hình.

## 3. Backend-as-a-Service (Supabase)

SmartPick sử dụng hệ sinh thái **Supabase (BOM 2.1.3)** thay thế Firebase:
- **Postgrest-kt:** Truy vấn dữ liệu mạnh mẽ qua Kotlin DSL.
- **GoTrue-kt (Auth):** Quản lý xác thực an toàn, tích hợp Google Sign-In qua Credential Manager API (`androidx.credentials`).
- **Storage-kt:** Quản lý lưu trữ tệp tin media (ảnh/video) với CDN.
- **Realtime-kt:** Đồng bộ hóa dữ liệu tức thì cho Chat, Like và Thông báo.
- **Functions-kt:** Gọi các Edge Functions của Supabase.

## 4. Kiến trúc & Quản lý Phụ thuộc

- **Clean Architecture:** Phân tách rõ ràng giữa các lớp Presentation, Domain và Data.
- **MVVM (Model-View-ViewModel):** Đảm bảo logic giao diện và dữ liệu tách biệt.
- **Dagger Hilt (2.51.1):** Framework Dependency Injection chính thức, tích hợp sâu với `hilt-navigation-compose`.
- **KSP (Kotlin Symbol Processing):** Thay thế KAPT để tăng tốc độ xử lý annotation cho Hilt.

## 5. Xử lý Media & Mạng

- **Ktor Client (2.3.8):** Engine xử lý mạng chính của Supabase SDK (OkHttp engine).
- **OkHttp 4.12.0:** Sử dụng trực tiếp cho các dịch vụ API bên thứ ba (Sightengine, Gemini REST).
- **Coil 2.6.0:** Thư viện tải ảnh hiệu năng cao cho Compose, tích hợp `coil-video` để giải mã khung hình video.
- **Media3 ExoPlayer 1.3.1:** Nền tảng phát video chuyên nghiệp (ExoPlayer, UI, Common).

## 6. Trí tuệ nhân tạo (AI)

- **Google Gemini (generativeai 0.7.0):** Sử dụng model Gemini 1.5 Flash cho Trợ lý ảo AI Curator và kiểm duyệt văn bản.
- **Sightengine API:** Kiểm duyệt hình ảnh, phát hiện nội dung nhạy cảm và vi phạm quy chuẩn cộng đồng.

## 7. Các thư viện bổ trợ khác

- **Kotlinx Serialization (1.6.2):** Chuyển đổi JSON sang Object nhanh chóng.
- **Kotlinx Datetime (0.6.0):** Xử lý thời gian chuẩn xác từ Database.
- **DataStore Preferences (1.1.1):** Lưu trữ cấu hình người dùng thay thế SharedPreferences.
- **Firebase Messaging (BOM 32.8.0):** Hỗ trợ thông báo đẩy (FCM).

---
Sự kết hợp này giúp SmartPick trở thành một ứng dụng mạnh mẽ, bảo mật và sẵn sàng cho các nhu cầu mở rộng trong tương lai.
