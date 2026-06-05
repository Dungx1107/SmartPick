package com.example.smartpick.features.profile.ui.main

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.smartpick.R
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.ReactionType
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.components.CreatePostPrompt
import com.example.smartpick.core.ui.components.post.PostItem
import com.example.smartpick.core.ui.theme.TextMuted
import com.example.smartpick.features.auth.viewmodel.AuthViewModel
import com.example.smartpick.features.profile.viewmodel.ProfileViewModel
import com.example.smartpick.navigation.Routes

@Composable
fun ProfileScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val user by authViewModel.currentUser.collectAsState()
    val posts by profileViewModel.userPosts.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    val context = LocalContext.current

    // FIX UX: Bộ lắng nghe vòng đời. Mỗi khi quay lại trang Profile, bắt buộc load lại data mới nhất
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                user?.id?.let { id -> profileViewModel.loadUserPosts(profileUserId = id, currentUserId = id) }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(user?.id) {
        user?.id?.let { id -> profileViewModel.loadUserPosts(profileUserId = id, currentUserId = id) }
    }

    ProfileContent(
        user = user,
        posts = posts,
        isLoading = isLoading,
        paddingValues = paddingValues,
        onLogout = { authViewModel.logout() },
        onEditProfile = { navController.navigate(Routes.EditProfile.route) },
        onCreatePostClick = { navController.navigate(Routes.CreatePost.route) },
        onPostClick = { postId -> navController.navigate(Routes.PostDetail.createRoute(postId)) },
        onHistoryClick = { navController.navigate("${Routes.Saved.route}?category=Lịch sử mua hàng") { launchSingleTop = true } },
        onNotificationsClick = { navController.navigate(Routes.Notifications.route) { launchSingleTop = true } },
        onDeletePost = { postId ->
            profileViewModel.deletePost(
                postId = postId,
                onSuccess = { Toast.makeText(context, "Đã xóa bài viết", Toast.LENGTH_SHORT).show() },
                onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            )
        },
        onEditPost = { postId -> navController.navigate("edit_post/$postId") },
        onReactionClick = { postId, type -> profileViewModel.toggleReaction(postId, type) },
        onShareClick = { postId, caption ->
            profileViewModel.sharePost(postId, caption) {
                Toast.makeText(context, "Đã chia sẻ lên Trang cá nhân!", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

@Composable
fun ProfileContent(
    user: User?,
    posts: List<Triple<Post, User, Product?>>,
    isLoading: Boolean,
    paddingValues: PaddingValues,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit,
    onCreatePostClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onHistoryClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onDeletePost: (String) -> Unit = {},
    onEditPost: (String) -> Unit = {},
    onReactionClick: (String, ReactionType) -> Unit = { _, _ -> },
    onShareClick: (String, String) -> Unit = { _, _ -> }
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(top = paddingValues.calculateTopPadding() + 8.dp, bottom = paddingValues.calculateBottomPadding() + 32.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
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
                Text("Bài viết của bạn", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 16.dp))
            }
        }

        if (isLoading && posts.isEmpty()) {
            item { Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) } }
        } else if (posts.isEmpty()) {
            item { Text("Bạn chưa có bài viết nào.", modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), textAlign = TextAlign.Center, color = TextMuted) }
        } else {
            items(items = posts, key = { it.first.id.toString() }) { (post, postUser, product) ->
                PostItem(
                    post = post, user = postUser, currentUserId = user?.id, product = product, isDetailView = false,
                    onPostClick = { onPostClick(post.id.toString()) }, onProductClick = { },
                    onReactionClick = { id, reaction -> onReactionClick(id, reaction) },
                    onShareClick = { caption -> onShareClick(post.id.toString(), caption) },
                    onDeleteClick = { onDeletePost(post.id.toString()) },
                    onEditClick = { onEditPost(post.id.toString()) }
                )
            }
        }

        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = onLogout, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.12f), contentColor = MaterialTheme.colorScheme.error), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(60.dp)) {
                    Icon(Icons.Default.ExitToApp, null); Spacer(modifier = Modifier.width(12.dp))
                    Text("Đăng xuất", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text("SmartPick Version 1.0", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
            }
        }
    }
}