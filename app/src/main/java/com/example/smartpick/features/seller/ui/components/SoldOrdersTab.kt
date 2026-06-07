package com.example.smartpick.features.seller.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smartpick.features.seller.data.SellerRepository

@Composable
fun SoldOrdersTab(
    orders: List<SellerRepository.SoldOrderItemDto>,
    onProductClick: (String) -> Unit
) {
    if (orders.isEmpty()) {
        EmptyStateView(message = "Chưa có đơn hàng nào được bán ra.")
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = orders,
            key = { order -> order.id }
        ) { order ->
            SoldOrderItemRow(order = order, onProductClick = onProductClick)
        }
    }
}

@Composable
private fun SoldOrderItemRow(
    order: SellerRepository.SoldOrderItemDto,
    onProductClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick(order.productId) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = order.products?.imageUrls?.firstOrNull() ?: "https://via.placeholder.com/150",
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F3F5)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Mã đơn: ${order.orderId.take(8).uppercase()}",
                    fontSize = 12.sp,
                    color = Color(0xFF6C757D),
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = order.products?.name ?: "Sản phẩm không xác định",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF212529),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Số lượng: ${order.quantity}",
                    fontSize = 13.sp,
                    color = Color(0xFF495057)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Đơn giá: ${order.priceAtPurchase.formatCurrency()}",
                        fontSize = 12.sp,
                        color = Color(0xFF868E96)
                    )

                    val totalPrice = order.priceAtPurchase * order.quantity
                    Text(
                        text = totalPrice.formatCurrency(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF2EC4B6)
                    )
                }
            }
        }
    }
}