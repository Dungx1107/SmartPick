package com.example.smartpick.core.utils

import com.example.smartpick.navigation.Routes

object NavigationUtils {
    private val hiddenBottomBarRoutes = setOf(
        Routes.Login.route,
        Routes.SignUp.route,
        Routes.EditProfile.route,
        Routes.CreatePost.route,
        Routes.PostDetail.route,
        Routes.Comments.route,
        Routes.Settings.route,
        Routes.Checkout.route,
        Routes.WriteReview.route,
        Routes.EditPost.route,
        Routes.ProductDetail.route,
        Routes.Cart.route
    )
    fun shouldShowBottomBar(currentRoute: String?): Boolean {
        if (currentRoute == null) return true
        val baseRoute = currentRoute.substringBefore("?").substringBefore("/")
        return hiddenBottomBarRoutes.none { it.substringBefore("/") == baseRoute }
    }

}