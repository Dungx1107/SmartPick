# Kiến trúc Dự án SmartPick

Tài liệu này mô tả chi tiết kiến trúc kỹ thuật của ứng dụng SmartPick, dựa trên các tiêu chuẩn phát triển Android hiện đại.

## 1. Tổng quan Kiến trúc

SmartPick áp dụng mô hình **Clean Architecture** kết hợp với **MVVM (Model-View-ViewModel)**. Sự kết hợp này giúp tách biệt rõ ràng giữa logic giao diện, logic nghiệp vụ và dữ liệu, giúp dự án dễ bảo trì và kiểm thử.

### Các lớp chính:
- **UI Layer (Presentation):** Sử dụng Jetpack Compose để xây dựng giao diện khai báo. ViewModels quản lý trạng thái UI và tương tác với các Repositories.
- **Domain/Data Layer:** Chứa logic nghiệp vụ, các Data Transfer Objects (DTO), Mappers và Repositories. Repositories đóng vai trò là "Single Source of Truth", điều phối dữ liệu từ Supabase.
- **Core Layer:** Chứa các thành phần dùng chung như mạng (Supabase Client, Moderation Service), các Models dữ liệu toàn cục, và các tiện ích (Utils).

## 2. Luồng Dữ liệu (Data Flow)

Ứng dụng tuân thủ luồng dữ liệu một chiều (Unidirectional Data Flow):
1. **User Action:** Người dùng tương tác với UI (vd: thêm vào giỏ hàng).
2. **ViewModel:** Nhận sự kiện, gọi hàm tương ứng trong Repository.
3. **Repository:** Thao tác với Supabase API (Postgrest, Auth, Storage) hoặc External API (Gemini, Sightengine).
4. **Data Source:** Supabase hoặc AI Service xử lý và trả về kết quả.
5. **State Update:** Repository chuyển đổi DTO sang Domain Model thông qua Mapper và trả dữ liệu về ViewModel. ViewModel cập nhật `StateFlow`.
6. **UI Update:** Compose tự động Re-compose khi trạng thái thay đổi.

## 3. Dependency Injection (DI)

Dự án sử dụng **Dagger Hilt** để quản lý phụ thuộc.
- **NetworkModule:** Cung cấp các instance của `SupabaseClient`, `OkHttpClient`, và `ModerationService`.
- **Repository Injection:** Các Repositories (Auth, Home, Feed, ...) được inject vào ViewModels.
- **Hilt ViewModel:** Sử dụng `@HiltViewModel` để đảm bảo ViewModel được quản lý theo vòng đời của Android.

## 4. Xử lý Bất đồng bộ (Async Handling)

Sử dụng **Kotlin Coroutines** và **Flow**:
- **Coroutines:** Xử lý các tác vụ mạng và database trên `Dispatchers.IO`.
- **Flow/StateFlow:** Truyền dữ liệu từ tầng Data lên UI một cách reactive.
- **Concurrent Execution:** Sử dụng `async`/`awaitAll` để tối ưu hóa các tác vụ song song như upload nhiều ảnh hoặc kiểm duyệt nội dung bằng nhiều AI cùng lúc.

## 5. Tích hợp Backend & AI

- **Supabase:** Backend chính (Auth, DB, Storage, Realtime).
- **Gemini AI:** Kiểm duyệt văn bản (nhận diện toxic, teencode) và Trợ lý tư vấn mua sắm.
- **Sightengine:** Kiểm duyệt hình ảnh (nhận diện ảnh nhạy cảm, bạo lực).

## 6. Điều hướng (Navigation)

Sử dụng **Jetpack Compose Navigation**:
- Định nghĩa các Route tập trung trong `Routes.kt`.
- `AppNavigation` quản lý `NavHost` và chuyển đổi màn hình linh hoạt.
- Hỗ trợ truyền tham số (Arguments) cho các màn hình chi tiết như `PostDetail`, `WriteReview`.

---
Kiến trúc này đảm bảo SmartPick có tính module hóa cao, dễ dàng mở rộng các tính năng thương mại điện tử và AI trong tương lai.
