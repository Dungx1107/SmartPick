# Quy ước Lập trình (Coding Conventions)

Để đảm bảo mã nguồn SmartPick luôn sạch sẽ, dễ đọc và dễ bảo trì, đội ngũ phát triển tuân thủ các quy tắc sau:

## 1. Cấu trúc Thư mục (Package Structure)
Dự án được tổ chức theo tính năng (Feature-based structure):
- `core/`: Chứa các thành phần dùng chung (model, network, utils).
- `features/{feature_name}/`: Chứa mã nguồn của từng tính năng cụ thể.
    - `ui/`: Các Composable Screen và Components.
    - `viewmodel/`: Các ViewModel quản lý trạng thái.
    - `data/`: Repositories và Data Sources (DTOs).
- `navigation/`: Quản lý định tuyến và sơ đồ điều hướng.

## 2. Quy tắc Đặt tên (Naming Conventions)

### Lớp và Đối tượng (Classes & Objects)
- Sử dụng **PascalCase**.
- **ViewModels:** Phải kết thúc bằng `ViewModel` (Ví dụ: `AuthViewModel`).
- **Repositories:** Phải kết thúc bằng `Repository` (Ví dụ: `FeedRepository`).
- **Screens:** Phải kết thúc bằng `Screen` (Ví dụ: `LoginScreen`).
- **Data Models:** Đặt tên danh từ số ít (Ví dụ: `Post`, `User`).

### Hàm và Biến (Functions & Variables)
- Sử dụng **camelCase**.
- Hàm Composable: Phải là danh từ hoặc cụm danh từ mô tả UI (Ví dụ: `NotificationItem`).
- Hàm xử lý sự kiện: Bắt đầu bằng `on` (Ví dụ: `onLoginClick`).

## 3. Quy tắc Jetpack Compose (UI)
- **Stateless Composables:** Ưu tiên tách logic quản lý state ra khỏi UI. Truyền state vào và nhận sự kiện ra qua lambdas.
- **Preview:** Mỗi file UI nên có ít nhất một hàm `@Preview` để kiểm tra giao diện nhanh.
- **Modifier:** Tham số `modifier` phải là tham số đầu tiên của Composable và có giá trị mặc định là `Modifier`.
- **Remember:** Sử dụng `remember` và `rememberSaveable` đúng cách để tránh re-composition không cần thiết.

## 4. Xử lý Dữ liệu & Bất đồng bộ
- **Coroutines:** Luôn chỉ định rõ Dispatcher. Sử dụng `Dispatchers.IO` cho network/disk và `Dispatchers.Main` cho UI.
- **Flow:** Sử dụng `StateFlow` trong ViewModel để quan sát trạng thái UI. Sử dụng `collectAsStateWithLifecycle()` trong Compose.
- **Repository Pattern:** ViewModel không được gọi trực tiếp Supabase SDK, phải thông qua Repository.

## 5. Xử lý Lỗi (Error Handling)
- Sử dụng khối `try-catch` trong Repository để bắt các lỗi mạng/database.
- Trả về `Result<T>` hoặc giá trị mặc định (null/empty list) kèm theo log lỗi.
- Hiển thị thông báo lỗi thân thiện cho người dùng qua `Snackbar` hoặc `Toast`.

## 6. Clean Code Principles
- **DRY (Don't Repeat Yourself):** Tách các logic hoặc UI component dùng chung vào `core/ui` hoặc `core/utils`.
- **SOLID:** Đảm bảo mỗi lớp chỉ thực hiện một nhiệm vụ duy nhất.
- **KISS (Keep It Simple, Stupid):** Ưu tiên code dễ hiểu hơn code ngắn gọn nhưng phức tạp.

---
Việc tuân thủ các quy tắc này là bắt buộc đối với tất cả thành viên tham gia phát triển dự án.