# Danh sách Tính năng (Features)

Dưới đây là chi tiết các tính năng đã và đang được triển khai trong dự án SmartPick, kèm theo các
lớp xử lý chính và trạng thái hiện tại.

## 1. Xác thực người dùng (Authentication)

- **Mô tả:** Đăng ký, đăng nhập bằng Email/Mật khẩu và Google Sign-In. Quản lý phiên làm việc.
- **Thành phần chính:** `AuthViewModel.kt`, `AuthRepository.kt`, `LoginScreen.kt`,
  `SignUpScreen.kt`.
- **Trạng thái:** Hoàn thành (Done).

## 2. Bảng tin cộng đồng (Social Feed)

- **Mô tả:** Hiển thị danh sách bài viết từ cộng đồng, hỗ trợ tải dữ liệu từ nhiều bảng (Posts,
  Users, Products).
- **Thành phần chính:** `FeedViewModel.kt`, `FeedRepository.kt`, `FeedScreen.kt`, `PostItem.kt`.
- **Trạng thái:** Hoàn thành (Done).

## 3. Trợ lý ảo AI (AI Chatbot)

- **Mô tả:** Chatbot thông minh hỗ trợ giải đáp thắc mắc về sản phẩm và mua sắm sử dụng Gemini AI.
- **Thành phần chính:** `ChatbotViewModel.kt`, `ChatbotScreen.kt`, `ChatService.kt`.
- **Trạng thái:** Hoàn thành (Done).

## 4. Tạo bài viết & Kiểm duyệt (Post Creation & Moderation)

- **Mô tả:** Người dùng đăng bài kèm ảnh/video. Hệ thống tự động kiểm duyệt hình ảnh nhạy cảm trước
  khi đăng.
- **Thành phần chính:** `CreatePostScreen.kt`, `PostCreationViewModel.kt`, `ModerationService.kt`.
- **Trạng thái:** Đang phát triển (In-progress) - Đang hoàn thiện phần upload media.

## 5. Chi tiết bài viết & Bình luận (Post Detail & Comments)

- **Mô tả:** Xem chi tiết nội dung, sản phẩm gắn kèm và tương tác bình luận.
- **Thành phần chính:** `PostDetailScreen.kt`, `PostDetailViewModel.kt`, `CommentItem.kt`,
  `CommentInputField.kt`.
- **Trạng thái:** Đang phát triển (In-progress) - Cần hoàn thiện logic lấy danh sách bình luận thực
  tế.

## 6. Hồ sơ cá nhân (User Profile)

- **Mô tả:** Hiển thị thông tin cá nhân, các bài viết đã đăng và chỉnh sửa hồ sơ.
- **Thành phần chính:** `ProfileScreen.kt`, `EditProfileScreen.kt`, `UserRepository.kt`.
- **Trạng thái:** Hoàn thành (Done).

## 7. Gắn thẻ Sản phẩm (Product Tagging)

- **Mô tả:** Tìm kiếm và gắn thông tin sản phẩm trực tiếp vào bài đánh giá.
- **Thành phần chính:** `ProductHorizontalTag.kt`, `Product.kt`.
- **Trạng thái:** Cơ bản (Done).

## 8. Thông báo (Notifications)

- **Mô tả:** Nhận thông báo khi có tương tác mới (Like, Comment).
- **Thành phần chính:** `NotificationScreen.kt`, `Notification.kt`.
- **Trạng thái:** Kế hoạch (TODO).
