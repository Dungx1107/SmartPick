# Thiết kế Cơ sở dữ liệu (Database Design)

SmartPick sử dụng **PostgreSQL** được cung cấp bởi Supabase. Hệ thống được thiết kế theo mô hình quan hệ (Relational Database) để đảm bảo tính toàn vẹn dữ liệu.

## 1. Sơ đồ Thực thể Quan hệ (ERD - Conceptual)

Hệ thống bao gồm các bảng chính: `users`, `products`, `posts`, `comments`, `comment_likes`, và `notifications`.

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
Lưu trữ thông tin sản phẩm mà người dùng muốn chia sẻ hoặc bán.
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

### Bảng: `notifications`
Quản lý thông báo thời gian thực.
- `id` (UUID, Primary Key): Mã thông báo.
- `receiver_id` (UUID, Foreign Key -> `users.id`): Người nhận thông báo.
- `sender_id` (UUID, Foreign Key -> `users.id`): Người gây ra hành động (người like, comment).
- `post_id` (UUID, Foreign Key -> `posts.id`): Bài viết liên quan.
- `type` (String): Loại thông báo (`like`, `comment`, `system`, `order`).
- `content` (Text, Nullable): Nội dung thông báo bổ sung.
- `is_read` (Boolean): Trạng thái đã đọc hay chưa.
- `created_at` (Timestamp): Thời điểm thông báo.

## 3. Các Ràng buộc & Logic (Constraints)
- **Cascade Delete:** Khi xóa một `post`, toàn bộ `comments` và `notifications` liên quan sẽ bị xóa tự động (tùy cấu hình Postgres).
- **Row Level Security (RLS):** Supabase áp dụng RLS để đảm bảo người dùng chỉ có thể sửa/xóa dữ liệu của chính mình.
- **RPC (Remote Procedure Call):** Sử dụng hàm `check_user_availability` trong Postgres để tối ưu việc kiểm tra trùng lặp email/username khi đăng ký.

## 4. Storage Buckets
- `avatars`: Chứa ảnh đại diện của người dùng.
- `media`: Chứa toàn bộ ảnh và video của bài viết và sản phẩm. Cơ chế đặt tên file: `post_media_{UUID}.extension`.
