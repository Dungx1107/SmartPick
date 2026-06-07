# BÁO CÁO XÂY DỰNG HỆ THỐNG CHIA SẺ VÀ GỢI Ý SẢN PHẨM THÔNG MINH - SMARTPICK

**Học phần:** Phát triển ứng dụng trên thiết bị di động
**Dự án:** SmartPick - Mạng xã hội & Thương mại điện tử tích hợp AI

---

## 1. ĐẶT VẤN ĐỀ

### 1.1. Bối cảnh
Trong xu thế mua sắm hiện nay, người dùng không chỉ tìm kiếm sản phẩm mà còn tìm kiếm sự tin cậy thông qua các trải nghiệm thực tế từ cộng đồng. Tuy nhiên, việc rây lọc thông tin chất lượng giữa hàng ngàn nội dung rác và ngôn từ tiêu cực là một thách thức lớn.

### 1.2. Mục tiêu đề tài
Xây dựng một nền tảng Android tích hợp AI giúp:
- Tạo môi trường chia sẻ (Review) an toàn nhờ kiểm duyệt tự động.
- Tối ưu hóa luồng mua sắm từ nội dung review đến lúc thanh toán.
- Cung cấp trợ lý ảo hỗ trợ người dùng 24/7.

---

## 2. PHÂN TÍCH & THIẾT KẾ

### 2.1. Yêu cầu chức năng
- **Xác thực:** Đăng nhập Email/Password và Google Sign-In.
- **Cộng đồng:** Đăng bài (ảnh/video), like, comment đa tầng, thông báo realtime.
- **Thương mại:** Quản lý sản phẩm, giỏ hàng, thanh toán và lịch sử đơn hàng.
- **AI:** Kiểm duyệt ảnh nhạy cảm (Sightengine), lọc văn bản độc hại (Gemini) và Chatbot tư vấn.

### 2.2. Kiến trúc Hệ thống
Áp dụng **Clean Architecture** và **MVVM** giúp tách biệt mã nguồn thành các lớp Presentation, Domain và Data. Sử dụng **Dagger Hilt** để quản lý sự phụ thuộc.

---

## 3. CÔNG NGHỆ SỬ DỤNG

- **Android:** Kotlin 2.0, Jetpack Compose, Coroutines, Flow.
- **Backend:** Supabase (PostgreSQL, Auth, Storage, Realtime).
- **AI:** Google Gemini 1.5 Flash, Sightengine Moderation.
- **Thư viện:** Coil, Media3 ExoPlayer, Ktor, OkHttp.

---

## 4. KẾT QUẢ ĐẠT ĐƯỢC

### 4.1. Về Kỹ thuật
- Hoàn thiện luồng dữ liệu một chiều (UDF) mượt mà với Jetpack Compose.
- Tích hợp thành công cơ chế kiểm duyệt AI vào luồng đăng bài, ngăn chặn nội dung xấu ngay từ nguồn.
- Hệ thống thông báo thời gian thực hoạt động ổn định qua Supabase Realtime.

### 4.2. Về Trải nghiệm
- Giao diện hiện đại theo phong cách Material 3.
- Tốc độ tải media và phản hồi AI đạt mức tối ưu.
- Quy trình từ xem review đến mua hàng được rút ngắn và minh bạch.

---

## 5. KẾT LUẬN

Dự án **SmartPick** đã hoàn thành mục tiêu xây dựng một ứng dụng di động hiện đại, an toàn và thông minh. Sự kết hợp giữa công nghệ Android MAD và các dịch vụ Cloud/AI hàng đầu giúp ứng dụng có khả năng cạnh tranh và sẵn sàng cho việc đưa vào sử dụng thực tế.

---
**Người thực hiện:** Đội ngũ phát triển SmartPick
**Ngày hoàn thành:** Tháng 12/2024
