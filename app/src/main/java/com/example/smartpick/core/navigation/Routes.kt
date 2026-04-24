package com.example.smartpick.core.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Home : Routes("home")
    object Feed : Routes("feed")
    object ChatBot : Routes("chatbot")
    object Saved : Routes("saved")
    object Profile : Routes("profile")
    object EditProfile : Routes("edit_profile")
    object SignUp : Routes("sign_up")

    object Notifications : Routes("notifications")

    object PostDetail : Routes("post_detail/{postId}") {
        const val ARG_POST_ID = "postId" // Đặt hằng số để dùng chung
        fun createRoute(postId: String) = "post_detail/$postId"
    }

    object Comments : Routes("comments/{postId}") {
        fun createRoute(postId: String) = "comments/$postId"
    }
}