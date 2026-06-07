package com.example.smartpick.features.profile.ui.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartpick.core.model.Order
import com.example.smartpick.core.model.OrderItemWithProduct
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.features.checkout.viewmodel.CheckoutViewModel
import com.example.smartpick.navigation.Routes

@Composable
fun SavedOrdersScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    checkoutViewModel: CheckoutViewModel = hiltViewModel()
) {
    val orders by checkoutViewModel.orders.collectAsState()

    // Gọi API nạp dữ liệu khi màn hình mở ra
    LaunchedEffect(Unit) {
        checkoutViewModel.loadOrderHistory()
    }

    SavedOrdersContent(
        paddingValues = paddingValues,
        orders = orders,
        onProductClick = { productId ->
            navController.navigate(
                Routes.ProductDetail.createRoute(
                    productId
                )
            )
        })
}

@Composable
fun SavedOrdersContent(
    paddingValues: PaddingValues,
    orders: List<Order>,
    onProductClick: (String) -> Unit,
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
            // Tiêu đề trang
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
                        fontWeight = FontWeight.Medium
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
                        OrderHistoryCard(
                            order = orderItem,
                            onProductClick = onProductClick
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Màn hình Lịch sử")
@Composable
fun SavedOrdersScreenPreview() {
    val mockItems = listOf(
        OrderItemWithProduct(
            id = "1",
            productId = "product_123",
            quantity = 1,
            priceAtPurchase = 1850000.0,
            productName = "Bàn phím Keychron K2 V2",
            productImageUrl = null
        ),
        OrderItemWithProduct(
            id = "2",
            productId = "product_124",
            quantity = 2,
            priceAtPurchase = 20000.0,
            productName = "Switch mỡ",
            productImageUrl = null
        )
    )
    val mockOrders = listOf(
        Order(
            id = "order_12345",
            totalAmount = 1890000.0,
            status = "completed",
            createdAt = "2026-06-07T08:00:00Z",
            items = mockItems
        )
    )
    SmartPickTheme {
        SavedOrdersContent(
            paddingValues = PaddingValues(),
            orders = mockOrders,
            onProductClick = {})
    }
}