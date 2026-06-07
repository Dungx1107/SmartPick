package com.example.smartpick.features.profile.ui.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.ReactionType
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.components.post.PostItem
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.features.feed.viewmodel.FeedViewModel

@Composable
fun LikedPostsScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    feedViewModel: FeedViewModel = hiltViewModel()
) {
    val reactedPosts by feedViewModel.reactedPosts.collectAsState()
    val isLoading by feedViewModel.isReactedLoading.collectAsState()

    LaunchedEffect(Unit) {
        feedViewModel.loadReactedPosts()
    }

    LikedPostsContent(
        paddingValues = paddingValues,
        isLoading = isLoading,
        reactedPosts = reactedPosts,
        onReactionClick = { postId, type -> feedViewModel.toggleReaction(postId, type) },
        onProductClick = { product ->
            if (product.id != null) {
                navController.navigate("product_detail/${product.id}")
            }
        },
        onNavigateToPostDetail = { postId -> navController.navigate("post_detail/$postId") }
    )
}

@Composable
fun LikedPostsContent(
    paddingValues: PaddingValues,
    isLoading: Boolean,
    reactedPosts: List<Triple<Post, User, Product?>>,
    onReactionClick: (String, ReactionType) -> Unit,
    onProductClick: (Product) -> Unit,
    onNavigateToPostDetail: (String) -> Unit, // FIX TẠI ĐÂY: Đổi dấu '=' thành dấu ':' chuẩn cú pháp tham số Kotlin
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                top = paddingValues.calculateTopPadding()
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFE63946),
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Bài viết đã thích",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = Color(0xFF1A1A1A)
                )
            }

            when {
                isLoading && reactedPosts.isEmpty() -> {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                reactedPosts.isEmpty() -> {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Bạn chưa thích bài đăng thảo luận nào",
                            color = Color(0xFF6C757D),
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 16.dp, start = 12.dp, end = 12.dp)
                    ) {
                        items(
                            items = reactedPosts,
                            key = { it.first.id ?: "" }
                        ) { (post, author, product) ->
                            PostItem(
                                post = post,
                                user = author,
                                product = product,
                                onReactionClick = onReactionClick,
                                onProductClick = onProductClick,
                                onPostClick = { onNavigateToPostDetail(post.id ?: "") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Màn hình bài viết đã thích")
@Composable
fun LikedPostsScreenPreview() {
    val mockPost = Post(
        id = "p_001",
        userId = "u_002",
        content = "Gợi ý cho anh em con bàn phím này gõ cực đỉnh cao, dùng mượt mà ổn định lắm nhé!",
        mediaUrls = emptyList(),
        createdAt = "2026-06-07T05:00:00Z",
        reactionCount = 42,
        currentUserReaction = ReactionType.LIKE
    )
    val mockUser = User(id = "u_002", fullName = "Nguyễn Xuân Dũng", avatarUrl = "")
    val mockProduct = Product(
        id = "prod_sample_01",
        ownerId = "owner_01",
        name = "Bàn phím cơ Custom Keychron K2 V2",
        brand = "Keychron",
        category = "Phụ kiện",
        price = 1850000.0,
        imageUrls = emptyList(),
        stock = 15,
        soldCount = 142
    )

    val mockReactedPosts = listOf(Triple(mockPost, mockUser, mockProduct))

    SmartPickTheme {
        LikedPostsContent(
            paddingValues = PaddingValues(),
            isLoading = false,
            reactedPosts = mockReactedPosts,
            onReactionClick = { _, _ -> },
            onProductClick = {},
            onNavigateToPostDetail = {}
        )
    }
}