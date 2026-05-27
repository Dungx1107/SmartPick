# BÁO CÁO XÂY DỰNG ỨNG DỤNG SMARTPICK - PHẦN 1: GIỚI THIỆU VÀ CÔNG NGHỆ

## CHƯƠNG 1: ĐẶT VẤN ĐỀ

### 1.1. Lý do chọn đề tài
Trong bối cảnh thương mại điện tử bùng nổ, người dùng không chỉ cần một nơi để mua sắm mà còn cần một cộng đồng tin cậy để tham khảo ý kiến. **SmartPick** ra đời nhằm giải quyết bài toán kết nối giữa nội dung số (Review/Feed) và giao dịch mua sắm, giúp người dùng đưa ra quyết định mua hàng thông minh hơn.

### 1.2. Mục tiêu đề tài
- Phát triển ứng dụng Android sử dụng công nghệ mới nhất (Jetpack Compose, Kotlin Flow).
- Xây dựng hệ thống quản lý giỏ hàng, đơn hàng và bài viết yêu thích tích hợp.
- Tối ưu hóa trải nghiệm người dùng thông qua giao diện hiện đại và AI hỗ trợ.

### 1.3. Phạm vi nghiên cứu
- Thiết kế UI/UX theo chuẩn Material Design 3.
- Xử lý dữ liệu thời gian thực với Supabase.
- Quản lý trạng thái phức tạp với MVVM và Clean Architecture.

## CHƯƠNG 2: CÔNG NGHỆ VÀ THƯ VIỆN SỬ DỤNG

### 2.1. Nền tảng phát triển
- **Ngôn ngữ:** Kotlin (phiên bản 2.0.0 với K2 Compiler).
- **UI Framework:** Jetpack Compose (BOM 2024.09.00).
- **Kiến trúc:** MVVM kết hợp Clean Architecture.

### 2.2. Thư viện quan trọng (Dependencies)
Dựa trên `build.gradle.kts`, ứng dụng sử dụng các thư viện chiến lược:
- **Supabase (Auth, Postgrest, Storage, Realtime):** Giải pháp Backend-as-a-Service thay thế cho Firebase, giúp quản lý dữ liệu PostgreSQL và xác thực người dùng.
- **Dagger Hilt:** Quản lý Dependency Injection giúp mã nguồn dễ mở rộng và kiểm thử.
- **Coil & Media3 ExoPlayer:** Xử lý hiển thị hình ảnh và phát video mượt mà trong Feed bài viết.
- **Navigation Compose:** Quản lý luồng chuyển màn hình Type-safe.

### 2.3. Trí tuệ nhân tạo (AI)
- **Google Gemini 1.5 Flash:** Hỗ trợ tư vấn mua sắm và kiểm duyệt nội dung văn bản.
- **Sightengine:** Tự động nhận diện và chặn các hình ảnh không phù hợp.
