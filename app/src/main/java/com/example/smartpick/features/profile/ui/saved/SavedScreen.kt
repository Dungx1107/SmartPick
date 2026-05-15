// File: app/src/main/java/com/example/smartpick/features/profile/ui/saved/SavedScreen.kt
package com.example.smartpick.features.profile.ui.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.ui.theme.PageBg
import com.example.smartpick.features.home.viewmodel.HomeViewModel

@Composable
fun SavedCollectionScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val cartItems by homeViewModel.cartItems.collectAsState()

    // Quản lý trạng thái Thư mục đang chọn
    var selectedCategory by remember { mutableStateOf("Giỏ hàng") }

    Scaffold(
        containerColor = PageBg,
        // Cấu trúc Bottom Bar Thanh toán cố định (Chỉ hiện khi ở tab Giỏ hàng và có đồ)
        bottomBar = {
            if (selectedCategory == "Giỏ hàng" && cartItems.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 16.dp,
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val total = cartItems.sumOf { (it.products?.price ?: 0.0) * it.quantity }
                        Column {
                            Text("Tổng cộng", fontSize = 12.sp, color = Color.Gray)
                            Text("${total}đ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        }
                        Button(
                            onClick = { /* TODO: Điều hướng sang màn thanh toán */ },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
                        ) {
                            Text("Thanh toán (${cartItems.sumOf { it.quantity }})")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section: Categories
            item(span = { GridItemSpan(2) }) {
                CategorySection(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }

            // Section: Nội dung thay đổi dựa trên Thư mục
            if (selectedCategory == "Giỏ hàng") {
                item(span = { GridItemSpan(2) }) {
                    Text(
                        "Giỏ hàng của bạn",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (cartItems.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text("Giỏ hàng trống", color = Color.Gray)
                        }
                    }
                } else {
                    items(cartItems) { item ->
                        CartGridCard(
                            item = item,
                            onIncrease = { homeViewModel.increaseQuantity(it) },
                            onDecrease = { homeViewModel.decreaseQuantity(it) }
                        )
                    }
                }
            } else {
                // Hiển thị danh sách Đã lưu bình thường
                item(span = { GridItemSpan(2) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (selectedCategory == "Tất cả mục") "Sản phẩm đã lưu (12)" else selectedCategory,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .background(Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                    .size(36.dp)
                            ) {
                                Icon(Icons.Default.GridView, contentDescription = "Grid", tint = Color(0xFF1E3A8A), modifier = Modifier.size(20.dp))
                            }
                            IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.List, contentDescription = "List", tint = Color.Gray, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                // Dữ liệu Mockup cũ
                val products = listOf(
                    ProductItem("Minimalist Silver Watch", "Thiết bị âm thanh", "$120.00", true),
                    ProductItem("Pro Sound Headphones", "Thiết bị âm thanh", "$299.00", false),
                    ProductItem("Organic Vase Set", "Đồ dùng nhà bếp", "$85.00", false),
                    ProductItem("Matte Black Kettle", "Đồ dùng nhà bếp", "$145.00", false)
                )
                items(products) { product ->
                    ProductCard(product)
                }

                item(span = { GridItemSpan(2) }) { AIBanner() }
            }

            // Khoảng trống dưới cùng để cuộn không bị vướng Nav Bar
            item(span = { GridItemSpan(2) }) { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// ==========================================
// CÁC COMPONENT PHỤ TRỢ (COMPONENTS)
// ==========================================

@Composable
fun CartGridCard(
    item: CartItem,
    onIncrease: (CartItem) -> Unit,
    onDecrease: (CartItem) -> Unit
) {
    val product = item.products ?: return
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = product.imageUrls.firstOrNull(),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${product.price}đ", fontSize = 12.sp, color = Color.Red, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF3F4F6),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        IconButton(onClick = { onDecrease(item) }, modifier = Modifier.size(32.dp)) {
                            Icon(
                                if (item.quantity > 1) Icons.Default.Remove else Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (item.quantity > 1) Color.Black else Color.Red
                            )
                        }
                        Text(item.quantity.toString(), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        IconButton(onClick = { onIncrease(item) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategorySection(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Thư mục", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = { /* TODO */ }, contentPadding = PaddingValues(0.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                CategoryItem("Giỏ hàng", Icons.Default.ShoppingCart, isSelected = selectedCategory == "Giỏ hàng") { onCategorySelected("Giỏ hàng") }
            }
            item {
                CategoryItem("Tất cả mục", Icons.Default.GridView, isSelected = selectedCategory == "Tất cả mục") { onCategorySelected("Tất cả mục") }
            }
        }
    }
}

@Composable
fun CategoryItem(title: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(100.dp)) {
        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 70.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isSelected) Color(0xFF476282) else Color(0xFFE2E8F0))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = if (isSelected) Color.White else Color(0xFF476282), modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun ProductCard(product: ProductItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.8f)
                    .background(Color(0xFFF1F5F9))
            ) {
                Box(modifier = Modifier.fillMaxSize().background(Color.LightGray))

                if (product.isSmartChoice) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFD6E4FF))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("SMART CHOICE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(product.category, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(product.price, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E3A8A))
                    Icon(Icons.Outlined.ShoppingCart, contentDescription = "Add to cart", tint = Color(0xFF476282), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun AIBanner() {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0xFF476282)).padding(24.dp)
    ) {
        Column {
            Text("AI GỢI Ý", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD6E4FF), letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Hoàn thiện bộ sưu\ntập của bạn", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, lineHeight = 28.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Dựa trên phong cách của bạn, chúng tôi tìm thấy 3 món đồ hoàn hảo để kết hợp cùng những gì bạn đã lưu.", fontSize = 12.sp, color = Color(0xFFD6E4FF))
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD6E4FF)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Khám phá ngay", color = Color(0xFF1E3A8A), fontWeight = FontWeight.Bold)
            }
        }
    }
}

data class ProductItem(
    val name: String,
    val category: String,
    val price: String,
    val isSmartChoice: Boolean
)