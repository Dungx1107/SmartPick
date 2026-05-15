package com.example.smartpick.core.utils

import com.example.smartpick.navigation.Routes

object NavigationUtils {
    private val hiddenRoutes = setOf(
        Routes.Login.route,
        Routes.EditProfile.route,
        Routes.SignUp.route,
        Routes.CreatePost.route,
        Routes.PostDetail.route,
        Routes.Comments.route,
        Routes.Settings.route
    )

    fun shouldShowBottomBar(currentRoute: String?): Boolean {
        return currentRoute !in hiddenRoutes
    }
}