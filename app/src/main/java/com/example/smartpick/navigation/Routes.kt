package com.example.smartpick.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Home : Routes("home")
    object Feed : Routes("feed")
    object ReviewHub : Routes("review_hub")
    object Saved : Routes("saved")
    object Profile : Routes("profile")
    object EditProfile : Routes("edit_profile")
    object SignUp : Routes("sign_up")
    object CreatePost : Routes("create_post")
    object Notifications : Routes("notifications")
    object Settings : Routes("settings")
    object Checkout : Routes("checkout")

    object EditPost : Routes("edit_post/{postId}") {
        const val ARG_POST_ID = "postId"
        fun createRoute(postId: String) = "edit_post/$postId"
    }
    object WriteReview : Routes("write_review/{productId}") {
        const val ARG_PRODUCT_ID = "productId"
        fun createRoute(productId: String) = "write_review/$productId"
    }

    object PostDetail : Routes("post_detail/{postId}") {
        const val ARG_POST_ID = "postId"
        fun createRoute(postId: String) = "post_detail/$postId"
    }

    object Comments : Routes("comments/{postId}/{postOwnerId}") {
        fun createRoute(postId: String, postOwnerId: String) = "comments/$postId/$postOwnerId"
    }

    object CommentsFromNotification : Routes("comments_notification/{postId}?commentId={commentId}") {
        fun createRoute(postId: String, commentId: String) = "comments_notification/$postId?commentId=$commentId"
    }

    // Dành riêng cho click từ thanh trạng thái (Status bar) của điện thoại
    object SystemPushDeepLink : Routes("system_push/{type}?post_id={postId}&target_id={targetId}") {
        // Route này không cần hàm createRoute vì nó chỉ được gọi thông qua Deep Link từ Intent
    }
}