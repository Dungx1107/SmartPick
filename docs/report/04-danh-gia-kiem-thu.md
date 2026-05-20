# Chương 4: Đánh giá và Kiểm thử

## 4.1. Các trường hợp kiểm thử chính (Test Cases)

### 4.1.1. Kiểm thử chức năng (Manual Testing)
| STT | Chức năng | Kịch bản | Kết quả mong đợi | Trạng thái |
|:---|:---|:---|:---|:---|
| 1 | Auth | Đăng ký với email đã tồn tại | Thông báo lỗi "Email already exists" | Pass |
| 2 | Feed | Vuốt để làm mới (Swipe to refresh) | Dữ liệu bài viết mới nhất được cập nhật | Pass |
| 3 | AI Moderation | Đăng bài có chứa từ ngữ thô tục | Chặn đăng bài, hiển thị cảnh báo từ AI | Pass |
| 4 | AI Moderation | Đăng bài có ảnh nhạy cảm | Chặn upload, thông báo ảnh không phù hợp | Pass |
| 5 | Cart | Thêm sản phẩm cùng loại nhiều lần | Số lượng (quantity) trong giỏ tăng lên | Pass |
| 6 | Checkout | Thanh toán khi giỏ hàng trống | Nút thanh toán bị vô hiệu hóa hoặc báo lỗi | Pass |
| 7 | Review | Đánh giá sản phẩm chưa mua | Hệ thống không hiển thị form đánh giá | Pass |

### 4.1.2. Unit Testing
Sử dụng MockK để giả lập các phản hồi từ API:
- **ModerationServiceTest:** Kiểm tra logic phân tách kết quả từ Gemini và Sightengine.
- **MapperTest:** Đảm bảo dữ liệu từ DTO sang Domain Model không bị mất mát thông tin.

## 4.2. Đánh giá hiệu năng và UX

- **Hiệu năng:** Ứng dụng phản hồi nhanh nhờ vào Ktor client và Coroutines. Việc sử dụng `ImageRequest` của Coil giúp tối ưu bộ nhớ khi hiển thị danh sách dài.
- **Trải nghiệm người dùng (UX):** Giao diện Material Design 3 mang lại cảm giác hiện đại. Các hiệu ứng chuyển cảnh của Compose mượt mà. Tuy nhiên, thời gian chờ AI kiểm duyệt có thể mất từ 1-3 giây tùy vào tốc độ mạng.

## 4.3. Các lỗi đã gặp và cách xử lý

1.  **Lỗi đồng bộ Realtime:** Ban đầu thông báo không tự cập nhật. 
    *   *Cách xử lý:* Chuyển sang sử dụng `postgres_changes` filter trong Supabase Realtime SDK.
2.  **Lỗi định dạng thời gian:** Database trả về UTC nhưng UI hiển thị sai múi giờ.
    *   *Cách xử lý:* Sử dụng thư viện `kotlinx-datetime` để convert sang Local TimeZone.
3.  **Xung đột thư viện Hilt trong Test:** 
    *   *Cách xử lý:* Cấu hình lại `HiltTestRunner` và sử dụng `kspAndroidTest`.

## 4.4. Hạn chế hiện tại
- Chưa có tính năng chat Realtime giữa người dùng với nhau (P2P Chat).
- Hệ thống thanh toán chỉ dừng lại ở mức mô phỏng (COD/Bank Transfer manual).
- AI chatbot đôi khi vẫn có hiện tượng "ảo giác" (hallucination) nếu dữ liệu sản phẩm quá ít.

## 4.5. Hướng phát triển tương lai
- Tích hợp thanh toán qua ZaloPay/Momo.
- Xây dựng hệ thống gợi ý (Recommendation System) dựa trên Collaborative Filtering.
- Hỗ trợ đa ngôn ngữ và chế độ Dark Mode hoàn thiện hơn.
