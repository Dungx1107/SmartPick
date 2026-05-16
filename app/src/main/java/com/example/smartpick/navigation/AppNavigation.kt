package com.example.smartpick.navigation

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
import com.example.smartpick.features.chatbot.ui.ChatbotScreen
import com.example.smartpick.features.auth.ui.LoginScreen
import com.example.smartpick.features.auth.ui.SignUpScreen
import com.example.smartpick.features.comment.ui.CommentsScreen
import com.example.smartpick.features.feed.ui.FeedScreen
import com.example.smartpick.features.post_creation.ui.CreatePostScreen
import com.example.smartpick.features.home.ui.HomeScreen
import com.example.smartpick.features.notification.ui.NotificationsScreen
import com.example.smartpick.features.notification.viewmodel.NotificationViewModel
import com.example.smartpick.features.profile.ui.main.ProfileScreen
import com.example.smartpick.features.profile.ui.saved.SavedCollectionScreen
import com.example.smartpick.features.profile.ui.edit.EditProfileScreen
import com.example.smartpick.features.post_detail.ui.PostDetailScreen
import com.example.smartpick.features.settings.ui.SettingsScreen


@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel(),
    notificationViewModel: NotificationViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val currentUser by authViewModel.currentUser.collectAsState()// Lắng nghe thông tin User hiện tại
    val isInitializing by authViewModel.isInitializing.collectAsState()

    val isMainScreen = shouldShowBottomBar(currentRoute)

    val unreadCount by notificationViewModel.unreadCount.collectAsState()

    // Lắng nghe thông báo ngay khi có userId
    LaunchedEffect(currentUser) {
        currentUser?.id?.let { userId ->
            notificationViewModel.subscribeToNotifications(userId)
        }
    }

    // Tự động điều hướng khi trạng thái đăng nhập thay đổi
    LaunchedEffect(currentUser, isInitializing) {
        // 1. Nếu đang khởi tạo thì không làm gì cả
        if (isInitializing) return@LaunchedEffect

        // 2. Kiểm tra xem NavController đã sẵn sàng chưa (tránh crash "graph not set")
        // Nếu currentDestination == null nghĩa là NavHost chưa gắn graph thành công
        val destination = navController.currentDestination ?: return@LaunchedEffect
        val route = destination.route

        if (currentUser != null) {
            // Nếu đã có user mà đang ở màn Auth -> Nhảy vào Home
            if (route == Routes.Login.route || route == Routes.SignUp.route) {
                navController.navigate(Routes.Home.route) {
                    popUpTo(Routes.Login.route) { inclusive = true }
                }
            }
        } else {
            // Nếu currentUser == null (đã logout hoặc chưa login)
            // Chỉ navigate nếu đang ở các màn hình bên trong (không phải Login/SignUp)
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
                        onMenuClick = {
                            navController.navigate(Routes.Settings.route)
                        },
                        onNotificationClick = {
                            navController.navigate(Routes.Notifications.route)
                        },
                        tagText = when (currentRoute) {
                            Routes.Home.route -> stringResource(R.string.app_name)
                            Routes.Feed.route -> stringResource(R.string.feeds)
                            Routes.ChatBot.route -> stringResource(R.string.ai_curator)
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
                                } // 1. Quay về màn hình đầu tiên (thường là Home) để tránh tích tụ stack
                                launchSingleTop =
                                    true //Tránh việc mở màn hình đó nhiều lần khi nhấn liên tục vào icon
                                restoreState = true  //Khôi phục lại trạng thái khi quay lại tab đó
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
                    HomeScreen(
                        navController = navController,
                        paddingValues = PaddingValues(0.dp)
                    )
                }

                composable(route = Routes.ChatBot.route) { ChatbotScreen() }
                composable(route = Routes.Saved.route) {
                    SavedCollectionScreen(navController = navController)
                }
                composable(route = Routes.Profile.route) {
                    ProfileScreen(
                        navController
                    )
                }
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
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Routes.Comments.route,
                    arguments = listOf(
                        navArgument("postId") { type = NavType.StringType },
                        navArgument("postOwnerId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val postId = backStackEntry.arguments?.getString("postId") ?: ""
                    val postOwnerId = backStackEntry.arguments?.getString("postOwnerId") ?: ""
                    val currentUser by authViewModel.currentUser.collectAsState()

                    CommentsScreen(
                        postId = postId,
                        postOwnerId = postOwnerId,
                        currentUserId = currentUser?.id ?: "",
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(route = Routes.Feed.route) {
                    FeedScreen(
                        paddingValues = PaddingValues(0.dp),
                        onPostClick = { postId ->
                            navController.navigate(Routes.PostDetail.createRoute(postId))
                        },
                        onCommentClick = { postId, ownerId ->
                            currentUser?.id?.let { //Chỉ chuyển trang khi người dùng đã đăng nhập
                                navController.navigate(Routes.Comments.createRoute(postId, ownerId))
                            }
                        },
                        onCreatePostClick = { navController.navigate(Routes.CreatePost.route) }
                    )
                }

                composable(route = Routes.CreatePost.route) {
                    CreatePostScreen(
                        currentUser = currentUser,
                        onClose = {
                            navController.popBackStack()
                        },
                    )
                }

                composable(route = Routes.Notifications.route) {
                    NotificationsScreen(
                        paddingValues = PaddingValues(0.dp),
                        onNotificationClick = { notification ->
                            println("Clicked on notification: ${notification.title}")
                        },
                        currentUserId = currentUser?.id ?: ""
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
            }
        }
    }
}
