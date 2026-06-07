# SmartPick - Mạng xã hội Chia sẻ & Mua sắm Sản phẩm Thông minh

SmartPick là một ứng dụng Android hiện đại, kết hợp hài hòa giữa trải nghiệm mạng xã hội chia sẻ và hệ thống thương mại điện tử. Ứng dụng tận dụng sức mạnh của Trí tuệ nhân tạo (AI) để kiến tạo một môi trường mua sắm an toàn, tin cậy và hỗ trợ người dùng tối đa trong việc lựa chọn sản phẩm.

## 🚀 Tính năng nổi bật

- **Cộng đồng Chia sẻ (Social Feed):** Đăng tải và khám phá các bài viết review sản phẩm với hình ảnh/video chất lượng cao. Tương tác mượt mà qua hệ thống Like, Comment đa tầng.
- **Thương mại điện tử tích hợp:** Gắn thẻ sản phẩm vào bài viết, quản lý giỏ hàng linh hoạt và quy trình thanh toán (Checkout) tối ưu cho cả mua lẻ và mua nhiều món.
- **Kiểm duyệt AI tự động:** 
    - **Văn bản:** Sử dụng Google Gemini AI để nhận diện ngôn ngữ độc hại, chửi thề hoặc nội dung không phù hợp.
    - **Hình ảnh:** Tích hợp Sightengine API để tự động chặn các hình ảnh nhạy cảm, bạo lực trước khi đăng tải.
- **Trợ lý AI Curator:** Chatbot tư vấn mua sắm thông minh dựa trên mô hình Gemini 1.5 Flash, giúp giải đáp thắc mắc và gợi ý phong cách.
- **Hệ thống Review tin cậy:** Cơ chế đánh giá sản phẩm chỉ dành cho người đã mua hàng, đảm bảo tính khách quan và trung thực.
- **Thông báo thời gian thực:** Cập nhật tức thì các tương tác (Like, Comment) và trạng thái đơn hàng thông qua Supabase Realtime.

## 🛠 Công nghệ cốt lõi

- **Android Stack:** Kotlin 2.0 (K2 Compiler), Jetpack Compose, Coroutines, Flow.
- **Kiến trúc:** Clean Architecture & MVVM, Dagger Hilt (Dependency Injection).
- **Backend (Supabase):** PostgreSQL, GoTrue (Auth), Storage, Realtime SDK.
- **AI Engines:** Google Gemini 1.5 Flash, Sightengine Moderation API.
- **Media:** Media3 ExoPlayer (Video streaming), Coil (Image loading & caching).
- **Network:** Ktor Client (cho Supabase) và OkHttp 4 (cho các API bên thứ 3).

## 🏗 Kiến trúc dự án

Dự án tuân thủ nghiêm ngặt mô hình **Clean Architecture**:
- **Presentation Layer:** Jetpack Compose UI và ViewModels quản lý trạng thái.
- **Domain Layer:** Logic nghiệp vụ và các Use Cases.
- **Data Layer:** Repositories điều phối dữ liệu từ local (DataStore) và remote (Supabase).

## 📁 Cấu trúc thư mục

```text
com.example.smartpick
├── core            # Network modules, DI, Common Components, Base Models
├── features        # Các module tính năng (Auth, Feed, Home, Cart, ...)
├── navigation      # Định tuyến và Type-safe Routes
└── MainActivity.kt # Entry point duy nhất
```

## ⚙️ Cài đặt & Chạy dự án

1. **Yêu cầu:** Android Studio Ladybug (2024.2.1) trở lên, JDK 17.
2. **Cấu hình API Keys:** Thêm các thông tin sau vào file `local.properties`:
   ```properties
   SUPABASE_URL=your_supabase_url
   SUPABASE_KEY=your_supabase_anon_key
   GEMINI_KEY=your_gemini_api_key
   SIGHTENGINE_USER=your_sightengine_user_id
   SIGHTENGINE_SECRET=your_sightengine_api_secret
   ```
3. **Build:** Đồng bộ Gradle và chạy ứng dụng trên Emulator hoặc thiết bị thật (API 24+).

---
© 2024 SmartPick. Phát triển bởi đội ngũ kỹ sư Android tâm huyết.
