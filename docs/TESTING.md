# Chiến lược & Kế hoạch Kiểm thử (Testing)

Tài liệu này trình bày cách thức đảm bảo chất lượng phần mềm cho dự án SmartPick thông qua các cấp
độ kiểm thử khác nhau.

## 1. Chiến lược Kiểm thử

Dự án áp dụng mô hình Kim tự tháp Kiểm thử (Testing Pyramid):

- **Unit Tests:** Tập trung vào logic nghiệp vụ và xử lý dữ liệu cô lập (như Moderation Service).
- **Integration Tests:** Đảm bảo sự phối hợp giữa ứng dụng và các service bên ngoài (Supabase, API).
- **Manual UI Testing:** Kiểm tra trải nghiệm người dùng thực tế trên thiết bị.

## 2. Unit Testing (Kiểm thử đơn vị)

Sử dụng **JUnit 4** và **MockK**.

### Các trường hợp đã thực hiện (Ví dụ: ModerationServiceTest):

- **Xử lý chuỗi rỗng:** Đảm bảo hệ thống không gọi API khi text input trống.
- **Xử lý JSON lỗi:** Giả lập trường hợp API trả về dữ liệu không đúng định dạng.
- **Xử lý thiếu field:** Đảm bảo hệ thống từ chối (trả về `false`) khi dữ liệu trả về từ Sightengine
  bị thiếu các chỉ số quan trọng để đảm bảo an toàn tối đa.

## 3. Integration Testing (Kiểm thử tích hợp)

Tập trung vào việc kết nối với các hệ thống Backend thật.

- **Connectivity Check:** Kiểm tra tính hợp lệ của API Key (Gemini, Sightengine) và khả năng kết nối
  mạng.
- **Supabase Interaction:** Kiểm tra luồng đăng ký, đăng nhập và lưu trữ dữ liệu thực tế trên
  database.

## 4. Manual UI Testing (Kiểm thử thủ công)

Thực hiện trên thiết bị thật (Android 13+) và Emulator:

| Chức năng         | Kịch bản kiểm thử             | Kết quả mong đợi                                         |
|:------------------|:------------------------------|:---------------------------------------------------------|
| **Auth**          | Đăng nhập bằng Google lần đầu | Tự động tạo user trong DB và gửi email chào mừng.        |
| **Post Creation** | Đăng bài có kèm ảnh nhạy cảm  | Hệ thống Sightengine nhận diện và chặn đăng bài.         |
| **Post Creation** | Đăng bài có nội dung chửi thề | Gemini AI nhận diện và yêu cầu chỉnh sửa nội dung.       |
| **Feed**          | Cuộn trang (Infinite scroll)  | Dữ liệu được load mượt mà, không giật lag.               |
| **Comments**      | Trả lời một bình luận (Reply) | Bình luận xuất hiện đúng cấp bậc (tầng 2).               |
| **Realtime**      | Một user khác like bài viết   | Nhận được thông báo tức thì mà không cần load lại trang. |

## 5. Các ca kiểm thử biên & Edge Cases

- **Mất kết nối mạng:** Ứng dụng phải hiển thị thông báo lỗi thân thiện thay vì crash.
- **File media quá lớn:** Xử lý timeout khi upload video dung lượng cao.
- **Token hết hạn:** Tự động điều hướng người dùng về màn hình Login khi session kết thúc.

## 6. Công cụ hỗ trợ

- **Logcat:** Theo dõi log hệ thống và lỗi runtime.
- **Supabase Dashboard:** Kiểm tra dữ liệu thực tế được lưu vào bảng.
- **Sightengine Playground:** Kiểm tra độ nhạy của thuật toán nhận diện ảnh.

---
Hệ thống kiểm thử giúp đảm bảo SmartPick hoạt động ổn định và an toàn trước khi đến tay người dùng
cuối.