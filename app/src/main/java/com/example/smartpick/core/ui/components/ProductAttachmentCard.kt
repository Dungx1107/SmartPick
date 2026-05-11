package com.example.smartpick.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.smartpick.core.ui.theme.PageBg
import com.example.smartpick.core.ui.theme.SurfaceCard
import java.text.NumberFormat
import java.util.*

@Composable
fun ProductAttachmentCard(
    product: Product,
    onClick: (String) -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { product.id?.let { onClick(it) } },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hiển thị ảnh đầu tiên của sản phẩm
            AsyncImage(
                model = product.imageUrls.firstOrNull(),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PageBg),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (!product.brand.isNullOrEmpty()) {
                    Text(
                        text = product.brand,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = formatCurrency(product.price),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            // Trạng thái sản phẩm
            Surface(
                color = if (product.status == "available") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (product.status == "available") "Sẵn có" else "Hết hàng",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    color = if (product.status == "available") Color(0xFF2E7D32) else Color(
                        0xFFC62828
                    )
                )
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return format.format(amount)
}

@Preview(showBackground = true)
@Composable
private fun ProductAttachmentCardPreview() {

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

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PageBg)
                .padding(16.dp)
        ) {
            ProductAttachmentCard(
                product = fakeProduct,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductAttachmentCardUnavailablePreview() {

    val fakeProduct = Product(
        id = "product_2",
        ownerId = "user_2",
        name = "MacBook Pro M3",
        brand = "Apple",
        category = "Laptop",
        price = 45990000.0,
        imageUrls = listOf(
            "https://images.unsplash.com/photo-1496181133206-80ce9b88a853"
        ),
        status = "sold",
        createdAt = "2026-05-11T10:00:00"
    )

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PageBg)
                .padding(16.dp)
        ) {
            ProductAttachmentCard(
                product = fakeProduct,
                onClick = {}
            )
        }
    }
}