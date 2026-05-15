package com.example.smartpick.features.notification.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.core.ui.theme.TextMuted

@Composable
fun NotificationFilterRow(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf(
        stringResource(R.string.TatCa),
        stringResource(R.string.ChuaDoc),
        stringResource(R.string.DonHang),
        stringResource(R.string.CongDong),
        stringResource(R.string.KhuyenMai),
        stringResource(R.string.HeThong)
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            val isSelected = filter == selectedFilter
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) else null,
                modifier = Modifier.clickable { onFilterSelected(filter) }
            ) {
                Text(
                    text = filter,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else TextMuted,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Notification Filter Row",
    showBackground = true
)
@Composable
fun NotificationFilterRowPreview() {
    SmartPickTheme {
        var selectedFilter by remember {
            mutableStateOf("Tất cả")
        }

        NotificationFilterRow(
            selectedFilter = selectedFilter,
            onFilterSelected = {
                selectedFilter = it
            }
        )
    }
}