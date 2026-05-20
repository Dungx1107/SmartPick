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
- `profile`: Trang cá nhân người dùng.
- `edit_profile`: Chỉnh sửa thông tin cá nhân.
- `create_post`: Màn hình tạo bài viết và sản phẩm.
- `notifications`: Danh sách thông báo.
- `checkout`: Màn hình thanh toán đơn hàng.
- `settings`: Cài đặt ứng dụng.
- `write_review/{productId}`: Màn hình viết đánh giá cho sản phẩm đã mua.
- `post_detail/{postId}`: Chi tiết bài viết.
- `comments/{postId}/{postOwnerId}`: Danh sách bình luận.

## 2. Luồng Điều hướng chính (User Flow)

### 2.1. Luồng Xác thực (Auth Flow)
- Kiểm tra session khi khởi động.
- `Login` -> `SignUp` hoặc ngược lại.
- Thành công -> Chuyển đến `Home`.

### 2.2. Luồng Mua sắm & Đánh giá (Shopping & Review Flow)
- `Home` -> Chọn sản phẩm -> (Chuyển đến bài viết liên quan) -> Thêm vào giỏ.
- Giỏ hàng (Dialog/Screen) -> `Checkout`.
- `Profile` -> `ReviewHub` -> `WriteReview` (cho các sản phẩm đã mua).

### 2.3. Luồng Cộng đồng (Social Flow)
- `Feed` -> `PostDetail` -> `Comments`.
- `Feed` -> `CreatePost` (Ẩn Bottom Bar).
- `Notifications` -> `Comments` (Deep link giả lập).

## 3. Quản lý UI
- **Bottom Bar:** Hiển thị tại `home`, `feed`, `review_hub` (hoặc chatbot), `saved`, `profile`.
- **Top Bar:** Thay đổi tiêu đề và action tùy theo màn hình hiện tại.

## 4. Đặc điểm Kỹ thuật
- Sử dụng `popUpTo` và `launchSingleTop` để tối ưu BackStack.
- Truyền tham số an toàn qua Route String.
- Trạng thái điều hướng được đồng bộ với `NavController`.
