package com.example.smartpick.features.feed.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.example.smartpick.core.ui.components.PostItem
import com.example.smartpick.core.ui.theme.PageBg
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.core.ui.theme.White
import com.example.smartpick.core.ui.components.CreatePostPrompt
import com.example.smartpick.features.auth.viewmodel.AuthViewModel
import com.example.smartpick.features.feed.viewmodel.FeedUiState
import com.example.smartpick.features.feed.viewmodel.FeedViewModel

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    paddingValues: PaddingValues,
    onPostClick: (String) -> Unit = {},
    onCommentClick: (String, String) -> Unit = { _, _ -> },
    onCreatePostClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    FeedContent(
        currentUser = currentUser,
        uiState = uiState,
        paddingValues = paddingValues,
        onPostClick = onPostClick,
        onCommentClick = onCommentClick,
        onCreatePostClick = onCreatePostClick,
    )
}

@Composable
fun FeedContent(
    currentUser: User?,
    uiState: FeedUiState,
    paddingValues: PaddingValues,
    onPostClick: (String) -> Unit,
    onCommentClick: (String, String) -> Unit = { _, _ -> },
    onCreatePostClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .padding(paddingValues)
    ) {
        when (uiState) {
            is FeedUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is FeedUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 8.dp,
                        bottom = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    // Create post prompt
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = White,
                            shadowElevation = 2.dp
                        ) {
                            CreatePostPrompt(
                                user = currentUser,
                                onClick = onCreatePostClick,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    // Title
                    item {
                        Text(
                            text = stringResource(R.string.DanhChoBan),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Posts
                    items(
                        uiState.posts,
                        key = { it.first.id.toString() }
                    ) { (post, user, product) ->

                        PostItem(
                            post = post,
                            user = user,
                            product = product,
                            isDetailView = false,
                            onPostClick = {
                                onPostClick(post.id.toString())
                            },
                            onCommentClick = {
                                onCommentClick(post.id.toString(), user.id)
                            },
                            onProductClick = { },
                            onViewImagesGalleryRequest = { imageUrls, index ->
                                // TODO
                            }
                        )
                    }
                }
            }

            is FeedUiState.Error -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FeedContentPreview() {

    val fakeUser1 = User(
        id = "user_1",
        email = "user1@gmail.com",
        username = "nguyenvana",
        fullName = "Nguyễn Văn A",
        avatarUrl = "https://i.pravatar.cc/300?img=1",
        phoneNumber = "0123456789",
        createdAt = "2026-05-11T10:00:00",
        updatedAt = "2026-05-11T10:00:00"
    )

    val fakeUser2 = User(
        id = "user_2",
        email = "user2@gmail.com",
        username = "tranthib",
        fullName = "Trần Thị B",
        avatarUrl = "https://i.pravatar.cc/300?img=5",
        phoneNumber = "0987654321",
        createdAt = "2026-05-11T10:00:00",
        updatedAt = "2026-05-11T10:00:00"
    )
    val fakeProduct1 = Product(
        id = "product_1",
        ownerId = "user_1",
        name = "Sony WH-1000XM5",
        brand = "Sony",
        category = "Tai nghe",
        price = 8990000.0,
        imageUrls = listOf(
            "https://images.unsplash.com/photo-1505740420928-5e560c06d30e",
            "https://images.unsplash.com/photo-1519677100203-a0e668c92439"
        ),
        status = "available",
        createdAt = "2026-05-11T10:00:00"
    )

    val fakeProduct2 = Product(
        id = "product_2",
        ownerId = "user_2",
        name = "MacBook Pro M3",
        brand = "Apple",
        category = "Laptop",
        price = 45990000.0,
        imageUrls = listOf(
            "https://images.unsplash.com/photo-1496181133206-80ce9b88a853"
        ),
        status = "available",
        createdAt = "2026-05-11T11:00:00"
    )

    val fakePost1 = Post(
        id = "post_1",
        userId = "user_1",
        productId = "product_1",
        content = "Tai nghe Sony WH-1000XM5 dùng cực kỳ ổn. Chống ồn rất tốt 🔥",
        mediaUrls = listOf(
            "https://images.unsplash.com/photo-1505740420928-5e560c06d30e",
            "https://images.unsplash.com/photo-1519677100203-a0e668c92439"
        ),
        status = "available",
        createdAt = "2026-05-11T10:00:00"
    )

    val fakePost2 = Post(
        id = "post_2",
        userId = "user_2",
        productId = "product_2",
        content = "Setup góc làm việc mới với MacBook Pro M3 😎",
        mediaUrls = listOf(
            "https://images.unsplash.com/photo-1496181133206-80ce9b88a853"
        ),
        status = "available",
        createdAt = "2026-05-11T11:00:00"
    )

    val fakePosts = listOf(
        Triple(fakePost1, fakeUser1, fakeProduct1),
        Triple(fakePost2, fakeUser2, fakeProduct2)
    )

    SmartPickTheme {
        FeedContent(
            currentUser = fakeUser1,
            uiState = FeedUiState.Success(fakePosts),
            paddingValues = PaddingValues(),
            onPostClick = {},
            onCommentClick = { _, _ -> },
            onCreatePostClick = {}
        )
    }
}

