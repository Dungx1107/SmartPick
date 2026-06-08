# Chương 4: Đánh giá và kiểm thử

Dựa trên tài liệu chiến lược kiểm thử thực tế (`TESTING.md`) và các bộ mã nguồn kiểm thử trong dự án, chương này trình bày quy trình, các kịch bản và kết quả đánh giá chất lượng hệ thống SmartPick.

## 4.1. Môi trường kiểm thử
- **Thiết bị:** Ứng dụng được kiểm thử trên các thiết bị Android thật (Pixel, Samsung) chạy Android 10 trở lên và Android Emulator (API 34).
- **Backend:** Dự án kết nối trực tiếp với dự án Supabase thực tế.
- **Công cụ:** Android Studio Logcat, Supabase Dashboard và các thư viện Mocking phục vụ Unit Test.

## 4.2. Chiến lược kiểm thử
Dự án áp dụng mô hình kiểm thử phân tầng, tập trung tối đa vào tính đúng đắn của logic nghiệp vụ tại tầng ViewModel và khả năng hoạt động của các dịch vụ bên thứ ba (AI).

### 4.2.1. Kiểm thử đơn vị (Unit Testing)
Đây là trọng tâm của quy trình kiểm soát chất lượng, sử dụng các công nghệ:
- **JUnit 4 & MockK:** Giả lập các Repository và Service để cô lập logic cần kiểm tra.
- **Turbine:** Thư viện chuyên dụng để xác minh các dữ liệu phát ra từ `StateFlow` hoặc `Flow`.
- **StandardTestDispatcher:** Kiểm soát thời gian và thứ tự thực thi của các tác vụ bất đồng bộ (Coroutines).

Các bộ test chính đã triển khai:
1.  **AuthViewModelTest:** Kiểm tra luồng đăng nhập/đăng ký, quản lý session người dùng (tự động cập nhật `currentUser`) và xử lý lỗi kết nối.
2.  **CartViewModelTest:** Xác minh tính toán tổng tiền, số lượng sản phẩm và ràng buộc tồn kho (chỉ cho phép tăng số lượng nếu `stock` đủ).
3.  **FeedViewModelTest:** Kiểm tra tính năng Like bài viết với cơ chế **Optimistic UI** (cập nhật UI ngay lập tức) và lọc dữ liệu bảng tin.
4.  **SellerViewModelTest:** Đảm bảo độ chính xác của các thuật toán tính doanh thu: `Doanh thu = Σ(số lượng * giá tại thời điểm mua)`.
5.  **ModerationServiceTest:** Kiểm duyệt màng lọc AI cho cả văn bản (Gemini) và hình ảnh (Sightengine), xử lý các trường hợp JSON sai định dạng hoặc thiếu trường dữ liệu.
6.  **Profile & EditProfile:** Kiểm tra luồng tải hồ sơ cá nhân và lưu thông tin cập nhật (bao gồm cả upload ảnh đại diện).

### 4.2.2. Kiểm thử thủ công (Manual Testing)
Được thực hiện để đánh giá trải nghiệm người dùng thực tế và các tính năng tương tác phần cứng:
- **Xác thực:** Đăng nhập bằng Google và kiểm tra session duy trì sau khi đóng ứng dụng.
- **Media:** Đăng bài kèm Video, kiểm tra tính năng tự động phát (Autoplay) trên bảng tin.
- **AI Moderation:** Cố tình đăng nội dung thô tục hoặc nhạy cảm để kiểm tra phản hồi của màng lọc AI.
- **Realtime:** Kiểm tra tốc độ đồng bộ Like/Comment trên hai thiết bị khác nhau (đảm bảo độ trễ < 1s).

## 4.3. Test Case tiêu biểu

| ID | Chức năng | Input/Kịch bản | Kết quả mong đợi | Trạng thái |
| :--- | :--- | :--- | :--- | :--- |
| TC01 | Xác thực | Đăng nhập bằng Google | Lưu session thành công, chuyển vào Home. | Đạt |
| TC02 | Kiểm duyệt AI | Đăng bài có từ ngữ thô tục | AI trả về `TOXIC`, chặn đăng và hiển thị cảnh báo. | Đạt |
| TC03 | Kiểm duyệt AI | Đăng ảnh nhạy cảm | Sightengine phát hiện rủi ro, chặn upload lên Storage. | Đạt |
| TC04 | Giỏ hàng | Tăng số lượng quá tồn kho | Hiển thị thông báo lỗi, không cho phép cập nhật. | Đạt |
| TC05 | Thanh toán | Đặt hàng thành công | Tạo đơn hàng, trừ kho, gửi thông báo cho Seller. | Đạt |
| TC06 | Realtime | Thích bài viết | Trạng thái nút Like cập nhật tức thì trên UI. | Đạt |

## 4.4. Đánh giá hệ thống

### 4.4.1. Ưu điểm
- **Kiến trúc ổn định:** Việc tách biệt logic giúp phát hiện lỗi sớm qua Unit Test, giảm thiểu bug ở tầng UI.
- **An toàn nội dung:** Màng lọc kép AI hoạt động hiệu quả, bảo vệ cộng đồng khỏi nội dung độc hại.
- **Hiệu năng mượt mà:** Sử dụng Flow và Optimistic UI giúp ứng dụng phản hồi nhanh chóng.

### 4.4.2. Hạn chế
- **Phụ thuộc Network:** Các bài test AI và Database yêu cầu kết nối mạng để thực thi chính xác.
- **Chi phí vận hành:** Việc sử dụng AI cho mọi bài đăng có thể gây tốn kém chi phí API khi quy mô lớn.

## 4.5. Kết luận chương
Hệ thống SmartPick đã vượt qua các giai đoạn kiểm thử nghiêm ngặt. Kết quả kiểm thử cho thấy ứng dụng đạt độ tin cậy cao về cả logic nghiệp vụ lẫn tính an toàn nội dung, sẵn sàng phục vụ người dùng cuối.
