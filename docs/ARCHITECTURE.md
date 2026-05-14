# Kiến trúc Hệ thống (Architecture)

Dự án SmartPick được xây dựng dựa trên kiến trúc **MVVM (Model-View-ViewModel)** kết hợp với các
nguyên tắc của **Clean Architecture** để đảm bảo tính dễ bảo trì, mở rộng và kiểm thử.

## 1. Các tầng kiến trúc (Layers)

### Tầng Presentation (Giao diện)

- **UI (Compose):** Sử dụng Jetpack Compose để xây dựng giao diện khai báo. Các file kết thúc bằng
  `Screen.kt` (ví dụ: `FeedScreen.kt`, `PostDetailScreen.kt`).
- **ViewModel:** Quản lý trạng thái UI và xử lý logic nghiệp vụ. Sử dụng `StateFlow` để phát dữ liệu
  tới UI. (ví dụ: `FeedViewModel.kt`).
- **UiState:** Mỗi màn hình có một data class định nghĩa trạng thái dữ liệu (Loading, Success,
  Error).

### Tầng Data (Dữ liệu)

- **Repository:** Đóng vai trò là nguồn dữ liệu duy nhất cho ViewModel. Repository quyết định lấy dữ
  liệu từ local hay remote (Supabase).
- **DTO (Data Transfer Objects):** Các lớp đại diện cho cấu trúc dữ liệu trả về từ API (ví dụ:
  `FullPostResponse.kt`).
- **Models:** Các Entity cốt lõi dùng trong toàn bộ ứng dụng (`Post.kt`, `User.kt`, `Product.kt`).

## 2. Dependency Injection (DI)

Dự án sử dụng **Hilt (Dagger)** để quản lý phụ thuộc.

- **Application Class:** `SmartPickApp.kt` được đánh dấu với `@HiltAndroidApp`.
- **Modules:** Các module cung cấp các instance như `SupabaseClient`.

## 3. Cấu trúc Module & Package

Dự án hiện tại là một module duy nhất (`:app`), nhưng được tổ chức theo tính năng (**Feature-based
structure**):

- `com.example.smartpick.core`: Chứa các thành phần dùng chung (network, ui components, models).
- `com.example.smartpick.features`: Mỗi thư mục con là một tính năng độc lập (auth, feed, chatbot,
  post_detail...).
    - `data/`: Repository và DTO.
    - `viewmodel/`: ViewModel và UI State.
    - `ui/`: Các Composable Screens và Components.

## 4. Luồng dữ liệu (Data Flow)

1. **User Interaction:** Người dùng tương tác với UI.
2. **ViewModel Action:** UI gọi một function trong ViewModel.
3. **Repository Fetch:** ViewModel gọi Repository để lấy dữ liệu.
4. **Data Source:** Repository gọi Supabase API.
5. **UI Update:** Dữ liệu trả về qua Flow, ViewModel cập nhật UiState, UI tự động vẽ lại.
