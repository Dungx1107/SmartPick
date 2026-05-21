package com.example.smartpick.features.profile.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartpick.R
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.components.CreatePostPrompt
import com.example.smartpick.core.ui.components.PostItem
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.core.ui.theme.TextMuted
import com.example.smartpick.features.auth.viewmodel.AuthViewModel
import com.example.smartpick.features.profile.viewmodel.ProfileViewModel
import com.example.smartpick.navigation.Routes

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val user by authViewModel.currentUser.collectAsState()
    val posts by profileViewModel.userPosts.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()

    // Tự động tải bài viết đồng bộ cấu trúc 2 chiều
    LaunchedEffect(user?.id) {
        user?.id?.let { id ->
            profileViewModel.loadUserPosts(profileUserId = id, currentUserId = id)
        }
    }

    ProfileContent(
        user = user,
        posts = posts,
        isLoading = isLoading,
        onLogout = { authViewModel.logout() },
        onEditProfile = { navController.navigate(Routes.EditProfile.route) },
        onCreatePostClick = { navController.navigate(Routes.CreatePost.route) },
        onPostClick = { postId -> navController.navigate(Routes.PostDetail.createRoute(postId)) },
        onHistoryClick = {
            navController.navigate("${Routes.Saved.route}?category=Lịch sử mua hàng") { launchSingleTop = true }
        },
        onNotificationsClick = {
            navController.navigate(Routes.Notifications.route) { launchSingleTop = true }
        }
    )
}

@Composable
fun ProfileContent(
    user: User?,
    posts: List<Triple<Post, User, Product?>>,
    isLoading: Boolean,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit,
    onCreatePostClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onHistoryClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileHeaderCard(user = user, onEditProfile = onEditProfile)
                Spacer(modifier = Modifier.height(28.dp))
                SettingsBentoGrid(onHistoryClick = onHistoryClick, onNotificationsClick = onNotificationsClick)
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                CreatePostPrompt(user = user, onClick = onCreatePostClick)
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(R.string.BaiVietCuaBan),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        if (isLoading && posts.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        } else if (posts.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.ChuaCoBaiViet),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                    textAlign = TextAlign.Center,
                    color = TextMuted
                )
            }
        } else {
            // Duyệt danh sách Triple đã được xử lý lồng ghép bài gốc từ Repository
            items(
                items = posts,
                key = { it.first.id.toString() }
            ) { (post, postUser, product) ->
                PostItem(
                    post = post,
                    user = postUser,
                    product = product,
                    onPostClick = { onPostClick(post.id.toString()) },
                    onProductClick = { /* Xử lý sản phẩm */ },
                    isDetailView = false
                )
            }
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(stringResource(R.string.DangXuat), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = stringResource(R.string.smartpick_version_1_0_0_2026),
                    fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold, letterSpacing = 1.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    SmartPickTheme {
        val mockUser = User(id = "1", fullName = "Nguyễn Xuân Dũng", username = "dungnx")
        ProfileContent(
            user = mockUser,
            posts = emptyList(),
            isLoading = false,
            onLogout = {},
            onEditProfile = {},
            onCreatePostClick = {},
            onPostClick = {}
        )
    }
}