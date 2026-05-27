package com.example.smartpick.core.ui.components.shopping

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.smartpick.R
import com.example.smartpick.core.ui.theme.TextMuted

@Composable
fun CategorySection(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.QuanLiMuaSam),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                CategoryItem(
                    stringResource(R.string.GioHang),
                    Icons.Default.ShoppingCart,
                    isSelected = selectedCategory == "Giỏ hàng",
                    onClick = { onCategorySelected("Giỏ hàng") })
            }
            item {
                CategoryItem(
                    stringResource(R.string.LichSuMuaHang),
                    Icons.Default.History,
                    isSelected = selectedCategory == "Lịch sử mua hàng",
                    onClick = { onCategorySelected("Lịch sử mua hàng") })
            }
            item {
                CategoryItem(
                    stringResource(R.string.BaiVietDaThich),
                    Icons.Default.Favorite,
                    isSelected = selectedCategory == "Bài viết đã thích",
                    onClick = { onCategorySelected("Bài viết đã thích") })
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
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                null,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            title,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}