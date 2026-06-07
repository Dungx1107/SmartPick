# Sơ đồ Điều hướng (Navigation)

SmartPick sử dụng **Jetpack Compose Navigation** để quản lý luồng di chuyển giữa các màn hình. Ứng dụng tuân thủ mô hình **Single Activity**, trong đó `MainActivity` là entry point duy nhất và `AppNavigation` điều phối nội dung.

## 1. Các Tuyến đường (Routes)

Các route được định nghĩa tập trung trong class `Routes.kt`:

- `login`: Màn hình Đăng nhập.
- `sign_up`: Màn hình Đăng ký tài khoản mới.
- `home`: Trang chủ (Sản phẩm gợi ý & Tìm kiếm).
- `feed`: Bảng tin cộng đồng (Bài viết).
- `review_hub`: Trung tâm quản lý đánh giá sản phẩm.
- `saved`: Bộ sưu tập bài viết đã lưu.
- `liked_posts`: Danh sách các bài viết người dùng đã thích.
- `profile`: Trang cá nhân người dùng.
- `edit_profile`: Chỉnh sửa thông tin cá nhân.
- `create_post`: Màn hình tạo bài viết và sản phẩm mới.
- `edit_post/{postId}`: Chỉnh sửa bài viết đã đăng.
- `notifications`: Danh sách thông báo.
- `cart`: Giỏ hàng của người dùng.
- `checkout`: Màn hình thanh toán (Hỗ trợ mua lẻ hoặc mua từ giỏ hàng).
- `seller_dashboard`: Trang quản lý dành cho người bán.
- `settings`: Cài đặt ứng dụng.
- `product_detail/{productId}`: Chi tiết sản phẩm.
- `post_detail/{postId}`: Chi tiết bài viết (Hỗ trợ nhảy đến bình luận cụ thể).
- `write_review/{productId}`: Màn hình viết đánh giá cho sản phẩm đã mua.
- `comments/{postId}/{postOwnerId}`: Danh sách bình luận của bài viết.
- `comments_notification/{postId}`: Xem bình luận từ thông báo.

## 2. Luồng Điều hướng chính (User Flow)

### 2.1. Luồng Xác thực (Auth Flow)
- Kiểm tra session khi khởi động.
- `Login` <-> `SignUp`.
- Thành công -> Chuyển đến `Home`.

### 2.2. Luồng Mua sắm & Đánh giá (Shopping & Review Flow)
- `Home`/`Feed` -> `ProductDetail` -> Thêm vào `Cart`.
- `Cart` -> `Checkout`.
- `Profile` -> `ReviewHub` -> `WriteReview`.

### 2.3. Luồng Cộng đồng (Social Flow)
- `Feed` -> `PostDetail` -> `Comments`.
- `Feed` -> `CreatePost`.
- `Notifications` -> `PostDetail` hoặc `CommentsFromNotification`.

## 3. Quản lý UI
- **Bottom Bar:** Hiển thị tại các màn hình chính: `home`, `feed`, `review_hub`, `saved`, `profile`.
- **Top Bar:** Linh hoạt thay đổi tiêu đề và các nút chức năng (Search, Cart, Settings) tùy theo ngữ cảnh.

## 4. Đặc điểm Kỹ thuật
- Sử dụng **Type-safe Navigation** (bằng cách định nghĩa tham số trong Route string).
- Tối ưu hóa BackStack với `launchSingleTop = true` và `restoreState = true`.
- Trạng thái điều hướng được quản lý tập trung qua `NavController`.
