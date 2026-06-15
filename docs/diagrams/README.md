# 🗺️ Hệ thống Sơ đồ Thiết kế Hệ thống SmartPick (Chương 3.3)

Thư mục này chứa toàn bộ hệ thống sơ đồ thiết kế hệ thống lớp, luồng xử lý và ca sử dụng của ứng dụng SmartPick phục vụ cho Chương 3.3 của báo cáo kỹ thuật. Mọi sơ đồ được thiết kế dựa trên mã nguồn thực tế của dự án, tuân thủ Clean Architecture và tích hợp các công nghệ Supabase, Gemini AI, Sightengine và FCM.

---

## 📂 Cấu trúc Thư mục Sơ đồ

Sơ đồ được lưu trữ song song trong hai cấu trúc thư mục để đảm bảo khả năng tương thích và dễ tra cứu:
1. **Theo cấu trúc phân loại Chương 3.3 (Bước 4):** Nằm trong các thư mục `3.3.x-.../`
2. **Theo cấu trúc phân loại module (Bước 2):** Nằm trong các thư mục viết thường như `use-case/`, `sequence/`, `activity/`, `class/`.

---

## 3.3.1. BIỂU ĐỒ CA SỬ DỤNG (USE-CASE DIAGRAM)
Biểu đồ mô tả các tác nhân (Khách, Người mua, Người bán) và các tính năng tương tác của ứng dụng bao gồm cả các liên kết nghiệp vụ AI.

* **[3.3.1.01] Sơ đồ ca sử dụng tổng quan (Overall Use Case):**
  - Mô tả đầy đủ các tác nhân Guest, Buyer, Seller và các ca sử dụng về xác thực, bảng tin, thương mại điện tử, trợ lý chatbot AI, kiểm duyệt bài viết và thống kê bán hàng.
  - 🔗 Liên kết: [01-overall-usecase.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.1-use-case/01-overall-usecase.mmd) (hoặc [bản rút gọn](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/use-case/01-overall-usecase.mmd))

---

## 3.3.2. BIỂU ĐỒ TUẦN TỰ (SEQUENCE DIAGRAM)
Mô tả chi tiết luồng tương tác thời gian giữa UI, ViewModels, Repositories, Supabase SDK và các API ngoại vi.

| STT | Tên sơ đồ | Mô tả luồng xử lý | Liên kết file |
|---|---|---|---|
| **01** | **Login Flow** | Đăng nhập bằng tài khoản email & mật khẩu thông qua Supabase Auth. | [01-login-flow.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.2-sequence/01-login-flow.mmd) |
| **02** | **Feed Load Flow** | Tải bài đăng bảng tin kèm thông tin sản phẩm và người đăng liên kết. | [02-feed-load-flow.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.2-sequence/02-feed-load-flow.mmd) |
| **03** | **Create Post with AI Moderation** | Tạo bài viết, kiểm duyệt text bằng LmStudio (qwen3) và kiểm duyệt ảnh bằng Sightengine trước khi đăng. | [03-create-post-with-ai-moderation.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.2-sequence/03-create-post-with-ai-moderation.mmd) |
| **04** | **Like Post Realtime** | Thả tim bài viết, thực hiện cập nhật giao diện ngay lập tức (Optimistic Update) và đồng bộ real-time qua Supabase WebSocket. | [04-like-post-realtime.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.2-sequence/04-like-post-realtime.mmd) |
| **05** | **Comment Realtime** | Đăng bình luận đa tầng, chèn vào DB, cập nhật danh sách RAM cục bộ và gửi thông báo ngầm qua FCM. | [05-comment-realtime.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.2-sequence/05-comment-realtime.mmd) |
| **06** | **Add to Cart** | Thêm sản phẩm vào giỏ hàng, cập nhật số lượng hoặc chèn mới bản ghi và tự động tải lại giỏ hàng. | [06-add-to-cart.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.2-sequence/06-add-to-cart.mmd) |
| **07** | **Checkout Transaction** | Tạo hóa đơn `orders`, chèn chi tiết `order_items` để kích hoạt trigger trừ kho tự động phía PostgreSQL và xử lý rollback khi lỗi. | [07-checkout-transaction.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.2-sequence/07-checkout-transaction.mmd) |
| **08** | **Notification Flow** | Lắng nghe bảng thông báo qua `postgresChangeFlow`, gọi Edge Function `send-fcm` để đẩy tin nhắn qua Google FCM Server. | [08-notification-flow.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.2-sequence/08-notification-flow.mmd) |
| **09** | **Product Management** | Người bán quản lý sản phẩm hiện có và truy vấn doanh số bán hàng từ liên kết order_items. | [09-product-management.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.2-sequence/09-product-management.mmd) |
| **10** | **Google Sign-In Flow** | Đăng nhập một chạm bằng Credential Manager SDK, xác thực với Supabase qua ID Token, ghi nhận user mới/cũ và gửi email. | [10-google-signin-flow.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.2-sequence/10-google-signin-flow.mmd) |

---

## 3.3.3. BIỂU ĐỒ LUỒNG HOẠT ĐỘNG (ACTIVITY DIAGRAM)
Mô tả quy trình nghiệp vụ dưới dạng flowchart logic, rẽ nhánh điều kiện và xử lý ngoại lệ chi tiết.

| STT | Tên sơ đồ | Mô tả luồng hoạt động | Liên kết file |
|---|---|---|---|
| **01** | **User Registration** | Luồng hoạt động đăng ký tài khoản mới, check trùng và gửi email. | [01-user-registration.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.3-activity/01-user-registration.mmd) |
| **02** | **Login** | Quy trình phân nhánh đăng nhập thủ công hoặc bằng tài khoản Google. | [02-login.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.3-activity/02-login.mmd) |
| **03** | **Create Post** | Luồng tạo bài đăng và sản phẩm tích hợp kiểm duyệt AI. | [03-create-post.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.3-activity/03-create-post.mmd) |
| **04** | **AI Moderation** | Tiến trình lọc văn bản (Qwen LLM) và hình ảnh (Sightengine API) chi tiết. | [04-ai-moderation.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.3-activity/04-ai-moderation.mmd) |
| **05** | **Product Publishing** | Người bán đăng thông tin mặt hàng mới tích hợp kiểm duyệt. | [05-product-publishing.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.3-activity/05-product-publishing.mmd) |
| **06** | **Add to Cart** | Luồng thêm sản phẩm vào giỏ, kiểm kho và cập nhật số lượng. | [06-add-to-cart.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.3-activity/06-add-to-cart.mmd) |
| **07** | **Three-Tier Inventory Defense** | Sơ đồ phòng thủ 3 tầng kho hàng: UI Level -> ViewModel Level -> Database Level (Database Constraint). | [07-three-tier-inventory-defense.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.3-activity/07-three-tier-inventory-defense.mmd) |
| **08** | **Checkout** | Luồng đặt đơn hàng chi tiết, check giỏ hàng, thông tin giao nhận và trừ kho. | [08-checkout.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.3-activity/08-checkout.mmd) |
| **09** | **Order Management** | Các bước người bán theo dõi và thống kê doanh thu đơn hàng đã bán. | [09-order-management.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.3-activity/09-order-management.mmd) |
| **10** | **Realtime Notification** | Quá trình đồng bộ hóa dữ liệu thông báo tức thì qua kênh WebSocket. | [10-realtime-notification.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.3-activity/10-realtime-notification.mmd) |

---

## 3.3.4. BIỂU ĐỒ LỚP (CLASS DIAGRAM)
Bao gồm các thuộc tính, phương thức, kiểu dữ liệu Kotlin, luồng StateFlow/Flow và quan hệ tiêm phụ thuộc (Dagger Hilt Dependency Injection).

| STT | Tên sơ đồ | Mô tả kiến trúc lớp | Liên kết file |
|---|---|---|---|
| **01** | **Clean Architecture Overview** | Sơ đồ khái quát cấu trúc 3 lớp: Presentation (Screens, VM) -> Domain (Models, Mappers) -> Data (Repos, DTOs). | [01-clean-architecture-overview.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.4-class/01-clean-architecture-overview.mmd) |
| **02** | **Feed Module** | Cấu trúc lớp của màn hình bảng tin, hiển thị bài viết và phản ứng tương tác. | [02-feed-module.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.4-class/02-feed-module.mmd) |
| **03** | **Post Module** | Cấu trúc lớp phục vụ tạo bài review và upload media. | [03-post-module.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.4-class/03-post-module.mmd) |
| **04** | **Product Module** | Cấu trúc các lớp chi tiết sản phẩm và tải đánh giá liên quan. | [04-product-module.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.4-class/04-product-module.mmd) |
| **05** | **Cart Module** | Cấu trúc các lớp của giỏ hàng và cập nhật số lượng vật phẩm. | [05-cart-module.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.4-class/05-cart-module.mmd) |
| **06** | **Order Module** | Cấu trúc các lớp thanh toán, đặt đơn và nạp lịch sử giao dịch. | [06-order-module.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.4-class/06-order-module.mmd) |
| **07** | **Notification Module** | Cấu trúc các lớp thông báo thời gian thực và quản lý Firebase Messaging Service. | [07-notification-module.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.4-class/07-notification-module.mmd) |
| **08** | **Auth Module** | Sơ đồ các lớp liên quan tới xác thực thủ công và Google Sign-In. | [08-auth-module.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.4-class/08-auth-module.mmd) |
| **09** | **Moderation Module** | Liên kết giữa ModerationService, LmStudioModerator và Exception kiểm duyệt. | [09-moderation-module.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.4-class/09-moderation-module.mmd) |
| **10** | **Realtime Module** | Cấu trúc hạ tầng thiết lập kênh Websocket thời gian thực của Supabase. | [10-realtime-module.mmd](file:///c:/btl/BTL-ANDROID/main/SmartPick/docs/diagrams/3.3.4-class/10-realtime-module.mmd) |
