# Quy ước Lập trình (Coding Conventions)

Tài liệu này trình bày các quy ước về đặt tên, cấu trúc thư mục và cách tổ chức mã nguồn được áp
dụng thống nhất trong dự án SmartPick.

## 1. Quy ước Đặt tên (Naming Conventions)

### Lớp và Đối tượng (Classes & Objects)

- **PascalCase:** Áp dụng cho tất cả tên lớp, đối tượng và giao diện.
- **Suffix (Hậu tố):**
    - **Screens:** Kết thúc bằng `Screen` (ví dụ: `FeedScreen.kt`, `LoginScreen.kt`).
    - **ViewModels:** Kết thúc bằng `ViewModel` (ví dụ: `FeedViewModel.kt`).
    - **Repositories:** Kết thúc bằng `Repository` (ví dụ: `FeedRepository.kt`).
    - **Data Transfer Objects:** Kết thúc bằng `Response` (ví dụ: `FullPostResponse.kt`).
    - **UI State:** Kết thúc bằng `UiState` (ví dụ: `FeedUiState`).

### Hàm và Biến (Functions & Variables)

- **camelCase:** Áp dụng cho tên hàm, biến và tham số.
- **Composables:** Phải sử dụng **PascalCase** (ví dụ: `PostItem`, `CommentInputField`).
- **Private Variables:** Biến private trong ViewModel bắt đầu bằng dấu gạch dưới (ví dụ:
  `_uiState`).

## 2. Cấu trúc Thư mục (Folder Structure)

Dự án tổ chức theo tính năng (**Feature-based structure**). Mỗi tính năng nằm trong một thư mục con
của `features/` với cấu trúc chuẩn:

```
feature_name/
├── data/           # Repositories, DTOs, API Interfaces
├── ui/             # Composables, Screens
│   └── components/ # Các UI components nhỏ dùng riêng cho feature
└── viewmodel/      # ViewModel và UiState định nghĩa
```

## 3. Tổ chức ViewModel & UI State

### Quản lý Trạng thái (State Management)

- Sử dụng `StateFlow` hoặc `MutableStateFlow` để quản lý trạng thái trong ViewModel.
- Luôn có một `UiState` đại diện cho toàn bộ dữ liệu hiển thị trên màn hình.

**Mẫu triển khai:**

```kotlin
// Định nghĩa State
sealed class FeedUiState {
    object Loading : FeedUiState()
    data class Success(val data: List<Post>) : FeedUiState()
    data class Error(val message: String) : FeedUiState()
}

// Trong ViewModel
private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
val uiState = _uiState.asStateFlow()
```

### Xử lý Sự kiện (Event Handling)

- ViewModel không chứa các logic liên quan đến UI (như Toast, Navigation trực tiếp).
- Các sự kiện từ UI được truyền qua các callback (ví dụ: `onPostClick: (String) -> Unit`).

## 4. Quy tắc Jetpack Compose

- **Stateless Composables:** Ưu tiên tách logic UI thành các hàm stateless (không giữ trạng thái nội
  bộ) để dễ dàng preview và test.
- **Preview:** Mỗi file Composable lớn nên có ít nhất một hàm `@Preview` với dữ liệu mẫu (Mock
  data).
- **Modifier:** Tham số `modifier: Modifier = Modifier` luôn là tham số đầu tiên của các Composable
  function để linh hoạt trong việc căn chỉnh từ bên ngoài.

## 5. Dependency Injection (Hilt)

- Luôn sử dụng `@Inject constructor` để cung cấp phụ thuộc cho Repository và ViewModel.
- Các module cung cấp instance (như `SupabaseClient`) phải được định nghĩa rõ ràng trong package
  `core/network` hoặc `core/di`.
