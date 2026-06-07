package com.example.smartpick.features.seller.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.core.ui.theme.SmartPickColor
import com.example.smartpick.features.seller.viewmodel.SellerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardScreen(
    onBackClick: () -> Unit,
    viewModel: SellerViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val products by viewModel.myProducts.collectAsState()
    val orders by viewModel.soldOrders.collectAsState()
    val stats by viewModel.sellerStats.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Trưng bày", "Thống kê", "Đơn đã bán")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gian hàng của tôi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SmartPickColor)
                }
            } else {
                when (selectedTabIndex) {
                    0 -> SellerProductsTab(products)
                    1 -> SellerStatsTab(stats)
                    2 -> SoldOrdersTab(orders)
                }
            }
        }
    }
}

@Composable
fun SellerProductsTab(products: List<com.example.smartpick.core.model.Product>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Sản phẩm đang bán (${products.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
        items(products) { product ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Column {
                        Text(product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Kho: ${product.stock} | Đã bán: ${product.soldCount}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Giá: ${String.format("%,.0f đ", product.price)}", color = SmartPickColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SellerStatsTab(stats: com.example.smartpick.features.seller.viewmodel.SellerStats) {
    Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        StatCard(title = "Tổng doanh thu", value = String.format("%,.0f VNĐ", stats.totalRevenue), icon = Icons.Default.MonetizationOn, color = SmartPickColor)
        StatCard(title = "Tổng đơn hàng", value = "${stats.totalOrders} đơn", icon = Icons.Default.ShoppingCart, color = MaterialTheme.colorScheme.secondary)
        StatCard(title = "Sản phẩm đã bán ra", value = "${stats.totalProductsSold} sản phẩm", icon = Icons.Default.Inventory, color = MaterialTheme.colorScheme.tertiary)
    }
}

@Composable
fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: androidx.compose.ui.graphics.Color) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            }
        }
    }
}

@Composable
fun SoldOrdersTab(orders: List<com.example.smartpick.features.seller.data.SellerRepository.SoldOrderItemDto>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (orders.isEmpty()) {
            item { Text("Chưa có đơn hàng nào được bán ra.", modifier = Modifier.padding(top = 20.dp)) }
        }
        items(orders) { order ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Mã đơn: ${order.orderId.take(8).uppercase()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Sản phẩm: ${order.products?.name ?: "Sản phẩm ẩn"}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Số lượng: ${order.quantity} | Đơn giá: ${String.format("%,.0f đ", order.priceAtPurchase)}")
                    Text("Ngày mua: ${order.createdAt?.split("T")?.firstOrNull() ?: ""}", fontSize = 12.sp)
                }
            }
        }
    }
}