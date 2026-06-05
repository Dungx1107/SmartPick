package com.example.smartpick.features.post_detail.ui

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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
import com.example.smartpick.core.ui.components.post.PostActionButton
import com.example.smartpick.core.ui.components.post.PostHeader // Bổ sung import này
import com.example.smartpick.core.ui.components.post.ReactionButton
import com.example.smartpick.core.ui.components.post.ReactionPopup
import com.example.smartpick.core.ui.components.post.SharePostDialog
import com.example.smartpick.core.ui.components.post.SharedPostCard
import com.example.smartpick.core.ui.theme.SmartPickColor
import com.example.smartpick.core.ui.theme.TextMuted
import com.example.smartpick.features.auth.viewmodel.AuthViewModel
import com.example.smartpick.features.comment.ui.components.CommentInputField
import com.example.smartpick.features.comment.ui.components.CommentItem
import com.example.smartpick.features.comment.viewmodel.CommentUIState
import com.example.smartpick.features.comment.viewmodel.CommentViewModel
import com.example.smartpick.features.feed.viewmodel.FeedUiState
import com.example.smartpick.features.feed.viewmodel.FeedViewModel
import com.example.smartpick.features.post_detail.viewmodel.PostDetailUiState
import com.example.smartpick.features.post_detail.viewmodel.PostDetailViewModel

@Composable
fun PostDetailScreen(
    viewModel: PostDetailViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    commentViewModel: CommentViewModel = hiltViewModel(),
    feedViewModel: FeedViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onCommentClick: ((String, String?) -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val feedState by feedViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val comments by commentViewModel.comments.collectAsState()
    val isCommentLoading by commentViewModel.isLoading.collectAsState()
    val replyingTo by commentViewModel.replyingTo.collectAsState()

    LaunchedEffect(uiState.post?.id, currentUser?.id) {
        val postId = uiState.post?.id
        val currentUserId = currentUser?.id
        if (postId != null && currentUserId != null) {
            commentViewModel.loadComments(postId, uiState.post?.userId, currentUserId)
        }
    }

    val currentPostId = uiState.post?.id
    val latestReaction = remember(currentPostId, feedState) {
        if (feedState is FeedUiState.Success && currentPostId != null) {
            (feedState as FeedUiState.Success).posts.find { it.first.id == currentPostId }?.first?.currentUserReaction
        } else uiState.post?.currentUserReaction
    }

    PostDetailContent(
        uiState = uiState,
        currentUser = currentUser,
        latestReaction = latestReaction,
        comments = comments,
        isCommentLoading = isCommentLoading,
        replyingTo = replyingTo,
        onBackClick = onBackClick,
        onReactionClick = { postId, reactionType ->
            feedViewModel.toggleReaction(
                postId,
                reactionType
            )
        },
        onShareClick = { postId, caption ->
            feedViewModel.sharePost(postId, caption) {
                Toast.makeText(
                    context,
                    "Đã chia sẻ lên Trang cá nhân!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        },
        onSendComment = { text ->
            val postId = uiState.post?.id
            val currentUserId = currentUser?.id
            val currentUserName = currentUser?.username.toString()
            if (postId != null && currentUserId != null) {
                commentViewModel.sendComment(
                    postId = postId,
                    userId = currentUserId,
                    content = text,
                    postOwnerId = uiState.post?.userId,
                    currentUserName = currentUserName
                )
            }
        },
        onLikeCommentClick = { commentId ->
            val postId = uiState.post?.id
            val currentUserId = currentUser?.id
            val currentUserName = currentUser?.username.toString()
            if (postId != null && currentUserId != null) {
                commentViewModel.toggleLikeComment(
                    commentId = commentId,
                    currentUserId = currentUserId,
                    postId = postId,
                    postOwnerId = uiState.post?.userId,
                    currentUserName = currentUserName
                )
            }
        },
        onReplyCommentClick = { comment -> commentViewModel.setReplyingTo(comment) },
        onCancelReply = { commentViewModel.setReplyingTo(null) },
        onRetry = { uiState.post?.id?.let { viewModel.loadPostDetail(it) } },
        onCommentClick = { postId, ownerId -> onCommentClick?.invoke(postId, ownerId) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailContent(
    uiState: PostDetailUiState,
    currentUser: User?,
    latestReaction: ReactionType?,
    comments: List<CommentUIState>,
    isCommentLoading: Boolean,
    replyingTo: CommentUIState?,
    onBackClick: () -> Unit,
    onReactionClick: (String, ReactionType) -> Unit,
    onShareClick: (String, String) -> Unit,
    onSendComment: (String) -> Unit,
    onLikeCommentClick: (String) -> Unit,
    onReplyCommentClick: (CommentUIState) -> Unit,
    onCancelReply: () -> Unit,
    onRetry: () -> Unit,
    onCommentClick: (String, String?) -> Unit,
) {
    var commentText by remember { mutableStateOf("") }
    var showReactionPopup by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }

    val post = uiState.post
    var localReaction by remember(post?.id, latestReaction) { mutableStateOf(latestReaction) }
    var localReactionCount by remember(
        post?.id,
        post?.reactionCount,
        latestReaction
    ) { mutableIntStateOf(post?.reactionCount ?: 0) }
    var localBreakdown by remember(
        post?.id,
        post?.reactionBreakdown,
        latestReaction
    ) { mutableStateOf(post?.reactionBreakdown ?: emptyMap()) }

    val handleReaction = { reaction: ReactionType ->
        val oldReaction = localReaction
        val newBreakdown = localBreakdown.toMutableMap()
        if (oldReaction == reaction) {
            localReaction = null
            localReactionCount = maxOf(0, localReactionCount - 1)
            newBreakdown[oldReaction] = maxOf(0, (newBreakdown[oldReaction] ?: 0) - 1)
            if (newBreakdown[oldReaction] == 0) newBreakdown.remove(oldReaction)
        } else {
            if (oldReaction != null) {
                newBreakdown[oldReaction] = maxOf(0, (newBreakdown[oldReaction] ?: 0) - 1)
                if (newBreakdown[oldReaction] == 0) newBreakdown.remove(oldReaction)
            } else localReactionCount += 1
            localReaction = reaction
            newBreakdown[reaction] = (newBreakdown[reaction] ?: 0) + 1
        }
        localBreakdown = newBreakdown
        post?.id?.let { onReactionClick(it, reaction) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.BaiViet),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            val bottomPadding = WindowInsets.navigationBars.union(WindowInsets.ime)
            Box(modifier = Modifier.windowInsetsPadding(bottomPadding)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
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
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Hủy",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    CommentInputField(
                        commentText = commentText,
                        onCommentChange = { commentText = it },
                        avatarAuthorUrl = currentUser?.avatarUrl,
                        onSend = { onSendComment(commentText); commentText = "" },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
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
                uiState.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )

                uiState.error != null -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = uiState.error, color = MaterialTheme.colorScheme.error); Button(
                    onClick = onRetry
                ) { Text("Thử lại") }
                }

                post != null && uiState.user != null -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {

                        item {
                            // FIX: Gọi đúng tên hàm PostHeader
                            PostHeader(
                                user = uiState.user,
                                createdAt = post.createdAt ?: "",
                                isShared = post.sharedPostId != null
                            )
                        }

                        if (post.sharedPost != null && post.sharedPostUser != null) {
                            item {
                                if (!post.content.isNullOrBlank()) Text(
                                    text = post.content,
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    ),
                                    fontSize = 16.sp
                                )
                                SharedPostCard(
                                    sharedPost = post.sharedPost,
                                    sharedUser = post.sharedPostUser,
                                    onPostClick = { },
                                    onReactionClick = { id, reaction ->
                                        onReactionClick(
                                            id,
                                            reaction
                                        )
                                    })
                            }
                        } else {
                            item {
                                if (!post.content.isNullOrBlank()) {
                                    Text(
                                        text = post.content,
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        ),
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp
                                    )
                                }
                            }
                            if (post.mediaUrls.isNotEmpty()) {
                                item {
                                    val pagerState =
                                        rememberPagerState(pageCount = { post.mediaUrls.size })
                                    Column {
                                        HorizontalPager(
                                            state = pagerState,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(350.dp)
                                        ) { page ->
                                            val url = post.mediaUrls[page]

                                            // Kiểm tra xem URL có đuôi video hay không
                                            val isVideo = url.lowercase().let {
                                                it.endsWith(".mp4") || it.contains(".mp4?") ||
                                                        it.endsWith(".mov") || it.contains(".mov?") ||
                                                        it.endsWith(".webm") || it.contains(".webm?")
                                            }

                                            if (isVideo) {
                                                // Gọi VideoPlayer. Nếu VideoPlayer của bạn nhận biến tên khác (vd: videoUrl) thì hãy đổi lại cho khớp nhé
                                                VideoPlayer(
                                                    videoUrl = url,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else {
                                                AsyncImage(
                                                    model = url,
                                                    contentDescription = null,
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                        }
                                        if (post.mediaUrls.size > 1) {
                                            Row(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 8.dp),
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                repeat(post.mediaUrls.size) { iteration ->
                                                    val color =
                                                        if (pagerState.currentPage == iteration) SmartPickColor else Color.LightGray
                                                    Box(
                                                        modifier = Modifier
                                                            .padding(2.dp)
                                                            .clip(CircleShape)
                                                            .background(color)
                                                            .size(6.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            uiState.product?.let { product ->
                                item {
                                    ProductHighlightCard(
                                        product = product,
                                        onClick = { })
                                }
                            }
                        }

                        item {
                            Box(modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)) {
                                Column {
                                    if (localReactionCount > 0) {
                                        Row(
                                            modifier = Modifier
                                                .padding(
                                                    horizontal = 16.dp,
                                                    vertical = 4.dp
                                                )
                                                .padding(bottom = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val topIcons =
                                                localBreakdown.entries.sortedByDescending { it.value }
                                                    .take(3).joinToString(" ") { it.key.getIcon() }
                                            Text(text = topIcons.ifEmpty { "👍" }, fontSize = 12.sp)
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = "$localReactionCount",
                                                fontSize = 13.sp,
                                                color = TextMuted
                                            )
                                        }
                                    }

                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.weight(1f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            ReactionButton(
                                                currentReaction = localReaction,
                                                onLongPress = { showReactionPopup = true },
                                                onClick = { handleReaction(ReactionType.LIKE) })
                                        }
                                        PostActionButton(
                                            icon = Icons.Outlined.ChatBubbleOutline,
                                            text = stringResource(R.string.BinhLuan),
                                            onClick = { post.let { onCommentClick(it.id.toString(), it.userId) } },
                                            modifier = Modifier.weight(1f)
                                        )
                                        PostActionButton(
                                            icon = Icons.Outlined.Share,
                                            text = stringResource(R.string.ChiaSe),
                                            onClick = { showShareDialog = true },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                                if (showReactionPopup) {
                                    ReactionPopup(
                                        onDismiss = { showReactionPopup = false },
                                        onReactionSelected = { reaction ->
                                            showReactionPopup = false
                                            handleReaction(reaction)
                                        })
                                }
                            }
                        }

                        item {
                            HorizontalDivider(
                                thickness = 8.dp,
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                            Text(
                                "Bình luận",
                                modifier = Modifier.padding(16.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        if (isCommentLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
                            }
                        } else if (comments.isEmpty()) {
                            item {
                                Text(
                                    text = "Chưa có bình luận nào. Hãy là người đầu tiên!",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    color = TextMuted,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            items(items = comments, key = { it.id }) { parentComment ->
                                CommentItem(
                                    state = parentComment,
                                    onLikeClick = onLikeCommentClick,
                                    onReplyClick = onReplyCommentClick
                                )
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
                        item { Spacer(modifier = Modifier.height(100.dp)) }
                    }
                }
            }
        }
    }

    if (showShareDialog) {
        SharePostDialog(
            onDismiss = { showShareDialog = false },
            onShare = { caption ->
                showShareDialog = false
                post?.id?.let { onShareClick(it, caption) }
            }
        )
    }
}

@Composable
fun ProductHighlightCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SmartPickColor.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, SmartPickColor.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = product.imageUrls.firstOrNull(),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1
                ); Text("${product.price}đ", color = SmartPickColor, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = SmartPickColor),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Mua ngay", fontSize = 12.sp) }
        }
    }
}