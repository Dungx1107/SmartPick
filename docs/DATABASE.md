# Cấu trúc Cơ sở dữ liệu (Database Schema)

Ứng dụng sử dụng **Supabase (PostgreSQL)** làm hệ quản trị cơ sở dữ liệu chính. Dữ liệu được tổ chức
thành các bảng có mối quan hệ chặt chẽ để phục vụ các tính năng mạng xã hội và thương mại điện tử.

## 1. Các bảng chính (Tables)

### Bảng `users` (Quản lý người dùng)

Lưu trữ thông tin hồ sơ người dùng, liên kết với hệ thống Auth của Supabase qua `id` (UUID).

- `id` (UUID, Primary Key): ID định danh từ `auth.users`.
- `full_name` (Text): Họ và tên hiển thị.
- `username` (Text, Unique): Tên người dùng duy nhất.
- `avatar_url` (Text): Đường dẫn đến ảnh đại diện trong Storage.
- `email` (Text): Địa chỉ email.
- `phone_number` (Text): Số điện thoại liên lạc.

### Bảng `posts` (Bài đăng)

Lưu trữ nội dung bài viết và các liên kết đa phương tiện.

- `id` (UUID, Primary Key): ID bài viết.
- `user_id` (UUID, Foreign Key): Liên kết với `users.id`.
- `product_id` (UUID, Foreign Key, Nullable): Liên kết với `products.id` nếu bài viết có gắn thẻ sản
  phẩm.
- `content` (Text): Nội dung văn bản của bài viết.
- `media_urls` (JSONB/Array): Danh sách các đường dẫn ảnh/video.
- `created_at` (Timestamp): Thời gian tạo bài viết.

### Bảng `products` (Sản phẩm)

Thông tin về sản phẩm được đánh giá hoặc giới thiệu.

- `id` (UUID, Primary Key): ID sản phẩm.
- `owner_id` (UUID, Foreign Key): Người tạo sản phẩm.
- `name` (Text): Tên sản phẩm.
- `brand` (Text): Thương hiệu.
- `price` (Numeric): Giá tham khảo.
- `image_urls` (JSONB/Array): Ảnh sản phẩm.

### Bảng `comments` (Bình luận)

- `id` (UUID, Primary Key): ID bình luận.
- `post_id` (UUID, Foreign Key): Thuộc về bài viết nào.
- `user_id` (UUID, Foreign Key): Người bình luận.
- `content` (Text): Nội dung bình luận.

## 2. Quan hệ (Relationships)

- **1-N (Users - Posts):** Một người dùng có thể có nhiều bài đăng.
- **1-N (Posts - Comments):** Một bài đăng có nhiều bình luận.
- **0/1-N (Products - Posts):** Một sản phẩm có thể được nhắc đến trong nhiều bài viết.

## 3. Repository Pattern

Dữ liệu được truy cập thông qua các Repository chuyên biệt, sử dụng `SupabaseClient` để thực hiện
các câu lệnh JOIN phức tạp:

- `FeedRepository.kt`: Thực hiện JOIN giữa `posts`, `users` và `products` bằng lệnh
  `.select(columns = Columns.raw("*, users(*), products(*)"))`.
- `PostDetailRepository.kt`: Lấy chi tiết bài viết và danh sách bình luận liên quan.
- `UserRepository.kt`: Quản lý thông tin cá nhân.
