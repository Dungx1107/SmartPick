package com.example.smartpick.features.cart.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartBottomSheet(
    cartItems: List<CartItem>,
    onIncrease: (CartItem) -> Unit,
    onDecrease: (CartItem) -> Unit,
    onDismiss: () -> Unit,
    onCheckout: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Giỏ hàng của bạn (${cartItems.sumOf { it.quantity }})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (cartItems.isEmpty()) {
                Text(
                    "Giỏ hàng trống",
                    color = TextMuted,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                    items(cartItems) { item ->
                        val product = item.product
                        if (product != null) {
                            ListItem(
                                colors = ListItemDefaults.colors(containerColor = White),
                                headlineContent = {
                                    Text(product.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                },
                                supportingContent = {
                                    val priceFormatted = String.format("%,.0f đ", product.price).replace(",", ".")
                                    Text("$priceFormatted", color = AccentBlue, fontWeight = FontWeight.Bold)
                                },
                                leadingContent = {
                                    AsyncImage(
                                        model = product.imageUrls.firstOrNull(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                },
                                trailingContent = {
                                    Surface(shape = RoundedCornerShape(20.dp), color = SurfaceCard) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            IconButton(onClick = { onDecrease(item) }, modifier = Modifier.size(30.dp)) {
                                                Icon(
                                                    imageVector = if (item.quantity > 1) Icons.Default.Remove else Icons.Default.Delete,
                                                    contentDescription = null,
                                                    tint = if (item.quantity > 1) TextSecondary else AccentBlue,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Text(
                                                text = item.quantity.toString(),
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            IconButton(onClick = { onIncrease(item) }, modifier = Modifier.size(30.dp)) {
                                                Icon(Icons.Default.Add, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = SurfaceCard)

                val total = cartItems.sumOf { (it.product?.price ?: 0.0) * it.quantity }
                val totalFormatted = String.format("%,.0f đ", total).replace(",", ".")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tổng cộng:", fontWeight = FontWeight.Bold, color = TextSecondary)
                    Text("$totalFormatted", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AccentBlue)
                }
            }

            Button(
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                enabled = cartItems.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = SmartPickColor)
            ) {
                Text("Thanh toán ngay", color = White)
            }
        }
    }
}