# 📊 Báo cáo Thiết kế Cơ sở dữ liệu - SmartPick

Hệ thống cơ sở dữ liệu của SmartPick sử dụng hệ quản trị cơ sở dữ liệu quan hệ **PostgreSQL**, được triển khai và quản lý trực tiếp trên nền tảng **Supabase Cloud**. Thiết kế này hỗ trợ đồng thời cả tính năng mạng xã hội và thương mại điện tử với cơ chế bảo mật cấp dòng (RLS) cùng các trigger tự động hóa.

---

## 1. Các bảng chính trong hệ thống

### 👤 Bảng `users` (Hồ sơ người dùng)
Lưu trữ thông tin người dùng được đồng bộ từ tài khoản xác thực của Supabase Auth.
* `id`: UUID (Primary Key, liên kết ngoại với `auth.users`).
* `email`: Địa chỉ email.
* `username`: Tên định danh duy nhất của người dùng.
* `full_name`: Tên đầy đủ hiển thị.
* `avatar_url`: Đường dẫn tới tệp ảnh đại diện trong Supabase Storage.
* `phone_number`: Số điện thoại liên hệ.
* `bio`: Đoạn tự giới thiệu ngắn gọn.
* `created_at`: Thời gian khởi tạo hồ sơ.

### 📦 Bảng `products` (Danh mục sản phẩm)
Quản lý các sản phẩm được đăng bán bởi các Seller.
* `id`: UUID (Primary Key).
* `owner_id`: ID người bán (Foreign Key liên kết tới `users.id`).
* `name`: Tên sản phẩm.
* `brand`: Thương hiệu sản phẩm.
* `category`: Danh mục phân loại.
* `price`: Giá bán hiện tại (Double).
* `stock`: Số lượng tồn kho hiện tại (Int).
* `image_urls`: Mảng các liên kết hình ảnh sản phẩm.
* `video_url`: Liên kết video giới thiệu sản phẩm (nếu có).
* `created_at`: Thời điểm đăng bán sản phẩm.

### 📝 Bảng `posts` (Bài viết chia sẻ / Review)
Nơi lưu trữ các bài viết cộng đồng, hỗ trợ gắn thẻ sản phẩm bán kèm.
* `id`: UUID (Primary Key).
* `user_id`: Người đăng bài viết (Foreign Key liên kết tới `users.id`).
* `product_id`: Sản phẩm được gắn thẻ liên kết (Foreign Key liên kết tới `products.id`, có thể NULL).
* `content`: Nội dung bằng chữ của bài viết review.
* `media_urls`: Mảng liên kết ảnh/video đính kèm bài viết.
* `shared_post_id`: ID bài viết gốc (nếu là bài chia sẻ lại - Nullable).
* `created_at`: Thời điểm đăng bài viết.

### 💬 Bảng `comments` (Bình luận đa tầng)
Hỗ trợ chức năng thảo luận, phản hồi đa tầng trên các bài viết.
* `id`: UUID (Primary Key).
* `post_id`: ID bài viết liên kết (Foreign Key tới `posts.id`).
* `user_id`: Người bình luận (Foreign Key tới `users.id`).
* `parent_id`: ID bình luận cha (Foreign Key tự liên kết tới `comments.id`, dùng để reply).
* `content`: Nội dung chữ của bình luận.
* `created_at`: Thời điểm bình luận.

### ❤️ Bảng `post_reactions` (Lượt thích bài viết)
Lưu trữ tương tác giữa người dùng và bài đăng.
* `id`: UUID (Primary Key).
* `post_id`: ID bài viết (Foreign Key tới `posts.id`).
* `user_id`: ID người dùng tương tác (Foreign Key tới `users.id`).
* `reaction_type`: Loại cảm xúc (LIKE, LOVE, HAHA, WOW, SAD, ANGRY).
* `created_at`: Thời điểm tương tác.

### 🛒 Bảng `cart_items` (Chi tiết giỏ hàng)
Quản lý giỏ hàng tạm thời của khách hàng.
* `id`: UUID (Primary Key).
* `user_id`: ID chủ giỏ hàng (Foreign Key tới `users.id`).
* `product_id`: ID sản phẩm trong giỏ (Foreign Key tới `products.id`).
* `quantity`: Số lượng đặt mua (Int).
* `post_id`: ID bài viết nguồn dẫn tới quyết định mua hàng (UUID).

### 🧾 Bảng `orders` & `order_items` (Hóa đơn mua bán)
* **Bảng `orders`:** Lưu trữ thông tin đơn hàng tổng quát.
  * `id`: UUID (Primary Key).
  * `user_id`: ID người mua (Foreign Key tới `users.id`).
  * `total_amount`: Tổng giá trị thanh toán đơn hàng.
  * `shipping_address`: Địa chỉ giao nhận hàng.
  * `phone_number`: Số điện thoại nhận hàng.
  * `payment_method`: Phương thức thanh toán (ví dụ: COD).
  * `status`: Trạng thái đơn hàng (pending, shipping, success, ...).
  * `created_at`: Thời gian đặt hàng.
* **Bảng `order_items`:** Chi tiết các sản phẩm nằm trong hóa đơn đặt mua.
  * `id`: UUID (Primary Key).
  * `order_id`: ID đơn hàng tổng (Foreign Key tới `orders.id`).
  * `product_id`: ID sản phẩm đặt mua (Foreign Key tới `products.id`).
  * `quantity`: Số lượng mua sản phẩm đó.
  * `price_at_purchase`: Giá bán của sản phẩm tại thời điểm chốt đơn.
  * `created_at`: Thời điểm ghi nhận chi tiết.

### ⭐ Bảng `reviews` (Đánh giá chất lượng)
Lưu trữ đánh giá sản phẩm (chỉ cho phép khi người dùng đã đặt hàng thành công sản phẩm đó).
* `id`: UUID (Primary Key).
* `user_id`: ID người đánh giá.
* `product_id`: ID sản phẩm được đánh giá.
* `rating`: Số sao (từ 1 đến 5).
* `content`: Nhận xét chi tiết.
* `created_at`: Thời điểm đánh giá.

### 🔔 Bảng `notifications` & `user_push_tokens` (Thông báo)
* **Bảng `notifications`:** Lịch sử thông báo hệ thống và tương tác.
  * `id`: UUID (Primary Key).
  * `receiver_id`: ID người nhận thông báo.
  * `sender_id`: ID người gửi hành động tương tác.
  * `type`: Phân loại thông báo (LIKE, COMMENT, ORDER, SYSTEM).
  * `title`: Tiêu đề thông báo.
  * `content`: Nội dung hiển thị của thông báo.
  * `target_id`: ID của đối tượng liên quan (orderId, commentId).
  * `post_id`: ID bài viết liên quan (nếu có).
  * `is_read`: Đã đọc hay chưa (Boolean).
  * `created_at`: Thời điểm gửi.
* **Bảng `user_push_tokens`:** Quản lý FCM push tokens của người dùng.
  * `user_id`: ID người dùng (Unique).
  * `token`: Token gửi tin nhắn đẩy FCM.
  * `device_type`: Loại thiết bị (android).
  * `updated_at`: Thời gian cập nhật token.

---

## 2. Các cơ chế đặc biệt trong thiết kế CSDL

### 🔒 Row Level Security (RLS)
Để đảm bảo an toàn thông tin, PostgreSQL trên Supabase thiết lập các chính sách bảo mật sau:
* Người dùng chỉ có quyền chỉnh sửa/xóa hồ sơ cá nhân và các bài viết/bình luận do chính họ sở hữu.
* Dữ liệu nhạy cảm như token đẩy (`user_push_tokens`) được khóa RLS, chỉ chủ sở hữu tài khoản hoặc tiến trình hệ thống mới có thể đọc/ghi.
* Cho phép mọi người đọc công khai danh mục sản phẩm và bảng tin bài viết, nhưng chặn quyền cập nhật trái phép của người dùng không sở hữu.

### ⚡ Database Triggers & Constraints
* **Tự động kiểm kho:** Khi có dữ liệu mới chèn vào bảng `order_items`, một Database Trigger sẽ được kích hoạt để tự động kiểm tra số lượng tồn kho trong bảng `products`. Nếu đủ tồn kho, hệ thống sẽ thực hiện trừ trực tiếp `products.stock`. Ngược lại, nếu vượt quá số lượng tồn kho thực tế, cơ sở dữ liệu sẽ chặn giao dịch và thực hiện Rollback lập tức để tránh lỗi bán quá (Over-selling).
* **Đồng bộ FCM Token:** Ràng buộc `UNIQUE(user_id, token)` trong bảng `user_push_tokens` cho phép thực hiện cú pháp `ON CONFLICT DO UPDATE` (upsert) mượt mà để cập nhật thiết bị đăng nhập mới nhất của khách hàng mà không làm nhân bản dòng dữ liệu thừa.
