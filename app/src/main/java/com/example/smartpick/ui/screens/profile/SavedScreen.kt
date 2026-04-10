package com.example.smartpick.ui.screens.profile

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.ui.theme.PageBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedCollectionScreen() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Categories
        item(span = { GridItemSpan(2) }) {
            CategorySection()
        }

        // Section: Grid Header
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sản phẩm đã lưu (12)", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .background(
                                Color(0xFFE2E8F0),
                                RoundedCornerShape(8.dp)
                            )
                            .size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.GridView,
                            contentDescription = "Grid",
                            tint = Color(0xFF1E3A8A),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.List,
                            contentDescription = "List",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Section: Products List
        val products = listOf(
            ProductItem("Minimalist Silver Watch", "Thiết bị âm thanh", "$120.00", true),
            ProductItem("Pro Sound Headphones", "Thiết bị âm thanh", "$299.00", false),
            ProductItem("Organic Vase Set", "Đồ dùng nhà bếp", "$85.00", false),
            ProductItem("Matte Black Kettle", "Đồ dùng nhà bếp", "$145.00", false)
        )
        items(products) { product ->
            ProductCard(product)
        }

        // Section: AI Banner
        item(span = { GridItemSpan(2) }) {
            AIBanner()
        }

        // Padding bottom for scrolling past nav bar
        item(span = { GridItemSpan(2) }) {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CategorySection() {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Thư mục", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = { /* TODO */ }, contentPadding = PaddingValues(0.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tạo mới")
            }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            item { CategoryItem("Tất cả mục", Icons.Default.GridView, true) }
            item { CategoryItem("Đồ dùng nhà bếp", Icons.Default.Restaurant, false) }
            item { CategoryItem("Thiết bị âm thanh", Icons.Default.Headphones, false) }
        }
    }
}

@Composable
fun CategoryItem(title: String, icon: ImageVector, isSelected: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 70.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isSelected) Color(0xFF476282) else Color(0xFFE2E8F0))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = if (isSelected) Color.White else Color(0xFF476282),
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
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
                // TODO: Replace with Coil AsyncImage in real project
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray)
                )

                if (product.isSmartChoice) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFD6E4FF))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "SMART CHOICE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(product.category, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        product.price,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1E3A8A)
                    )
                    Icon(
                        Icons.Outlined.ShoppingCart,
                        contentDescription = "Add to cart",
                        tint = Color(0xFF476282),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AIBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF476282))
            .padding(24.dp)
    ) {
        Column {
            Text(
                "AI GỢI Ý",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD6E4FF),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Hoàn thiện bộ sưu\ntập của bạn",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                lineHeight = 28.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Dựa trên phong cách của bạn, chúng tôi tìm thấy 3 món đồ hoàn hảo để kết hợp cùng những gì bạn đã lưu.",
                fontSize = 12.sp,
                color = Color(0xFFD6E4FF)
            )
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

@Preview
@Composable
fun SavedCollectionScreenPreview() {
    SavedCollectionScreen()
}
