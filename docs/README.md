# SmartPick - Mạng xã hội Chia sẻ & Mua sắm Sản phẩm Thông minh

SmartPick là một nền tảng Android hiện đại kết hợp giữa mạng xã hội chia sẻ trải nghiệm sản phẩm và hệ thống thương mại điện tử cơ bản. Ứng dụng tích hợp trí tuệ nhân tạo (AI) để mang lại môi trường an toàn và hỗ trợ người dùng tìm kiếm sản phẩm tối ưu.

## 🚀 Tính năng nổi bật

- **Cộng đồng Chia sẻ (Social Feed):** Đăng tải bài viết kèm hình ảnh/video chất lượng cao. Tương tác qua bình luận đa tầng và hệ thống "thả tim".
- **Thương mại điện tử (E-commerce):** Gắn thẻ sản phẩm vào bài viết, quản lý giỏ hàng, thanh toán và theo dõi lịch sử đơn hàng.
- **Kiểm duyệt AI tự động:** 
    - **Văn bản:** Sử dụng Gemini AI để phát hiện ngôn ngữ độc hại, teencode.
    - **Hình ảnh:** Sử dụng Sightengine để chặn ảnh nhạy cảm, bạo lực.
- **Trợ lý AI (AI Curator):** Chatbot tư vấn mua sắm thông minh dựa trên dữ liệu sản phẩm thực tế trong hệ thống.
- **Hệ thống Đánh giá (Review Hub):** Cơ chế đánh giá tin cậy, chỉ cho phép người đã mua sản phẩm để lại nhận xét và điểm số.
- **Thông báo Realtime:** Cập nhật tức thì các tương tác và trạng thái đơn hàng qua Supabase Realtime.

## 🛠 Công nghệ cốt lõi

- **Android Stack:** Kotlin 2.0, Jetpack Compose, Hilt, Coroutines, Flow.
- **Backend (Supabase):** PostgreSQL, GoTrue (Auth), Storage, Realtime SDK.
- **AI Engines:** Google Gemini 1.5 Flash, Sightengine API.
- **Media:** Media3 ExoPlayer (Video), Coil (Image loading & Caching).
- **Navigation:** Type-safe Compose Navigation.

## 🏗 Kiến trúc dự án

Dự án áp dụng **Clean Architecture** kết hợp mô hình **MVVM**:
- **UI:** Khai báo bằng Jetpack Compose, quản lý bởi ViewModel.
- **Data:** Phân tách rõ ràng giữa DTO (Data Transfer Object) và Domain Model qua các Mappers.
- **Core:** Chứa các logic dùng chung như Moderation Service, Network Module.

## 📁 Cấu trúc thư mục chính

```text
com.example.smartpick
├── core            # Cấu hình mạng, DI, Models chung, Utils
├── features        # Module hóa theo tính năng
│   ├── auth        # Xác thực người dùng & Google Sign-In
│   ├── feed        # Bảng tin cộng đồng
│   ├── home        # Trang chủ & Quản lý sản phẩm/giỏ hàng
│   ├── review      # Trung tâm đánh giá sản phẩm
│   ├── chatbot     # Trợ lý ảo AI
│   └── ...
├── navigation      # Định tuyến và sơ đồ chuyển màn hình
└── MainActivity.kt # Entry point duy nhất
```

## ⚙️ Cài đặt & Phát triển

1. **Yêu cầu:** Android Studio Ladybug trở lên, JDK 17.
2. **Cấu hình môi trường:** Tạo file `local.properties` tại gốc dự án:
   ```properties
   SUPABASE_URL=...
   SUPABASE_KEY=...
   GEMINI_KEY=...
   SIGHTENGINE_USER=...
   SIGHTENGINE_SECRET=...
   ```
3. **Build:** Chạy lệnh `./gradlew assembleDebug` hoặc nhấn nút Run trên Android Studio.

---
© 2024 SmartPick Team. Dự án được phát triển cho học phần Phát triển ứng dụng di động.
