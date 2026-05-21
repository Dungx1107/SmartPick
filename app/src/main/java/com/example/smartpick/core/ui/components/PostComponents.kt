package com.example.smartpick.core.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import coil.compose.AsyncImage
import com.example.smartpick.R
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.ReactionType
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.theme.AccentBlue
import com.example.smartpick.core.ui.theme.SmartPickColor
import com.example.smartpick.core.ui.theme.TextMuted

@Composable
fun PostHeader(
    user: User,
    createdAt: String,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.fullName ?: stringResource(R.string.smartpick_user),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = createdAt.split("T").firstOrNull() ?: "",
                fontSize = 12.sp,
                color = TextMuted
            )
        }
        IconButton(onClick = onMoreClick) {
            Icon(Icons.Default.MoreVert, null, tint = TextMuted)
        }
    }
}

@Composable
fun PostMainContent(
    modifier: Modifier = Modifier,
    content: String?,
    mediaUrls: List<String> = emptyList(),
    product: Product? = null,
    onMediaClick: (Int) -> Unit = {},
    onProductClick: (Product) -> Unit = {},
) {
    Column(modifier = modifier) {
        if (!content.isNullOrBlank()) {
            Text(
                text = content,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                fontSize = 15.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Gọi component MediaGrid xịn xò để hiển thị ảnh
        if (mediaUrls.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            MediaGrid(
                mediaUrls = mediaUrls,
                onMediaClick = onMediaClick,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        product?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .clickable { onProductClick(it) },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            ) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = it.imageUrls.firstOrNull(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = it.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "${it.price}đ", color = AccentBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// COMPONENT MỚI: Xử lý lưới ảnh thông minh
@Composable
fun MediaGrid(
    mediaUrls: List<String>,
    modifier: Modifier = Modifier,
    onMediaClick: (Int) -> Unit
) {
    val imageHeight = 220.dp
    val imageShape = RoundedCornerShape(8.dp)

    Box(modifier = modifier.fillMaxWidth()) {
        when (mediaUrls.size) {
            1 -> {
                // Trường hợp 1 ảnh: Hiển thị full
                AsyncImage(
                    model = mediaUrls[0],
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                        .clip(imageShape)
                        .clickable { onMediaClick(0) },
                    contentScale = ContentScale.Crop
                )
            }
            2 -> {
                // Trường hợp 2 ảnh: Chia đôi đều nhau
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AsyncImage(
                        model = mediaUrls[0],
                        contentDescription = null,
                        modifier = Modifier.weight(1f).height(imageHeight).clip(imageShape).clickable { onMediaClick(0) },
                        contentScale = ContentScale.Crop
                    )
                    AsyncImage(
                        model = mediaUrls[1],
                        contentDescription = null,
                        modifier = Modifier.weight(1f).height(imageHeight).clip(imageShape).clickable { onMediaClick(1) },
                        contentScale = ContentScale.Crop
                    )
                }
            }
            else -> {
                // Trường hợp 3 ảnh trở lên: Chia đôi, ảnh 2 có Overlay số đếm
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AsyncImage(
                        model = mediaUrls[0],
                        contentDescription = null,
                        modifier = Modifier.weight(1f).height(imageHeight).clip(imageShape).clickable { onMediaClick(0) },
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(imageHeight)
                            .clip(imageShape)
                            .clickable { onMediaClick(1) } // Click vào ảnh overlay vẫn mở được Gallery
                    ) {
                        AsyncImage(
                            model = mediaUrls[1],
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Lớp phủ màu đen mờ (Alpha 40%)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${mediaUrls.size - 2}",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SharedPostCard(sharedPost: Post) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Bài đăng được chia sẻ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SmartPickColor,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = sharedPost.content ?: "",
                fontSize = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PostActionButton(icon: ImageVector, text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, color = TextMuted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ReactionButton(
    currentReaction: ReactionType?,
    onLongPress: () -> Unit,
    onClick: () -> Unit
) {
    val isReacted = currentReaction != null
    val buttonColor = if (isReacted) SmartPickColor else TextMuted

    val text = when (currentReaction) {
        ReactionType.LIKE -> "Thích"
        ReactionType.LOVE -> "Yêu thích"
        ReactionType.HAHA -> "Haha"
        ReactionType.WOW -> "Wow"
        ReactionType.SAD -> "Buồn"
        ReactionType.ANGRY -> "Phẫn nộ"
        null -> stringResource(R.string.thich)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() },
                    onTap = { onClick() }
                )
            }
            .padding(vertical = 12.dp)
    ) {
        if (isReacted) {
            Text(currentReaction!!.getIcon(), fontSize = 18.sp)
        } else {
            Icon(Icons.Outlined.ThumbUp, null, modifier = Modifier.size(20.dp), tint = TextMuted)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 13.sp, color = buttonColor, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ReactionPopup(
    onDismiss: () -> Unit,
    onReactionSelected: (ReactionType) -> Unit
) {
    // FIX 2: Thay vì dùng Modifier.offset ở thẻ Card làm lệch vùng cảm ứng,
    // ta di chuyển nguyên cái Window của Popup bằng IntOffset
    val density = LocalDensity.current
    val yOffset = with(density) { -55.dp.roundToPx() }

    Popup(
        onDismissRequest = onDismiss,
        alignment = Alignment.TopCenter,
        offset = IntOffset(0, yOffset),
        properties = PopupProperties(
            focusable = true,
            dismissOnClickOutside = true,
            clippingEnabled = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(30.dp),
            elevation = CardDefaults.cardElevation(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                ReactionType.entries.forEach { type ->
                    Text(
                        text = type.getIcon(),
                        fontSize = 32.sp,
                        modifier = Modifier
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .clickable { onReactionSelected(type) }
                    )
                }
            }
        }
    }
}

@Composable
fun PostItem(
    post: Post,
    user: User,
    product: Product? = null,
    onPostClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onProductClick: (Product) -> Unit = {},
    onViewImagesGalleryRequest: (List<String>, Int) -> Unit = { _, _ -> },
    onReactionClick: (ReactionType) -> Unit = {},
    onShareClick: () -> Unit = {},
    isDetailView: Boolean = false,
) {
    var showReactionPopup by remember { mutableStateOf(false) }

    var localReaction by remember(post.currentUserReaction) { mutableStateOf(post.currentUserReaction) }
    var localReactionCount by remember(post.reactionCount, post.currentUserReaction) { mutableIntStateOf(post.reactionCount) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onPostClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            PostHeader(user = user, createdAt = post.createdAt ?: "Vừa xong")

            PostMainContent(
                content = post.content,
                mediaUrls = post.mediaUrls,
                product = product,
                onMediaClick = { clickedIndex ->
                    onViewImagesGalleryRequest(post.mediaUrls, clickedIndex)
                },
                onProductClick = onProductClick
            )


            if (localReactionCount > 0) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "👍 ❤️ 😂", fontSize = 12.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(text = "$localReactionCount", fontSize = 13.sp, color = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {

                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            ReactionButton(
                                currentReaction = localReaction,
                                onLongPress = { showReactionPopup = true },
                                onClick = {
                                    if (localReaction == null) {
                                        localReaction = ReactionType.LIKE
                                        localReactionCount += 1
                                        onReactionClick(ReactionType.LIKE)
                                    } else {
                                        val oldReaction = localReaction!!
                                        localReaction = null
                                        localReactionCount = maxOf(0, localReactionCount - 1)
                                        onReactionClick(oldReaction)
                                    }
                                }
                            )
                        }

                        PostActionButton(
                            icon = Icons.Outlined.ChatBubbleOutline,
                            text = stringResource(R.string.BinhLuan),
                            onClick = onCommentClick,
                            modifier = Modifier.weight(1f)
                        )
                        PostActionButton(
                            icon = Icons.Outlined.Share,
                            text = stringResource(R.string.ChiaSe),
                            onClick = onShareClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (showReactionPopup) {
                    ReactionPopup(
                        onDismiss = { showReactionPopup = false },
                        onReactionSelected = { selectedReaction ->
                            if (localReaction == null) {
                                localReactionCount += 1
                            }
                            localReaction = selectedReaction
                            showReactionPopup = false
                            onReactionClick(selectedReaction)
                        }
                    )
                }
            }
        }
    }
}