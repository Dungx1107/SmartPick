package com.example.smartpick.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.R
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import com.example.smartpick.core.theme.PageBg
import com.example.smartpick.core.theme.TextPrimary
import com.example.smartpick.core.theme.TextSecondary
import com.example.smartpick.features.auth.viewmodel.AuthViewModel
import com.example.smartpick.features.home.ui.components.AICuratorBanner
import com.example.smartpick.features.home.ui.components.HeroBanner
import com.example.smartpick.features.home.ui.components.ProductCard
import com.example.smartpick.features.home.ui.components.SearchBar

@Composable
fun HomeScreenRoute(
    authViewModel: AuthViewModel = hiltViewModel(),
//    homeViewModel: HomeViewModel = hiltViewModel(), // ViewModel quản lý sản phẩm
    products: List<Product> = emptyList()
) {
    val user by authViewModel.currentUser.collectAsState()
//    val products by homeViewModel.products.collectAsState() // Giả sử có StateFlow products

    user?.let { currentUser ->
        HomeScreen(
            user = currentUser,
            products = products
        )
    }
}

@Composable
fun HomeScreen(
    user: User,
    products: List<Product> = emptyList()
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Thanh tìm kiếm
        item {
            Spacer(Modifier.height(12.dp))
            SearchBar()
        }

        // 2. Banner chính
        item {
            Spacer(Modifier.height(16.dp))
            HeroBanner()
        }

        // 3. Tiêu đề mục gợi ý
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                Text(
                    stringResource(R.string.GoiYChoBan),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                user.fullName?.let {
                    Text(
                        stringResource(R.string.DuaTrenSoThich, it),
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // 4. Danh sách sản phẩm (Grid 2 cột)
        items(products.chunked(2)) { rowItems ->
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { product ->
                    ProductCard(product = product, modifier = Modifier.weight(1f))
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        // 5. Banner AI Curator
        item {
            Spacer(Modifier.height(24.dp))
            AICuratorBanner()
        }
    }
}


// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun HomeScreenPreview() {
    val mockProducts = listOf(
        Product(
            id = "1",
            name = "Tai nghe chống ồn WH-1000XM5",
            branch = "SONY",
            price = 8490000.0,
            category = "Audio",
            imageUrl = null
        ),
        Product(
            id = "2",
            name = "iPad Pro M4 11 inch (2024)",
            branch = "APPLE",
            price = 28990000.0,
            category = "Tablet",
            imageUrl = null
        ),
        Product(
            id = "3",
            name = "Đồng hồ thông minh Watch S3",
            branch = "XIAOMI",
            price = 3590000.0,
            category = "Wearable",
            imageUrl = null
        ),
        Product(
            id = "4",
            name = "Loa Bluetooth Aura Studio 4",
            branch = "HARMAN KARDON",
            price = 6990000.0,
            category = "Smart Home",
            imageUrl = null
        )
    )

    val sampleUser = User(
        id = "123",
        fullName = "Nguyễn Xuân Dũng",
        username = "dung_uet_2005",
        email = "dung.nx@vnu.edu.vn",
        avatarUrl = "https://example.com/avatar.jpg" // Có thể để null nếu chưa có ảnh mẫu
    )
    MaterialTheme {
        HomeScreen(
            user = sampleUser,
            products = mockProducts // Nạp User giả vào đây
        )

    }
}