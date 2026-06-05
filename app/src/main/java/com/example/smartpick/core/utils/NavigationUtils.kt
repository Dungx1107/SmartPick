package com.example.smartpick.core.utils

import com.example.smartpick.navigation.Routes

object NavigationUtils {
    // Danh sách các màn hình cần ẩn thanh Menu dưới cùng và TopBar mặc định
    private val hiddenRoutes = setOf(
        Routes.Login.route,
        Routes.EditProfile.route,
        Routes.SignUp.route,
        Routes.CreatePost.route,
        Routes.PostDetail.route,
        Routes.Comments.route,
        Routes.Settings.route,
        Routes.Checkout.route,
        Routes.WriteReview.route
    )

    fun shouldShowBottomBar(currentRoute: String?): Boolean {
        if (currentRoute == null) return true
        val baseRoute = currentRoute.substringBefore("?").substringBefore("/")
        // Ẩn BottomBar nếu đang ở màn hình Chỉnh sửa bài viết
        if (baseRoute == "edit_post") return false
        return hiddenRoutes.none { it.substringBefore("/") == baseRoute }
    }

    fun shouldShowTopBar(currentRoute: String?): Boolean {
        if (currentRoute == null) return true
        val baseRoute = currentRoute.substringBefore("?").substringBefore("/")
        // Ẩn TopBar mặc định nếu đang ở màn Thanh Toán hoặc màn Chỉnh sửa bài viết
        if (baseRoute == "edit_post" || baseRoute == Routes.Checkout.route.substringBefore("/")) return false
        return hiddenRoutes.none { it.substringBefore("/") == baseRoute }
    }
}