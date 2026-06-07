# Kiến trúc Hệ thống SmartPick

SmartPick được thiết kế theo các nguyên tắc kỹ thuật phần mềm tiên tiến, đảm bảo tính dễ mở rộng, bảo trì và khả năng kiểm thử cao.

## 1. Mô hình Kiến trúc Tổng thể

Ứng dụng áp dụng **Clean Architecture** kết hợp với mô hình **MVVM (Model-View-ViewModel)**. Cấu trúc này chia hệ thống thành các lớp độc lập với các trách nhiệm rõ ràng:

### Lớp Presentation (Giao diện)
- **Công nghệ:** Jetpack Compose.
- **Thành phần:** 
    - **UI Screens/Components:** Khai báo giao diện bằng code Kotlin, phản ứng với các thay đổi trạng thái (State).
    - **ViewModels:** Quản lý trạng thái UI bằng `StateFlow`, xử lý logic điều hướng và gọi các Repository. Sử dụng `@HiltViewModel` để quản lý vòng đời.

### Lớp Domain (Nghiệp vụ)
- **Thành phần:**
    - **Domain Models:** Các đối tượng dữ liệu thuần túy (POJO) đại diện cho thực thể trong hệ thống (User, Post, Product, Order).
    - **Mappers:** Chuyển đổi dữ liệu từ tầng Data (DTO) sang tầng Domain để đảm bảo tính độc lập.
    - **Use Cases (Tùy chọn):** Đóng gói các logic nghiệp vụ phức tạp hoặc được tái sử dụng nhiều nơi.

### Lớp Data (Dữ liệu)
- **Thành phần:**
    - **Repositories:** Điểm truy cập dữ liệu duy nhất (Single Source of Truth). Quyết định lấy dữ liệu từ mạng (Supabase) hay bộ nhớ cục bộ.
    - **Data Sources:** 
        - **Remote:** Supabase SDK (Postgrest, Auth, Storage, Realtime).
        - **Local:** DataStore cho cấu hình người dùng.
    - **DTOs (Data Transfer Objects):** Các class được định nghĩa theo cấu trúc JSON của API/Database.

## 2. Các Thành phần Cốt lõi (Core)

- **Dependency Injection (DI):** Sử dụng **Dagger Hilt** để quản lý việc cung cấp các instance (như SupabaseClient, OkHttpClient) xuyên suốt ứng dụng.
- **Xử lý Bất đồng bộ:** Dựa hoàn toàn vào **Kotlin Coroutines** và **Flow**. Các tác vụ nặng được đẩy xuống `Dispatchers.IO`.
- **Hệ thống Network:** 
    - **Supabase Client:** Cấu hình tập trung trong `core/network`.
    - **Moderation Service:** Một service đặc biệt tích hợp cả Gemini AI và Sightengine để kiểm duyệt nội dung trước khi đẩy lên tầng Data.

## 3. Luồng Hoạt động (Data Flow)

Ứng dụng tuân thủ luồng dữ liệu một chiều (**Unidirectional Data Flow - UDF**):
1. Người dùng tương tác với **Compose Screen**.
2. Sự kiện (Event) được gửi đến **ViewModel**.
3. ViewModel thực hiện logic hoặc gọi hàm tương ứng trong **Repository**.
4. Repository thực hiện các tác vụ mạng/IO, sau đó trả về kết quả dưới dạng **Result** hoặc **Flow**.
5. ViewModel cập nhật **UI State**.
6. UI tự động cập nhật nhờ cơ chế **Recomposition** của Jetpack Compose.

## 4. Đặc điểm Nổi bật trong Thiết kế
- **Realtime Sync:** Tận dụng Supabase Realtime để đồng bộ thông báo và giỏ hàng ngay lập tức mà không cần kéo để làm mới (Pull-to-refresh).
- **AI-First Integration:** AI (Gemini, Sightengine) được tích hợp sâu vào luồng xử lý dữ liệu ở tầng Data, đóng vai trò như một màng lọc tự động.
- **Type-safe Navigation:** Mọi đường dẫn màn hình và tham số truyền qua đều được quản lý tập trung, giảm thiểu lỗi runtime.

---
Kiến trúc này giúp SmartPick sẵn sàng cho việc mở rộng thêm các tính năng phức tạp như Thanh toán trực tuyến hoặc Hệ thống gợi ý máy học trong tương lai.
