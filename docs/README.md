# SmartPick - Ứng dụng Hỗ trợ Mua sắm & Kết nối Cộng đồng

## Tổng quan dự án

**SmartPick** là một ứng dụng di động được xây dựng trên nền tảng Android, kết hợp giữa mạng xã hội
chia sẻ trải nghiệm sản phẩm và trợ lý mua sắm thông minh (Chatbot AI).

Dự án tập trung vào việc giúp người dùng tìm kiếm, đánh giá và lưu trữ các sản phẩm yêu thích thông
qua các bài đăng từ cộng đồng.

## Mục đích

Xây dựng một hệ sinh thái nơi người dùng có thể:

- Chia sẻ các bài viết đánh giá (Review) sản phẩm kèm hình ảnh/video
- Nhận diện và gán thẻ sản phẩm trực tiếp vào bài viết
- Tương tác với cộng đồng thông qua bình luận và yêu thích
- Sử dụng AI để được tư vấn mua sắm một cách nhanh chóng

## Đối tượng người dùng

- Người tiêu dùng muốn tìm kiếm đánh giá thực tế trước khi mua sắm
- Các Reviewer muốn chia sẻ trải nghiệm cá nhân về sản phẩm
- Người dùng cần một trợ lý AI để giải đáp thắc mắc về các loại hàng hóa

## Tóm tắt các tính năng chính

| Tính năng               | Mô tả                                                  | File liên quan                          |
|-------------------------|--------------------------------------------------------|-----------------------------------------|
| **Bảng tin (Feed)**     | Hiển thị danh sách bài đăng mới nhất từ cộng đồng      | `FeedScreen.kt`                         |
| **Chi tiết bài viết**   | Xem nội dung đầy đủ, sản phẩm đính kèm và bình luận    | `PostDetailScreen.kt`                   |
| **Trợ lý AI (Chatbot)** | Tích hợp Gemini AI để hỗ trợ giải đáp thông tin        | `ChatScreen.kt`                         |
| **Quản lý sản phẩm**    | Cho phép gán thẻ sản phẩm vào bài viết                 | `Product.kt`, `ProductHorizontalTag.kt` |
| **Hệ thống bình luận**  | Tương tác trực tiếp trên từng bài viết                 | `CommentItem.kt`                        |
| **Xác thực người dùng** | Đăng ký, đăng nhập và quản lý hồ sơ qua Supabase Auth  | -                                       |
| **Kiểm duyệt nội dung** | Tích hợp Sightengine để lọc ảnh/nội dung không phù hợp | -                                       |