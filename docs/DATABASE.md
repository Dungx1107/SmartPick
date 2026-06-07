# Thiết kế Cơ sở dữ liệu (Database Design)

SmartPick sử dụng **PostgreSQL** được cung cấp bởi nền tảng Supabase. Hệ thống được thiết kế theo mô hình quan hệ chặt chẽ, tận dụng tính năng Row Level Security (RLS) để đảm bảo an toàn dữ liệu.

## 1. Các Bảng chính

### Bảng: `users`
Lưu trữ thông tin định danh và hồ sơ cá nhân.
- `id`: UUID (Primary Key, liên kết với `auth.users`).
- `email`: Địa chỉ email.
- `username`: Tên định danh duy nhất.
- `full_name`: Họ và tên hiển thị.
- `avatar_url`: Đường dẫn ảnh đại diện trong Storage.
- `phone_number`: Số điện thoại.
- `bio`: Giới thiệu bản thân.
- `created_at`: Thời điểm tham gia.

### Bảng: `products`
Quản lý thông tin sản phẩm trong hệ thống thương mại điện tử.
- `id`: UUID (Primary Key).
- `owner_id`: ID người bán (liên kết với `users.id`).
- `name`: Tên sản phẩm.
- `brand`: Thương hiệu.
- `category`: Danh mục sản phẩm.
- `price`: Giá bán.
- `description`: Mô tả chi tiết.
- `image_urls`: Mảng các đường dẫn ảnh sản phẩm.
- `video_url`: Đường dẫn video giới thiệu (nếu có).
- `status`: Trạng thái (available, out_of_stock, ...).

### Bảng: `posts`
Lưu trữ nội dung các bài viết review/chia sẻ.
- `id`: UUID (Primary Key).
- `user_id`: Người đăng (liên kết với `users.id`).
- `product_id`: Sản phẩm được gắn thẻ (Nullable).
- `content`: Nội dung bài viết.
- `media_urls`: Mảng các đường dẫn ảnh/video của bài viết.
- `created_at`: Thời điểm đăng bài.

### Bảng: `comments`
Hỗ trợ thảo luận đa tầng trên bài viết.
- `id`: UUID (Primary Key).
- `post_id`: ID bài viết liên quan.
- `user_id`: Người bình luận.
- `parent_id`: ID bình luận cha (dùng cho tính năng reply - Trả lời).
- `content`: Nội dung bình luận.

### Bảng: `reactions` (hoặc `likes`)
Lưu trữ tương tác giữa người dùng và bài viết.
- `user_id`: ID người dùng.
- `post_id`: ID bài viết được thích.
- `created_at`: Thời điểm thực hiện tương tác.

### Bảng: `cart_items`
Quản lý trạng thái giỏ hàng hiện tại.
- `id`: UUID (Primary Key).
- `user_id`: ID chủ sở hữu giỏ hàng.
- `product_id`: ID sản phẩm.
- `quantity`: Số lượng.

### Bảng: `orders` & `order_items`
Lưu trữ thông tin đơn hàng và chi tiết sản phẩm đã mua.
- `orders`: Lưu thông tin tổng quan, địa chỉ giao hàng, tổng tiền và trạng thái.
- `order_items`: Lưu chi tiết giá và số lượng từng sản phẩm tại thời điểm mua.

### Bảng: `reviews`
Đánh giá sản phẩm từ người dùng đã mua hàng.
- `id`, `user_id`, `product_id`.
- `rating`: Số sao (1-5).
- `content`: Nội dung nhận xét.

### Bảng: `notifications`
Lưu trữ lịch sử thông báo.
- `receiver_id`, `sender_id`, `post_id`.
- `type`: Loại thông báo (like, comment, system, order).
- `is_read`: Trạng thái đã xem.

### Bảng: `user_push_tokens`
Quản lý Firebase Cloud Messaging (FCM) tokens.
- `user_id`: ID người dùng.
- `push_token`: Token dùng để gửi thông báo đẩy.

## 2. Bảo mật Dữ liệu (RLS)
Hệ thống áp dụng chính sách **Row Level Security** của PostgreSQL để:
- Người dùng chỉ có thể sửa/xóa dữ liệu do chính họ tạo ra.
- Dữ liệu nhạy cảm (như tokens) chỉ có thể truy cập bởi chủ sở hữu.
- Các bảng công khai (posts, products) cho phép mọi người đọc nhưng hạn chế quyền ghi.

## 3. Storage Buckets
- `avatars`: Chứa ảnh đại diện người dùng.
- `post_media`: Chứa ảnh và video của các bài đăng review.
- `product_media`: Chứa ảnh/video giới thiệu sản phẩm.
