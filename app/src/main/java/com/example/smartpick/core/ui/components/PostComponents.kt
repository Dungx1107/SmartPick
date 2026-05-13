package com.example.smartpick.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User

/**
 * THÀNH PHẦN 1: PostHeader
 * Hiển thị thông tin người đăng và thời gian.
 */
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
        ProfileAvatar(avatarUrl = user.avatarUrl, size = 40.dp)
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.fullName ?: stringResource(R.string.smartpick_user),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1C1E21)
            )
            Text(text = createdAt, fontSize = 12.sp, color = Color(0xFF65676B))
        }
        IconButton(onClick = onMoreClick) {
            Icon(Icons.Outlined.MoreHoriz, null, tint = Color(0xFF65676B))
        }
    }
}

/**
 * THÀNH PHẦN 2: PostMainContent
 * Bao gồm Text, Media (Grid) và Product Tag (nếu có).
 */
@Composable
fun PostMainContent(
    content: String?,
    mediaUrls: List<String> = emptyList(),
    product: Product? = null,
    onMediaClick: (Int) -> Unit = {},
    onProductClick: (Product) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (!content.isNullOrBlank()) {
            Text(
                text = content,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                fontSize = 15.sp,
                lineHeight = 20.sp,
                color = Color(0xFF050505)
            )
        }

        if (mediaUrls.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            MediaGrid(mediaUrls = mediaUrls)
        }

        product?.let {
            Spacer(modifier = Modifier.height(12.dp))
            ProductHorizontalTag(
                product = it,
                modifier = Modifier.padding(horizontal = 12.dp),
                onClick = { onProductClick(it) }
            )
        }
    }
}

/**
 * THÀNH PHẦN 3: PostFooterActions
 * Các nút Like, Comment, Share.
 */
@Composable
fun PostFooterActions(
    onLikeClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
) {
    Column(modifier = Modifier) {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 12.dp),
            thickness = 0.5.dp,
            color = Color(0xFFE4E6EB)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            PostActionButton(
                Icons.Outlined.FavoriteBorder,
                stringResource(R.string.thich),
                onLikeClick,
                Modifier.weight(1f)
            )
            PostActionButton(
                Icons.Outlined.ChatBubbleOutline,
                stringResource(R.string.BinhLuan),
                onCommentClick,
                Modifier.weight(1f)
            )
            PostActionButton(
                Icons.Outlined.Share,
                stringResource(R.string.ChiaSe),
                onShareClick,
                Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PostActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = Color(0xFF65676B))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 13.sp,
                color = Color(0xFF65676B),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * CONTAINER TỔNG HỢP: PostItem
 * Dùng để hiển thị một bài viết trọn gói.
 */
@Composable
fun PostItem(
    post: Post,
    user: User,
    product: Product? = null,
    onPostClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onProductClick: (Product) -> Unit = {},
    onViewImagesGalleryRequest: (List<String>, Int) -> Unit = { _, _ -> },
    isDetailView: Boolean = false,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onPostClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp) // Flat style like Facebook
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            PostHeader(user = user, createdAt = post.createdAt ?: "Vừa xong")
            PostMainContent(
                content = post.content,
                mediaUrls = post.mediaUrls,
                product = product,
                // Ánh xạ sự kiện click ảnh trong grid sang yêu cầu xem gallery
                onMediaClick = { clickedIndex ->
                    // Truyền toàn bộ danh sách ảnh của bài viết và vị trí được nhấp
                    onViewImagesGalleryRequest(post.mediaUrls, clickedIndex)
                }, onProductClick = onProductClick
            )
            Spacer(modifier = Modifier.height(8.dp))
            PostFooterActions(onCommentClick = onCommentClick)
        }
    }
}
