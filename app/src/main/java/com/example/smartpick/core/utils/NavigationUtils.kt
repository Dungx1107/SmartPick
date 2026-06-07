package com.example.smartpick.core.utils

import com.example.smartpick.navigation.Routes

object NavigationUtils {

    fun shouldShowBottomBar(currentRoute: String?): Boolean {
        if (currentRoute == null) return false

        // 1. Tách bỏ dữ liệu truy vấn động sau dấu '?' để lấy tên gốc của route hiện tại
        // Ví dụ: "checkout?productId=prod_01" -> "checkout"
        val currentBaseRoute = currentRoute.substringBefore("?")

        // 2. Danh sách các Route tĩnh cần ẩn Bottom Bar (Lấy gốc bằng substringBefore("?"))
        val hiddenStaticRoutes = setOf(
            Routes.Login.route,
            Routes.SignUp.route,
            Routes.EditProfile.route,
            Routes.CreatePost.route,
            Routes.Settings.route,
            Routes.Cart.route,
            Routes.Checkout.route.substringBefore("?") // Tự động cắt chuỗi cấu trúc trả về "checkout"
        )

        // 3. Danh sách các Route động có tham số dạng gạch chéo '/' (Cắt chuỗi trước ký tự mở ngoặc nhọn '{')
        val isDynamicHiddenRoute =
            currentBaseRoute.startsWith(Routes.ProductDetail.route.substringBefore("/{")) ||
                    currentBaseRoute.startsWith(Routes.PostDetail.route.substringBefore("/{")) ||
                    currentBaseRoute.startsWith(Routes.EditPost.route.substringBefore("/{")) ||
                    currentBaseRoute.startsWith(Routes.WriteReview.route.substringBefore("/{")) ||
                    currentBaseRoute.startsWith(Routes.Comments.route.substringBefore("/{")) ||
                    currentBaseRoute.startsWith(Routes.CommentsFromNotification.route.substringBefore("/{"))

        // 4. Trả về false (ẩn) nếu khớp với bất kỳ điều kiện ẩn nào phía trên
        if (currentBaseRoute in hiddenStaticRoutes || isDynamicHiddenRoute) {
            return false
        }

        return true
    }
}