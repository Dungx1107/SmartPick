package com.example.smartpick.features.seller.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.smartpick.core.model.Product
import com.example.smartpick.features.seller.data.SellerRepository
import com.example.smartpick.features.seller.ui.components.ActiveProductsTab
import com.example.smartpick.features.seller.ui.components.DashboardTabs
import com.example.smartpick.features.seller.ui.components.RevenueTab
import com.example.smartpick.features.seller.ui.components.SoldOrdersTab
import com.example.smartpick.features.seller.viewmodel.SellerStats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardContent(
    isLoading: Boolean,
    products: List<Product>,
    orders: List<SellerRepository.SoldOrderItemDto>,
    stats: SellerStats,
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    initialTabIndex: Int = 0
) {
    var selectedTabIndex by remember { mutableIntStateOf(initialTabIndex) }
    val orderCountLabel = remember(orders) { "Đơn hàng đã bán (${orders.size})" }

    Scaffold(
        modifier = modifier,
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
                DashboardTabs(
                    selectedTabIndex = selectedTabIndex,
                    orderCountLabel = orderCountLabel,
                    onTabSelected = { selectedTabIndex = it }
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedTabIndex) {
                        0 -> ActiveProductsTab(
                            products = products,
                            onProductClick = onProductClick
                        )
                        1 -> SoldOrdersTab(
                            orders = orders,
                            onProductClick = onProductClick
                        )
                        2 -> RevenueTab(stats = stats)
                    }
                }
            }
        }
    }
}
