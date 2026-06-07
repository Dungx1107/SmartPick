# BÁO CÁO XÂY DỰNG ỨNG DỤNG SMARTPICK - PHẦN 2: PHÂN TÍCH VÀ THIẾT KẾ

## CHƯƠNG 3: PHÂN TÍCH YÊU CẦU VÀ THIẾT KẾ HỆ THỐNG

### 3.1. Phân tích yêu cầu người dùng (Use Case)
Dựa trên các tính năng đã triển khai, hệ thống tập trung vào các nhóm Use Case chiến lược sau:

#### 3.1.1. Nhóm Quản lý Thương mại (Shopping & E-commerce)
- **Khám phá Sản phẩm:** Người dùng tìm kiếm và xem chi tiết sản phẩm từ Home hoặc Feed.
- **Quản lý Giỏ hàng:** Thêm/xóa sản phẩm, cập nhật số lượng (Xử lý qua `CartViewModel`).
- **Thanh toán (Checkout):** Quy trình nhập thông tin giao hàng và xác nhận đơn hàng, hỗ trợ cả mua lẻ (`productId`) và mua từ giỏ hàng (`cartItemIds`).

#### 3.1.2. Nhóm Tương tác Xã hội (Social Hub)
- **Chia sẻ Trải nghiệm:** Đăng bài viết review kèm media (ảnh/video) và gắn thẻ sản phẩm.
- **Tương tác Cộng đồng:** Thích bài viết, thảo luận qua hệ thống bình luận đa tầng.
- **Lưu trữ Cá nhân:** Quản lý các bài viết đã thích (`liked_posts`) và các mục đã lưu (`saved`).

### 3.2. Thiết kế luồng hoạt động (Activity Diagrams)

#### 3.2.1. Luồng Kiểm duyệt và Đăng bài (Moderation & Publishing)
Đây là luồng quan trọng nhất thể hiện tính "Smart" của ứng dụng:
1. **Input:** Người dùng nhập text và chọn media.
2. **AI Processing:** 
   - Gemini AI quét nội dung văn bản để tìm ngôn ngữ độc hại.
   - Sightengine quét hình ảnh để tìm nội dung nhạy cảm/bạo lực.
3. **Decision:** 
   - Nếu an toàn: Tiến hành upload media lên Supabase Storage và lưu record vào PostgreSQL.
   - Nếu không an toàn: Hiển thị cảnh báo chi tiết và ngăn chặn hành động đăng bài.

#### 3.2.2. Luồng Điều hướng (Navigation Flow)
Sử dụng **Type-safe Compose Navigation**:
- Truyền tham số an toàn (ví dụ: `postId` cho chi tiết bài viết, `productId` cho chi tiết sản phẩm).
- Quản lý trạng thái Bottom Bar linh hoạt dựa trên Route hiện tại.

### 3.3. Thiết kế Cơ sở dữ liệu (Database Design)
Hệ thống PostgreSQL trên Supabase được thiết kế chuẩn hóa:
- **Bảng `users`:** Lưu trữ Profile và Auth link.
- **Bảng `products` & `posts`:** Lưu thông tin cốt lõi của hệ thống, liên kết qua `product_id`.
- **Bảng `cart_items` & `orders`:** Quản lý vòng đời mua sắm.
- **Bảng `notifications`:** Điều phối thông báo thời gian thực giữa các user.

### 3.4. Thiết kế Giao diện (UI Design)
- **Ngôn ngữ thiết kế:** Material Design 3.
- **Trải nghiệm:** Tối ưu hóa cho việc vuốt chạm, sử dụng các thành phần như `Scaffold`, `LazyColumn`, `LazyVerticalGrid`.
- **Tính phản hồi:** Giao diện tự động cập nhật (Recomposition) ngay khi `UIState` từ ViewModel thay đổi.
