package com.example.smartpick.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Home : Routes("home")
    object Feed : Routes("feed")
    object ChatBot : Routes("chatbot")
    object Saved : Routes("saved")
    object Profile : Routes("profile")
    object EditProfile : Routes("edit_profile")
    object SignUp : Routes("sign_up")

    object CreatePost : Routes("create_post")

    object Notifications : Routes("notifications")

    object PostDetail : Routes("post_detail/{postId}") {
        const val ARG_POST_ID = "postId"
        fun createRoute(postId: String) = "post_detail/$postId"
    }

    object Comments : Routes("comments/{postId}/{postOwnerId}") {
        fun createRoute(postId: String, postOwnerId: String) = "comments/$postId/$postOwnerId"
    }

    object Settings : Routes("settings")
}