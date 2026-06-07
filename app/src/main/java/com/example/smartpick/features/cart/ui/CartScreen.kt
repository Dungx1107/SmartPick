package com.example.smartpick.features.cart.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.Product
import com.example.smartpick.features.cart.ui.components.CartBottomBar
import com.example.smartpick.features.cart.ui.components.CartItemRow

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    selectedIds: Set<String>,
    onToggleSelect: (String) -> Unit,
    onSelectAll: (Boolean) -> Unit,
    onIncrease: (CartItem) -> Unit,
    onDecrease: (CartItem) -> Unit,
    onRemove: (String) -> Unit,
    onBack: () -> Unit,
    onNavigateToPost: (String) -> Unit,
    onCheckout: (List<String>) -> Unit,
    onProductClick: (String) -> Unit
) {
    var isEditMode by rememberSaveable { mutableStateOf(false) }

    // Dùng Box làm Root Container để quản lý layer, gán fillMaxSize phủ kín toàn bộ màn hình điện thoại
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. THANH TIÊU ĐỀ TRÊN CÙNG (Top Bar) - Áp sát mép trên hệ thống qua statusBarsPadding
            CartTopBar(
                itemCount = cartItems.size,
                isEditMode = isEditMode,
                onEditModeChange = { isEditMode = it },
                onBack = onBack
            )

            // 2. DANH SÁCH MẶT HÀNG (LazyColumn) - Chiếm toàn bộ không gian ở giữa
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems, key = { it.id ?: "" }) { item ->
                    CartItemRow(
                        item = item,
                        isSelected = selectedIds.contains(item.id),
                        onToggleSelect = { item.id?.let { onToggleSelect(it) } },
                        onIncrease = { onIncrease(item) },
                        onDecrease = { onDecrease(item) },
                        onNavigateToPost = { item.originPostId?.let { onNavigateToPost(it) } },
                        onProductClick = onProductClick
                    )
                }
            }

            // 3. THANH ĐIỀU HƯỚNG DƯỚI CÙNG (Bottom Bar) - Áp sát mép đáy điện thoại qua navigationBarsPadding
            CartBottomBar(
                cartItems = cartItems,
                selectedIds = selectedIds,
                onSelectAll = onSelectAll,
                onCheckout = {
                    if (selectedIds.isNotEmpty()) {
                        onCheckout(selectedIds.toList())
                    }
                },
                isEditMode = isEditMode,
                onDeleteSelected = {
                    cartItems.filter { selectedIds.contains(it.id) }.forEach {
                        it.id?.let { id -> onRemove(id) }
                    }
                }
            )
        }
    }
}

@Composable
fun CartTopBar(
    itemCount: Int,
    isEditMode: Boolean,
    onEditModeChange: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(R.string.QuayLai),
                tint = Color(0xFF1A1A1A)
            )
        }

        Text(
            text = stringResource(R.string.GioHang) + " ($itemCount)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.weight(1f)
        )

        Text(
            text = if (isEditMode) stringResource(R.string.xong) else stringResource(R.string.ChinhSua),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A1A),
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .clickable { onEditModeChange(!isEditMode) }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CartScreenPreview() {
    val mockProducts = listOf(
        Product(
            id = "p1",
            ownerId = "o1",
            name = "Áo khoác Bomber nỉ dày cao cấp SmartPick mẫu mới 2026",
            price = 350000.0,
            imageUrls = emptyList(),
            ownerName = "Nguyễn Xuân Dũng"
        ),
        Product(
            id = "p2",
            ownerId = "o2",
            name = "Tai nghe không dây Bluetooth chống ồn chủ động ANC thế hệ 5",
            price = 1250000.0,
            imageUrls = emptyList(),
            ownerName = "Cửa Hàng Công Nghệ AI"
        )
    )

    // Giữ nguyên phần bọc mockCartItems và lệnh gọi hàm CartScreen phía dưới...
    val mockCartItems = listOf(
        CartItem(
            id = "c1",
            userId = "u1",
            productId = "p1",
            quantity = 2,
            originPostId = "post_abc",
            product = mockProducts[0]
        ),
        CartItem(
            id = "c2",
            userId = "u1",
            productId = "p2",
            quantity = 1,
            originPostId = "post_xyz",
            product = mockProducts[1]
        )
    )

    CartScreen(
        cartItems = mockCartItems,
        selectedIds = setOf("c1"),
        onToggleSelect = {},
        onSelectAll = {},
        onIncrease = {},
        onDecrease = {},
        onRemove = {},
        onBack = {},
        onNavigateToPost = {},
        onCheckout = {},
        onProductClick = {}
    )
}