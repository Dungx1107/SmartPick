package com.example.smartpick.features.profile.ui.main

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.features.auth.viewmodel.AuthViewModel
import com.example.smartpick.features.feed.viewmodel.FeedViewModel
import com.example.smartpick.features.profile.viewmodel.ProfileViewModel
import com.example.smartpick.features.seller.viewmodel.SellerViewModel
import com.example.smartpick.navigation.Routes

@Composable
fun ProfileScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    feedViewModel: FeedViewModel = hiltViewModel(),
    sellerViewModel: SellerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentUser by authViewModel.currentUser.collectAsState()
    val posts by profileViewModel.userPosts.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()

    val likedPosts by feedViewModel.reactedPosts.collectAsState()
    val sellerProducts by sellerViewModel.myProducts.collectAsState()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                currentUser?.id?.let { uid ->
                    profileViewModel.loadUserPosts(profileUserId = uid, currentUserId = uid)
                    feedViewModel.loadReactedPosts()
                    sellerViewModel.loadSellerData()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { uid ->
            profileViewModel.loadUserPosts(profileUserId = uid, currentUserId = uid)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        ProfileContent(
            user = currentUser,
            posts = posts,
            likedPosts = likedPosts,
            sellerProducts = sellerProducts,
            isLoading = isLoading,
            onEditProfile = { navController.navigate(Routes.EditProfile.route) },
            onSettingsClick = { navController.navigate(Routes.Settings.route) },
            onPostClick = { postId -> navController.navigate(Routes.PostDetail.createRoute(postId)) },
            onProductClick = { productId -> navController.navigate(Routes.ProductDetail.createRoute(productId)) },
            onDeletePost = { postId ->
                profileViewModel.deletePost(
                    postId = postId,
                    onSuccess = { Toast.makeText(context, "Đã xóa bài viết", Toast.LENGTH_SHORT).show() },
                    onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                )
            },
            onReactionClick = { postId, rType -> profileViewModel.toggleReaction(postId, rType) }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Màn hình Hồ sơ cá nhân")
@Composable
fun ProfileScreenPreview() {
    SmartPickTheme {
        val mockMe = User(
            id = "user_me",
            email = "dung.nx@gmail.com",
            username = "dungnx_2005",
            fullName = "Nguyễn Xuân Dũng",
            avatarUrl = null
        )

        val mockAuthor = User(
            id = "user_author",
            email = "author@gmail.com",
            username = "tac_gia_goc",
            fullName = "Người Đăng Bài Gốc",
            avatarUrl = null
        )

        val mockProduct = Product(
            id = "prod_01",
            ownerId = "user_author",
            name = "Bàn phím cơ Custom Keychron K2 V2",
            brand = "Keychron",
            category = "Phụ kiện",
            price = 1850000.0,
            imageUrls = emptyList(),
            status = "available",
            stock = 12,
            soldCount = 45
        )

        val mockPostOrigin = Post(
            id = "post_original_id",
            userId = "user_author",
            content = "Nội dung bài viết gốc cực kỳ hữu ích về AI và hệ thống dữ liệu ứng dụng thực tế.",
            createdAt = "2026-06-05T10:00:00Z"
        )

        // Giả lập danh sách bài viết của tôi, bao gồm cả bài viết tôi tự viết và bài viết tôi bấm SHARE về tường
        val mockPosts = listOf(
            // Bài viết 1: Bài viết do chính tôi SHARE về kèm dòng trạng thái (caption)
            Triple(
                Post(
                    id = "post_share_01",
                    userId = "user_me",
                    content = "Bài viết này hay quá anh em ơi, đáng để lưu lại và refactor theo ngay!",
                    createdAt = "2026-06-07T08:00:00Z",
                    sharedPostId = "post_original_id",
                    sharedPost = mockPostOrigin,       // Đưa bài đăng gốc vào cấu trúc dữ liệu lồng
                    sharedPostUser = mockAuthor       // Đưa thông tin tác giả bài đăng gốc vào
                ),
                mockMe,
                mockProduct // Sản phẩm đính kèm ban đầu của bài viết gốc
            ),
            // Bài viết 2: Bài đăng thông thường do tôi tự viết
            Triple(
                Post(
                    id = "post_normal_02",
                    userId = "user_me",
                    content = "Hôm nay vừa hoàn thiện xong tính năng bóc tách tab chia sẻ giống Facebook.",
                    createdAt = "2026-06-06T15:30:00Z"
                ),
                mockMe,
                null
            )
        )

        ProfileContent(
            user = mockMe,
            posts = mockPosts,
            likedPosts = emptyList(),
            sellerProducts = emptyList(),
            isLoading = false,
            onEditProfile = {},
            onSettingsClick = {},
            onPostClick = {},
            onProductClick = {},
            onDeletePost = {},
            onReactionClick = { _, _ -> }
        )
    }
}