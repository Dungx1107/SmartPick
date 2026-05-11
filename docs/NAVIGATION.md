# Quản lý Điều hướng (Navigation)

Dự án sử dụng thư viện **Jetpack Navigation Compose** để quản lý luồng di chuyển giữa các màn hình.
Toàn bộ cấu hình điều hướng được tập trung tại package `com.example.smartpick.navigation`.

## 1. Định nghĩa Tuyến đường (Routes)

Các tuyến đường được định nghĩa dưới dạng một `sealed class` trong file `Routes.kt`. Điều này giúp
đảm bảo an toàn kiểu dữ liệu và dễ dàng quản lý các tham số.

- **Login (`login`):** Màn hình đăng nhập (Start destination).
- **SignUp (`sign_up`):** Màn hình đăng ký tài khoản.
- **Home (`home`):** Màn hình chính (Trang chủ).
- **Feed (`feed`):** Danh sách bài viết từ cộng đồng.
- **ChatBot (`chatbot`):** Giao diện trò chuyện với AI Gemini.
- **PostDetail (`post_detail/{postId}`):** Xem chi tiết một bài viết cụ thể.
- **CreatePost (`create_post`):** Giao diện tạo bài viết mới.
- **Profile (`profile`):** Trang cá nhân của người dùng.
- **EditProfile (`edit_profile`):** Chỉnh sửa thông tin cá nhân.

## 2. Truyền tham số (Navigation Arguments)

Dự án sử dụng các tham số trực tiếp trên đường dẫn (Path parameters).

**Ví dụ: Chuyển sang màn hình chi tiết bài viết**

- **Định nghĩa:** `object PostDetail : Routes("post_detail/{postId}")`
- **Truyền dữ liệu:** `navController.navigate(Routes.PostDetail.createRoute(postId))`
- **Nhận dữ liệu:** Sử dụng `navArgument` trong `NavHost` để trích xuất `postId`.

```kotlin
composable(
    route = Routes.PostDetail.route,
    arguments = listOf(navArgument(Routes.PostDetail.ARG_POST_ID) {
        type = NavType.StringType
    })
) { entry ->
    val postId = entry.arguments?.getString(Routes.PostDetail.ARG_POST_ID)
    // ...
}
```

## 3. Thành phần Giao diện Điều hướng

- **AppNavigation.kt:** Chứa `NavHost`, định nghĩa toàn bộ Graph của ứng dụng và logic Scaffold (
  TopBar/BottomBar).
- **MainBottomBar.kt:** Thanh điều hướng dưới cùng, hiển thị cho các màn hình chính (Home, Feed,
  ChatBot, Profile).
- **MainTopBar.kt:** Thanh tiêu đề phía trên, thay đổi tiêu đề động dựa trên route hiện tại.

## 4. Logic Ẩn/Hiện Navigation Bar

Sử dụng hàm tiện ích `shouldShowBottomBar(route)` trong `NavigationUtils.kt` để quyết định xem một
màn hình có nên hiển thị thanh BottomBar hay không (Ví dụ: Ẩn ở màn hình Login, SignUp, CreatePost).
