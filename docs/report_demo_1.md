# BÁO CÁO XÂY DỰNG ỨNG DỤNG SMARTPICK - PHẦN 1: GIỚI THIỆU VÀ CÔNG NGHỆ

## CHƯƠNG 1: ĐẶT VẤN ĐỀ

### 1.1. Lý do chọn đề tài
Trong kỷ nguyên số, việc mua sắm trực tuyến không chỉ đơn thuần là giao dịch hàng hóa mà còn là sự kết nối cộng đồng. Người tiêu dùng ngày càng thông thái hơn, họ tìm kiếm những đánh giá thực tế và khách quan trước khi quyết định mua hàng. **SmartPick** ra đời như một giải pháp tiên phong, kết hợp giữa sức mạnh của mạng xã hội chia sẻ trải nghiệm và hệ thống thương mại điện tử tích hợp AI, nhằm tạo ra một hệ sinh thái mua sắm an toàn, minh bạch và thông minh.

### 1.2. Mục tiêu đề tài
- **Phát triển ứng dụng Android hiện đại:** Sử dụng 100% Jetpack Compose và các thư viện trong bộ giải pháp Modern Android Development (MAD).
- **Tích hợp Trí tuệ nhân tạo (AI):** Ứng dụng Google Gemini AI và Sightengine để tự động hóa quy trình kiểm duyệt nội dung và hỗ trợ người dùng.
- **Xây dựng Backend thời gian thực:** Tận dụng sức mạnh của Supabase để quản lý dữ liệu PostgreSQL, xác thực người dùng và đồng bộ hóa Realtime.
- **Tối ưu hóa trải nghiệm người dùng (UX):** Tạo luồng mua sắm liền mạch từ việc đọc review đến lúc nhận hàng.

### 1.3. Phạm vi nghiên cứu
- Thiết kế giao diện tuân thủ Material Design 3.
- Triển khai kiến trúc Clean Architecture để đảm bảo tính mở rộng.
- Xử lý đa phương tiện (ảnh/video) hiệu năng cao trên thiết bị di động.

## CHƯƠNG 2: CÔNG NGHỆ VÀ THƯ VIỆN SỬ DỤNG

### 2.1. Ngôn ngữ và Nền tảng
- **Kotlin 2.0:** Tận dụng K2 Compiler cho tốc độ build nhanh và mã nguồn an toàn hơn.
- **Jetpack Compose:** Framework xây dựng UI khai báo giúp tăng tốc độ phát triển và giảm thiểu lỗi.

### 2.2. Thư viện chiến lược
- **Supabase (Auth, DB, Storage, Realtime):** Giải pháp Backend mạnh mẽ, thay thế Firebase với khả năng tùy biến cao.
- **Dagger Hilt:** Quản lý Dependency Injection, giúp mã nguồn sạch và dễ kiểm thử.
- **Coil & Media3 ExoPlayer:** Bộ đôi xử lý hình ảnh và video chuyên nghiệp, đảm bảo trải nghiệm media mượt mà trên Feed.
- **Navigation Compose:** Điều hướng Type-safe giữa các màn hình.

### 2.3. Giải pháp AI tích hợp
- **Google Gemini 1.5 Flash:** Xử lý ngôn ngữ tự nhiên, tư vấn mua sắm và kiểm duyệt văn bản.
- **Sightengine:** Kiểm duyệt hình ảnh chuyên sâu, phát hiện và chặn nội dung không phù hợp (nhạy cảm, bạo lực, vũ khí) theo thời gian thực.
