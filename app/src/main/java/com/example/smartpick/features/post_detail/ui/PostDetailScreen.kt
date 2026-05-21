package com.example.smartpick.features.post_detail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.smartpick.R
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.ReactionType
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.components.PostActionButton
import com.example.smartpick.core.ui.components.PostHeader
import com.example.smartpick.core.ui.components.ProductAttachmentCard
import com.example.smartpick.core.ui.components.ReactionButton
import com.example.smartpick.core.ui.components.ReactionPopup
import com.example.smartpick.core.ui.components.VideoPlayer
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.core.ui.theme.TextMuted
import com.example.smartpick.core.utils.FileUtils
import com.example.smartpick.features.post_detail.viewmodel.PostDetailUiState
import com.example.smartpick.features.post_detail.viewmodel.PostDetailViewModel

@Composable
fun PostDetailScreen(
    viewModel: PostDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onCommentClick: (String, String) -> Unit = { _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsState()

    PostDetailContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onCommentClick = onCommentClick,
        onReactionClick = { postId, reactionType ->
            // viewModel.toggleReaction(postId, reactionType) // Bạn có thể thêm logic thả cảm xúc vào PostDetailViewModel sau
        },
        onShareClick = { postId ->
            // viewModel.sharePost(postId) // Bạn có thể thêm logic chia sẻ vào PostDetailViewModel sau
        },
        onRetry = {
            uiState.post?.id?.let { viewModel.loadPostDetail(it) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailContent(
    uiState: PostDetailUiState,
    onBackClick: () -> Unit,
    onCommentClick: (String, String) -> Unit,
    onReactionClick: (String, ReactionType) -> Unit = { _, _ -> },
    onShareClick: (String) -> Unit = {},
    onRetry: () -> Unit
) {
    // Thêm State để quản lý popup cảm xúc
    var showReactionPopup by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.BaiViet),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
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
                        item { PostHeader(user = user, createdAt = post.createdAt ?: "") }

                        item {
                            if (!post.content.isNullOrBlank()) {
                                Text(
                                    text = post.content,
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        uiState.product?.let { product ->
                            item {
                                ProductAttachmentCard(
                                    product = product,
                                    onClick = { /* Điều hướng sang chi tiết SP */ }
                                )
                            }
                        }

                        items(post.mediaUrls) { url ->
                            if (FileUtils.isVideoUrl(url)) {
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

                        item {
                            // 1. Hiển thị số lượng cảm xúc
                            if (post.reactionCount > 0) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "👍 ❤️ 😂", fontSize = 12.sp)
                                    Spacer(Modifier.width(4.dp))
                                    Text(text = "${post.reactionCount}", fontSize = 13.sp, color = TextMuted)
                                }
                            }

                            // 2. Cụm nút tương tác (Thay thế cho PostFooterActions cũ)
                            Box(modifier = Modifier.padding(bottom = 16.dp)) {
                                Column {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    )
                                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                            ReactionButton(
                                                currentReaction = post.currentUserReaction,
                                                onLongPress = { showReactionPopup = true },
                                                onClick = {
                                                    val reactionToToggle = if (post.currentUserReaction == null) ReactionType.LIKE else post.currentUserReaction
                                                    onReactionClick(post.id.toString(), reactionToToggle)
                                                }
                                            )
                                        }

                                        PostActionButton(
                                            icon = Icons.Outlined.ChatBubbleOutline,
                                            text = stringResource(R.string.BinhLuan),
                                            onClick = {
                                                val postId = post.id ?: return@PostActionButton
                                                onCommentClick(postId, post.userId)
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                        PostActionButton(
                                            icon = Icons.Outlined.Share,
                                            text = stringResource(R.string.ChiaSe),
                                            onClick = { onShareClick(post.id.toString()) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                // 3. Popup Cảm xúc
                                if (showReactionPopup) {
                                    ReactionPopup(
                                        onDismiss = { showReactionPopup = false },
                                        onReactionSelected = { reaction ->
                                            onReactionClick(post.id.toString(), reaction)
                                            showReactionPopup = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PostDetailContentPreview() {
    SmartPickTheme {
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
            reactionCount = 10,
            currentUserReaction = ReactionType.LOVE,
            createdAt = "2 giờ trước"
        )

        val fakeUiState = PostDetailUiState(
            isLoading = false,
            post = fakePost,
            user = fakeUser,
            product = fakeProduct,
            error = null
        )

        PostDetailContent(
            uiState = fakeUiState,
            onBackClick = {},
            onCommentClick = { _, _ -> },
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PostDetailErrorPreview() {
    SmartPickTheme {
        PostDetailContent(
            uiState = PostDetailUiState(
                isLoading = false,
                error = "Không thể tải bài viết"
            ),
            onBackClick = {},
            onCommentClick = { _, _ -> },
            onRetry = {}
        )
    }
}