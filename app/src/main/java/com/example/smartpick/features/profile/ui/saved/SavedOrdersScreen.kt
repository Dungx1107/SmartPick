package com.example.smartpick.features.profile.ui.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartpick.core.model.Order
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.features.checkout.viewmodel.CheckoutViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun SavedOrdersScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    checkoutViewModel: CheckoutViewModel = hiltViewModel()
) {
    val orders by checkoutViewModel.orders.collectAsState()

    LaunchedEffect(Unit) {
        checkoutViewModel.loadOrderHistory()
    }

    SavedOrdersContent(
        paddingValues = paddingValues,
        orders = orders
    )
}

@Composable
fun SavedOrdersContent(
    paddingValues: PaddingValues,
    orders: List<Order>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                top = paddingValues.calculateTopPadding()
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Lịch sử mua hàng",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = Color(0xFF1A1A1A)
                )
            }

            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Chưa có đơn hàng nào trong lịch sử",
                        color = Color(0xFF6C757D),
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(
                        items = orders,
                        key = { it.id }
                    ) { orderItem ->
                        OrderHistoryCard(order = orderItem)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderHistoryCard(order: Order, modifier: Modifier = Modifier) {
    val formattedDate = remember(order.createdAt) {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val outputFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
            val date = inputFormat.parse(order.createdAt)
            if (date != null) outputFormat.format(date) else order.createdAt
        } catch (e: Exception) {
            if (order.createdAt.contains("T")) order.createdAt.substringBefore("T") else order.createdAt
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF4EA8DE), Color(0xFF5390D9))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mã đơn: #${order.id.take(8).uppercase()}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212529)
                    )

                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFD1E7DD),
                        contentColor = Color(0xFF0F5132)
                    ) {
                        Text(
                            text = order.status.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(text = "Thời gian: $formattedDate", fontSize = 12.sp, color = Color(0xFF6C757D))
                Text(text = "Thanh toán: ${order.paymentMethod ?: "COD"}", fontSize = 12.sp, color = Color(0xFF6C757D))

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Tổng tiền:", fontSize = 13.sp, color = Color(0xFF495057), modifier = Modifier.padding(end = 4.dp))
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

@Preview(showBackground = true, showSystemUi = true, name = "Màn hình lịch sử mua hàng")
@Composable
fun SavedOrdersScreenPreview() {
    val mockOrders = listOf(
        Order(id = "order_12345678", totalAmount = 1850000.0, paymentMethod = "Ví MoMo", status = "completed", createdAt = "2026-06-07T08:00:00.000000Z"),
        Order(id = "order_87654321", totalAmount = 650000.0, paymentMethod = "COD", status = "completed", createdAt = "2026-06-01T14:30:00.000000Z")
    )
    SmartPickTheme {
        SavedOrdersContent(paddingValues = PaddingValues(), orders = mockOrders)
    }
}