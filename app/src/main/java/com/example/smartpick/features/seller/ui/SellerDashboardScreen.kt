package com.example.smartpick.features.seller.ui

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.smartpick.core.data.dto.ProductDto
import com.example.smartpick.core.model.Product
import com.example.smartpick.features.seller.data.SellerRepository
import com.example.smartpick.features.seller.viewmodel.SellerStats
import com.example.smartpick.features.seller.viewmodel.SellerViewModel

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

    SellerDashboardContent(
        isLoading = isLoading,
        products = products,
        orders = orders,
        stats = stats,
        onBackClick = onBackClick,
        onProductClick = { productId ->
            navController.navigate("product_detail/$productId")
        }
    )
}

// ============================================================================
// PREVIEWS FOR SCREEN LAYER
// ============================================================================

// Dữ liệu giả lập khớp chính xác với cấu trúc Model hệ thống
private val dummyProducts = listOf(
    Product(
        id = "p1",
        ownerId = "seller1",
        name = "iPhone 15 Pro Max 256GB",
        price = 32990000.0,
        stock = 10,
        imageUrls = listOf("https://picsum.photos/200")
    ),
    Product(
        id = "p2",
        ownerId = "seller1",
        name = "MacBook Air M3 16GB",
        price = 28990000.0,
        stock = 5,
        imageUrls = listOf("https://picsum.photos/201")
    )
)

private val dummyOrders = listOf(
    SellerRepository.SoldOrderItemDto(
        id = "o1",
        orderId = "ORD123456789",
        productId = "p1",
        quantity = 2,
        priceAtPurchase = 32990000.0,
        products = ProductDto(
            id = "p1",
            ownerId = "seller1",
            name = "iPhone 15 Pro Max 256GB",
            price = 32990000.0,
            imageUrls = listOf("https://picsum.photos/200")
        )
    )
)

private val dummyStats = SellerStats(
    totalRevenue = 94970000.0,
    totalOrders = 3,
    totalProductsSold = 3
)

@Preview(name = "Screen - Tab Sản Phẩm", showBackground = true, showSystemUi = true)
@Composable
private fun SellerDashboardScreenTab1Preview() {
    val mockNavController = rememberNavController()
    // Gọi trực tiếp Content Component từ tầng Screen Preview để bỏ qua khởi tạo ViewModel thực tế
    SellerDashboardContent(
        isLoading = false,
        products = dummyProducts,
        orders = dummyOrders,
        stats = dummyStats,
        initialTabIndex = 0, // Tab sản phẩm đang bán
        onBackClick = {},
        onProductClick = {}
    )
}

@Preview(name = "Screen - Tab Đơn Hàng", showBackground = true, showSystemUi = true)
@Composable
private fun SellerDashboardScreenTab2Preview() {
    SellerDashboardContent(
        isLoading = false,
        products = dummyProducts,
        orders = dummyOrders,
        stats = dummyStats,
        initialTabIndex = 1, // Ép giao diện hiển thị Tab đơn hàng đã bán
        onBackClick = {},
        onProductClick = {}
    )
}

@Preview(name = "Screen - Tab Doanh Thu", showBackground = true, showSystemUi = true)
@Composable
private fun SellerDashboardScreenTab3Preview() {
    SellerDashboardContent(
        isLoading = false,
        products = dummyProducts,
        orders = dummyOrders,
        stats = dummyStats,
        initialTabIndex = 2, // Ép giao diện hiển thị Tab thống kê doanh thu
        onBackClick = {},
        onProductClick = {}
    )
}

@Preview(name = "Screen - Trạng thái Loading", showBackground = true, showSystemUi = true)
@Composable
private fun SellerDashboardScreenLoadingPreview() {
    SellerDashboardContent(
        isLoading = true,
        products = emptyList(),
        orders = emptyList(),
        stats = dummyStats,
        initialTabIndex = 0,
        onBackClick = {},
        onProductClick = {}
    )
}