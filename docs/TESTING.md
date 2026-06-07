# Chiến lược & Kế hoạch Kiểm thử (Testing)

Dự án SmartPick áp dụng quy trình kiểm thử nghiêm ngặt để đảm bảo tính ổn định của hệ thống, đặc biệt là các tính năng tích hợp AI và luồng thanh toán.

## 1. Các cấp độ Kiểm thử

### 1.1. Unit Testing (Kiểm thử đơn vị)
- **Framework:** JUnit 4, MockK.
- **Mục tiêu:** Kiểm tra logic xử lý dữ liệu cô lập, các hàm chuyển đổi (Mappers) và các Service logic.
- **Trường hợp điển hình (`ModerationServiceTest`):**
    - Kiểm tra phản hồi khi AI trả về kết quả "SAFE" hoặc "TOXIC".
    - Xử lý các trường hợp ngoại lệ khi API của Gemini hoặc Sightengine gặp sự cố.
    - Đảm bảo các chuỗi ký tự rỗng không gây lỗi hệ thống.

### 1.2. Integration Testing (Kiểm thử tích hợp)
- **Framework:** Hilt Testing, Coroutines Test.
- **Mục tiêu:** Kiểm tra sự phối hợp giữa Repositories và Supabase.
- **Trường hợp điển hình:** Luồng đăng ký user mới -> tự động tạo profile trong bảng `users`.

### 1.3. UI Testing (Kiểm thử giao diện)
- **Công cụ:** Compose UI Test, Preview Screenshots.
- **Mục tiêu:** Đảm bảo giao diện hiển thị đúng trên các kích thước màn hình khác nhau và các tương tác của người dùng (Click, Scroll) hoạt động mượt mà.

## 2. Quy trình Kiểm thử thủ công (Manual Testing)

| Tính năng | Kịch bản kiểm thử | Trạng thái |
| :--- | :--- | :--- |
| **Auth** | Đăng nhập bằng Google và kiểm tra session duy trì sau khi tắt app. | Đạt |
| **Feed** | Đăng bài kèm Video, kiểm tra tính năng tự động phát (Autoplay). | Đạt |
| **AI Moderation** | Cố tình đăng nội dung thô tục để kiểm tra màng lọc Gemini AI. | Đạt |
| **Cart & Order** | Thêm sản phẩm, thanh toán và kiểm tra lịch sử đơn hàng. | Đạt |
| **Realtime** | Mở app trên 2 máy, thực hiện Like/Comment để xem tốc độ đồng bộ. | Đạt |

## 3. Các lưu ý quan trọng khi Test
- **API Keys:** Luôn đảm bảo `local.properties` có đủ keys hợp lệ trước khi chạy các bài test liên quan đến mạng.
- **Database:** Sử dụng môi trường staging hoặc database test để tránh làm ảnh hưởng đến dữ liệu thực của người dùng.
- **Edge Cases:** Đặc biệt chú trọng kiểm tra trường hợp mất kết nối mạng đột ngột khi đang upload file dung lượng lớn.

---
© 2024 SmartPick Quality Assurance Team.
