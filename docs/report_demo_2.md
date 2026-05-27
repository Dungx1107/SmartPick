# BÁO CÁO XÂY DỰNG ỨNG DỤNG SMARTPICK - PHẦN 2: PHÂN TÍCH VÀ THIẾT KẾ

## CHƯƠNG 3: PHÂN TÍCH YÊU CẦU VÀ THIẾT KẾ HỆ THỐNG

### 3.1. Phân tích yêu cầu người dùng (Use Case)
Dựa trên các tính năng đã triển khai, hệ thống tập trung vào các nhóm Use Case chính sau:

#### 3.1.1. Nhóm quản lý mua sắm (Shopping Management)
- **Thêm sản phẩm vào giỏ hàng:** Người dùng có thể chọn sản phẩm từ các bài viết Review.
- **Quản lý giỏ hàng:** Xem danh sách, tăng/giảm số lượng (Xử lý trong `CartViewModel`).
- **Thanh toán (Checkout):** Chuyển đổi từ giỏ hàng sang đơn hàng.

#### 3.1.2. Nhóm tương tác cộng đồng (Social Interaction)
- **Xem bảng tin (Feed):** Hiển thị bài viết kèm hình ảnh/video.
- **Yêu thích bài viết (Like/Reaction):** Lưu trạng thái tương tác vào Database.
- **Bộ sưu tập đã lưu (Saved Collection):** Truy cập nhanh các nội dung đã thích và lịch sử mua hàng.

### 3.2. Thiết kế luồng hoạt động (Activity Diagrams)

#### 3.2.1. Luồng Quản lý Bộ sưu tập (Saved Flow)
Luồng này được thể hiện rõ nét qua màn hình `SavedCollectionScreen`:
1. **Khởi tạo:** Màn hình nhận `initialCategory` (mặc định là "Giỏ hàng").
2. **Xử lý sự kiện:** 
   - Nếu chọn tab "Bài viết đã thích": `feedViewModel.loadReactedPosts()` được gọi.
   - Nếu chọn tab "Giỏ hàng": `cartViewModel.refreshCart()` được gọi.
3. **Hiển thị:** Sử dụng `LazyVerticalGrid` để render dữ liệu dựa trên trạng thái (State) từ ViewModel.

#### 3.2.2. Luồng Kiểm duyệt và Đăng bài (Moderation Flow)
- Người dùng tải lên Media -> Gửi đến API Sightengine (Hình ảnh) và Gemini AI (Văn bản).
- Nếu nội dung sạch -> Lưu vào Supabase Storage & Database.
- Nếu vi phạm -> Hiển thị thông báo từ chối.

### 3.3. Thiết kế Cơ sở dữ liệu (Database Design)
Hệ thống sử dụng PostgreSQL (Supabase) với mô hình quan hệ:
- **Bảng `posts`:** Lưu trữ nội dung bài viết, liên kết với `users` (tác giả).
- **Bảng `products`:** Chứa thông tin giá, kho, thương hiệu.
- **Bảng `cart_items`:** Lưu trạng thái tạm thời của giỏ hàng người dùng.
- **Bảng `reactions`:** Lưu mối quan hệ N-N giữa người dùng và bài viết (Like/Save).

### 3.4. Thiết kế Giao diện (UI Design)
- Áp dụng **Material Design 3** với hệ màu `SmartPickColor` và `AccentBlue`.
- Tối ưu hóa trải nghiệm vuốt chạm bằng `LazyColumn` và `LazyVerticalGrid`.
- Sử dụng `Card`, `Surface` để phân cấp nội dung rõ ràng.
