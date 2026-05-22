package com.example.smartpick.core.ui.components

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
fun PostHeader(user: User, createdAt: String, modifier: Modifier = Modifier, isShared: Boolean = false, onMoreClick: () -> Unit = {}) {
    Row(modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(model = user.avatarUrl, contentDescription = null, modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray), contentScale = ContentScale.Crop)
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            if (isShared) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)) { append(user.fullName ?: "Người dùng") }
                        append(" đã chia sẻ một bài viết.")
                    },
                    fontSize = 15.sp, color = TextMuted
                )
            } else {
                Text(text = user.fullName ?: stringResource(R.string.smartpick_user), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
            }
            Text(text = createdAt.split("T").firstOrNull() ?: "", fontSize = 12.sp, color = TextMuted)
        }
        IconButton(onClick = onMoreClick) { Icon(Icons.Default.MoreVert, null, tint = TextMuted) }
    }
}

@Composable
fun MediaGrid(mediaUrls: List<String>, modifier: Modifier = Modifier, onMediaClick: (Int) -> Unit) {
    val imageHeight = 220.dp
    val imageShape = RoundedCornerShape(8.dp)
    Box(modifier = modifier.fillMaxWidth()) {
        when (mediaUrls.size) {
            1 -> AsyncImage(model = mediaUrls[0], contentDescription = null, modifier = Modifier.fillMaxWidth().height(imageHeight).clip(imageShape).clickable { onMediaClick(0) }, contentScale = ContentScale.Crop)
            2 -> Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                AsyncImage(model = mediaUrls[0], contentDescription = null, modifier = Modifier.weight(1f).height(imageHeight).clip(imageShape).clickable { onMediaClick(0) }, contentScale = ContentScale.Crop)
                AsyncImage(model = mediaUrls[1], contentDescription = null, modifier = Modifier.weight(1f).height(imageHeight).clip(imageShape).clickable { onMediaClick(1) }, contentScale = ContentScale.Crop)
            }
            else -> Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                AsyncImage(model = mediaUrls[0], contentDescription = null, modifier = Modifier.weight(1f).height(imageHeight).clip(imageShape).clickable { onMediaClick(0) }, contentScale = ContentScale.Crop)
                Box(modifier = Modifier.weight(1f).height(imageHeight).clip(imageShape).clickable { onMediaClick(1) }) {
                    AsyncImage(model = mediaUrls[1], contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)), contentAlignment = Alignment.Center) {
                        Text(text = "+${mediaUrls.size - 2}", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PostMainContent(modifier: Modifier = Modifier, content: String?, mediaUrls: List<String> = emptyList(), product: Product? = null, onMediaClick: (Int) -> Unit = {}, onProductClick: (Product) -> Unit = {}) {
    Column(modifier = modifier) {
        if (!content.isNullOrBlank()) Text(text = content, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontSize = 15.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurface)
        if (mediaUrls.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            MediaGrid(mediaUrls = mediaUrls, onMediaClick = onMediaClick, modifier = Modifier.padding(horizontal = 12.dp))
        }
        product?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).clickable { onProductClick(it) }, shape = RoundedCornerShape(8.dp), border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(model = it.imageUrls.firstOrNull(), contentDescription = null, modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = it.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        // FIX: Format tiền tệ cho khung sản phẩm đính kèm ở bài viết
                        val priceFormatted = String.format("%,.0f đ", it.price).replace(",", ".")
                        Text(text = priceFormatted, color = AccentBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SharedPostCard(
    sharedPost: Post,
    sharedUser: User,
    onPostClick: () -> Unit,
    onReactionClick: (String, ReactionType) -> Unit = { _, _ -> }
) {
    var showReactionPopup by remember { mutableStateOf(false) }
    var localReaction by remember(sharedPost.currentUserReaction) { mutableStateOf(sharedPost.currentUserReaction) }
    var localReactionCount by remember(sharedPost.reactionCount, sharedPost.currentUserReaction) { mutableIntStateOf(sharedPost.reactionCount) }
    var localBreakdown by remember(sharedPost.reactionBreakdown, sharedPost.currentUserReaction) { mutableStateOf(sharedPost.reactionBreakdown) }

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
        onReactionClick(sharedPost.id.toString(), reaction)
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp).clickable { onPostClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            PostHeader(user = sharedUser, createdAt = sharedPost.createdAt ?: "Vừa xong")
            PostMainContent(content = sharedPost.content, mediaUrls = sharedPost.mediaUrls, product = null, onMediaClick = { _ -> onPostClick() })

            if (localReactionCount > 0) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    val topIcons = localBreakdown.entries.sortedByDescending { it.value }.take(3).joinToString(" ") { it.key.getIcon() }
                    Text(text = topIcons.ifEmpty { "👍" }, fontSize = 12.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(text = "$localReactionCount", fontSize = 13.sp, color = TextMuted)
                }
            }

            Box {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            ReactionButton(currentReaction = localReaction, onLongPress = { showReactionPopup = true }, onClick = { handleReaction(ReactionType.LIKE) })
                        }
                        PostActionButton(icon = Icons.Outlined.ChatBubbleOutline, text = stringResource(R.string.BinhLuan), onClick = { onPostClick() }, modifier = Modifier.weight(1f))
                    }
                }
                if (showReactionPopup) {
                    ReactionPopup(onDismiss = { showReactionPopup = false }, onReactionSelected = { reaction ->
                        showReactionPopup = false
                        handleReaction(reaction)
                    })
                }
            }
        }
    }
}

@Composable
fun PostActionButton(icon: ImageVector, text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxHeight().clickable { onClick() }.padding(vertical = 12.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, color = TextMuted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ReactionButton(currentReaction: ReactionType?, onLongPress: () -> Unit, onClick: () -> Unit) {
    val buttonColor = if (currentReaction != null) SmartPickColor else TextMuted
    val text = when (currentReaction) {
        ReactionType.LIKE -> "Thích"; ReactionType.LOVE -> "Yêu thích"; ReactionType.HAHA -> "Haha"; ReactionType.WOW -> "Wow"; ReactionType.SAD -> "Buồn"; ReactionType.ANGRY -> "Phẫn nộ"; null -> stringResource(R.string.thich)
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().pointerInput(Unit) { detectTapGestures(onLongPress = { onLongPress() }, onTap = { onClick() }) }.padding(vertical = 12.dp)) {
        if (currentReaction != null) Text(currentReaction.getIcon(), fontSize = 18.sp) else Icon(Icons.Outlined.ThumbUp, null, modifier = Modifier.size(20.dp), tint = TextMuted)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 13.sp, color = buttonColor, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ReactionPopup(onDismiss: () -> Unit, onReactionSelected: (ReactionType) -> Unit) {
    val yOffset = with(LocalDensity.current) { -55.dp.roundToPx() }
    Popup(onDismissRequest = onDismiss, alignment = Alignment.TopCenter, offset = IntOffset(0, yOffset), properties = PopupProperties(focusable = true, dismissOnClickOutside = true, clippingEnabled = false)) {
        Card(shape = RoundedCornerShape(30.dp), elevation = CardDefaults.cardElevation(10.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                ReactionType.entries.forEach { type -> Text(text = type.getIcon(), fontSize = 32.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp).clickable { onReactionSelected(type) }) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharePostDialog(onDismiss: () -> Unit, onShare: (String) -> Unit) {
    var caption by remember { mutableStateOf("") }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(bottom = 32.dp)) {
            Text("Chia sẻ bài viết", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = caption,
                onValueChange = { caption = it },
                placeholder = { Text("Hãy nói gì đó về bài viết này...") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onShare(caption) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = SmartPickColor)) {
                Text("Chia sẻ ngay")
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
    onProductClick: (Product) -> Unit = {},
    onReactionClick: (String, ReactionType) -> Unit = { _, _ -> },
    onShareClick: (String) -> Unit = {},
    isDetailView: Boolean = false,
) {
    var showReactionPopup by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }

    var localReaction by remember(post.currentUserReaction) { mutableStateOf(post.currentUserReaction) }
    var localReactionCount by remember(post.reactionCount, post.currentUserReaction) { mutableIntStateOf(post.reactionCount) }
    var localBreakdown by remember(post.reactionBreakdown, post.currentUserReaction) { mutableStateOf(post.reactionBreakdown) }

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
        onReactionClick(post.id.toString(), reaction)
    }

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onPostClick() }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), shape = RoundedCornerShape(0.dp)) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {

            PostHeader(user = user, createdAt = post.createdAt ?: "Vừa xong", isShared = post.sharedPostId != null)

            if (post.sharedPost != null && post.sharedPostUser != null) {
                if (!post.content.isNullOrBlank()) {
                    Text(text = post.content, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontSize = 15.sp)
                }
                SharedPostCard(
                    sharedPost = post.sharedPost,
                    sharedUser = post.sharedPostUser,
                    onPostClick = { onPostClick() },
                    onReactionClick = onReactionClick
                )
            } else {
                PostMainContent(content = post.content, mediaUrls = post.mediaUrls, product = product, onMediaClick = { _ -> onPostClick() }, onProductClick = onProductClick)
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (localReactionCount > 0) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    val topIcons = localBreakdown.entries.sortedByDescending { it.value }.take(3).joinToString(" ") { it.key.getIcon() }
                    Text(text = topIcons.ifEmpty { "👍" }, fontSize = 12.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(text = "$localReactionCount", fontSize = 13.sp, color = TextMuted)
                }
            }

            Box {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            ReactionButton(currentReaction = localReaction, onLongPress = { showReactionPopup = true }, onClick = { handleReaction(ReactionType.LIKE) })
                        }
                        PostActionButton(icon = Icons.Outlined.ChatBubbleOutline, text = stringResource(R.string.BinhLuan), onClick = { onPostClick() }, modifier = Modifier.weight(1f))
                        PostActionButton(icon = Icons.Outlined.Share, text = stringResource(R.string.ChiaSe), onClick = { showShareDialog = true }, modifier = Modifier.weight(1f))
                    }
                }
                if (showReactionPopup) {
                    ReactionPopup(onDismiss = { showReactionPopup = false }, onReactionSelected = { reaction ->
                        showReactionPopup = false
                        handleReaction(reaction)
                    })
                }
            }
        }
    }

    if (showShareDialog) {
        SharePostDialog(
            onDismiss = { showShareDialog = false },
            onShare = { caption ->
                showShareDialog = false
                onShareClick(caption)
            }
        )
    }
}

// FIX: Cập nhật Format Tiền tệ VNĐ cho phần ProductHighlightCard ở màn Chi Tiết (PostDetail)
@Composable
fun ProductHighlightCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SmartPickColor.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, SmartPickColor.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = product.imageUrls.firstOrNull(), contentDescription = null, modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)

                // Format Giá tiền Việt Nam
                val priceFormatted = String.format("%,.0f đ", product.price).replace(",", ".")
                Text(priceFormatted, color = SmartPickColor, fontWeight = FontWeight.Bold)
            }
            Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = SmartPickColor), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp), shape = RoundedCornerShape(8.dp)) {
                Text("Mua ngay", fontSize = 12.sp)
            }
        }
    }
}