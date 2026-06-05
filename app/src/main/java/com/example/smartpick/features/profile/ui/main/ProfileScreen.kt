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
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.smartpick.R
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.components.CreatePostPrompt
import com.example.smartpick.core.ui.components.post.PostItem
import com.example.smartpick.core.ui.theme.TextMuted
import com.example.smartpick.features.auth.viewmodel.AuthViewModel
import com.example.smartpick.features.feed.data.FeedRepository
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
    val soldItems by profileViewModel.soldItems.collectAsState() // State Hàng đã bán
    val isLoading by profileViewModel.isLoading.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(user?.id) {
        user?.id?.let { id -> profileViewModel.loadUserPosts(profileUserId = id, currentUserId = id) }
    }

    ProfileContent(
        user = user,
        posts = posts,
        soldItems = soldItems,
        isLoading = isLoading,
        paddingValues = paddingValues,
        onLogout = { authViewModel.logout() },
        onEditProfile = { navController.navigate(Routes.EditProfile.route) },
        onCreatePostClick = { navController.navigate(Routes.CreatePost.route) },
        onPostClick = { postId -> navController.navigate(Routes.PostDetail.createRoute(postId)) },
        onDeletePost = { postId ->
            profileViewModel.deletePost(postId,
                onSuccess = { Toast.makeText(context, "Đã xóa bài viết", Toast.LENGTH_SHORT).show() },
                onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            )
        },
        onHistoryClick = { navController.navigate("${Routes.Saved.route}?category=Lịch sử mua hàng") { launchSingleTop = true } },
        onNotificationsClick = { navController.navigate(Routes.Notifications.route) { launchSingleTop = true } }
    )
}

@Composable
fun ProfileContent(
    user: User?,
    posts: List<Triple<Post, User, com.example.smartpick.core.model.Product?>>,
    soldItems: List<FeedRepository.SoldItemDto>,
    isLoading: Boolean,
    paddingValues: PaddingValues,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit,
    onCreatePostClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onDeletePost: (String) -> Unit,
    onHistoryClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Bài viết của bạn", "Sản phẩm đã bán")
    val context = LocalContext.current

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
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // TABS CHUYỂN ĐỔI BÀI VIẾT / ĐÃ BÁN
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTab]), color = MaterialTheme.colorScheme.primary)
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) MaterialTheme.colorScheme.primary else TextMuted
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isLoading) {
            item { Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 3.dp) } }
        } else {
            if (selectedTab == 0) {
                if (posts.isEmpty()) {
                    item { Text(stringResource(R.string.ChuaCoBaiViet), modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), textAlign = TextAlign.Center, color = TextMuted) }
                } else {
                    items(posts, key = { it.first.id.toString() }) { (post, postUser, product) ->
                        PostItem(
                            post = post,
                            user = postUser,
                            currentUserId = user?.id,
                            product = product,
                            onPostClick = { onPostClick(post.id.toString()) },
                            onDeleteClick = { onDeletePost(post.id.toString()) },
                            onEditClick = { Toast.makeText(context, "Tính năng Sửa đang được phát triển", Toast.LENGTH_SHORT).show() }
                        )
                    }
                }
            } else {
                if (soldItems.isEmpty()) {
                    item { Text("Bạn chưa bán được sản phẩm nào", modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), textAlign = TextAlign.Center, color = TextMuted) }
                } else {
                    items(soldItems, key = { it.id }) { item ->
                        SoldItemCard(item)
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = onLogout, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.12f), contentColor = MaterialTheme.colorScheme.error), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(60.dp)) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(stringResource(R.string.DangXuat), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(text = stringResource(R.string.smartpick_version_1_0_0_2026), fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}

// CARD HIỂN THỊ HÀNG ĐÃ BÁN RẤT CHUYÊN NGHIỆP
@Composable
fun SoldItemCard(item: FeedRepository.SoldItemDto) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.products?.imageUrls?.firstOrNull(),
                contentDescription = null,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.products?.name ?: "Sản phẩm",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Đã bán: ${String.format("%,.0f đ", item.priceAtPurchase).replace(",", ".")} x ${item.quantity}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ngày xuất đơn: ${item.createdAt?.split("T")?.firstOrNull() ?: ""}",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}