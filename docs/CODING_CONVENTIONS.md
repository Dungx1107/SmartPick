# Quy ước Lập trình (Coding Conventions) - SmartPick Project

Tài liệu này định nghĩa các tiêu chuẩn mã nguồn nhằm duy trì tính nhất quán, dễ đọc và dễ bảo trì cho dự án SmartPick.

## 1. Nguyên tắc Chung
- **Clean Architecture:** Phân tách rõ ràng giữa Presentation, Domain và Data.
- **UDF (Unidirectional Data Flow):** Trạng thái đi xuống, sự kiện đi lên.
- **SOLID & KISS:** Giữ cho mã nguồn đơn giản, trách nhiệm duy nhất.

## 2. Quy tắc Đặt tên

### 2.1. Lớp và Đối tượng (PascalCase)
- **ViewModel:** Phải kết thúc bằng `ViewModel` (ví dụ: `FeedViewModel`).
- **Repository:** Phải kết thúc bằng `Repository` (ví dụ: `AuthRepository`).
- **Screen:** Các Composable cấp màn hình kết thúc bằng `Screen` (ví dụ: `CartScreen`).
- **Component:** Các Composable nhỏ kết thúc bằng tên linh kiện (ví dụ: `PostItem`, `StandardButton`).
- **DTO:** Các lớp dữ liệu từ API kết thúc bằng `Dto` (ví dụ: `ProductDto`).

### 2.2. Hàm và Biến (camelCase)
- **Hàm Composable:** Phải là Danh từ (ví dụ: `ProductCard`).
- **Hàm xử lý sự kiện:** Bắt đầu bằng `on` (ví dụ: `onAddToCartClick`).
- **Biến trạng thái:** Nếu là Flow/MutableState, có thể dùng tiền tố `_` cho bản private (ví dụ: `_uiState`).

## 3. Kiến trúc Thư mục (Feature-based)
Dự án được tổ chức theo tính năng thay vì theo loại lớp:
- `com.example.smartpick.features.{feature_name}`
    - `ui/`: Chứa Screen và Components.
    - `viewmodel/`: Chứa ViewModel.
    - `data/`: Chứa Repository, DTOs và Mappers.
    - `model/`: Chứa Domain models (nếu cần riêng biệt).

## 4. Jetpack Compose Guidelines
- **Modifier:** Luôn là tham số đầu tiên của Composable (sau tham số bắt buộc) và có giá trị mặc định `Modifier`.
- **State Hoisting:** Đẩy trạng thái lên cấp cao nhất có thể để linh kiện dễ tái sử dụng.
- **Preview:** Cung cấp `@Preview` cho mọi linh kiện UI với dữ liệu mẫu (Mock data).
- **Theme:** Sử dụng `MaterialTheme.colorScheme` và `MaterialTheme.typography` thay vì mã màu/font cứng.

## 5. Xử lý Dữ liệu & Mạng
- **Coroutines:** Sử dụng `viewModelScope` cho các tác vụ trong ViewModel. Luôn chỉ định `Dispatchers.IO` cho Network/Database.
- **Flow:** Ưu tiên sử dụng `StateFlow` để quan sát trạng thái UI.
- **Exception Handling:** Xử lý lỗi tại tầng Repository và trả về `Result<T>` hoặc đóng gói lỗi vào UI State để hiển thị thông báo.

## 6. Resources & Assets
- **Strings:** Mọi chuỗi văn bản hiển thị phải đặt trong `strings.xml`.
- **Icons:** Ưu tiên sử dụng `Icons.Rounded` hoặc `Icons.Filled` từ thư viện Material Icons.
- **Dimensions:** Sử dụng các giá trị chuẩn (8dp, 16dp) để đảm bảo tính nhất quán của lưới (Grid).

---
© 2024 SmartPick Development Team.
