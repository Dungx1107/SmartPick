# Sơ đồ Điều hướng (Navigation)

SmartPick sử dụng **Jetpack Compose Navigation** để quản lý luồng di chuyển giữa các màn hình. Ứng dụng tuân thủ mô hình **Single Activity**, trong đó `MainActivity` là entry point duy nhất và `AppNavigation` điều phối nội dung.

## 1. Các Tuyến đường (Routes)

Các route được định nghĩa tập trung trong class `Routes.kt` dưới dạng `sealed class` để đảm bảo type-safety:

- `login`: Màn hình Đăng nhập.
- `sign_up`: Màn hình Đăng ký tài khoản mới.
- `home`: Trang chủ (Hiển thị sản phẩm gợi ý).
- `feed`: Bảng tin cộng đồng (Posts).
- `chatbot`: Trợ lý AI (Gemini Chat).
- `saved`: Bộ sưu tập bài viết đã lưu.
- `profile`: Trang cá nhân người dùng.
- `edit_profile`: Chỉnh sửa thông tin cá nhân.
- `create_post`: Màn hình tạo bài viết và gắn thẻ sản phẩm.
- `notifications`: Danh sách thông báo.
- `post_detail/{postId}`: Chi tiết một bài viết cụ thể.
- `comments/{postId}/{postOwnerId}`: Danh sách bình luận của một bài viết.

## 2. Luồng Điều hướng chính (User Flow)

### 2.1. Luồng Xác thực (Auth Flow)
- Khi mở app, `AuthViewModel` kiểm tra trạng thái session từ Supabase.
- Nếu chưa login: Điều hướng đến `login`.
- Nếu đã login: Điều hướng trực tiếp đến `home`.
- Sau khi Đăng xuất: Xóa session và quay lại màn hình `login`, đồng thời dọn sạch BackStack.

### 2.2. Luồng Chính (Main Flow - Bottom Navigation)
Ứng dụng sử dụng một Bottom Bar để chuyển đổi nhanh giữa các màn hình cốt lõi:
- **Home:** Khám phá sản phẩm.
- **Feed:** Xem bài viết từ cộng đồng.
- **AI Curator:** Chat với trợ lý ảo.
- **Saved:** Xem lại các bài đã lưu.
- **Profile:** Quản lý cá nhân.

### 2.3. Luồng Tạo nội dung (Content Flow)
- Từ màn hình `Feed`, người dùng nhấn nút "+" -> Chuyển đến `create_post`.
- Sau khi đăng bài thành công hoặc nhấn "Hủy" -> Quay lại màn hình `Feed`.

### 2.4. Luồng Tương tác (Engagement Flow)
- Click vào bài viết ở `Feed` -> Chuyển đến `post_detail`.
- Click vào icon bình luận -> Chuyển đến `comments`.
- Từ `comments`, có thể click vào Avatar user để chuyển đến `profile` của người đó.

## 3. Quản lý Trạng thái Bottom Bar
- Bottom Bar chỉ hiển thị ở các màn hình chính (`home`, `feed`, `chatbot`, `saved`, `profile`).
- Các màn hình như `login`, `sign_up`, `create_post`, `post_detail` sẽ ẩn Bottom Bar để tối ưu không gian hiển thị.
- Logic ẩn/hiện được xử lý tập trung trong `NavigationUtils.shouldShowBottomBar`.

## 4. Đặc điểm Kỹ thuật
- **Deep Linking:** Cấu trúc route cho phép mở rộng tính năng Deep Link (ví dụ: mở trực tiếp một sản phẩm từ link bên ngoài).
- **PopUpTo:** Khi điều hướng giữa các tab Bottom Bar, ứng dụng sử dụng `popUpTo(startDestination)` với `saveState = true` và `restoreState = true` để tránh tích tụ stack và giữ trạng thái cuộn của người dùng.
- **LaunchSingleTop:** Tránh việc mở chồng nhiều instance của cùng một màn hình khi người dùng nhấn liên tục vào icon điều hướng.
