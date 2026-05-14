package com.example.smartpick.features.post_detail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.smartpick.R
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.components.PostFooterActions
import com.example.smartpick.core.ui.components.PostHeader
import com.example.smartpick.core.ui.components.ProductAttachmentCard
import com.example.smartpick.core.ui.components.VideoPlayer
import com.example.smartpick.core.ui.theme.PageBg
import com.example.smartpick.core.ui.theme.White
import com.example.smartpick.core.utils.FileUtils
import com.example.smartpick.features.post_detail.viewmodel.PostDetailUiState
import com.example.smartpick.features.post_detail.viewmodel.PostDetailViewModel

@Composable
fun PostDetailScreen(
    viewModel: PostDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    PostDetailContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onRetry = {
            uiState.post?.id?.let { viewModel.loadPostDetail(it) }  // Nếu có lỗi, cho phép tải lại

        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailContent(
    uiState: PostDetailUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.BaiViet), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        containerColor = PageBg
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(White)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = uiState.error, color = MaterialTheme.colorScheme.error)
                        Button(onClick = onRetry) { Text("Thử lại") }
                    }
                }

                uiState.post != null -> {
                    val post = uiState.post
                    val user = uiState.user!!

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        // Header: Thông tin người đăng
                        item { PostHeader(user = user, createdAt = post.createdAt ?: "") }

                        // Nội dung bài viết
                        item {
                            if (!post.content.isNullOrBlank()) {
                                Text(text = post.content, modifier = Modifier.padding(16.dp))
                            }
                        }

                        // Sản phẩm đính kèm (nếu có)
                        uiState.product?.let { product ->
                            item {
                                ProductAttachmentCard(
                                    product = product,
                                    onClick = { /* Điều hướng sang chi tiết SP */ }
                                )
                            }
                        }

                        items(post.mediaUrls) { url ->
                            if (FileUtils.isVideoUrl(url)) {// Nếu là video, gọi trình phát chuyên dụng
                                VideoPlayer(
                                    videoUrl = url,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            } else {
                                AsyncImage(
                                    model = url,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    contentScale = ContentScale.FillWidth
                                )
                            }
                        }
                        item { PostFooterActions(onLikeClick = {}, onCommentClick = {}) }

                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PostDetailContentPreview() {

    val fakeUser = User(
        id = "user_1",
        email = "user1@gmail.com",
        username = "nguyenvana",
        fullName = "Nguyễn Văn A",
        avatarUrl = "https://i.pravatar.cc/300?img=12",
        phoneNumber = "0123456789",
        createdAt = "2026-05-11T10:00:00",
        updatedAt = "2026-05-11T10:00:00"
    )

    val fakeProduct = Product(
        id = "product_1",
        ownerId = "user_1",
        name = "Sony WH-1000XM5",
        brand = "Sony",
        category = "Tai nghe",
        price = 8990000.0,
        imageUrls = listOf(
            "https://images.unsplash.com/photo-1505740420928-5e560c06d30e"
        ),
        status = "available",
        createdAt = "2026-05-11T10:00:00"
    )

    val fakePost = Post(
        id = "post_1",
        userId = "user_1",
        productId = "product_1",
        content = "Tai nghe Sony WH-1000XM5 chống ồn cực kỳ tốt. Pin rất trâu và đeo lâu không đau tai 🔥",
        mediaUrls = listOf(
            "https://images.unsplash.com/photo-1505740420928-5e560c06d30e",
            "https://images.unsplash.com/photo-1519677100203-a0e668c92439",
            "https://images.unsplash.com/photo-1496181133206-80ce9b88a853"
        ),
        status = "available",
        createdAt = "2 giờ trước"
    )

    val fakeUiState = PostDetailUiState(
        isLoading = false,
        post = fakePost,
        user = fakeUser,
        product = fakeProduct,
        error = null
    )

    MaterialTheme {
        PostDetailContent(
            uiState = fakeUiState,
            onBackClick = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PostDetailLoadingPreview() {

    MaterialTheme {
        PostDetailContent(
            uiState = PostDetailUiState(
                isLoading = true
            ),
            onBackClick = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PostDetailErrorPreview() {

    MaterialTheme {
        PostDetailContent(
            uiState = PostDetailUiState(
                isLoading = false,
                error = "Không thể tải bài viết"
            ),
            onBackClick = {},
            onRetry = {}
        )
    }
}