# Danh sách Tính năng SmartPick

Hệ thống SmartPick cung cấp một bộ tính năng đầy đủ cho một mạng xã hội chia sẻ sản phẩm thông minh.

## 1. Hệ thống Xác thực (Authentication)
- **Đăng ký/Đăng nhập thủ công:** Qua Email và mật khẩu.
- **Google Sign-In:** Tích hợp SDK Credentials mới nhất để đăng nhập một chạm.
- **Xác thực Email:** Gửi email chào mừng và yêu cầu xác thực qua SendGrid (EmailHelper).
- **Kiểm tra trùng lặp:** Tự động kiểm tra tính khả dụng của username và email khi đăng ký qua Database RPC.

## 2. Bảng tin Sản phẩm (Product Feed)
- **Trang chủ (Home):** Hiển thị các sản phẩm nổi bật và bộ lọc danh mục.
- **Bảng tin (Feed):** Luồng bài viết từ cộng đồng.
- **Hỗ trợ đa phương tiện:** Hiển thị danh sách ảnh (Carousel) và video (ExoPlayer).
- **Tương tác nhanh:** Like, Comment và Share trực tiếp từ feed.

## 3. Tạo Bài viết & Quản lý Sản phẩm
- **Đăng bài đa phương tiện:** Cho phép chọn nhiều ảnh/video từ thư viện.
- **Gắn thẻ sản phẩm:** Tạo sản phẩm mới đi kèm bài viết (Tên, thương hiệu, giá, danh mục).
- **Tự động Upload:** Upload media lên Supabase Storage với cơ chế coroutine song song.
- **Kiểm duyệt AI:** Tự động quét nội dung nhạy cảm (ảnh) và ngôn ngữ thô tục (văn bản) trước khi đăng.

## 4. Hệ thống Bình luận & Tương tác
- **Bình luận đa tầng:** Hỗ trợ reply (trả lời) bình luận của người khác.
- **Like bình luận:** Người dùng có thể thả tim cho các ý kiến hữu ích.
- **Realtime UI:** Cập nhật số lượng like và bình luận ngay lập tức.

## 5. Trợ lý ảo AI (AI Curator)
- **Chatbot Gemini:** Tích hợp Gemini 1.5 Flash để tư vấn mua sắm.
- **Context-aware:** Tư vấn dựa trên xu hướng sản phẩm hiện có trong hệ thống.
- **Giao diện Chat:** Trải nghiệm nhắn tin mượt mà với AI.

## 6. Thông báo (Notifications)
- **Đa dạng loại hình:** Thông báo về đơn hàng, tương tác cộng đồng, khuyến mãi và hệ thống.
- **Realtime Updates:** Nhận thông báo tức thì khi có người comment hoặc like bài viết qua Supabase Realtime.
- **Bộ lọc thông báo:** Phân loại thông báo để dễ dàng quản lý.

## 7. Cá nhân hóa & Quản lý Tài khoản
- **Hồ sơ người dùng:** Cập nhật thông tin cá nhân, ảnh đại diện.
- **Bộ sưu tập đã lưu:** Lưu lại các bài viết quan tâm để xem sau.
- **Quản lý sản phẩm đã đăng:** Theo dõi danh sách sản phẩm cá nhân.

## 8. Quản trị & An toàn (Admin & Safety)
- **Kiểm duyệt Văn bản:** Sử dụng Gemini AI để nhận diện teencode, chửi thề, xúc phạm.
- **Kiểm duyệt Hình ảnh:** Sử dụng Sightengine để chặn ảnh bạo lực, nhạy cảm, vũ khí.
- **Hệ thống Log:** Ghi nhận lỗi và hoạt động hệ thống qua Logger tùy chỉnh.
