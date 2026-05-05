package com.example.smartpick.features.feed.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import com.example.smartpick.core.theme.DividerColor
import com.example.smartpick.core.theme.White

@Composable
fun PostItem(
    post: Post,
    user: User,
    product: Product? = null, // Thêm sản phẩm đính kèm (nếu có)
    onPostClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {}, // Click vào card sản phẩm
    onMediaClick: (Int) -> Unit = {}, // Click vào ảnh/video cụ thể
    showFullContent: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clickable(onClick = onPostClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp), // Bo góc vừa phải kiểu hiện đại
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .background(White)
                .padding(vertical = 8.dp)
        ) {
            // 1. Thông tin người đăng
            PostHeader(user = user, createdAt = post.createdAt.toString())

            // 2. Nội dung văn bản và Lưới đa phương tiện (Ảnh/Video)
            // Sử dụng mediaUrls thay vì images cũ
            PostContent(
                content = post.content,
                mediaUrls = post.mediaUrls,
                maxLines = if (showFullContent) Int.MAX_VALUE else 3,
                onMediaClick = onMediaClick
            )

            // 3. Hiển thị Card Sản phẩm nếu bài đăng có đính kèm Product
            product?.let {
                Spacer(modifier = Modifier.height(8.dp))
                ProductTagCard(
                    product = it,
                    onClick = { onProductClick(it.id.toString()) }
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                thickness = 0.5.dp,
                color = DividerColor.copy(alpha = 0.2f)
            )

            // 4. Các nút tương tác (Like, Comment, Share)
            PostFooterActions(onCommentClick = onCommentClick)
        }
    }
}

/**
 * Thành phần hiển thị tóm tắt sản phẩm đính kèm bên trong bài viết
 */
@Composable
fun ProductTagCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            // Ảnh nhỏ sản phẩm
            com.example.smartpick.features.profile.ui.ProfileAvatar(
                avatarUrl = product.imageUrls.firstOrNull(),
                size = 50.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = "${product.price} VNĐ", // Sau này dùng formatter cho tiền tệ
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFEF4444),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }

            Text(
                text = "Xem",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF3B82F6)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FullPostWithProductPreview() {
    val mockUser = User(
        id = "u1",
        fullName = "Nguyễn Xuân Dũng",
        avatarUrl = null
    )

    val mockProduct = Product(
        id = "pro1",
        ownerId = "u1",
        name = "Bàn phím cơ Akko 3068B Multi-modes",
        brand = "Akko",
        price = 1550000.0,
        category = "Phụ kiện máy tính"
    )

    val mockPost = Post(
        id = "p1",
        userId = "u1",
        productId = "pro1",
        content = "Cuối cùng cũng tậu được em bàn phím này, gõ cực phê mọi người ạ! Link cho ai cần nhé.",
        createdAt = "Vừa xong",
        mediaUrls = listOf("https://via.placeholder.com/600")
    )

    MaterialTheme {
        PostItem(
            post = mockPost,
            user = mockUser,
            product = mockProduct
        )
    }
}