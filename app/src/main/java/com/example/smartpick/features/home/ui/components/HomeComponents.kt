// File: app/src/main/java/com/example/smartpick/features/home/ui/components/HomeComponents.kt
package com.example.smartpick.features.home.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smartpick.R
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.ui.theme.*

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onMicClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(White)
            .border(1.dp, SurfaceCard, RoundedCornerShape(28.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, null, tint = TextMuted, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    stringResource(R.string.TimKiemSanPham),
                    color = TextMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                modifier = Modifier.fillMaxWidth()
            )
        }

        IconButton(
            onClick = onMicClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(Icons.Default.Mic, contentDescription = "Tìm kiếm bằng giọng nói", tint = TextSecondary)
        }
    }
}

@Composable
fun ProductGridCard(
    product: Product,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onProductClick(product) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = product.imageUrls.firstOrNull(),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Thêm thông tin "Đã bán"
                Text(
                    text = "Đã bán ${product.soldCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${product.price}đ",
                        color = SmartPickColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    IconButton(
                        onClick = { onAddToCart(product) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Add to Cart",
                            tint = AccentBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

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
                Text("Giỏ hàng trống", color = TextMuted, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                cartItems.forEach { item ->
                    val product = item.product
                    if (product != null) {
                        ListItem(
                            colors = ListItemDefaults.colors(containerColor = White),
                            headlineContent = { Text(product.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            supportingContent = { Text("${product.price}đ", color = ErrorRed, fontWeight = FontWeight.Bold) },
                            leadingContent = {
                                AsyncImage(
                                    model = product.imageUrls.firstOrNull(),
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            },
                            trailingContent = {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = SurfaceCard
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        IconButton(onClick = { onDecrease(item) }, modifier = Modifier.size(30.dp)) {
                                            Icon(
                                                imageVector = if (item.quantity > 1) Icons.Default.Remove else Icons.Default.Delete,
                                                contentDescription = null,
                                                tint = if (item.quantity > 1) TextSecondary else ErrorRed,
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
                                            Icon(Icons.Default.Add, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = SurfaceCard)

                val total = cartItems.sumOf { (it.product?.price ?: 0.0) * it.quantity }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tổng cộng:", fontWeight = FontWeight.Bold, color = TextSecondary)
                    Text("${total}đ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ErrorRed)
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

@Composable
fun ProductDetailContent(
    product: Product,
    onViewFeed: () -> Unit,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = product.imageUrls.firstOrNull(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = product.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
        Text(text = "${product.price}đ", style = MaterialTheme.typography.titleLarge, color = ErrorRed, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onViewFeed,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Xem bài đăng & Đánh giá trong Feed", color = SmartPickColor)
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onAddToCart,
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SmartPickColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Thêm Giỏ Hàng", color = White)
            }
            Button(
                onClick = onBuyNow,
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Mua Ngay", color = White)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}