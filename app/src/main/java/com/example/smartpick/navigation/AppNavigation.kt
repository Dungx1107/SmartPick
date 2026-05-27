package com.example.smartpick.navigation

import androidx.navigation.navDeepLink
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.smartpick.core.utils.NavigationUtils.shouldShowTopBar
import com.example.smartpick.features.auth.viewmodel.AuthViewModel
import com.example.smartpick.features.auth.ui.LoginScreen
import com.example.smartpick.features.auth.ui.SignUpScreen
import com.example.smartpick.features.checkout.ui.CheckoutScreen
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

    val showBottomBar = shouldShowBottomBar(currentRoute)
    val showTopBar = shouldShowTopBar(currentRoute)
    val unreadCount by notificationViewModel.unreadCount.collectAsState()

    var feedScrollToTopTrigger by remember { mutableLongStateOf(0L) }

    LaunchedEffect(currentUser) {
        currentUser?.id?.let { userId ->
            notificationViewModel.subscribeToNotifications(userId)
        }
    }

    LaunchedEffect(currentUser, isInitializing) {
        if (isInitializing) return@LaunchedEffect
        val route = navController.currentDestination?.route

        if (currentUser == null) {
            if (route != Routes.Login.route && route != Routes.SignUp.route) {
                navController.navigate(Routes.Login.route) { popUpTo(0) { inclusive = true } }
            }
        } else {
            if (route == Routes.Login.route || route == Routes.SignUp.route) {
                navController.navigate(Routes.Home.route) {
                    popUpTo(Routes.Login.route) {
                        inclusive = true
                    }
                }
            }
        }
    }

    if (isInitializing) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
        }
    } else {
        Scaffold(
            topBar = {
                if (showTopBar) {
                    MainTopBar(
                        onMenuClick = { navController.navigate(Routes.Settings.route) },
                        onNotificationClick = { navController.navigate(Routes.Notifications.route) },
                        onTitleClick = {
                            if (currentRoute == Routes.Feed.route) {
                                feedScrollToTopTrigger = System.currentTimeMillis()
                            }
                        },
                        tagText = when (currentRoute) {
                            Routes.Home.route -> stringResource(R.string.app_name)
                            Routes.Feed.route -> stringResource(R.string.feeds)
                            Routes.ReviewHub.route -> stringResource(R.string.reviews)
                            Routes.Saved.route -> stringResource(R.string.saved)
                            Routes.Profile.route -> stringResource(R.string.profile)
                            Routes.CreatePost.route -> stringResource(R.string.create_post)
                            Routes.Notifications.route -> stringResource(R.string.notifications)
                            Routes.EditProfile.route -> stringResource(R.string.profile)
                            else -> null
                        },
                        showNotificationBadge = unreadCount
                    )
                }
            },
            bottomBar = {
                if (showBottomBar) {
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
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->

            NavHost(
                navController = navController,
                startDestination = Routes.Login.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(route = Routes.SignUp.route) {
                    SignUpScreen(
                        onLoginClick = { navController.navigate(Routes.Login.route) },
                        onNavigateToHome = {}
                    )
                }

                composable(route = Routes.Login.route) {
                    LoginScreen(
                        onNavigateToHome = {},
                        onNavigateToSignUp = { navController.navigate(Routes.SignUp.route) }
                    )
                }

                composable(route = Routes.Home.route) {
                    HomeScreen(navController = navController, paddingValues = innerPadding)
                }

                composable(route = Routes.Feed.route) {
                    FeedScreen(
                        paddingValues = innerPadding,
                        scrollToTopTrigger = feedScrollToTopTrigger,
                        onPostClick = { postId ->
                            navController.navigate(
                                Routes.PostDetail.createRoute(
                                    postId
                                )
                            )
                        },
                        onCreatePostClick = { navController.navigate(Routes.CreatePost.route) }
                    )
                }

                composable(route = Routes.Profile.route) {
                    ProfileScreen(navController = navController, paddingValues = innerPadding)
                }

                composable(route = Routes.ReviewHub.route) {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)) {
                        ReviewHubScreen(
                            paddingValues = innerPadding,
                            onNavigateToWriteReview = { productId ->
                                navController.navigate(
                                    Routes.WriteReview.createRoute(
                                        productId
                                    )
                                )
                            }
                        )
                    }
                }

                composable(route = Routes.Saved.route) {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)) {
                        SavedCollectionScreen(
                            navController = navController,
                            paddingValues = PaddingValues(0.dp)
                        )
                    }
                }

                composable(
                    route = "${Routes.Saved.route}?category={category}",
                    arguments = listOf(navArgument("category") {
                        type = NavType.StringType; defaultValue = "Giỏ hàng"
                    })
                ) { backStackEntry ->
                    val category = backStackEntry.arguments?.getString("category") ?: "Giỏ hàng"
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)) {
                        SavedCollectionScreen(
                            navController = navController,
                            initialCategory = category,
                            paddingValues = PaddingValues(0.dp)
                        )
                    }
                }

                composable(route = Routes.Notifications.route) {
                    NotificationsScreen(
                        paddingValues = innerPadding,
                        currentUserId = currentUser?.id ?: "",
                        onNotificationClick = { notification ->
                            if (notification.type == NotificationType.COMMUNITY) {
                                val postId = notification.postId ?: ""
                                val commentId = notification.targetId

                                if (postId.isNotEmpty()) {
                                    if (!commentId.isNullOrEmpty()) navController.navigate(
                                        Routes.CommentsFromNotification.createRoute(
                                            postId,
                                            commentId
                                        )
                                    )
                                    else navController.navigate("comments_notification/$postId")
                                }
                            }
                        }
                    )
                }

                composable(
                    route = Routes.WriteReview.route,
                    arguments = listOf(navArgument(Routes.WriteReview.ARG_PRODUCT_ID) {
                        type = NavType.StringType
                    })
                ) { backStackEntry ->
                    val productId =
                        backStackEntry.arguments?.getString(Routes.WriteReview.ARG_PRODUCT_ID) ?: ""
                    WriteReviewScreen(
                        productId = productId,
                        onBack = { navController.popBackStack() },
                        onReviewSubmitted = { navController.popBackStack() })
                }

                composable(route = Routes.Checkout.route) {
                    CheckoutScreen(
                        onBack = { navController.popBackStack() },
                        onNavigateToSuccess = {
                            navController.navigate(Routes.Saved.route) {
                                popUpTo(
                                    Routes.Home.route
                                )
                            }
                        }
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
                    PostDetailScreen(onBackClick = { navController.popBackStack() })
                }

                composable(route = Routes.CreatePost.route) {
                    CreatePostScreen(
                        currentUser = currentUser,
                        onClose = { navController.popBackStack() })
                }

                composable(Routes.Settings.route) {
                    SettingsScreen(
                        onBackClick = { navController.popBackStack() },
                        onLogoutSuccess = {}
                    )
                }

                composable(
                    route = Routes.CommentsFromNotification.route,
                    arguments = listOf(
                        navArgument("postId") { type = NavType.StringType },
                        navArgument("commentId") {
                            type = NavType.StringType; nullable = true; defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val postId = backStackEntry.arguments?.getString("postId") ?: ""
                    val commentId = backStackEntry.arguments?.getString("commentId")
                    CommentsScreen(
                        postId = postId, postOwnerId = null, currentUserId = currentUser?.id ?: "",
                        currentUserName = currentUser?.fullName ?: "Một người dùng",
                        targetCommentId = commentId,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}