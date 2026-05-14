// File: app/src/main/java/com/example/smartpick/features/home/ui/components/HomeComponents.kt
package com.example.smartpick.features.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smartpick.R
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.ui.theme.AccentBlue
import com.example.smartpick.core.ui.theme.TextMuted
import com.example.smartpick.core.ui.theme.TextSecondary
import com.example.smartpick.core.ui.theme.White
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Mic
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.Delete

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
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(28.dp))
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
                    fontSize = 14.sp
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
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

// ─── Hero Banner ──────────────────────────────────────────────────────────────
@Composable
fun HeroBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0D2137), Color(0xFF1E5F99), Color(0xFF0A3D6B))
                )
            )
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(140.dp)
                .offset(x = 200.dp, y = (-30).dp)
                .background(
                    Brush.radialGradient(listOf(Color(0x40009FFF), Color.Transparent)),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .offset(x = 240.dp, y = 60.dp)
                .background(
                    Brush.radialGradient(listOf(Color(0x3000C8FF), Color.Transparent)),
                    CircleShape
                )
        )

        // Text content
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp, end = 140.dp)
        ) {
            Text(
                stringResource(R.string.KyNguyenAmThanhAI),
                color = White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 28.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.TraiNghiemTinhTeAI),
                color = Color(0xCCFFFFFF),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(White)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    stringResource(R.string.KhamPhaNgay), color = AccentBlue,
                    fontSize = 12.sp, fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Right side placeholder for product image
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .size(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x33FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Headphones, contentDescription = null,
                tint = Color(0x99FFFFFF), modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun AICuratorBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFEBF3FF))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Tìm kiếm bằng AI\nCurator?",
                fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                color = AccentBlue, lineHeight = 26.sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Hãy để trí tuệ nhân tạo của Lumina giúp bạn tìm thấy sản phẩm hoàn hảo chỉ qua một cuộc trò chuyện ngắn.",
                fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp
            )
            Spacer(Modifier.height(16.dp))

            // Nút bấm bắt đầu ngay
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF1A4FA0), AccentBlue)))
                    .clickable { /* Xử lý mở Chat AI */ }
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, null, tint = White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Bắt đầu ngay", color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${product.price}đ",
                        color = MaterialTheme.colorScheme.primary,
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
                            tint = MaterialTheme.colorScheme.primary,
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
    cartItems: List<Product>,
    onRemoveItem: (Product) -> Unit, // Đã thêm
    onDismiss: () -> Unit,
    onCheckout: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Giỏ hàng của bạn (${cartItems.size})",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (cartItems.isEmpty()) {
                Text("Giỏ hàng trống", modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                cartItems.forEach { item ->
                    ListItem(
                        headlineContent = { Text(item.name) },
                        supportingContent = { Text("${item.price}đ") },
                        leadingContent = {
                            AsyncImage(
                                model = item.imageUrls.firstOrNull(),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                            )
                        },
                        trailingContent = {
                            IconButton(onClick = { onRemoveItem(item) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red)
                            }
                        }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                val total = cartItems.sumOf { it.price }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tổng cộng:", fontWeight = FontWeight.Bold)
                    Text("${total}đ", fontWeight = FontWeight.Bold, color = Color.Red)
                }
            }

            Button(
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                enabled = cartItems.isNotEmpty()
            ) {
                Text("Thanh toán ngay")
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

        Text(text = product.name, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = "${product.price}đ", fontSize = 20.sp, color = Color.Red, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onViewFeed,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Xem bài đăng & Đánh giá trong Feed")
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onAddToCart,
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Thêm Giỏ Hàng")
            }
            Button(
                onClick = onBuyNow,
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Mua Ngay")
            }
        }

        Spacer(modifier = Modifier.height(32.dp)) // Padding cho thanh điều hướng hệ thống
    }
}