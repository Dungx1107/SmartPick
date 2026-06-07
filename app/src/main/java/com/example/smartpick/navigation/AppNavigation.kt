package com.example.smartpick.navigation

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.smartpick.core.utils.NavigationUtils.shouldShowBottomBar
import com.example.smartpick.features.auth.ui.LoginScreen
import com.example.smartpick.features.auth.ui.SignUpScreen
import com.example.smartpick.features.auth.viewmodel.AuthViewModel
import com.example.smartpick.features.cart.ui.CartScreen
import com.example.smartpick.features.cart.viewmodel.CartViewModel
import com.example.smartpick.features.checkout.ui.CheckoutScreen
import com.example.smartpick.features.comment.ui.CommentsScreen
import com.example.smartpick.features.feed.ui.FeedScreen
import com.example.smartpick.features.home.ui.HomeScreen
import com.example.smartpick.features.notification.data.NotificationType
import com.example.smartpick.features.notification.ui.NotificationsScreen
import com.example.smartpick.features.notification.viewmodel.NotificationViewModel
import com.example.smartpick.features.post_creation.ui.CreatePostScreen
import com.example.smartpick.features.post_creation.ui.EditPostScreen
import com.example.smartpick.features.post_detail.ui.PostDetailScreen
import com.example.smartpick.features.product_detail.ui.ProductDetailScreen
import com.example.smartpick.features.profile.ui.edit.EditProfileScreen
import com.example.smartpick.features.profile.ui.main.ProfileScreen
import com.example.smartpick.features.profile.ui.saved.LikedPostsScreen
import com.example.smartpick.features.profile.ui.saved.SavedOrdersScreen
import com.example.smartpick.features.review.ui.ReviewHubScreen
import com.example.smartpick.features.review.ui.WriteReviewScreen
import com.example.smartpick.features.seller.ui.SellerDashboardScreen
import com.example.smartpick.features.settings.ui.SettingsScreen

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
            bottomBar = {
                if (showBottomBar) {
                    MainBottomBar(
                        navController = navController,
                        unreadCount = unreadCount, // KHẮC PHỤC LỖI: Truyền unreadCount xuống MainBottomBar
                        onNavigate = { route ->
                            // Tránh điều hướng lại nếu đang ở chính màn hình đó
                            if (currentRoute?.substringBefore("?")
                                    ?.substringBefore("/") != route.substringBefore("?")
                                    ?.substringBefore("/")
                            ) {
                                navController.navigate(route) {
                                    // Pop up về start destination của đồ thị để tránh tích tụ backstack
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Tránh khởi tạo lại màn hình nếu nó đã nằm ở trên cùng của backstack
                                    launchSingleTop = true
                                    // Khôi phục lại trạng thái cuộn hoặc dữ liệu trước đó của màn hình khi quay lại
                                    restoreState = true
                                }
                            }
                        },
                        onFeedReselect = {
                            feedScrollToTopTrigger = System.currentTimeMillis()
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

                composable(Routes.Home.route) {
                    HomeScreen(
                        navController = navController,
                        paddingValues = innerPadding,
                        onProductClick = { product ->
                            if (product.id != null) {
                                navController.navigate(Routes.ProductDetail.createRoute(product.id))
                            }
                        },
                    )

                }

                composable(Routes.Feed.route) {
                    FeedScreen(
                        paddingValues = innerPadding,
                        scrollToTopTrigger = feedScrollToTopTrigger,
                        onPostClick = { postId ->
                            navController.navigate(Routes.PostDetail.createRoute(postId))
                        },
                        onCreatePostClick = { navController.navigate(Routes.CreatePost.route) },
                        onEditPostClick = { postId ->
                            navController.navigate(Routes.EditPost.createRoute(postId))
                        },
                    )
                }

                composable(route = Routes.Profile.route) {
                    ProfileScreen(navController = navController, paddingValues = innerPadding)
                }

                composable(route = Routes.ReviewHub.route) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        SavedOrdersScreen(
                            navController = navController,
                            paddingValues = PaddingValues(0.dp)
                        )
                    }
                }
                composable(route = Routes.LikedPosts.route) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        LikedPostsScreen(
                            navController = navController,
                            paddingValues = PaddingValues(0.dp)
                        )
                    }
                }
                composable(route = Routes.Notifications.route) {
                    NotificationsScreen(
                        paddingValues = innerPadding,
                        currentUserId = currentUser?.id ?: "",
                        viewModel = notificationViewModel,
                        onNotificationClick = { notification ->
                            if (notification.type == NotificationType.COMMUNITY) {
                                val postId = notification.postId ?: ""
                                val commentId = notification.targetId

                                if (postId.isNotEmpty()) {
                                    navController.navigate(
                                        Routes.PostDetail.createRoute(
                                            postId,
                                            commentId
                                        )
                                    )
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

                composable(
                    route = Routes.Checkout.route,
                    arguments = listOf(
                        navArgument(Routes.Checkout.ARG_PRODUCT_ID) {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        },
                        navArgument(Routes.Checkout.ARG_QUANTITY) {
                            type = NavType.IntType
                            defaultValue = 1
                        },
                        navArgument(Routes.Checkout.ARG_CART_ITEM_IDS) {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val productId =
                        backStackEntry.arguments?.getString(Routes.Checkout.ARG_PRODUCT_ID)
                    val quantity =
                        backStackEntry.arguments?.getInt(Routes.Checkout.ARG_QUANTITY) ?: 1
                    val cartItemIds =
                        backStackEntry.arguments?.getString(Routes.Checkout.ARG_CART_ITEM_IDS)

                    CheckoutScreen(
                        onBack = { navController.popBackStack() },
                        onNavigateToSuccess = {
                            navController.navigate("${Routes.Saved.route}?category=Lịch sử mua hàng") {
                                popUpTo(Routes.Home.route) {
                                    inclusive = false
                                } // Giữ lại trang chủ trong backstack
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(route = Routes.EditProfile.route) {
                    EditProfileScreen(onNavigateBack = { navController.popBackStack() })
                }

                composable(
                    route = Routes.PostDetail.route,
                    arguments = listOf(
                        navArgument(Routes.PostDetail.ARG_POST_ID) { type = NavType.StringType },
                        navArgument(Routes.PostDetail.ARG_COMMENT_ID) {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
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

                composable(
                    route = Routes.EditPost.route,
                    arguments = listOf(navArgument(Routes.EditPost.ARG_POST_ID) {
                        type = NavType.StringType
                    })
                ) { backStackEntry ->
                    val postId =
                        backStackEntry.arguments?.getString(Routes.EditPost.ARG_POST_ID) ?: ""
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        EditPostScreen(
                            postId = postId,
                            onClose = { navController.popBackStack() }
                        )
                    }
                }

                composable(
                    route = Routes.ProductDetail.route,
                    arguments = listOf(
                        navArgument(Routes.ProductDetail.ARG_PRODUCT_ID) {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    val productId =
                        backStackEntry.arguments?.getString(Routes.ProductDetail.ARG_PRODUCT_ID)
                            ?: ""

                    Box(modifier = Modifier.fillMaxSize()) {
                        ProductDetailScreen(
                            productId = productId,
                            navController = navController
                        )
                    }
                }

                composable(route = Routes.Cart.route) {
                    val cartViewModel: CartViewModel = hiltViewModel()
                    val cartItems by cartViewModel.cartItems.collectAsState()
                    val selectedIds by cartViewModel.selectedIds.collectAsState()

                    CartScreen(
                        cartItems = cartItems,
                        selectedIds = selectedIds,
                        onToggleSelect = { id -> cartViewModel.toggleSelection(id) },
                        onSelectAll = { isSelectAll -> cartViewModel.selectAll(isSelectAll) },
                        onIncrease = { item -> cartViewModel.increaseQuantity(item) },
                        onDecrease = { item -> cartViewModel.decreaseQuantity(item) },
                        onRemove = { id -> cartViewModel.removeItem(id) },
                        onBack = {
                            navController.popBackStack()     // Quay lại màn hình trước đó
                        },
                        onNavigateToPost = { postId ->
                            navController.navigate(Routes.PostDetail.createRoute(postId))
                        },
                        onCheckout = { selectedList ->
                            val cartItemIdsParam = selectedList.joinToString(",")
                            navController.navigate(
                                Routes.Checkout.createRoute(cartItemIds = cartItemIdsParam)
                            )
                        },
                        onProductClick = { productId ->
                            navController.navigate(Routes.ProductDetail.createRoute(productId))
                        }
                    )
                }

                composable(Routes.SellerDashboard.route) {
                    SellerDashboardScreen(
                        onBackClick = { navController.popBackStack() },
                        navController = navController
                    )
                }
            }
        }
    }
}