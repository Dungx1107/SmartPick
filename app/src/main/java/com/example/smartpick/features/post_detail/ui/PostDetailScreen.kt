package com.example.smartpick.features.post_detail.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.smartpick.R
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.ReactionType
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.components.*
import com.example.smartpick.core.ui.theme.SmartPickColor
import com.example.smartpick.core.ui.theme.TextMuted
import com.example.smartpick.features.auth.viewmodel.AuthViewModel
import com.example.smartpick.features.comment.ui.components.CommentInputField
import com.example.smartpick.features.comment.ui.components.CommentItem
import com.example.smartpick.features.comment.viewmodel.CommentUIState
import com.example.smartpick.features.comment.viewmodel.CommentViewModel
import com.example.smartpick.features.post_detail.viewmodel.PostDetailUiState
import com.example.smartpick.features.post_detail.viewmodel.PostDetailViewModel

@Composable
fun PostDetailScreen(
    viewModel: PostDetailViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    commentViewModel: CommentViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // States từ CommentViewModel
    val comments by commentViewModel.comments.collectAsState()
    val isCommentLoading by commentViewModel.isLoading.collectAsState()
    val replyingTo by commentViewModel.replyingTo.collectAsState()

    // Tự động load comments khi có postId
    LaunchedEffect(uiState.post?.id, currentUser?.id) {
        val postId = uiState.post?.id
        val currentUserId = currentUser?.id
        if (postId != null && currentUserId != null) {
            commentViewModel.loadComments(
                postId = postId,
                postOwnerId = uiState.post?.userId,
                currentUserId = currentUserId
            )
        }
    }

    PostDetailContent(
        uiState = uiState,
        currentUser = currentUser,
        comments = comments,
        isCommentLoading = isCommentLoading,
        replyingTo = replyingTo,
        onBackClick = onBackClick,
        onReactionClick = { postId, reactionType ->
            // Bổ sung toggleReaction bài viết (Nếu đã viết hàm trong PostDetailViewModel)
        },
        onSendComment = { text ->
            val postId = uiState.post?.id
            val currentUserId = currentUser?.id
            val postOwnerId = uiState.post?.userId
            if (postId != null && currentUserId != null) {
                commentViewModel.sendComment(postId, currentUserId, text, postOwnerId)
            }
        },
        onLikeCommentClick = { commentId ->
            val postId = uiState.post?.id
            val currentUserId = currentUser?.id
            val postOwnerId = uiState.post?.userId
            if (postId != null && currentUserId != null) {
                commentViewModel.toggleLikeComment(commentId, currentUserId, postId, postOwnerId)
            }
        },
        onReplyCommentClick = { comment ->
            commentViewModel.setReplyingTo(comment)
        },
        onCancelReply = {
            commentViewModel.setReplyingTo(null)
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
    currentUser: User?,
    comments: List<CommentUIState>,
    isCommentLoading: Boolean,
    replyingTo: CommentUIState?,
    onBackClick: () -> Unit,
    onReactionClick: (String, ReactionType) -> Unit,
    onSendComment: (String) -> Unit,
    onLikeCommentClick: (String) -> Unit,
    onReplyCommentClick: (CommentUIState) -> Unit,
    onCancelReply: () -> Unit,
    onRetry: () -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    var showReactionPopup by remember { mutableStateOf(false) }

    val post = uiState.post
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
            // THANH NHẬP BÌNH LUẬN GẮN ĐÁY - CÓ HỖ TRỢ HIỂN THỊ TRẠNG THÁI "ĐANG TRẢ LỜI..."
            val bottomPadding = WindowInsets.navigationBars.union(WindowInsets.ime)
            Box(modifier = Modifier.windowInsetsPadding(bottomPadding)) {
                Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
                    // Nếu đang trong chế độ Reply, hiển thị dải màu báo hiệu
                    if (replyingTo != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Đang trả lời ${replyingTo.authorName}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            IconButton(
                                onClick = onCancelReply,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Hủy", modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // Tái sử dụng Component CommentInputField cực xịn của bạn
                    CommentInputField(
                        commentText = commentText,
                        onCommentChange = { commentText = it },
                        avatarAuthorUrl = currentUser?.avatarUrl,
                        onSend = {
                            onSendComment(commentText)
                            commentText = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
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

                        // 1. HEADER (Người đăng bài)
                        item { PostHeader(user = uiState.user, createdAt = post.createdAt ?: "") }

                        // 2. TEXT CONTENT
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

                        // 3. MEDIA (PAGER VUỐT ẢNH DẠNG CAROUSEL)
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

                        // 4. SẢN PHẨM NỔI BẬT ĐÍNH KÈM
                        uiState.product?.let { product ->
                            item { ProductHighlightCard(product = product, onClick = { /* Mở chi tiết SP */ }) }
                        }

                        // 5. CỤM TƯƠNG TÁC LIKE / SHARE BÀI VIẾT
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

                            Box(modifier = Modifier.padding(bottom = 8.dp)) {
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
                                            onClick = { /* Bấm bình luận ở đây có thể gắn FocusRequester vào thanh Input (Nếu rảnh) */ },
                                            modifier = Modifier.weight(1f)
                                        )
                                        PostActionButton(
                                            icon = Icons.Outlined.Share,
                                            text = stringResource(R.string.ChiaSe),
                                            onClick = { /* Tạm ẩn */ },
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

                        // ==========================================
                        // 6. TÍCH HỢP HỆ THỐNG COMMENT THẬT TỪ DATABASE
                        // ==========================================
                        item {
                            HorizontalDivider(thickness = 8.dp, color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            Text("Bình luận", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        if (isCommentLoading) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        } else if (comments.isEmpty()) {
                            item {
                                Text(
                                    text = "Chưa có bình luận nào. Hãy là người đầu tiên!",
                                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    color = TextMuted,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            // Duyệt danh sách Parent Comments
                            items(items = comments, key = { it.id }) { parentComment ->
                                CommentItem(
                                    state = parentComment,
                                    onLikeClick = onLikeCommentClick,
                                    onReplyClick = onReplyCommentClick
                                )

                                // Nếu Parent Comment có các Reply (Bình luận con)
                                if (parentComment.replies.isNotEmpty()) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        parentComment.replies.forEachIndexed { index, reply ->
                                            CommentItem(
                                                state = reply,
                                                onLikeClick = onLikeCommentClick,
                                                onReplyClick = onReplyCommentClick,
                                                isReply = true,
                                                isLastReply = index == parentComment.replies.size - 1
                                            )
                                        }
                                    }
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                            }
                        }

                        // Thêm 1 khoảng trắng trống ở đáy để tránh bình luận bị bàn phím ảo che mất
                        item { Spacer(modifier = Modifier.height(100.dp)) }
                    }
                }
            }
        }
    }
}

// ---------------- CÁC COMPONENT PHỤ BỔ SUNG CỦA MÀN CHI TIẾT ----------------

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