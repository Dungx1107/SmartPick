package com.example.smartpick.features.post_detail.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.smartpick.core.ui.components.*
import com.example.smartpick.core.ui.theme.SmartPickColor
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.core.ui.theme.SurfaceCard
import com.example.smartpick.core.ui.theme.TextMuted
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
            // Thêm hàm vào ViewModel sau
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
    onRetry: () -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    var showReactionPopup by remember { mutableStateOf(false) }

    val post = uiState.post
    // Optimistic UI states
    var localReaction by remember(post?.currentUserReaction) { mutableStateOf(post?.currentUserReaction) }
    var localReactionCount by remember(post?.reactionCount, post?.currentUserReaction) { mutableIntStateOf(post?.reactionCount ?: 0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.BaiViet), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            CommentInputBar(
                value = commentText,
                onValueChange = { commentText = it },
                onSendClick = { commentText = "" }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.surface)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
                }
                uiState.error != null -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = uiState.error, color = MaterialTheme.colorScheme.error)
                        Button(onClick = onRetry) { Text("Thử lại") }
                    }
                }
                post != null && uiState.user != null -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item { PostHeader(user = uiState.user, createdAt = post.createdAt ?: "") }

                        item {
                            if (!post.content.isNullOrBlank()) {
                                Text(
                                    text = post.content,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp
                                )
                            }
                        }

                        // HIỂN THỊ ẢNH DẠNG VUỐT (PAGER) THAY VÌ LƯỚI
                        if (post.mediaUrls.isNotEmpty()) {
                            item {
                                val pagerState = rememberPagerState(pageCount = { post.mediaUrls.size })
                                Column {
                                    HorizontalPager(
                                        state = pagerState,
                                        modifier = Modifier.fillMaxWidth().height(350.dp)
                                    ) { page ->
                                        AsyncImage(
                                            model = post.mediaUrls[page],
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    if (post.mediaUrls.size > 1) {
                                        Row(
                                            Modifier.fillMaxWidth().padding(top = 8.dp),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            repeat(post.mediaUrls.size) { iteration ->
                                                val color = if (pagerState.currentPage == iteration) SmartPickColor else Color.LightGray
                                                Box(modifier = Modifier.padding(2.dp).clip(CircleShape).background(color).size(6.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // SẢN PHẨM NỔI BẬT ĐÍNH KÈM
                        uiState.product?.let { product ->
                            item { ProductHighlightCard(product = product, onClick = { /* Mở chi tiết SP */ }) }
                        }

                        // Đã xóa phần gọi SharedPostCard bị lỗi

                        item {
                            if (localReactionCount > 0) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "👍 ❤️ 😂", fontSize = 12.sp)
                                    Spacer(Modifier.width(4.dp))
                                    Text(text = "$localReactionCount", fontSize = 13.sp, color = TextMuted)
                                }
                            }

                            Box(modifier = Modifier.padding(bottom = 16.dp)) {
                                Column {
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                            ReactionButton(
                                                currentReaction = localReaction,
                                                onLongPress = { showReactionPopup = true },
                                                onClick = {
                                                    if (localReaction == null) {
                                                        localReaction = ReactionType.LIKE
                                                        localReactionCount += 1
                                                        onReactionClick(post.id.toString(), ReactionType.LIKE)
                                                    } else {
                                                        val old = localReaction!!
                                                        localReaction = null
                                                        localReactionCount = maxOf(0, localReactionCount - 1)
                                                        onReactionClick(post.id.toString(), old)
                                                    }
                                                }
                                            )
                                        }

                                        PostActionButton(
                                            icon = Icons.Outlined.ChatBubbleOutline,
                                            text = stringResource(R.string.BinhLuan),
                                            onClick = { onCommentClick(post.id.toString(), post.userId) },
                                            modifier = Modifier.weight(1f)
                                        )
                                        PostActionButton(
                                            icon = Icons.Outlined.Share,
                                            text = stringResource(R.string.ChiaSe),
                                            onClick = { /* Share tính sau */ },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                if (showReactionPopup) {
                                    ReactionPopup(
                                        onDismiss = { showReactionPopup = false },
                                        onReactionSelected = { reaction ->
                                            if (localReaction == null) localReactionCount += 1
                                            localReaction = reaction
                                            showReactionPopup = false
                                            onReactionClick(post.id.toString(), reaction)
                                        }
                                    )
                                }
                            }
                        }

                        // DANH SÁCH BÌNH LUẬN
                        item {
                            HorizontalDivider(thickness = 8.dp, color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            Text("Bình luận", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        items(5) { CommentItem() }
                    }
                }
            }
        }
    }
}

// ---------------- CÁC COMPONENT PHỤ CỦA MÀN CHI TIẾT ----------------

@Composable
fun ProductHighlightCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SmartPickColor.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, SmartPickColor.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = product.imageUrls.firstOrNull(),
                contentDescription = null,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
                Text("${product.price}đ", color = SmartPickColor, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = SmartPickColor),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Mua ngay", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun CommentItem() {
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()) {
        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.LightGray))
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceCard)) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text("Người dùng", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Bài viết rất hay, sản phẩm có vẻ chất lượng!", fontSize = 14.sp)
                }
            }
            Text("2 giờ trước", modifier = Modifier.padding(start = 8.dp, top = 4.dp), fontSize = 12.sp, color = TextMuted)
        }
    }
}

@Composable
fun CommentInputBar(value: String, onValueChange: (String) -> Unit, onSendClick: () -> Unit) {
    Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).imePadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text("Viết bình luận...") },
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp)
            )
            IconButton(
                onClick = onSendClick,
                enabled = value.isNotBlank(),
                colors = IconButtonDefaults.iconButtonColors(contentColor = SmartPickColor)
            ) {
                Icon(Icons.Default.Send, contentDescription = null)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PostDetailErrorPreview() {
    SmartPickTheme {
        PostDetailContent(
            uiState = PostDetailUiState(isLoading = false, error = "Không thể tải bài viết"),
            onBackClick = {}, onCommentClick = { _, _ -> }, onRetry = {}
        )
    }
}