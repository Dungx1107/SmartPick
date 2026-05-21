# Danh sách Tính năng SmartPick

Hệ thống SmartPick cung cấp một bộ tính năng đầy đủ cho một mạng xã hội chia sẻ sản phẩm thông minh kết hợp thương mại điện tử cơ bản.

## 1. Hệ thống Xác thực (Authentication)
- **Đăng ký/Đăng nhập thủ công:** Qua Email và mật khẩu.
- **Google Sign-In:** Tích hợp SDK Credentials mới nhất để đăng nhập một chạm.
- **Xác thực Email:** Gửi email chào mừng và yêu cầu xác thực.
- **Quản lý Session:** Duy trì trạng thái đăng nhập lâu dài qua Supabase Auth.

## 2. Bảng tin Sản phẩm (Product Feed)
- **Trang chủ (Home):** Hiển thị các sản phẩm gợi ý và cho phép tìm kiếm sản phẩm.
- **Bảng tin (Feed):** Luồng bài viết từ cộng đồng.
- **Hỗ trợ đa phương tiện:** Hiển thị danh sách ảnh (Carousel) và video (ExoPlayer).
- **Tương tác nhanh:** Like, Comment và Share trực tiếp từ feed.

## 3. Tạo Bài viết & Quản lý Sản phẩm
- **Đăng bài đa phương tiện:** Cho phép chọn nhiều ảnh/video từ thư viện.
- **Gắn thẻ sản phẩm:** Tạo sản phẩm mới đi kèm bài viết (Tên, thương hiệu, giá, danh mục).
- **Kiểm duyệt AI:** Tự động quét nội dung nhạy cảm (ảnh) qua Sightengine và ngôn ngữ thô tục (văn bản) qua Gemini AI trước khi đăng.

## 4. Thương mại điện tử (E-commerce)
- **Giỏ hàng (Cart):** Thêm sản phẩm vào giỏ, tăng giảm số lượng, xóa khỏi giỏ hàng.
- **Thanh toán (Checkout):** Nhập địa chỉ, số điện thoại và chọn phương thức thanh toán để hoàn tất đơn hàng.
- **Lịch sử đơn hàng:** Xem lại danh sách các đơn hàng đã mua.
- **Đánh giá sản phẩm (Reviews):** Người dùng chỉ có thể đánh giá những sản phẩm họ ĐÃ MUA. Hỗ trợ chấm điểm (1-5 sao) và để lại bình luận.

## 5. Hệ thống Bình luận & Tương tác
- **Bình luận đa tầng:** Hỗ trợ reply (trả lời) bình luận của người khác.
- **Like bình luận:** Người dùng có thể thả tim cho các ý kiến hữu ích.
- **Realtime UI:** Cập nhật số lượng like và bình luận ngay lập tức qua Supabase Realtime.

## 6. Trợ lý ảo AI (AI Curator)
- **Chatbot Gemini:** Tích hợp Gemini 1.5 Flash để tư vấn mua sắm.
- **Context-aware:** Tư vấn dựa trên xu hướng sản phẩm hiện có trong hệ thống.
- **Giao diện Chat:** Trải nghiệm nhắn tin mượt mà với AI.

## 7. Thông báo (Notifications)
- **Đa dạng loại hình:** Thông báo về tương tác cộng đồng (like, comment), hệ thống và đơn hàng.
- **Realtime Updates:** Nhận thông báo tức thì qua Supabase Realtime.

## 8. Cá nhân hóa & Quản lý Tài khoản
- **Hồ sơ người dùng:** Cập nhật thông tin cá nhân, ảnh đại diện, số điện thoại.
- **Bộ sưu tập đã lưu:** Lưu lại các bài viết quan tâm để xem sau.
- **Review Hub:** Quản lý các sản phẩm chờ đánh giá và các đánh giá đã gửi.
- **Cài đặt:** Quản lý tài khoản và cấu hình ứng dụng.
