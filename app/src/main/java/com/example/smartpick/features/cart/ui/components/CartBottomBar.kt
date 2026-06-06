package com.example.smartpick.features.cart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import kotlin.collections.contains


@Composable
fun CartBottomBar(
    cartItems: List<CartItem>,
    selectedIds: Set<String>,
    onSelectAll: (Boolean) -> Unit,
    onCheckout: () -> Unit,
    isEditMode: Boolean,
    onDeleteSelected: () -> Unit
) {
    val isAllSelected = cartItems.isNotEmpty() && selectedIds.size == cartItems.size
    val selectedItems = cartItems.filter { selectedIds.contains(it.id) }
    val totalPrice = selectedItems.sumOf { (it.product?.price ?: 0.0) * it.quantity }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isAllSelected,
                onCheckedChange = onSelectAll,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFE2C55))
            )
            Text("Tất cả", fontSize = 14.sp, color = Color(0xFF333333))
        }

        if (isEditMode) {
            Button(
                onClick = onDeleteSelected,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFE2C55)),
                shape = RoundedCornerShape(24.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
                enabled = selectedIds.isNotEmpty()
            ) {
                Text("Xóa (${selectedIds.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Row {
                        Text(stringResource(R.string.Tong), fontSize = 14.sp, color = Color(0xFF333333))
                        Text(
                            text = String.format("%,.0f đ", totalPrice),
                            color = Color(0xFFFE2C55),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Button(
                    onClick = onCheckout,
                    enabled = selectedIds.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFE2C55),
                        disabledContainerColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    Text(stringResource(R.string.ThanhToan), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartBottomBarPreview() {
    val cartItems = listOf(
        CartItem(
            id = "cart_1",
            userId = "user_1",
            productId = "product_1",
            quantity = 2,
            product = Product(
                id = "product_1",
                ownerId = "owner_1",
                name = "iPhone 15 Pro Max",
                price = 34990000.0
            )
        ),
        CartItem(
            id = "cart_2",
            userId = "user_1",
            productId = "product_2",
            quantity = 1,
            product = Product(
                id = "product_2",
                ownerId = "owner_2",
                name = "AirPods Pro 2",
                price = 5990000.0
            )
        )
    )

    CartBottomBar(
        cartItems = cartItems,
        selectedIds = setOf("cart_1", "cart_2"),
        onSelectAll = {},
        onCheckout = {},
        isEditMode = false,
        onDeleteSelected = {}
    )
}