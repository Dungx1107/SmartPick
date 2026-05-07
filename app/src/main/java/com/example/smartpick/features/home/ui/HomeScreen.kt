package com.example.smartpick.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.R
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.theme.PageBg
import com.example.smartpick.core.ui.theme.TextPrimary
import com.example.smartpick.core.ui.theme.TextSecondary
import com.example.smartpick.features.auth.viewmodel.AuthViewModel
import com.example.smartpick.features.feed.ui.components.PostItem
import com.example.smartpick.features.home.ui.components.AICuratorBanner
import com.example.smartpick.features.home.ui.components.HeroBanner
import com.example.smartpick.features.home.ui.components.SearchBar

@Composable
fun HomeScreenRoute(
    authViewModel: AuthViewModel = hiltViewModel(),
    // viewModel: HomeViewModel = hiltViewModel(), // Quản lý dữ liệu Feed/Home
    onPostClick: (String) -> Unit = {},
    onCommentClick: (String) -> Unit = {}
) {
    val user by authViewModel.currentUser.collectAsState()
    // val posts by viewModel.posts.collectAsState()

    user?.let { currentUser ->
        HomeScreen(
            user = currentUser,
            posts = emptyList(), // Sẽ thay bằng dữ liệu từ ViewModel
            onPostClick = onPostClick,
            onCommentClick = onCommentClick
        )
    }
}

@Composable
fun HomeScreen(
    user: User,
    posts: List<Triple<Post, User, Product?>> = emptyList(),
    onPostClick: (String) -> Unit = {},
    onCommentClick: (String) -> Unit = {},
    onCreatePostClick: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Header & Search Bar
        item {
            Spacer(Modifier.height(12.dp))
            SearchBar()
        }

        // 2. Hero Banner (Khuyến mãi/Sự kiện)
        item {
            Spacer(Modifier.height(16.dp))
            HeroBanner()
        }

        // 3. AI Curator (Tính năng đặc trưng của SmartPick)
        item {
            Spacer(Modifier.height(16.dp))
            AICuratorBanner()
        }

        // 4. Section Title
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                Text(
                    text = stringResource(R.string.GoiYChoBan),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                user.fullName?.let {
                    Text(
                        text = stringResource(R.string.DuaTrenSoThich, it),
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // 5. Social Feed (Danh sách bài đăng tích hợp sản phẩm)
        items(posts, key = { it.first.id.toString() }) { (post, author, product) ->
            Box(modifier = Modifier.padding(bottom = 8.dp)) {
                PostItem(
                    post = post,
                    user = author,
                    product = product,
                    onPostClick = { onPostClick(post.id.toString()) },
                    onCommentClick = { onCommentClick(post.id.toString()) }
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun HomeScreenPreview() {
    val sampleUser = User(
        id = "123",
        fullName = "Nguyễn Xuân Dũng",
        username = "dungx1107",
        email = "dung.nx@vnu.edu.vn",
        avatarUrl = null
    )

    val mockProducts = listOf(
        Product(
            id = "pro_1",
            ownerId = "123",
            name = "Tai nghe chống ồn WH-1000XM5",
            brand = "SONY",
            price = 8490000.0,
            category = "Audio",
            imageUrls = listOf("https://via.placeholder.com/150")
        )
    )

    val mockPosts = listOf(
        Triple(
            Post(
                id = "p1",
                userId = "123",
                productId = "pro_1",
                content = "Nghe nhạc cực đỉnh với con Sony XM5 này, chống ồn 10/10 luôn!",
                createdAt = "2 giờ trước",
                mediaUrls = listOf("https://via.placeholder.com/600x400")
            ),
            sampleUser,
            mockProducts[0]
        ),
        Triple(
            Post(
                id = "p2",
                userId = "456",
                content = "Mọi người tư vấn giúp mình có nên nâng cấp lên iPad M4 lúc này không?",
                createdAt = "5 giờ trước",
                mediaUrls = emptyList()
            ),
            User(id = "456", fullName = "Lê Hải An", avatarUrl = null),
            null
        )
    )

    MaterialTheme {
        HomeScreen(
            user = sampleUser,
            posts = mockPosts,
            onPostClick = {},
            onCommentClick = {},
            onCreatePostClick = {}
        )
    }
}