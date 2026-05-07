package com.example.smartpick.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.ui.theme.AccentBlue
import com.example.smartpick.core.ui.theme.TextMuted
import com.example.smartpick.core.ui.theme.White
import java.util.Locale

/**
 * BIẾN THỂ 1: ProductVerticalCard (Dạng dọc)
 * Dùng cho Grid ở trang Home, Tìm kiếm.
 */
@Composable
fun ProductVerticalCard(
    product: Product,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = product.imageUrls.firstOrNull(),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                product.brand?.let {
                    Text(it, fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                }
                Text(product.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        "${String.format(Locale.getDefault(), "%,.0f", product.price)}đ",
                        color = AccentBlue,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(Icons.Outlined.AddShoppingCart, null, tint = AccentBlue, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

/**
 * BIẾN THỂ 2: ProductHorizontalTag (Dạng ngang)
 * Dùng đính kèm trong Bài viết (Post), Chat, hoặc Danh sách nhỏ.
 */
@Composable
fun ProductHorizontalTag(
    product: Product,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileAvatar(
                avatarUrl = product.imageUrls.firstOrNull(),
                size = 50.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${String.format(Locale.getDefault(), "%,.0f", product.price)} VNĐ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFEF4444),
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Xem",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF3B82F6),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
