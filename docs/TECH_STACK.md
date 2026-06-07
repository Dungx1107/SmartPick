# Phân tích Công nghệ & Thư viện (Tech Stack)

Dự án SmartPick được xây dựng trên nền tảng công nghệ Android hiện đại nhất (Modern Android Development - MAD), tập trung vào hiệu suất, khả năng mở rộng và trải nghiệm người dùng mượt mà.

## 1. Ngôn ngữ & Nền tảng

- **Kotlin 2.0:** Sử dụng K2 Compiler mang lại tốc độ biên dịch vượt trội và khả năng kiểm soát mã nguồn chặt chẽ.
- **Android SDK:** Hỗ trợ tối đa Android 15 (API 36), mức tối thiểu API 24 (Android 7.0).
- **Gradle Kotlin DSL:** Quản lý cấu hình dự án bằng ngôn ngữ Kotlin, hỗ trợ Version Catalogs (libs.versions.toml) để quản lý phiên bản thư viện tập trung.

## 2. Giao diện người dùng (UI Framework)

- **Jetpack Compose (BOM 2024.09.00+):** Framework xây dựng UI khai báo hoàn toàn, giúp giảm thiểu mã nguồn và tăng tính linh hoạt.
- **Material Design 3:** Áp dụng các tiêu chuẩn thiết kế mới nhất của Google, hỗ trợ các thành phần UI hiện đại và hệ màu sắc tùy chỉnh.
- **Compose Navigation:** Quản lý luồng di chuyển Type-safe, hỗ trợ truyền tham số phức tạp giữa các màn hình.

## 3. Backend-as-a-Service (Supabase)

SmartPick sử dụng hệ sinh thái **Supabase** để thay thế Firebase, mang lại sức mạnh của PostgreSQL:
- **Postgrest-kt:** Truy vấn dữ liệu mạnh mẽ qua Kotlin DSL.
- **GoTrue-kt (Auth):** Quản lý xác thực an toàn, tích hợp mượt mà với Google Sign-In qua Credential Manager API.
- **Storage-kt:** Quản lý lưu trữ tệp tin media (ảnh/video) với CDN tích hợp.
- **Realtime-kt:** Đồng bộ hóa dữ liệu tức thì cho các tính năng Chat, Like và Thông báo.

## 4. Kiến trúc & Quản lý Phụ thuộc

- **Clean Architecture:** Phân tách rõ ràng giữa các lớp Presentation, Domain và Data.
- **MVVM (Model-View-ViewModel):** Đảm bảo logic giao diện và dữ liệu được tách biệt, hỗ trợ quản lý trạng thái bền vững.
- **Dagger Hilt:** Framework Dependency Injection chính thức, giúp quản lý vòng đời của các thành phần hệ thống một cách tự động.

## 5. Xử lý Media & Mạng

- **Ktor Client:** Engine xử lý mạng chính của Supabase SDK, tối ưu cho Kotlin Coroutines.
- **OkHttp 4:** Sử dụng cho các dịch vụ API bên thứ ba (Sightengine, Gemini REST).
- **Coil (Compose):** Thư viện tải ảnh hiệu năng cao, hỗ trợ caching thông minh và giải mã khung hình video (Video Frame Decoder).
- **Media3 ExoPlayer:** Nền tảng phát video chuyên nghiệp, hỗ trợ streaming mượt mà trong bảng tin.

## 6. Trí tuệ nhân tạo (AI)

- **Google Gemini 1.5 Flash:** Sử dụng thông qua SDK `generativeai` cho Trợ lý ảo AI Curator và kiểm duyệt văn bản tự động.
- **Sightengine API:** Giải pháp hàng đầu về kiểm duyệt hình ảnh, giúp phát hiện nội dung nhạy cảm và vi phạm quy chuẩn cộng đồng.

## 7. Các thư viện bổ trợ khác

- **Kotlinx Serialization:** Chuyển đổi JSON sang Object nhanh chóng và an toàn.
- **Kotlinx Datetime:** Xử lý thời gian chuẩn xác theo ISO 8601, hỗ trợ đa múi giờ.
- **DataStore Preferences:** Lưu trữ các cấu hình người dùng nhẹ nhàng, thay thế hoàn toàn SharedPreferences cũ.
- **Firebase Messaging (FCM):** Hỗ trợ gửi thông báo đẩy (Push Notifications) đến thiết bị.

---
Sự kết hợp này giúp SmartPick trở thành một ứng dụng mạnh mẽ, bảo mật và sẵn sàng cho các nhu cầu mở rộng trong tương lai.
