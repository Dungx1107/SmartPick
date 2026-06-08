# Chiến lược & Kế hoạch Kiểm thử (Testing) - SmartPick Project

Tài liệu này mô tả chi tiết quy trình kiểm thử của dự án SmartPick, đảm bảo tính ổn định của hệ thống, độ chính xác của logic nghiệp vụ và tính an toàn của nội dung thông qua AI.

## 1. Kiểm thử đơn vị (Unit Testing)

Chúng tôi tập trung kiểm thử logic tại tầng **ViewModel** và **Service** để đảm bảo dữ liệu được xử lý chính xác, xử lý lỗi tốt và tối ưu trải nghiệm người dùng trước khi hiển thị lên giao diện.

### 1.1. Công cụ & Framework chủ đạo
- **JUnit 4**: Framework thực thi kiểm thử tiêu chuẩn.
- **MockK**: Thư viện Mocking mạnh mẽ để giả lập các Dependencies (Repositories, APIs).
- **Kotlinx-Coroutines-Test**: Sử dụng `StandardTestDispatcher` để kiểm soát Dispatchers và thời gian chạy Coroutine.
- **Turbine**: Thư viện chuyên dụng để kiểm thử các luồng dữ liệu `Flow`, `StateFlow` và `SharedFlow`.

### 1.2. Chi tiết các bộ kiểm thử đã triển khai

#### A. Xác thực và Phân quyền (`AuthViewModelTest`)
- **Quản lý Session:** Kiểm tra việc tự động cập nhật `currentUser` và trạng thái khởi tạo (`isInitializing`) khi nhận tín hiệu từ Supabase.
- **Luồng Đăng nhập (Sign In):** 
    - Xác minh chuỗi trạng thái: `Idle -> Loading -> Success`.
    - Xử lý lỗi: Kiểm tra thông báo lỗi khi sai mật khẩu hoặc lỗi kết nối.
    - Validation: Chặn các trường hợp để trống thông tin ngay tại ViewModel để tránh gọi API lãng phí.
- **Đăng ký (Sign Up):** Kiểm tra logic kiểm tra trùng lặp Email/Username trước khi tiến hành tạo tài khoản.

#### B. Giỏ hàng & Logic Kho hàng (`CartViewModelTest`)
- **Tính toán Real-time:** Sử dụng `Turbine` để xác minh `totalCartCount` cập nhật chính xác mỗi khi danh sách sản phẩm trong giỏ hàng thay đổi.
- **Ràng buộc tồn kho (Stock Validation):** 
    - Kiểm tra logic tăng số lượng: Chỉ cho phép cập nhật nếu còn đủ hàng trong kho (`stock`). Phát lỗi nếu vượt quá.
- **Quản lý lựa chọn:** Kiểm tra tính năng chọn từng món (`toggleSelection`) và chọn tất cả (`selectAll`).

#### C. Bảng tin (Feed) & Tương tác xã hội (`FeedViewModelTest`)
- **Optimistic UI (Cập nhật tức thì):** Khi người dùng Reaction (Like), UI sẽ cập nhật trạng thái ngay lập tức trước khi API phản hồi để tạo trải nghiệm mượt mà.
- **Tải dữ liệu:** Lọc bỏ các bài viết không hợp lệ và xử lý các trạng thái lỗi mạng (`FeedUiState.Error`).
- **Quản lý nội dung:** Kiểm tra các tính năng Chia sẻ bài viết (Share Post) và Xóa bài viết (Delete Post).

#### D. Dashboard Người bán (`SellerViewModelTest`)
- **Độ chính xác tài chính:** Kiểm tra thuật toán tính tổng doanh thu: `Doanh thu = Σ(số lượng * giá tại thời điểm mua)`.
- **Thống kê sản lượng:** Xác minh tính chính xác của tổng số đơn hàng và tổng số lượng sản phẩm vật lý đã xuất kho.

#### E. Hồ sơ & Chỉnh sửa thông tin (`ProfileViewModelTest`, `EditProfileViewModelTest`)
- **Hiển thị hồ sơ:** Kiểm tra tải danh sách bài viết cá nhân và danh sách sản phẩm đã bán.
- **Chỉnh sửa:** Kiểm tra luồng lưu Profile khi thay đổi ảnh đại diện (upload lên storage) hoặc chỉ thay đổi thông tin văn bản.

#### F. Kiểm duyệt nội dung AI (`ModerationServiceTest`)
- **Văn bản (Gemini API):** Xử lý các trường hợp chuỗi rỗng, khoảng trắng và lỗi định dạng JSON.
- **Hình ảnh (Sightengine API):** Kiểm tra logic chấm điểm độ an toàn (Nudity, Weapon, Violence). Chặn hình ảnh nếu thiếu dữ liệu điểm số.

## 2. Quy trình Kiểm thử thủ công (Manual Testing)

| Tính năng | Kịch bản kiểm thử | Trạng thái |
| :--- | :--- | :--- |
| **Auth** | Đăng nhập bằng Google và kiểm tra session duy trì sau khi đóng app. | Đạt |
| **Feed** | Đăng bài kèm Video, kiểm tra tính năng tự động phát (Autoplay). | Đạt |
| **AI Filter** | Cố tình đăng nội dung thô tục/nhạy cảm để kiểm tra màng lọc AI. | Đạt |
| **Cart & Order** | Thêm sản phẩm, thanh toán và kiểm tra lịch sử đơn hàng. | Đạt |
| **Realtime** | Kiểm tra tốc độ đồng bộ Like/Comment trên hai thiết bị (< 1s). | Đạt |

## 3. Lưu ý quan trọng cho Lập trình viên
- **Dispatcher Control:** Luôn sử dụng `setMain` và `resetMain` trong `@Before` và `@After` để kiểm soát mã không đồng bộ.
- **Flow Testing:** Ưu tiên sử dụng thư viện `Turbine` để "await" các dữ liệu phát ra từ Flow thay vì sử dụng `delay()` thủ công.
- **API Keys:** Đảm bảo file `local.properties` chứa đúng keys (Gemini, Sightengine, Supabase) trước khi chạy các bài test tích hợp.

---
© 2024 SmartPick Quality Assurance Team.
Cập nhật lần cuối: 24/05/2024.
