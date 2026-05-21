# Thiết kế Cơ sở dữ liệu (Database Design)

SmartPick sử dụng **PostgreSQL** được cung cấp bởi Supabase. Hệ thống được thiết kế theo mô hình quan hệ (Relational Database) để đảm bảo tính toàn vẹn dữ liệu.

## 1. Sơ đồ Thực thể Quan hệ (ERD - Conceptual)

Hệ thống bao gồm các bảng chính: `users`, `products`, `posts`, `comments`, `comment_likes`, `notifications`, `cart_items`, `orders`, `order_items`, và `reviews`.

## 2. Chi tiết các Bảng

### Bảng: `users`
Lưu trữ thông tin định danh và hồ sơ người dùng.
- `id` (UUID, Primary Key): Liên kết trực tiếp với `auth.users` của Supabase.
- `email` (String, Unique): Địa chỉ email của người dùng.
- `username` (String, Unique): Tên định danh duy nhất.
- `full_name` (String): Họ và tên đầy đủ.
- `avatar_url` (String, Nullable): Đường dẫn ảnh đại diện trong Storage.
- `phone_number` (String, Nullable): Số điện thoại liên lạc.
- `created_at` (Timestamp): Thời điểm đăng ký.

### Bảng: `products`
Lưu trữ thông tin sản phẩm.
- `id` (UUID, Primary Key): Mã sản phẩm.
- `owner_id` (UUID, Foreign Key -> `users.id`): Người sở hữu sản phẩm.
- `name` (String): Tên sản phẩm.
- `brand` (String): Thương hiệu.
- `category` (String): Danh mục (Thiết bị, Phụ kiện, ...).
- `price` (Numeric): Giá sản phẩm.
- `image_urls` (Array[String]): Danh sách URL ảnh sản phẩm.
- `video_url` (String, Nullable): URL video giới thiệu.
- `status` (String): Trạng thái (available, sold, ...).

### Bảng: `posts`
Lưu trữ các bài viết trên bảng tin.
- `id` (UUID, Primary Key): Mã bài viết.
- `user_id` (UUID, Foreign Key -> `users.id`): Người đăng bài.
- `product_id` (UUID, Foreign Key -> `products.id`, Nullable): Sản phẩm được gắn kèm bài viết.
- `content` (Text): Nội dung mô tả/chia sẻ.
- `media_urls` (Array[String]): Danh sách ảnh/video của bài viết.
- `created_at` (Timestamp): Thời điểm đăng bài.

### Bảng: `comments`
Hỗ trợ bình luận đa tầng.
- `id` (UUID, Primary Key): Mã bình luận.
- `post_id` (UUID, Foreign Key -> `posts.id`): Bài viết được bình luận.
- `user_id` (UUID, Foreign Key -> `users.id`): Người bình luận.
- `parent_id` (UUID, Foreign Key -> `comments.id`, Nullable): ID của bình luận cha (dùng cho tính năng reply).
- `content` (Text): Nội dung bình luận.
- `created_at` (Timestamp): Thời điểm bình luận.

### Bảng: `cart_items`
Quản lý giỏ hàng của người dùng.
- `id` (UUID, Primary Key).
- `user_id` (UUID, Foreign Key -> `users.id`).
- `product_id` (UUID, Foreign Key -> `products.id`).
- `quantity` (Integer): Số lượng sản phẩm trong giỏ.

### Bảng: `orders`
Lưu trữ thông tin đơn hàng sau khi thanh toán.
- `id` (UUID, Primary Key).
- `user_id` (UUID, Foreign Key -> `users.id`).
- `total_amount` (Numeric): Tổng giá trị đơn hàng.
- `shipping_address` (Text): Địa chỉ nhận hàng.
- `phone_number` (String): Số điện thoại nhận hàng.
- `payment_method` (String): Phương thức thanh toán.
- `status` (String): Trạng thái đơn hàng (mặc định: `completed`).
- `created_at` (Timestamp).

### Bảng: `order_items`
Chi tiết các sản phẩm trong một đơn hàng.
- `id` (UUID, Primary Key).
- `order_id` (UUID, Foreign Key -> `orders.id`).
- `product_id` (UUID, Foreign Key -> `products.id`).
- `quantity` (Integer): Số lượng tại thời điểm mua.
- `price_at_purchase` (Numeric): Giá sản phẩm tại thời điểm mua.

### Bảng: `reviews`
Lưu trữ đánh giá của người dùng về sản phẩm đã mua.
- `id` (UUID, Primary Key).
- `user_id` (UUID, Foreign Key -> `users.id`).
- `product_id` (UUID, Foreign Key -> `products.id`).
- `rating` (Integer): Điểm đánh giá (1-5).
- `content` (Text): Nội dung đánh giá.
- `created_at` (Timestamp).

### Bảng: `notifications`
Quản lý thông báo thời gian thực.
- `id` (UUID, Primary Key).
- `receiver_id` (UUID, Foreign Key -> `users.id`).
- `sender_id` (UUID, Foreign Key -> `users.id`).
- `post_id` (UUID, Foreign Key -> `posts.id`).
- `type` (String): Loại thông báo (`like`, `comment`, `system`, `order`).
- `content` (Text, Nullable).
- `is_read` (Boolean).
- `created_at` (Timestamp).

## 3. Các Ràng buộc & Logic (Constraints)
- **RLS (Row Level Security):** Áp dụng nghiêm ngặt để bảo vệ dữ liệu cá nhân.
- **Foreign Keys:** Đảm bảo tính toàn vẹn giữa người dùng, sản phẩm, đơn hàng và đánh giá.
- **Triggers:** Tự động cập nhật `updated_at` hoặc gửi thông báo Realtime (nếu có).

## 4. Storage Buckets
- `avatars`: Ảnh đại diện.
- `media`: Ảnh/Video bài viết và sản phẩm.
