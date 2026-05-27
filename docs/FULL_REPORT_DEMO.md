# BÁO CÁO XÂY DỰNG HỆ THỐNG SMARTPICK - BẢN ĐẦY ĐỦ

---

## CHƯƠNG 1: ĐẶT VẤN ĐỀ

### 1.1. Lý do chọn đề tài
Trong bối cảnh thương mại điện tử bùng nổ, người dùng không chỉ cần một nơi để mua sắm mà còn cần một cộng đồng tin cậy để tham khảo ý kiến. **SmartPick** ra đời nhằm giải quyết bài toán kết nối giữa nội dung số (Review/Feed) và giao dịch mua sắm, giúp người dùng đưa ra quyết định mua hàng thông minh hơn.

### 1.2. Mục tiêu đề tài
- Phát triển ứng dụng Android sử dụng công nghệ mới nhất (Jetpack Compose, Kotlin Flow).
- Xây dựng hệ thống quản lý giỏ hàng, đơn hàng và bài viết yêu thích tích hợp.
- Tối ưu hóa trải nghiệm người dùng thông qua giao diện hiện đại và AI hỗ trợ.

### 1.3. Phạm vi nghiên cứu
- Thiết kế UI/UX theo chuẩn Material Design 3.
- Xử lý dữ liệu thời gian thực với Supabase.
- Quản lý trạng thái phức tạp với MVVM và Clean Architecture.

---

## CHƯƠNG 2: CÔNG NGHỆ VÀ THƯ VIỆN SỬ DỤNG

### 2.1. Nền tảng phát triển
- **Ngôn ngữ:** Kotlin (phiên bản 2.0.0 với K2 Compiler).
- **UI Framework:** Jetpack Compose (BOM 2024.09.00).
- **Kiến trúc:** MVVM kết hợp Clean Architecture.

### 2.2. Thư viện quan trọng (Dependencies)
Dựa trên `build.gradle.kts`, ứng dụng sử dụng các thư viện chiến lược:
- **Supabase (Auth, Postgrest, Storage, Realtime):** Giải pháp Backend-as-a-Service thay thế cho Firebase, giúp quản lý dữ liệu PostgreSQL và xác thực người dùng.
- **Dagger Hilt:** Quản lý Dependency Injection giúp mã nguồn dễ mở rộng và kiểm thử.
- **Coil & Media3 ExoPlayer:** Xử lý hiển thị hình ảnh và phát video mượt mà trong Feed bài viết.
- **Navigation Compose:** Quản lý luồng chuyển màn hình Type-safe.

### 2.3. Trí tuệ nhân tạo (AI)
- **Google Gemini 1.5 Flash:** Hỗ trợ tư vấn mua sắm và kiểm duyệt nội dung văn bản.
- **Sightengine:** Tự động nhận diện và chặn các hình ảnh không phù hợp.

---

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

---

## CHƯƠNG 4: ĐÁNH GIÁ VÀ KIỂM THỬ

### 4.1. Kịch bản kiểm thử (Test Cases)
Để đảm bảo ứng dụng vận hành ổn định, chúng tôi tập trung vào kiểm thử các chức năng cốt lõi.

#### 4.1.1. Kiểm thử chức năng Giỏ hàng (Cart Testing)
| Bước thực hiện | Kết quả mong đợi | Trạng thái |
| :--- | :--- | :--- |
| Nhấn nút "+" tại một sản phẩm | Số lượng tăng lên 1, tổng tiền cập nhật ngay lập tức. | Thành công |
| Nhấn nút "-" khi số lượng bằng 1 | Sản phẩm được hỏi để xóa hoặc tự động xóa khỏi giỏ. | Thành công |
| Nhấn "Thanh toán" | Chuyển hướng đúng sang màn hình Checkout với đủ dữ liệu. | Thành công |

#### 4.1.2. Kiểm thử luồng Bài viết đã thích (Saved Posts Testing)
- **Dữ liệu đầu vào:** Danh sách các bài viết đã được người dùng nhấn Like.
- **Hành động:** Truy cập tab "Bài viết đã thích" trong màn hình Saved.
- **Kết quả:** `isReactedLoading` hiển thị indicator, sau đó render đúng danh sách `reactedPosts` từ ViewModel.

### 4.2. Đánh giá hiệu năng và giao diện
- **Giao diện:** Sử dụng công cụ Preview của Jetpack Compose giúp kiểm tra hiển thị trên nhiều kích thước màn hình mà không cần chạy máy ảo.
- **Hiệu năng:** Việc sử dụng `collectAsState` giúp UI chỉ recompose khi dữ liệu thực sự thay đổi, giảm thiểu giật lag.
- **Tính năng AI:** Tốc độ phản hồi của Gemini 1.5 Flash đạt mức ổn định (dưới 2 giây cho một yêu cầu tư vấn).

---

## CHƯƠNG 5: KẾT LUẬN VÀ HƯỚNG PHÁT TRIỂN

### 5.1. Kết quả đạt được
- Hoàn thiện hệ thống quản lý mua sắm cá nhân hóa (Saved Screen).
- Tích hợp thành công luồng dữ liệu từ Supabase Realtime vào UI Compose.
- Đảm bảo tính nhất quán về dữ liệu giữa Giỏ hàng, Đơn hàng và Feed.

### 5.2. Hướng phát triển tiếp theo
- **Mở rộng tính năng Saved:** Cho phép người dùng tạo các thư mục lưu trữ tùy chỉnh (Collections).
- **Thanh toán trực tuyến:** Tích hợp SDK của VNPay hoặc Momo thay vì chỉ quản lý đơn hàng nội bộ.
- **Gợi ý thông minh:** Sử dụng AI để gợi ý các bài viết Review dựa trên lịch sử mua hàng.

---
**Người thực hiện:** Senior Android Engineer
**Dự án:** SmartPick 2024
