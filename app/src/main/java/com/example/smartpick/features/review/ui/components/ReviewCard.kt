package com.example.smartpick.features.review.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.Review
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.core.ui.theme.TextMuted
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ReviewCard(
    review: Review,
    onProductClick: (String) -> Unit, // Luồng sự kiện click điều hướng chuẩn chỉ
    modifier: Modifier = Modifier
) {
    // Lấy trực tiếp product nằm trong cấu trúc đối tượng review đã được map chuẩn
    val product = review.product
    val formattedPrice = remember(product?.price) {
        val vietnamLocale = Locale.Builder()
            .setLanguage("vi")
            .setRegion("VN")
            .build()

        val formatter = NumberFormat.getCurrencyInstance(vietnamLocale)
        formatter.format(product?.price ?: 0.0)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { product?.id?.let { onProductClick(it) } },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // --- PHẦN 1: THẺ TÓM TẮT SẢN PHẨM ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = product?.imageUrls?.firstOrNull(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.outlineVariant),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product?.name ?: "Sản phẩm không tồn tại",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Người bán: ${product?.ownerName ?: "Thành viên SmartPick"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = formattedPrice,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- PHẦN 2: NỘI DUNG ĐÁNH GIÁ CỦA CHÍNH BẠN ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Đánh giá của bạn:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (index < review.rating) Color(0xFFFFC107) else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = review.content.ifBlank { "Không có nội dung bình luận." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Ngày: ${review.createdAt.split("T").firstOrNull() ?: ""}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewCardPreview() {
    val mockProduct = Product(
        id = "9be45dec-e595-4a1a-bf87-574871eea17a",
        ownerId = "seller_123",
        name = "Bình nước thể thao giữ nhiệt cao cấp SmartPick 1000ml",
        brand = "SmartPick",
        category = "Đồ gia dụng",
        price = 250000.0,
        imageUrls = listOf("https://via.placeholder.com/150"),
        ownerName = "Gia Dụng Hub Hà Nội"
    )

    SmartPickTheme {
        ReviewCard(
            review = Review(
                id = "review_1",
                userId = "user_1",
                productId = "9be45dec-e595-4a1a-bf87-574871eea17a",
                rating = 5,
                content = "Bình giữ nhiệt rất tốt, chất liệu inox 316 dày dặn, shop bọc hàng kỹ, đáng tiền mua nha mọi người!",
                createdAt = "2026-06-07T19:45:00",
                product = mockProduct
            ),
            onProductClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}