# Danh sách Tính năng SmartPick

Hệ thống SmartPick cung cấp một bộ tính năng đầy đủ, kết hợp giữa mạng xã hội chia sẻ trải nghiệm và thương mại điện tử thông minh.

## 1. Hệ thống Xác thực & Người dùng (Authentication)
- **Đăng nhập Đa dạng:** Hỗ trợ đăng nhập truyền thống (Email/Password) và Google Sign-In (Sử dụng Credential Manager API mới nhất).
- **Quản lý Hồ sơ:** Cập nhật thông tin cá nhân, ảnh đại diện, số điện thoại và quản lý trạng thái tài khoản.
- **Xác thực Bảo mật:** Tích hợp Supabase Auth với cơ chế Row Level Security (RLS) để bảo vệ dữ liệu người dùng.

## 2. Bảng tin Cộng đồng (Social Feed)
- **Khám phá Nội dung:** Luồng bài viết review sản phẩm từ cộng đồng với giao diện cuộn mượt mà.
- **Hỗ trợ Đa phương tiện:** Xem ảnh dưới dạng Carousel và phát video trực tiếp bằng ExoPlayer.
- **Tương tác Xã hội:** Thả tim (Like) bài viết, bình luận đa tầng (Nested comments) và chia sẻ liên kết bài viết.

## 3. Quản lý Sản phẩm & Người bán
- **Gắn thẻ Sản phẩm:** Cho phép người dùng gắn thông tin sản phẩm (tên, giá, thương hiệu) vào bài viết review.
- **Seller Dashboard:** Giao diện dành riêng cho người bán để quản lý danh sách sản phẩm và theo dõi đơn hàng.
- **Chi tiết Sản phẩm:** Hiển thị thông tin đầy đủ, đánh giá từ người mua và các bài viết liên quan.

## 4. Thương mại điện tử (E-commerce)
- **Giỏ hàng Thông minh (Cart):** Thêm/xóa sản phẩm, cập nhật số lượng và tự động tính toán tổng tiền.
- **Thanh toán Linh hoạt (Checkout):** Hỗ trợ mua nhanh một sản phẩm hoặc thanh toán toàn bộ giỏ hàng với thông tin giao hàng chi tiết.
- **Lịch sử Đơn hàng:** Theo dõi trạng thái các đơn hàng đã thực hiện.

## 5. Kiểm duyệt & Trí tuệ nhân tạo (AI)
- **Kiểm duyệt Hình ảnh:** Sử dụng Sightengine API để tự động phát hiện và chặn ảnh nhạy cảm, bạo lực hoặc vũ khí khi người dùng đăng bài.
- **Kiểm duyệt Văn bản:** Tích hợp Gemini AI để lọc các bình luận hoặc nội dung bài viết có ngôn ngữ độc hại, thô tục.
- **AI Curator (Chatbot):** Trợ lý ảo sử dụng model Gemini 1.5 Flash để tư vấn phong cách, gợi ý sản phẩm và trả lời câu hỏi của người dùng.

## 6. Thông báo & Tương tác Realtime
- **Hệ thống Thông báo:** Nhận thông báo tức thì khi có người like, comment vào bài viết hoặc có cập nhật về đơn hàng.
- **Dữ liệu Realtime:** Tự động cập nhật số lượng tương tác và trạng thái giỏ hàng mà không cần tải lại trang.

## 7. Cá nhân hóa
- **Bộ sưu tập đã lưu (Saved):** Quản lý các bài viết đã lưu để xem lại sau.
- **Review Hub:** Trung tâm quản lý các đánh giá sản phẩm, cho phép người dùng viết review cho các sản phẩm đã mua thành công.
- **Cài đặt ứng dụng:** Tùy chỉnh trải nghiệm cá nhân và quản lý bảo mật.
