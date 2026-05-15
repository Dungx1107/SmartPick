# Kiến trúc Dự án SmartPick

Tài liệu này mô tả chi tiết kiến trúc kỹ thuật của ứng dụng SmartPick, dựa trên các tiêu chuẩn phát triển Android hiện đại.

## 1. Tổng quan Kiến trúc

SmartPick áp dụng mô hình **Clean Architecture** kết hợp với **MVVM (Model-View-ViewModel)**. Sự kết hợp này giúp tách biệt rõ ràng giữa logic giao diện, logic nghiệp vụ và dữ liệu, giúp dự án dễ bảo trì và kiểm thử.

### Các lớp chính:
- **UI Layer (Presentation):** Sử dụng Jetpack Compose để xây dựng giao diện khai báo. ViewModels quản lý trạng thái UI và tương tác với các Repositories.
- **Domain/Data Layer:** Chứa logic nghiệp vụ và Repositories. Repositories đóng vai trò là "Single Source of Truth", điều phối dữ liệu từ Supabase.
- **Core Layer:** Chứa các thành phần dùng chung như mạng (Supabase Client), các Models dữ liệu, và các tiện ích (Utils).

## 2. Luồng Dữ liệu (Data Flow)

Ứng dụng tuân thủ luồng dữ liệu một chiều (Unidirectional Data Flow):
1. **User Action:** Người dùng tương tác với UI (vd: nhấn nút Like).
2. **ViewModel:** Nhận sự kiện, gọi hàm tương ứng trong Repository.
3. **Repository:** Thao tác với Supabase API thông qua các module của Supabase (Postgrest, Auth, Storage).
4. **Data Source:** Supabase xử lý và trả về kết quả.
5. **State Update:** Repository trả dữ liệu về ViewModel. ViewModel cập nhật `StateFlow`.
6. **UI Update:** Compose tự động Re-compose khi trạng thái thay đổi.

## 3. Dependency Injection (DI)

Dự án sử dụng **Dagger Hilt** để quản lý phụ thuộc.
- **NetworkModule:** Cung cấp các instance duy nhất của `SupabaseClient` và `OkHttpClient`.
- **Repository Injection:** Các Repositories được đánh dấu `@Singleton` và inject vào ViewModels.
- **Hilt ViewModel:** Sử dụng `hiltViewModel()` trong Compose để lấy instance của ViewModel đã được inject.

## 4. Xử lý Bất đồng bộ (Async Handling)

Sử dụng **Kotlin Coroutines** và **Flow**:
- **Coroutines:** Xử lý các tác vụ mạng và database trên `Dispatchers.IO`.
- **Flow/StateFlow:** Truyền dữ liệu từ tầng Data lên UI một cách reactive.
- **Concurrent Upload:** Trong tính năng tạo bài viết, ứng dụng sử dụng `async`/`awaitAll` để upload nhiều ảnh/video cùng lúc, tối ưu hóa thời gian chờ.

## 5. Tích hợp Backend (Supabase)

Supabase được sử dụng làm Backend chính với các module:
- **GoTrue (Auth):** Quản lý đăng nhập Email và Google Sign-In.
- **Postgrest (Database):** Thao tác dữ liệu qua cơ chế RESTful tự động.
- **Storage:** Lưu trữ file media (ảnh/video) trong các buckets.
- **Realtime:** Lắng nghe các thay đổi dữ liệu (như thông báo mới) theo thời gian thực.

## 6. Cơ chế Kiểm duyệt Nội dung (Moderation)

Đây là một điểm đặc biệt trong kiến trúc của SmartPick:
- **ModerationService:** Một service độc lập thực hiện kiểm tra nội dung trước khi cho phép đăng tải.
- **Text Moderation:** Sử dụng Gemini API để phân tích ngôn ngữ độc hại.
- **Image Moderation:** Sử dụng Sightengine API để phát hiện nội dung nhạy cảm, bạo lực.

## 7. Điều hướng (Navigation)

Sử dụng **Jetpack Compose Navigation**:
- Định nghĩa các Route trong `Routes.kt`.
- `AppNavigation` quản lý `NavHost` và logic chuyển màn hình (như tự động chuyển về Login khi logout).
- Hỗ trợ truyền tham số phức tạp (Post ID, User ID) giữa các màn hình.

---
Kiến trúc này đảm bảo SmartPick có thể mở rộng dễ dàng (Scalability) và duy trì sự ổn định cao khi thêm các tính năng mới trong tương lai.