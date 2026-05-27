# BÁO CÁO XÂY DỰNG ỨNG DỤNG SMARTPICK - PHẦN 3: KIỂM THỬ VÀ ĐÁNH GIÁ

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
- **Giao diện:** Sử dụng công cụ Preview của Jetpack Compose (như đã demo trong `SavedCollectionContentReactedPreview`) giúp kiểm tra hiển thị trên nhiều kích thước màn hình mà không cần chạy máy ảo.
- **Hiệu năng:** Việc sử dụng `collectAsState` giúp UI chỉ recompose khi dữ liệu thực sự thay đổi, giảm thiểu giật lag.
- **Tính năng AI:** Tốc độ phản hồi của Gemini 1.5 Flash đạt mức ổn định (dưới 2 giây cho một yêu cầu tư vấn).

## CHƯƠNG 5: KẾT LUẬN VÀ HƯỚNG PHÁT TRIỂN

### 5.1. Kết quả đạt được
- Hoàn thiện hệ thống quản lý mua sắm cá nhân hóa (Saved Screen).
- Tích hợp thành công luồng dữ liệu từ Supabase Realtime vào UI Compose.
- Đảm bảo tính nhất quán về dữ liệu giữa Giỏ hàng, Đơn hàng và Feed.

### 5.2. Hướng phát triển tiếp theo
- **Mở rộng tính năng Saved:** Cho phép người dùng tạo các thư mục lưu trữ tùy chỉnh (Collections).
- **Thanh toán trực tuyến:** Tích hợp SDK của VNPay hoặc Momo thay vì chỉ quản lý đơn hàng nội bộ.
- **Gợi ý thông minh:** Sử dụng AI để gợi ý các bài viết Review dựa trên lịch sử mua hàng trong tab "Lịch sử mua hàng".

---
*Báo cáo được trích xuất từ tài liệu kỹ thuật dự án SmartPick.*
