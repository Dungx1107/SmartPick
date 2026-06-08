# Chương 1: Đặt vấn đề

## 1.1. Bối cảnh thực tế
Trong kỷ nguyên công nghiệp 4.0, hành vi mua sắm của người tiêu dùng đã có sự thay đổi mạnh mẽ. Mô hình Thương mại điện tử truyền thống đang dần chuyển dịch sang "Social Commerce" (Thương mại xã hội), nơi các quyết định mua hàng bị ảnh hưởng lớn bởi các nội dung đánh giá (review), chia sẻ trải nghiệm thực tế trên các mạng xã hội. Tuy nhiên, sự tách biệt giữa nền tảng nội dung (TikTok, Facebook, Instagram) và nền tảng giao dịch (Shopee, Lazada) tạo ra sự đứt gãy trong trải nghiệm khách hàng.

## 1.2. Lý do xây dựng hệ thống
Dự án **SmartPick** ra đời nhằm giải quyết sự đứt gãy này bằng cách tích hợp trực tiếp khả năng mua sắm vào một nền tảng mạng xã hội thu nhỏ. Người dùng không chỉ xem các bài review chất lượng cao (hình ảnh, video) mà còn có thể tiến hành đặt mua sản phẩm ngay tại bài viết đó. Sự hỗ trợ của Trí tuệ nhân tạo (AI) giúp cá nhân hóa tư vấn và đảm bảo môi trường cộng đồng trong sạch.

## 1.3. Vấn đề cần giải quyết
- **Nội dung thiếu tin cậy:** Các sàn thương mại điện tử tràn ngập đánh giá ảo. SmartPick tập trung vào nội dung từ cộng đồng với màng lọc kiểm duyệt chặt chẽ.
- **Trải nghiệm mua sắm rườm rà:** Giảm thiểu các bước từ lúc xem review đến lúc chốt đơn.
- **Nội dung độc hại:** Sử dụng AI để tự động ngăn chặn các hình ảnh nhạy cảm và bình luận xúc phạm.
- **Khó khăn khi lựa chọn:** Người dùng cần một trợ lý ảo am hiểu sản phẩm để tư vấn 24/7.

## 1.4. Nhu cầu người dùng
- **Người mua (Buyer):** Tìm kiếm thông tin thực tế, mua hàng nhanh chóng và được hỗ trợ tư vấn thông minh.
- **Người sáng tạo (Creator/Reviewer):** Nền tảng chuyên nghiệp để chia sẻ đam mê và kết nối sản phẩm.
- **Người bán (Seller):** Quản lý gian hàng, theo dõi doanh thu và tiếp cận khách hàng qua nội dung sáng tạo.

## 2. Mục tiêu hệ thống

### 2.1. Mục tiêu tổng quát
Phát triển ứng dụng Android "SmartPick" - một hệ sinh thái Social Commerce toàn diện, tích hợp AI để tối ưu hóa hành trình mua sắm của người dùng.

### 2.2. Mục tiêu cụ thể
- Xây dựng mạng xã hội chia sẻ video/ảnh review với tính năng tương tác (Like, Comment đa tầng).
- Hoàn thiện luồng thương mại điện tử: Giỏ hàng, Thanh toán, Lịch sử đơn hàng.
- Tích hợp hệ thống kiểm duyệt tự động (AI Moderation) cho văn bản và hình ảnh.
- Triển khai AI Chatbot tư vấn sản phẩm thông minh.
- Cung cấp Dashboard quản lý doanh thu và sản phẩm cho Người bán.

## 3. Đối tượng sử dụng
Dựa trên phân tích mã nguồn thực tế, hệ thống phục vụ 3 nhóm đối tượng chính:
1. **Khách (Guest):** Người dùng chưa định danh, có thể tìm hiểu về ứng dụng.
2. **Người dùng (User/Buyer):** Đối tượng trọng tâm, thực hiện các hoạt động xã hội và mua sắm.
3. **Người bán (Seller):** Người dùng có thêm quyền quản trị gian hàng và xem báo cáo kinh doanh.

## 4. Phạm vi hệ thống

### 4.1. Chức năng hiện có (Dựa trên Source Code)
- **Xác thực:** Đăng ký/Đăng nhập qua Email và Google.
- **Cộng đồng:** Feed bài viết, đăng bài (media), tương tác Realtime (Like/Comment).
- **Mua sắm:** Xem sản phẩm, Giỏ hàng, Checkout, Lịch sử mua hàng, Đánh giá sản phẩm.
- **AI:** Tư vấn Chatbot (Gemini), Kiểm duyệt nội dung (Sightengine).
- **Cá nhân:** Quản lý Profile, Bài viết đã thích, Thông báo.
- **Quản trị:** Seller Dashboard (Doanh thu, Đơn hàng đã bán).

### 4.2. Chức năng chưa hỗ trợ
- Thanh toán trực tuyến (e-wallet).
- Hệ thống tích điểm và mã giảm giá phức tạp.

## 5. Ý nghĩa thực tiễn
SmartPick không chỉ là một công cụ mua sắm mà còn là một cộng đồng tin cậy. Ứng dụng giúp tiết kiệm thời gian cho người mua, tăng uy tín cho người bán và đảm bảo an toàn không gian mạng thông qua công nghệ AI tiên tiến.

## 6. Kết luận chương
Chương này đã làm rõ tầm nhìn và các mục tiêu cốt lõi của SmartPick. Đây là cơ sở để thiết kế các giải pháp kỹ thuật và lựa chọn công nghệ phù hợp trong các chương tiếp theo.
