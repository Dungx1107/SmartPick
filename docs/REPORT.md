# BÁO CÁO XÂY DỰNG HỆ THỐNG CHIA SẺ VÀ GỢI Ý SẢN PHẨM THÔNG MINH - SMARTPICK

**Học phần:** Phát triển ứng dụng trên thiết bị di động
**Chức danh:** Senior Android Engineer & Technical Writer

---

## 1. ĐẶT VẤN ĐỀ

### 1.1. Bối cảnh
Trong kỷ nguyên số, việc mua sắm không chỉ dừng lại ở giao dịch mà còn là sự trải nghiệm và chia sẻ. Người dùng có xu hướng tin tưởng vào các đánh giá, trải nghiệm thực tế từ cộng đồng hơn là các quảng cáo truyền thống. Tuy nhiên, các nền tảng hiện nay thường gặp vấn đề về tin rác (spam) và nội dung không lành mạnh.

### 1.2. Lý do chọn đề tài
Đề tài **SmartPick** được lựa chọn nhằm kết hợp sức mạnh của mạng xã hội chia sẻ với trí tuệ nhân tạo (AI) để tạo ra một môi trường mua sắm an toàn, thông minh và mang tính cộng đồng cao.

### 1.3. Mục tiêu đề tài
- Xây dựng ứng dụng Android mượt mà, hiện đại bằng Jetpack Compose.
- Tích hợp hệ thống Backend thời gian thực mạnh mẽ (Supabase).
- Ứng dụng AI để kiểm duyệt nội dung tự động và tư vấn người dùng.
- Tối ưu hóa trải nghiệm chia sẻ đa phương tiện (ảnh và video).

---

## 2. PHÂN TÍCH YÊU CẦU

### 2.1. Yêu cầu chức năng (Functional Requirements)
- **Quản lý người dùng:** Đăng ký, đăng nhập (thủ công & Google), quản lý hồ sơ.
- **Tương tác cộng đồng:** Đăng bài viết chia sẻ, bình luận đa tầng, thả tim.
- **Quản lý sản phẩm:** Đăng bán hoặc giới thiệu sản phẩm kèm bài viết.
- **Thông báo:** Nhận thông báo thời gian thực về tương tác và trạng thái đơn hàng.
- **Trợ lý AI:** Chatbot tư vấn sản phẩm dựa trên nhu cầu người dùng.
- **An toàn nội dung:** Tự động chặn ảnh/văn bản vi phạm quy chuẩn cộng đồng.

### 2.2. Yêu cầu phi chức năng (Non-functional Requirements)
- **Hiệu năng:** Tải dữ liệu và hình ảnh nhanh, mượt mà (Lazy Loading).
- **Bảo mật:** Xác thực đa lớp, phân quyền truy cập dữ liệu (RLS).
- **Tính sẵn sàng:** Hệ thống hoạt động 24/7 trên nền tảng Cloud.
- **Giao diện:** Thân thiện, tuân thủ Material Design 3.

---

## 3. THIẾT KẾ HỆ THỐNG

### 3.1. Kiến trúc phần mềm (Architecture)
Hệ thống sử dụng kiến trúc **Clean Architecture** chia làm 3 lớp:
1. **Presentation Layer:** Sử dụng ViewModels và StateFlow để quản lý trạng thái UI.
2. **Domain Layer:** Chứa logic nghiệp vụ xử lý dữ liệu.
3. **Data Layer:** Thực hiện gọi API qua Supabase SDK và các REST service.

### 3.2. Thiết kế Cơ sở dữ liệu
Hệ thống sử dụng PostgreSQL với các bảng chính:
- `users`: Thông tin tài khoản và profile.
- `posts`: Nội dung bài viết và đường dẫn media.
- `products`: Thông tin chi tiết sản phẩm thương mại.
- `comments`: Lưu trữ bình luận và phân cấp reply.
- `notifications`: Lưu trữ và điều phối thông báo.

### 3.3. Luồng hoạt động chính
- **Luồng đăng bài:** Chọn media -> Kiểm duyệt AI (Text/Image) -> Nếu an toàn -> Upload Storage -> Lưu Database.
- **Luồng tương tác:** User action -> Update DB -> Trigger Realtime -> Push Notification tới người nhận.

---

## 4. CÔNG NGHỆ SỬ DỤNG

### 4.1. Android Client
- **Jetpack Compose:** Xây dựng UI hiện đại, giảm thiểu mã nguồn thừa.
- **Hilt:** Tiêm phụ thuộc (DI) giúp code sạch và dễ test.
- **Coroutines & Flow:** Xử lý bất đồng bộ mạnh mẽ, tránh block UI thread.
- **ExoPlayer:** Hỗ trợ phát video chất lượng cao trong bài đăng.

### 4.2. Backend & AI Integration
- **Supabase:** Giải pháp thay thế Firebase, cung cấp Database, Auth và Storage cực nhanh.
- **Gemini AI:** Sử dụng model `gemini-1.5-flash` để xử lý ngôn ngữ tự nhiên và tư vấn sản phẩm.
- **Sightengine:** Chuyên biệt cho việc nhận diện hình ảnh nhạy cảm với độ chính xác trên 95%.

---

## 5. KẾT QUẢ ĐẠT ĐƯỢC VÀ ĐÁNH GIÁ

### 5.1. Kết quả đạt được
- Ứng dụng đã hoàn thiện các tính năng cốt lõi từ đăng nhập đến chia sẻ bài viết.
- Tích hợp thành công AI vào luồng nghiệp vụ thực tế (không chỉ là chatbot đơn thuần).
- Giao diện đồng nhất, mượt mà và dễ sử dụng.

### 5.2. Ưu điểm
- Kiến trúc chuẩn, dễ dàng mở rộng và bảo trì.
- Khả năng kiểm duyệt nội dung tự động giúp giảm tải cho đội ngũ admin.
- Sử dụng công nghệ mới nhất giúp ứng dụng nhẹ và hiệu quả.

### 5.3. Hạn chế
- Phụ thuộc vào tốc độ API của bên thứ ba (Gemini, Sightengine).
- Chưa tích hợp thanh toán trực tiếp trong ứng dụng.

---

## 6. HƯỚNG PHÁT TRIỂN

- **E-commerce Integration:** Tích hợp cổng thanh toán (Momo, VNPay).
- **ML Recommendation:** Xây dựng hệ thống gợi ý sản phẩm cá nhân hóa sâu.
- **Community Features:** Thêm tính năng Livestream và Group Chat.
- **Multi-platform:** Mở rộng lên iOS bằng Kotlin Multiplatform (KMP).

---

## 7. KẾT LUẬN

Dự án **SmartPick** đã chứng minh được tính khả thi của việc kết hợp mạng xã hội với trí tuệ nhân tạo trên thiết bị di động. Với cấu trúc bền vững và công nghệ hiện đại, dự án hoàn toàn có thể phát triển thành một sản phẩm thương mại thực tế, góp phần làm sạch môi trường mạng và hỗ trợ người dùng mua sắm thông minh hơn.

---
**Người báo cáo:** Senior Android Engineer
**Ngày hoàn thành:** 24/05/2024
