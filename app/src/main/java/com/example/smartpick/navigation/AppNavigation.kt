// File: app/src/main/java/com/example/smartpick/navigation/AppNavigation.kt
package com.example.smartpick.navigation

import androidx.navigation.navDeepLink
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.smartpick.R
import com.example.smartpick.core.utils.NavigationUtils.shouldShowBottomBar
import com.example.smartpick.features.auth.viewmodel.AuthViewModel
import com.example.smartpick.features.auth.ui.LoginScreen
import com.example.smartpick.features.auth.ui.SignUpScreen
import com.example.smartpick.features.comment.ui.CommentsScreen
import com.example.smartpick.features.feed.ui.FeedScreen
import com.example.smartpick.features.post_creation.ui.CreatePostScreen
import com.example.smartpick.features.home.ui.HomeScreen
import com.example.smartpick.features.notification.data.NotificationType
import com.example.smartpick.features.notification.ui.NotificationsScreen
import com.example.smartpick.features.notification.viewmodel.NotificationViewModel
import com.example.smartpick.features.profile.ui.main.ProfileScreen
import com.example.smartpick.features.profile.ui.saved.SavedCollectionScreen
import com.example.smartpick.features.profile.ui.edit.EditProfileScreen
import com.example.smartpick.features.post_detail.ui.PostDetailScreen
import com.example.smartpick.features.settings.ui.SettingsScreen
// IMPORT MỚI:
import com.example.smartpick.features.review.ui.ReviewHubScreen
import com.example.smartpick.features.review.ui.WriteReviewScreen

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel(),
    notificationViewModel: NotificationViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val currentUser by authViewModel.currentUser.collectAsState()
    val isInitializing by authViewModel.isInitializing.collectAsState()

    val isMainScreen = shouldShowBottomBar(currentRoute)
    val unreadCount by notificationViewModel.unreadCount.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.id?.let { userId ->
            notificationViewModel.subscribeToNotifications(userId)
        }
    }

    LaunchedEffect(currentUser, isInitializing) {
        if (isInitializing) return@LaunchedEffect

        val destination = navController.currentDestination ?: return@LaunchedEffect
        val route = destination.route

        if (currentUser != null) {
            if (route == Routes.Login.route || route == Routes.SignUp.route) {
                navController.navigate(Routes.Home.route) {
                    popUpTo(Routes.Login.route) { inclusive = true }
                }
            }
        } else {
            if (route != Routes.Login.route && route != Routes.SignUp.route) {
                navController.navigate(Routes.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    if (isInitializing) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF1E3A8A))
        }
    } else {
        Scaffold(
            topBar = {
                if (isMainScreen) {
                    MainTopBar(
                        onMenuClick = { navController.navigate(Routes.Settings.route) },
                        onNotificationClick = { navController.navigate(Routes.Notifications.route) },
                        tagText = when (currentRoute) {
                            Routes.Home.route -> stringResource(R.string.app_name)
                            Routes.Feed.route -> stringResource(R.string.feeds)
                            Routes.ReviewHub.route -> stringResource(R.string.reviews)
                            Routes.Saved.route -> stringResource(R.string.saved)
                            Routes.Profile.route -> stringResource(R.string.profile)
                            Routes.CreatePost.route -> stringResource(R.string.create_post)
                            Routes.Notifications.route -> stringResource(R.string.notifications)
                            else -> null
                        },
                        showNotificationBadge = unreadCount
                    )
                }
            },
            bottomBar = {
                if (isMainScreen) {
                    MainBottomBar(
                        navController = navController,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { padding ->

            NavHost(
                navController = navController,
                startDestination = Routes.Login.route,
                modifier = if (isMainScreen) Modifier.padding(padding) else Modifier.fillMaxSize()
            ) {
                composable(route = Routes.SignUp.route) {
                    SignUpScreen(
                        onLoginClick = { navController.navigate(Routes.Login.route) },
                        onNavigateToHome = {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.SignUp.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(route = Routes.Login.route) {
                    LoginScreen(
                        onNavigateToHome = {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.Login.route) { inclusive = true }
                            }
                        },
                        onNavigateToSignUp = { navController.navigate(Routes.SignUp.route) }
                    )
                }

                composable(route = Routes.Home.route) {
                    HomeScreen(navController = navController, paddingValues = PaddingValues(0.dp))
                }

                // FIX: Thay thế ChatBotScreen bằng ReviewHubScreen
                composable(route = Routes.ReviewHub.route) {
                    ReviewHubScreen(
                        onNavigateToWriteReview = { productId ->
                            navController.navigate(Routes.WriteReview.createRoute(productId))
                        }
                    )
                }

                composable(
                    route = Routes.WriteReview.route,
                    arguments = listOf(navArgument(Routes.WriteReview.ARG_PRODUCT_ID) { type = NavType.StringType })
                ) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getString(Routes.WriteReview.ARG_PRODUCT_ID) ?: ""
                    WriteReviewScreen(
                        productId = productId,
                        onBack = { navController.popBackStack() },
                        onReviewSubmitted = {
                            // Quay lại màn hình trước đó sau khi gửi thành công
                            navController.popBackStack()
                        }
                    )
                }

                composable(route = Routes.Checkout.route) {
                    com.example.smartpick.features.home.ui.CheckoutScreen(
                        onBack = { navController.popBackStack() },
                        onNavigateToSuccess = {
                            navController.navigate(Routes.Saved.route) {
                                popUpTo(Routes.Home.route)
                            }
                        }
                    )
                }

                composable(route = Routes.Saved.route) { SavedCollectionScreen(navController = navController) }

                composable(route = Routes.Profile.route) { ProfileScreen(navController) }

                composable(route = Routes.EditProfile.route) {
                    EditProfileScreen(onNavigateBack = { navController.popBackStack() })
                }

                composable(
                    route = Routes.PostDetail.route,
                    arguments = listOf(navArgument(Routes.PostDetail.ARG_POST_ID) {
                        type = NavType.StringType
                    })
                ) {
                    PostDetailScreen(
                        onBackClick = { navController.popBackStack() },
                        onCommentClick = { postId, ownerId ->
                            currentUser?.id?.let {
                                navController.navigate(Routes.Comments.createRoute(postId, ownerId))
                            }
                        }
                    )
                }

                composable(
                    route = "${Routes.Comments.route}?commentId={commentId}",
                    arguments = listOf(
                        navArgument("postId") { type = NavType.StringType },
                        navArgument("postOwnerId") { type = NavType.StringType; nullable = true; defaultValue = null },
                        navArgument("commentId") { type = NavType.StringType; nullable = true; defaultValue = null }
                    )
                ) { backStackEntry ->
                    val postId = backStackEntry.arguments?.getString("postId") ?: ""
                    val postOwnerId = backStackEntry.arguments?.getString("postOwnerId")
                    val commentId = backStackEntry.arguments?.getString("commentId")

                    CommentsScreen(
                        postId = postId,
                        postOwnerId = postOwnerId,
                        currentUserId = currentUser?.id ?: "",
                        targetCommentId = commentId,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(route = Routes.Feed.route) {
                    FeedScreen(
                        paddingValues = PaddingValues(0.dp),
                        onPostClick = { postId -> navController.navigate(Routes.PostDetail.createRoute(postId)) },
                        onCommentClick = { postId, ownerId ->
                            currentUser?.id?.let {
                                navController.navigate(Routes.Comments.createRoute(postId, ownerId))
                            }
                        },
                        onCreatePostClick = { navController.navigate(Routes.CreatePost.route) }
                    )
                }

                composable(route = Routes.CreatePost.route) {
                    CreatePostScreen(currentUser = currentUser, onClose = { navController.popBackStack() })
                }

                composable(route = Routes.Notifications.route) {
                    NotificationsScreen(
                        paddingValues = PaddingValues(0.dp),
                        currentUserId = currentUser?.id ?: "",
                        onNotificationClick = { notification ->
                            if (notification.type == NotificationType.COMMUNITY) {
                                val postId = notification.postId ?: ""
                                val commentId = notification.targetId

                                if (postId.isNotEmpty()) {
                                    if (!commentId.isNullOrEmpty()) {
                                        navController.navigate(Routes.CommentsFromNotification.createRoute(postId, commentId))
                                    } else {
                                        navController.navigate("comments_notification/$postId")
                                    }
                                }
                            }
                        }
                    )
                }

                composable(Routes.Settings.route) {
                    SettingsScreen(
                        onBackClick = { navController.popBackStack() },
                        onLogoutSuccess = {
                            navController.navigate(Routes.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    route = "${Routes.Saved.route}?category={category}",
                    arguments = listOf(
                        navArgument("category") {
                            type = NavType.StringType
                            defaultValue = "Giỏ hàng"
                        }
                    )
                ) { backStackEntry ->
                    val category = backStackEntry.arguments?.getString("category") ?: "Giỏ hàng"
                    SavedCollectionScreen(navController = navController, initialCategory = category)
                }

                composable(
                    route = Routes.CommentsFromNotification.route,
                    arguments = listOf(
                        navArgument("postId") { type = NavType.StringType },
                        navArgument("commentId") { type = NavType.StringType; nullable = true; defaultValue = null }
                    )
                ) { backStackEntry ->
                    val postId = backStackEntry.arguments?.getString("postId") ?: ""
                    val commentId = backStackEntry.arguments?.getString("commentId")

                    CommentsScreen(
                        postId = postId,
                        postOwnerId = null,
                        currentUserId = currentUser?.id ?: "",
                        currentUserName = currentUser?.fullName ?: "Một người dùng",
                        targetCommentId = commentId,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // 1. Luồng IN-APP: Xử lý click từ NotificationsScreen (Giữ nguyên logic của bạn, KHÔNG chứa deepLinks)
                composable(
                    route = Routes.CommentsFromNotification.route,
                    arguments = listOf(
                        navArgument("postId") { type = NavType.StringType },
                        navArgument("commentId") { type = NavType.StringType; nullable = true; defaultValue = null }
                    )
                ) { backStackEntry ->
                    val postId = backStackEntry.arguments?.getString("postId") ?: ""
                    val commentId = backStackEntry.arguments?.getString("commentId")

                    CommentsScreen(
                        postId = postId,
                        postOwnerId = null,
                        currentUserId = currentUser?.id ?: "",
                        targetCommentId = commentId,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // 2. Luồng SYSTEM PUSH: Hứng Deep Link từ hệ điều hành (khi bấm vào push notification)
                composable(
                    route = Routes.SystemPushDeepLink.route,
                    arguments = listOf(
                        navArgument("type") { type = NavType.StringType },
                        navArgument("postId") { type = NavType.StringType; nullable = true; defaultValue = null },
                        navArgument("targetId") { type = NavType.StringType; nullable = true; defaultValue = null }
                    ),
                    deepLinks = listOf(
                        navDeepLink { uriPattern = "smartpick://notification/{type}?post_id={postId}&target_id={targetId}" }
                    )
                ) { backStackEntry ->
                    val type = backStackEntry.arguments?.getString("type") ?: ""
                    val postId = backStackEntry.arguments?.getString("postId") ?: ""
                    val targetId = backStackEntry.arguments?.getString("targetId")

                    // Chuyển hướng tới màn hình tương ứng với loại thông báo
                    when (type) {
                        "comment", "reply" -> {
                            CommentsScreen(
                                postId = postId,
                                postOwnerId = null,
                                currentUserId = currentUser?.id ?: "",
                                targetCommentId = targetId,
                                onBackClick = {
                                    // Khi bấm từ bên ngoài vào, nếu ấn back thì nên đưa về Home thay vì thoát app
                                    navController.navigate(Routes.Home.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                        "like" -> {
                            PostDetailScreen(
                                onBackClick = {
                                    navController.navigate(Routes.Home.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onCommentClick = { pId, ownerId ->
                                    currentUser?.id?.let {
                                        navController.navigate(Routes.Comments.createRoute(pId, ownerId))
                                    }
                                }
                            )
                        }
                        else -> {
                            // System notification hoặc các type khác -> Mở trang chủ
                            LaunchedEffect(Unit) {
                                navController.navigate(Routes.Home.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}