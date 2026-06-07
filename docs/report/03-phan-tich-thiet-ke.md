# Chương 3: Phân tích và Thiết kế hệ thống

## 3.1. Phân tích yêu cầu

### 3.1.1. Yêu cầu chức năng
- **Người dùng:** Đăng ký, đăng nhập (Email/Google), cập nhật hồ sơ, quản lý tài khoản.
- **Cộng đồng:** Đăng bài viết chia sẻ (hỗ trợ nhiều ảnh/video), thích bài viết, bình luận đa tầng, xem thông báo tương tác.
- **Thương mại điện tử:** Tìm kiếm sản phẩm, xem chi tiết, quản lý giỏ hàng, thanh toán đơn hàng (Checkout), xem lịch sử mua sắm và đánh giá sản phẩm (Reviews).
- **AI & An toàn:** Tư vấn mua sắm với AI Curator, tự động kiểm duyệt hình ảnh và văn bản độc hại.

### 3.1.2. Yêu cầu phi chức năng
- **Tính khả dụng:** Giao diện trực quan, mượt mà, tuân thủ Material Design 3.
- **Hiệu năng:** Tải dữ liệu và media nhanh, phản hồi AI dưới 3 giây.
- **Bảo mật:** Phân quyền dữ liệu (RLS) trên Supabase, xác thực an toàn.

## 3.2. Sơ đồ Use Case

Hệ thống bao gồm các tác nhân chính là Người dùng và các API AI (Gemini, Sightengine). Các chức năng chính được phân bổ qua các module: Auth, Feed, Home, Cart, Review, Chatbot và Notification.

## 3.3. Thiết kế Cơ sở dữ liệu (Database Schema)

Dựa trên PostgreSQL của Supabase, hệ thống gồm các thực thể chính:
- `users`: Thông tin định danh và profile.
- `products`: Thông tin sản phẩm, giá, người bán.
- `posts`: Nội dung review, liên kết media và sản phẩm.
- `comments`: Lưu trữ thảo luận đa tầng.
- `cart_items`: Quản lý trạng thái giỏ hàng.
- `orders` & `order_items`: Lưu trữ lịch sử giao dịch.
- `reviews`: Đánh giá tin cậy từ người mua hàng.
- `notifications`: Thông báo thời gian thực.

## 3.4. Thiết kế Điều hướng (Navigation Design)

Sơ đồ điều hướng được xây dựng trên **Jetpack Compose Navigation** với các Route chính:
- **Nhóm Chính (Bottom Bar):** Home, Feed, Review Hub, Saved, Profile.
- **Nhóm Chức năng:** Product Detail, Post Detail, Create Post, Cart, Checkout, Notifications, Settings.
- **Nhóm Bổ trợ:** Write Review, Edit Profile, Seller Dashboard.

## 3.5. Luồng xử lý AI Moderation

Mọi nội dung bài đăng (văn bản và hình ảnh) đều đi qua `ModerationService` trước khi được lưu:
1. **Text:** Gửi prompt tới Gemini 1.5 Flash để phân loại (SAFE/TOXIC).
2. **Image:** Gửi URL/File tới Sightengine để kiểm tra các chỉ số (Nudity, Violence, Weapon).
3. **Kết quả:** Chỉ khi cả hai đều trả về kết quả an toàn, bài viết mới được khởi tạo trên hệ thống.
