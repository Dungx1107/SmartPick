package com.example.smartpick.core.utils

import com.example.smartpick.navigation.Routes

object NavigationUtils {
    // Danh sách ẩn thanh Menu dưới cùng (BottomBar)
    private val hiddenBottomBarRoutes = setOf(
        Routes.Login.route,
        Routes.EditProfile.route,
        Routes.SignUp.route,
        Routes.CreatePost.route,
        Routes.PostDetail.route,
        Routes.Comments.route,
        Routes.Settings.route,
        Routes.Checkout.route,
        Routes.WriteReview.route,
    )

    // Danh sách ẩn thanh Tiêu đề trên cùng (TopAppBar)
    private val hiddenTopBarRoutes = setOf(
        Routes.Login.route,
        Routes.EditProfile.route,
        Routes.SignUp.route,
        Routes.CreatePost.route,
        Routes.PostDetail.route,
        Routes.Comments.route,
        Routes.Settings.route,
        Routes.Checkout.route,
        Routes.WriteReview.route,
        Routes.ReviewHub.route,
        Routes.Notifications.route,
        Routes.Saved.route


    )

    fun shouldShowBottomBar(currentRoute: String?): Boolean {
        // Lọc bỏ phần tham số (?) và phần path động (/) để so sánh gốc
        val baseRoute = currentRoute?.substringBefore("?")?.substringBefore("/")
        return hiddenBottomBarRoutes.none { baseRoute == it.substringBefore("/") }
    }

    fun shouldShowTopBar(currentRoute: String?): Boolean {
        val baseRoute = currentRoute?.substringBefore("?")?.substringBefore("/")
        return hiddenTopBarRoutes.none { baseRoute == it.substringBefore("/") }
    }
}