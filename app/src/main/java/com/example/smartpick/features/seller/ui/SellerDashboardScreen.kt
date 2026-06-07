package com.example.smartpick.features.seller.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.smartpick.core.data.dto.ProductDto
import com.example.smartpick.core.model.Product
import com.example.smartpick.features.seller.data.SellerRepository
import com.example.smartpick.features.seller.viewmodel.SellerStats
import com.example.smartpick.features.seller.viewmodel.SellerViewModel
import com.example.smartpick.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardScreen(
    onBackClick: () -> Unit,
    navController: NavController,
    viewModel: SellerViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val products by viewModel.myProducts.collectAsState()
    val orders by viewModel.soldOrders.collectAsState()
    val stats by viewModel.sellerStats.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gian hàng của tôi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // ĐÃ CHỈNH SỬA: Thanh điều khiển chia làm 3 Tab nằm ngay dưới tiêu đề ứng dụng
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }) {
                        Text(
                            "Sản phẩm đang bán",
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }) {
                        Text(
                            "Đơn hàng đã bán (${orders.size})",
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Tab(selected = selectedTabIndex == 2, onClick = { selectedTabIndex = 2 }) {
                        Text(
                            "Tổng doanh thu",
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Khu vực hiển thị nội dung chi tiết theo từng Tab điều khiển
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedTabIndex) {
                        0 -> ActiveProductsTab(
                            products = products,
                            onProductClick = { productId ->
                                navController.navigate(Routes.ProductDetail.createRoute(productId))
                            })

                        1 -> SoldOrdersTab(
                            orders = orders,
                            onProductClick = { productId ->
                                navController.navigate(
                                    Routes.ProductDetail.createRoute(
                                        productId
                                    )
                                )
                            }
                        )

                        2 -> RevenueTab(stats = stats)
                    }
                }
            }
        }
    }
}

// --- Tab 1: Danh sách sản phẩm của người bán ---
@Composable
fun ActiveProductsTab(
    products: List<Product>,
    onProductClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (products.isEmpty()) {
            item {
                Text(
                    "Bạn chưa đăng tải sản phẩm nào.",
                    modifier = Modifier.padding(top = 20.dp),
                    color = Color.Gray
                )
            }
        }
        items(products) { product ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onProductClick(product.id.toString()) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = product.imageUrls.firstOrNull()
                            ?: "https://via.placeholder.com/150",
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            product.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "Kho: ${product.stock} | Giá: ${
                                String.format("%,.0f đ", product.price).replace(",", ".")
                            }", fontSize = 13.sp, color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

// --- Tab 2: Quản lý đơn hàng đã bán (Có tích hợp ảnh sản phẩm và điều hướng click) ---
@Composable
fun SoldOrdersTab(
    orders: List<SellerRepository.SoldOrderItemDto>,
    onProductClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (orders.isEmpty()) {
            item {
                Text(
                    "Chưa có đơn hàng nào được bán ra.",
                    modifier = Modifier.padding(top = 20.dp),
                    color = Color.Gray
                )
            }
        }
        items(orders) { order ->
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
                        model = order.products?.imageUrls?.firstOrNull()
                            ?: "https://via.placeholder.com/150",
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
                            val priceFormatted =
                                String.format("%,.0f đ", order.priceAtPurchase).replace(",", ".")
                            Text(
                                text = "Đơn giá: $priceFormatted",
                                fontSize = 12.sp,
                                color = Color(0xFF868E96)
                            )

                            val totalFormatted =
                                String.format("%,.0f đ", order.priceAtPurchase * order.quantity)
                                    .replace(",", ".")
                            Text(
                                text = totalFormatted,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF2EC4B6)
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Tab 3: Thống kê chi tiết tổng doanh thu gian hàng ---
@Composable
fun RevenueTab(stats: com.example.smartpick.features.seller.viewmodel.SellerStats) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F8F5))
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = Color(0xFF2EC4B6),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "Tổng doanh thu tích lũy",
                        fontSize = 14.sp,
                        color = Color(0xFF118A7E),
                        fontWeight = FontWeight.Medium
                    )
                    val totalRevenueFormatted =
                        String.format("%,.0f đ", stats.totalRevenue).replace(",", ".")
                    Text(
                        totalRevenueFormatted,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F5132)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = Color(0xFFE71D36),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Tổng số đơn", fontSize = 13.sp, color = Color.Gray)
                    Text(
                        stats.totalOrders.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = Color(0xFFFF9F1C),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Sản phẩm đã bán", fontSize = 13.sp, color = Color.Gray)
                    Text(
                        stats.totalProductsSold.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- Preview 1: Kiểm tra giao diện Tab Tổng Doanh Thu mới thay thế cho StatsSection cũ ---
@Preview(showBackground = true)
@Composable
private fun RevenueTabPreview() {
    MaterialTheme {
        RevenueTab(
            stats = SellerStats(
                totalRevenue = 156750000.0,
                totalOrders = 42,
                totalProductsSold = 68
            )
        )
    }
}

// --- Preview 2: Kiểm tra danh sách đơn hàng đã bán kèm hình ảnh và giá cả ---
@Preview(
    showBackground = true,
    showSystemUi = true,
    widthDp = 420,
    heightDp = 900
)
@Composable
private fun SoldOrdersTabPreview() {
    val iphone = ProductDto(
        id = "p1",
        ownerId = "seller1",
        name = "iPhone 15 Pro Max 256GB",
        brand = "Apple",
        category = "Điện thoại",
        price = 32990000.0,
        imageUrls = listOf("https://picsum.photos/400/400"),
        stock = 10,
        soldCount = 15
    )

    val macbook = ProductDto(
        id = "p2",
        ownerId = "seller1",
        name = "MacBook Air M3 16GB / 512GB",
        brand = "Apple",
        category = "Laptop",
        price = 28990000.0,
        imageUrls = listOf("https://picsum.photos/401/401"),
        stock = 5,
        soldCount = 8
    )

    val orders = listOf(
        SellerRepository.SoldOrderItemDto(
            id = "1",
            orderId = "ORD123456789",
            productId = "p1",
            quantity = 2,
            priceAtPurchase = 32990000.0,
            createdAt = "2026-06-07",
            products = iphone
        ),
        SellerRepository.SoldOrderItemDto(
            id = "2",
            orderId = "ORD987654321",
            productId = "p2",
            quantity = 1,
            priceAtPurchase = 28990000.0,
            createdAt = "2026-06-06",
            products = macbook
        )
    )

    MaterialTheme {
        SoldOrdersTab(
            orders = orders,
            onProductClick = {}
        )
    }
}

// --- Preview 3: Mô phỏng toàn bộ bố cục Dashboard chia 3 phần chuẩn hóa mới ---
@Preview(
    showBackground = true,
    showSystemUi = true,
    widthDp = 420,
    heightDp = 900
)
@Composable
private fun SellerDashboardContentPreview() {
    val stats = SellerStats(
        totalRevenue = 156750000.0,
        totalOrders = 42,
        totalProductsSold = 68
    )

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Thanh TabRow điều khiển 3 phần nằm ngay trên cùng dưới thanh tiêu đề
            TabRow(
                selectedTabIndex = 1, // Đang mô phỏng hiển thị Tab Đơn hàng đã bán
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(selected = false, onClick = {}) {
                    Text(
                        text = "Sản phẩm đang bán",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp
                    )
                }
                Tab(selected = true, onClick = {}) {
                    Text(
                        text = "Đơn hàng đã bán (1)",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp
                    )
                }
                Tab(selected = false, onClick = {}) {
                    Text(
                        text = "Tổng doanh thu",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp
                    )
                }
            }

            // 2. Vùng hiển thị nội dung chi tiết tương ứng bên dưới
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                SoldOrdersTab(
                    orders = listOf(
                        SellerRepository.SoldOrderItemDto(
                            id = "1",
                            orderId = "ORD123456789",
                            productId = "p1",
                            quantity = 2,
                            priceAtPurchase = 32990000.0,
                            products = ProductDto(
                                id = "p1",
                                ownerId = "seller1",
                                name = "iPhone 15 Pro Max 256GB",
                                price = 32990000.0,
                                imageUrls = listOf("https://picsum.photos/400/400")
                            )
                        )
                    ),
                    onProductClick = {}
                )
            }
        }
    }
}