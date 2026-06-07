package com.example.smartpick.features.profile.ui.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smartpick.core.model.Order
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


@Composable
fun OrderHistoryCard(
    order: Order,
    onProductClick: (String) -> Unit, // THÊM: Lambda điều hướng
    modifier: Modifier = Modifier
) {
    val formattedDate = remember(order.createdAt) {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val outputFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
            val date = inputFormat.parse(order.createdAt)
            if (date != null) outputFormat.format(date) else order.createdAt
        } catch (e: Exception) {
            order.createdAt.substringBefore("T")
        }
    }

    val firstItem = order.items.firstOrNull()
    val otherItemsCount = if (order.items.size > 1) order.items.size - 1 else 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                // Khi nhấn vào Card, nếu có sản phẩm thì kích hoạt điều hướng
                firstItem?.productId?.let { productId ->
                    onProductClick(productId)
                }
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // Giữ nguyên phần Row và Column hiển thị nội dung bên trong...
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // AsyncImage và các thành phần Text giữ nguyên như bước trước
            AsyncImage(
                model = firstItem?.productImageUrl ?: "https://via.placeholder.com/150",
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF1F3F5)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Đơn #${order.id.take(8).uppercase()}",
                        fontSize = 12.sp,
                        color = Color(0xFF6C757D),
                        fontWeight = FontWeight.Medium
                    )

                    Surface(
                        shape = CircleShape,
                        color = if (order.status == "completed") Color(0xFFD1E7DD) else Color(0xFFFFF3CD),
                        contentColor = if (order.status == "completed") Color(0xFF0F5132) else Color(0xFF856404)
                    ) {
                        Text(
                            text = order.status.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Text(
                    text = if (otherItemsCount > 0) {
                        "${firstItem?.productName ?: "Sản phẩm"} và $otherItemsCount sản phẩm khác"
                    } else {
                        firstItem?.productName ?: "Sản phẩm chưa cập nhật"
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212529),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Ngày mua: $formattedDate",
                    fontSize = 12.sp,
                    color = Color(0xFF868E96)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Thanh toán: ${order.paymentMethod ?: "COD"}",
                        fontSize = 12.sp,
                        color = Color(0xFF868E96)
                    )

                    val totalFormatted = String.format("%,.0f đ", order.totalAmount).replace(",", ".")
                    Text(
                        text = totalFormatted,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE63946)
                    )
                }
            }
        }
    }
}