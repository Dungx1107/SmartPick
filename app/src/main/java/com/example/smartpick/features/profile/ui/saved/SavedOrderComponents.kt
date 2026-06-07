package com.example.smartpick.features.profile.ui.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smartpick.core.model.Order
import com.example.smartpick.core.model.OrderItemWithProduct
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun OrderHistoryCard(
    order: Order,
    onProductClick: (String) -> Unit,
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

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // --- PHẦN HHEADER ĐƠN HÀNG ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Đơn #${order.id.take(8).uppercase()}",
                        fontSize = 13.sp,
                        color = Color(0xFF6C757D),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ngày mua: $formattedDate",
                        fontSize = 11.sp,
                        color = Color(0xFF868E96)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = if (order.status == "completed") Color(0xFFD1E7DD) else Color(0xFFFFF3CD),
                    contentColor = if (order.status == "completed") Color(0xFF0F5132) else Color(0xFF856404)
                ) {
                    Text(
                        text = order.status.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFE9ECEF), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // --- PHẦN DANH SÁCH SẢN PHẨM CHI TIẾT ---
            // Duyệt qua TẤT CẢ các món hàng có trong đơn, hiển thị rõ ràng từng mục một
            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onProductClick(item.productId) } // Click vào món nào nhảy thẳng sang Chi tiết món đó
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = item.productImageUrl ?: "https://via.placeholder.com/150",
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F3F5)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.productName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212529),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Số lượng: x${item.quantity}",
                                fontSize = 12.sp,
                                color = Color(0xFF868E96)
                            )
                            val priceFormatted = String.format("%,.0f đ", item.priceAtPurchase).replace(",", ".")
                            Text(
                                text = priceFormatted,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF495057)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFE9ECEF), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // --- PHẦN DƯỚI ĐÁY ĐƠN HÀNG (TỔNG TOÀN ĐƠN) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hình thức: ${order.paymentMethod?.uppercase() ?: "COD"}",
                    fontSize = 12.sp,
                    color = Color(0xFF868E96),
                    fontWeight = FontWeight.Medium
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Tổng tiền: ",
                        fontSize = 13.sp,
                        color = Color(0xFF212529)
                    )
                    val totalFormatted = String.format("%,.0f đ", order.totalAmount).replace(",", ".")
                    Text(
                        text = totalFormatted,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE63946)
                    )
                }
            }
        }
    }
}

// --- PHẦN PREVIEW GIẢ LẬP GIAO DIỆN ---

@Preview(showBackground = true, name = "Đơn hàng 1 sản phẩm")
@Composable
fun OrderHistoryCardSinglePreview() {
    val mockOrder = Order(
        id = "ORD-778899",
        status = "completed",
        totalAmount = 70000.0,
        paymentMethod = "COD",
        createdAt = "2026-06-08T00:41:35.000000Z",
        items = listOf(
            OrderItemWithProduct(
                id = "item_1",
                productId = "prod_01",
                quantity = 1,
                priceAtPurchase = 70000.0,
                productName = "Bình nước thể thao 500ml",
                productImageUrl = null
            )
        )
    )

    Box(modifier = Modifier.padding(16.dp)) {
        OrderHistoryCard(order = mockOrder, onProductClick = {})
    }
}

@Preview(showBackground = true, name = "Đơn hàng nhiều sản phẩm (Bung chi tiết)")
@Composable
fun OrderHistoryCardMultiplePreview() {
    val mockOrder = Order(
        id = "ORD-112233",
        status = "completed",
        totalAmount = 500000.0,
        paymentMethod = "Chuyển khoản",
        createdAt = "2026-06-08T10:15:00.000000Z",
        items = listOf(
            OrderItemWithProduct(
                id = "item_1",
                productId = "prod_01",
                quantity = 1,
                priceAtPurchase = 100000.0,
                productName = "Bàn phím cơ DareU EK87",
                productImageUrl = null
            ),
            OrderItemWithProduct(
                id = "item_2",
                productId = "prod_02",
                quantity = 1,
                priceAtPurchase = 400000.0,
                productName = "Giày Thời Trang Sneaker Nam",
                productImageUrl = null
            )
        )
    )

    Box(modifier = Modifier.padding(16.dp)) {
        OrderHistoryCard(order = mockOrder, onProductClick = {})
    }
}