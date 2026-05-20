# Chương 2: Công nghệ sử dụng

## 2.1. Danh sách công nghệ và thư viện chính

| Nhóm | Công nghệ / Thư viện | Mục đích sử dụng |
|:---|:---|:---|
| **Ngôn ngữ** | Kotlin 2.0 | Ngôn ngữ lập trình hiện đại, an toàn và ngắn gọn. |
| **Giao diện (UI)** | Jetpack Compose | Framework xây dựng giao diện khai báo (Declarative UI). |
| **Kiến trúc** | MVVM + Clean Architecture | Tách biệt logic, dễ bảo trì và kiểm thử. |
| **Dependency Injection** | Hilt (Dagger) | Quản lý phụ thuộc tự động, giảm boilerplate code. |
| **Backend (BaaS)** | Supabase | Database (Postgres), Auth, Storage và Realtime. |
| **Xử lý mạng** | Ktor & OkHttp | Thực hiện các yêu cầu HTTP tới Supabase và AI API. |
| **Bất đồng bộ** | Coroutines & Flow | Xử lý các tác vụ tốn thời gian mà không gây treo ứng dụng. |
| **Trí tuệ nhân tạo (AI)** | Gemini 1.5 Flash | Trợ lý tư vấn sản phẩm và kiểm duyệt văn bản. |
| **Kiểm duyệt ảnh** | Sightengine API | Nhận diện và chặn các hình ảnh vi phạm chính sách. |
| **Xử lý Media** | Media3 ExoPlayer & Coil | Phát video và tải/hiển thị hình ảnh tối ưu. |
| **Định dạng dữ liệu** | Kotlinx Serialization | Chuyển đổi JSON sang Object nhanh chóng. |

## 2.2. Lý do chọn công nghệ

1.  **Kotlin & Jetpack Compose:** Đây là bộ đôi tiêu chuẩn của Google cho phát triển Android hiện đại. Compose giúp giảm 40% lượng code UI so với XML truyền thống.
2.  **Supabase:** Cung cấp đầy đủ các tính năng của Firebase nhưng dựa trên PostgreSQL, cho phép truy vấn quan hệ mạnh mẽ và khả năng mở rộng tốt.
3.  **Hilt:** Giúp việc quản lý các instance của Repository, Client mạng trở nên tập trung, tránh việc khởi tạo thủ công ở nhiều nơi.
4.  **Gemini AI:** Là một trong những mô hình ngôn ngữ lớn (LLM) mạnh nhất hiện nay, có tốc độ phản hồi nhanh (phiên bản Flash) phù hợp cho ứng dụng di động.

## 2.3. Ưu và nhược điểm

### Ưu điểm:
- **Tốc độ phát triển nhanh:** Nhờ các thư viện mạnh mẽ và framework hiện đại.
- **Hiệu năng cao:** Ứng dụng mượt mà, tiêu tốn ít tài nguyên nhờ vào sự tối ưu của Compose và Coroutines.
- **An toàn dữ liệu:** Tận dụng Row Level Security (RLS) của Postgres.

### Nhược điểm:
- **Phụ thuộc bên thứ ba:** Hệ thống phụ thuộc nhiều vào các API Key và sự ổn định của Supabase/AI Services.
- **Yêu cầu kết nối mạng:** Hầu hết các tính năng AI và Social đều cần internet để hoạt động.
