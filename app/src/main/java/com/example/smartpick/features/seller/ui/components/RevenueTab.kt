package com.example.smartpick.features.seller.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.features.seller.viewmodel.SellerStats

@Composable
fun RevenueTab(stats: SellerStats) {
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
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = Color(0xFF2EC4B6),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Tổng doanh thu tích lũy",
                        fontSize = 14.sp,
                        color = Color(0xFF118A7E),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = stats.totalRevenue.formatCurrency(),
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
            StatSummaryCard(
                title = "Tổng số đơn",
                value = stats.totalOrders.toString(),
                icon = Icons.Default.ShoppingCart,
                iconTint = Color(0xFFE71D36),
                modifier = Modifier.weight(1f)
            )

            StatSummaryCard(
                title = "Sản phẩm đã bán",
                value = stats.totalProductsSold.toString(),
                icon = Icons.Default.TrendingUp,
                iconTint = Color(0xFFFF9F1C),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatSummaryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontSize = 13.sp, color = Color.Gray)
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}