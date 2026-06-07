package com.example.smartpick.features.seller.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardTabs(
    selectedTabIndex: Int,
    orderCountLabel: String,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Sản phẩm đang bán", orderCountLabel, "Tổng doanh thu")

    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier.fillMaxWidth()
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) }
            ) {
                Text(
                    text = title,
                    modifier = Modifier.padding(16.dp),
                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun EmptyStateView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(top = 40.dp),
            color = Color.Gray
        )
    }
}

// Extension function hỗ trợ format tiền tệ nhanh cho toàn bộ feature seller
fun Double.formatCurrency(): String {
    return String.format("%,.0f đ", this).replace(",", ".")
}