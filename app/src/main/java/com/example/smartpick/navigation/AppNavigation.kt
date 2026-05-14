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
import com.example.smartpick.features.feed.ui.FeedScreen
import com.example.smartpick.features.post_creation.ui.CreatePostScreen
import com.example.smartpick.features.profile.ui.ProfileScreen
import com.example.smartpick.features.profile.ui.SavedCollectionScreen
import com.example.smartpick.features.profile.ui.EditProfileScreen
import com.example.smartpick.features.home.ui.HomeScreen

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val currentUser by authViewModel.currentUser.collectAsState()// Lắng nghe thông tin User hiện tại
    val isInitializing by authViewModel.isInitializing.collectAsState()

    // Tự động điều hướng khi trạng thái đăng nhập thay đổi
    LaunchedEffect(currentUser) {
        if (currentUser != null && currentRoute == Routes.Login.route) {
            // Nếu đã có user mà đang ở màn Login -> Nhảy vào Home luôn
            navController.navigate(Routes.Home.route) {
                popUpTo(Routes.Login.route) { inclusive = true }
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
                if (shouldShowBottomBar(currentRoute)) {
                    MainTopBar(
                        onMenuClick = {
                            // TODO: Mở navigation drawer
                        },
                        tagText = when (currentRoute) {
                            Routes.Home.route -> stringResource(R.string.app_name)
                            Routes.Feed.route -> stringResource(R.string.feeds)
                            Routes.ChatBot.route -> stringResource(R.string.ai_curator)
                            Routes.Saved.route -> stringResource(R.string.saved)
                            Routes.Profile.route -> stringResource(R.string.profile)
                            Routes.CreatePost.route -> stringResource(R.string.create_post)
                            else -> null
                        },
                        showCartBadge = currentRoute == Routes.Home.route // Chỉ hiện tag AI ASSISTANT ở màn Home
                    )
                }
            },
            bottomBar = {
                if (shouldShowBottomBar(currentRoute)) {
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
                modifier = Modifier.padding(padding)
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

                // Các màn hình đơn giản khác cũng làm tương tự
                composable(route = Routes.ChatBot.route) { ChatbotScreen() }
                composable(route = Routes.Saved.route) { SavedCollectionScreen() }
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
                ) { entry ->
                    val postId = entry.arguments?.getString(Routes.PostDetail.ARG_POST_ID) ?: ""
//                    PostDetailScreen(
//                        postId = postId,
//                        onBackClick = { navController.popBackStack() }
//                    )
                }

                composable(route = Routes.Feed.route) {
                    FeedScreen(
                        paddingValues = PaddingValues(0.dp),
                        onPostClick = { postId ->
                            navController.navigate(Routes.PostDetail.createRoute(postId))
                        },
                        onCommentClick = { postId ->
                            navController.navigate(Routes.Comments.createRoute(postId))
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
            }
        }
    }
}
