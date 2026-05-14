# Danh sách Công nghệ (Tech Stack)

Dự án SmartPick sử dụng các thư viện và công nghệ hiện đại nhất trong hệ sinh thái Android để đảm
bảo hiệu suất và trải nghiệm người dùng.

## 1. Ngôn ngữ & Framework chính

- **Kotlin:** Ngôn ngữ lập trình chính cho toàn bộ dự án.
- **Jetpack Compose:** Framework xây dựng giao diện người dùng hiện đại, khai báo (Declarative UI).
- **Kotlin Coroutines & Flow:** Quản lý lập trình bất đồng bộ và luồng dữ liệu thời gian thực.

## 2. Backend & Database (BaaS)

- **Supabase (v2.1.3):** Giải pháp thay thế Firebase, cung cấp:
    - **Postgrest-kt:** Truy vấn cơ sở dữ liệu PostgreSQL.
    - **GoTrue-kt:** Quản lý xác thực người dùng (Email/Password, Google).
    - **Storage-kt:** Quản lý lưu trữ hình ảnh và video.
- **Ktor Client (v2.3.8):** Engine HTTP cho Supabase client.

## 3. Dependency Injection

- **Dagger Hilt (v2.51.1):** Tự động hóa việc quản lý và cung cấp các phụ thuộc (Dependencies) trong
  ứng dụng.
- **Hilt Navigation Compose:** Hỗ trợ inject ViewModel trực tiếp vào các Composable.

## 4. Hình ảnh & Media

- **Coil (v2.6.0):** Thư viện tải và hiển thị hình ảnh tối ưu.
- **Coil-video:** Hỗ trợ trích xuất và hiển thị khung hình từ video (Thumbnail).

## 5. AI & Kiểm duyệt nội dung

- **Gemini AI:** Sử dụng Google Generative AI để xây dựng tính năng Chatbot tư vấn.
- **Sightengine:** Dịch vụ bên thứ ba dùng để kiểm duyệt hình ảnh nhạy cảm (Moderation).
- **OkHttp (v4.12.0):** Sử dụng cho các yêu cầu HTTP tùy chỉnh tới Sightengine.

## 6. Tiện ích khác

- **Kotlinx Serialization (v1.6.2):** Chuyển đổi dữ liệu JSON từ API thành các đối tượng Kotlin.
- **Jetpack Navigation Compose:** Quản lý điều hướng và truyền tham số giữa các màn hình.
- **Credentials Manager:** Hỗ trợ đăng nhập Google một cách an toàn và hiện đại.
