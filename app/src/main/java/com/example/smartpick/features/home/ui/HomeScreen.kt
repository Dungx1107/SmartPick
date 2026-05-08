package com.example.smartpick.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.smartpick.core.ui.components.ProductVerticalCard
import com.example.smartpick.core.ui.theme.PageBg
import com.example.smartpick.core.ui.theme.TextPrimary
import com.example.smartpick.core.ui.theme.TextSecondary
import com.example.smartpick.features.auth.viewmodel.AuthViewModel
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
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg),
        contentPadding = PaddingValues(
            start = 12.dp,
            end = 12.dp,
            bottom = 24.dp,
            top = 0.dp // Bạn có thể chỉnh lại giá trị top tùy ý
        ), horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // --- PHẦN HEADER (Mỗi cái là 1 item riêng để tối ưu cuộn) ---

        item(span = { GridItemSpan(2) }) {
            Column {
                Spacer(Modifier.height(12.dp))
                SearchBar()
            }
        }

        item(span = { GridItemSpan(2) }) {
            HeroBanner()
        }

        item(span = { GridItemSpan(2) }) {
            AICuratorBanner()
        }

        item(span = { GridItemSpan(2) }) {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)) {
                Text(
                    text = stringResource(R.string.GoiYChoBan),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                user.fullName?.let {
                    Text(
                        text = stringResource(R.string.DuaTrenSoThich, it),
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // --- PHẦN GRID SẢN PHẨM (Tự động chia 2 cột) ---

        val productItems = posts.filter { it.third != null }

        items(
            items = productItems,
            key = { it.first.id.toString() }
        ) { (post, author, product) ->
            product?.let {
                ProductVerticalCard(
                    product = it,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onPostClick(post.id.toString()) }
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