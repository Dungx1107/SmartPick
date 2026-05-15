# SmartPick - Ứng dụng Chia sẻ & Gợi ý Sản phẩm Thông minh

SmartPick là một nền tảng mạng xã hội thu nhỏ dành cho cộng đồng yêu thích mua sắm và chia sẻ trải nghiệm sản phẩm. Ứng dụng tích hợp công nghệ AI để kiểm duyệt nội dung và hỗ trợ người dùng tìm kiếm sản phẩm phù hợp.

## 🚀 Tính năng chính

- **Bảng tin (Feed):** Theo dõi các bài viết chia sẻ về sản phẩm từ cộng đồng. Hỗ trợ hiển thị hình ảnh và video chất lượng cao.
- **Tạo bài viết & Bán hàng:** Cho phép người dùng đăng tải cảm nhận kèm hình ảnh/video. Hỗ trợ gắn thông tin sản phẩm trực tiếp vào bài viết.
- **Hệ thống Tương tác:** Bình luận (hỗ trợ reply tầng), Thích bình luận, và Thông báo thời gian thực.
- **AI Curator (Chatbot):** Tích hợp Gemini AI để tư vấn sản phẩm và giải đáp thắc mắc của người dùng.
- **Kiểm duyệt Thông minh:** Tự động kiểm duyệt văn bản (Gemini) và hình ảnh (Sightengine) để đảm bảo môi trường cộng đồng sạch.
- **Quản lý Cá nhân:** Hồ sơ người dùng, bộ sưu tập bài viết đã lưu, và quản lý sản phẩm cá nhân.

## 🛠 Công nghệ sử dụng

- **Ngôn ngữ:** Kotlin
- **UI Framework:** Jetpack Compose (Modern Android UI)
- **Backend as a Service:** Supabase (Auth, Database, Storage, Realtime)
- **Dependency Injection:** Hilt
- **Asynchronous:** Coroutines & Flow
- **Image/Video Loading:** Coil
- **Media Player:** ExoPlayer (Media3)
- **AI Integration:** Google Gemini API & Sightengine API

## 🏗 Kiến trúc dự án

Dự án tuân thủ mô hình **Clean Architecture** kết hợp với **MVVM (Model-View-ViewModel)**:

- **UI Layer:** Jetpack Compose Screens & ViewModels.
- **Domain/Data Layer:** Repositories xử lý logic nghiệp vụ và giao tiếp với Supabase.
- **Core Layer:** Chứa các module dùng chung như Network, Models, Utils.

## 📁 Cấu trúc thư mục

```text
com.example.smartpick
├── core            # Chứa các thành phần cốt lõi (Network, Model, Utils)
├── features        # Chia theo tính năng (Auth, Feed, Post, Profile, ...)
│   ├── auth        # Đăng nhập, đăng ký, Google Sign-In
│   ├── feed        # Hiển thị bảng tin
│   ├── chatbot     # AI Assistant
│   ├── comment     # Hệ thống bình luận
│   └── ...
├── navigation      # Quản lý điều hướng (Routes, NavHost)
└── MainActivity.kt # Điểm khởi đầu của ứng dụng
```

## ⚙️ Cấu hình & Chạy Project

1. **Yêu cầu:** Android Studio Koala trở lên, JDK 17.
2. **Cấu hình API Key:** Tạo file `local.properties` tại thư mục gốc và thêm các khóa sau:
   ```properties
   SUPABASE_URL=your_supabase_url
   SUPABASE_KEY=your_supabase_anon_key
   GEMINI_KEY=your_gemini_api_key
   SIGHTENGINE_USER=your_sightengine_user
   SIGHTENGINE_SECRET=your_sightengine_secret
   ```
3. **Build & Run:** Chọn thiết bị ảo hoặc thật và nhấn Run.

## 📈 Hướng phát triển tương lai

- Tích hợp thanh toán trực tuyến.
- Hệ thống gợi ý sản phẩm dựa trên hành vi người dùng (Machine Learning).
- Mở rộng tính năng Livestream bán hàng.
- Hỗ trợ đa ngôn ngữ.

---
© 2024 SmartPick Team. Developed for Mobile Development Course.