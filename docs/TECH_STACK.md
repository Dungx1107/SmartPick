# Phân tích Công nghệ & Thư viện (Tech Stack)

Dự án SmartPick được xây dựng trên nền tảng công nghệ Android hiện đại nhất (năm 2024), tập trung
vào hiệu suất, khả năng mở rộng và trải nghiệm người dùng mượt mà.

## 1. Ngôn ngữ & Nền tảng

- **Kotlin (1.9.x+):** Ngôn ngữ lập trình chính, sử dụng các tính năng cao cấp như Coroutines, Flow,
  và Serialization.
- **Android SDK:** Target API 36 (Android 15), Min API 24 (Android 7.0).
- **Gradle Kotlin DSL:** Quản lý build script bằng Kotlin giúp đồng bộ và dễ quản lý dependency.

## 2. UI Framework (Jetpack Compose)

- **Jetpack Compose:** Thay thế hoàn toàn XML để xây dựng UI dạng khai báo (Declarative UI).
- **Material Design 3:** Sử dụng bộ linh kiện UI mới nhất từ Google, hỗ trợ Dynamic Color và giao
  diện hiện đại.
- **Compose Navigation:** Quản lý luồng di chuyển giữa các màn hình trong Single Activity
  Architecture.

## 3. Backend & Data (Supabase)

SmartPick sử dụng **Supabase** như một giải pháp thay thế mã nguồn mở cho Firebase:

- **Postgrest-kt:** Thao tác với PostgreSQL database qua Kotlin DSL.
- **GoTrue-kt:** Quản lý định danh, đăng nhập Email/Password và Social Login (Google).
- **Storage-kt:** Lưu trữ file media.
- **Realtime-kt:** Đồng bộ dữ liệu tức thì (Thông báo, Like, Comment).

## 4. Quản lý Trạng thái & Logic

- **ViewModel (Architecture Components):** Lưu trữ và quản lý dữ liệu liên quan đến UI, sống sót qua
  các thay đổi cấu hình.
- **Coroutines & Flow:** Xử lý bất đồng bộ. Luồng dữ liệu từ DB được đẩy lên UI dưới dạng
  `StateFlow` hoặc `SharedFlow`.
- **Dagger Hilt:** Framework Dependency Injection (DI) chính thức được Google khuyến khích, giúp
  giảm boilerplate code và dễ dàng testing.

## 5. Xử lý Media & Mạng

- **Ktor Client:** Engine mạng mà Supabase SDK sử dụng, nhẹ và hiệu quả.
- **OkHttp 4:** Sử dụng cho các custom API service (như ModerationService).
- **Coil (Compose):** Thư viện load ảnh tối ưu cho Compose, hỗ trợ caching và video frames.
- **Media3 ExoPlayer:** Engine phát video mạnh mẽ từ Google, dùng để phát video trong bài đăng.

## 6. Trí tuệ nhân tạo (AI)

- **Google Gemini API (Gemini 1.5 Flash):** Sử dụng làm trợ lý ảo tư vấn sản phẩm và kiểm duyệt văn
  bản tự động.
- **Sightengine API:** Service chuyên dụng để nhận diện nội dung hình ảnh (nhạy cảm, bạo lực, vũ
  khí).

## 7. Các thư viện bổ trợ khác

- **Kotlinx Serialization:** Chuyển đổi dữ liệu JSON sang Object Kotlin cực nhanh và an toàn kiểu dữ
  liệu.
- **Kotlinx Datetime:** Xử lý thời gian đồng bộ với múi giờ của Supabase.
- **Android Credentials:** Hỗ trợ Google Sign-In theo cơ chế mới bảo mật hơn.

---
Việc lựa chọn Tech Stack này giúp SmartPick đạt được sự cân bằng giữa tốc độ phát triển (
productivity) và chất lượng sản phẩm cuối cùng.