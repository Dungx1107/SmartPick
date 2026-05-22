package com.example.smartpick.features.home.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.smartpick.core.model.ReviewResponse
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
            .clip(RoundedCornerShape(8.dp))
            .background(White)
            .border(1.dp, SurfaceCard, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, null, tint = TextMuted, modifier = Modifier.size(20.dp))
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
            Icon(
                Icons.Default.Mic,
                contentDescription = "Tìm kiếm bằng giọng nói",
                tint = TextSecondary
            )
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
            .clickable { onProductClick(product) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                AsyncImage(
                    model = product.imageUrls.firstOrNull(),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = { onAddToCart(product) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(30.dp)
                        .background(White.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = "Add to Cart",
                        tint = AccentBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                    modifier = Modifier.height(36.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val priceFormatted = String.format("%,.0f", product.price).replace(",", ".")
                    Text(
                        text = "₫$priceFormatted",
                        color = SmartPickColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )

                    Text(
                        text = "Đã bán ${if(product.soldCount > 1000) "${product.soldCount/1000}k" else product.soldCount}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartBottomSheet(
    cartItems: List<CartItem>,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
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
                cartItems.forEach { item ->
                    val product = item.product
                    if (product != null) {
                        ListItem(
                            colors = ListItemDefaults.colors(containerColor = White),
                            headlineContent = {
                                Text(
                                    product.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            supportingContent = {
                                val priceFormatted = String.format("%,.0f", product.price).replace(",", ".")
                                Text(
                                    "₫$priceFormatted",
                                    color = ErrorRed,
                                    fontWeight = FontWeight.Bold
                                )
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
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = SurfaceCard
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        IconButton(onClick = { item.id?.let { onDecrease(it) } }, modifier = Modifier.size(30.dp)) {
                                            Icon(if (item.quantity > 1) Icons.Default.Remove else Icons.Default.Delete, null, tint = if (item.quantity > 1) TextSecondary else ErrorRed, modifier = Modifier.size(16.dp))
                                        }
                                        Text(text = item.quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        IconButton(onClick = { item.id?.let { onIncrease(it) } }, modifier = Modifier.size(30.dp)) {
                                            Icon(Icons.Default.Add, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = SurfaceCard)

                val total = cartItems.sumOf { (it.product?.price ?: 0.0) * it.quantity }
                val totalFormatted = String.format("%,.0f", total).replace(",", ".")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tổng cộng:", fontWeight = FontWeight.Bold, color = TextSecondary)
                    Text("₫$totalFormatted", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ErrorRed)
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

// FIX: GHIM NÚT MUA NGAY XUỐNG ĐÁY MÀN HÌNH CHI TIẾT SẢN PHẨM
@Composable
fun ProductDetailContent(
    product: Product,
    reviews: List<ReviewResponse>,
    canReview: Boolean,
    onViewFeed: () -> Unit,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit,
    onSubmitReview: (Int, String) -> Unit
) {
    var reviewRating by rememberSaveable { mutableIntStateOf(5) }
    var reviewContent by rememberSaveable { mutableStateOf("") }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            // Thanh BottomBar ghim ở đáy chứa 2 nút Mua Ngay và Thêm Giỏ Hàng
            Surface(shadowElevation = 16.dp, color = White) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Chừa chỗ cho bottomBar
                .padding(horizontal = 16.dp)
        ) {
            item {
                Column {
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

                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val priceFormatted = String.format("%,.0f", product.price).replace(",", ".")
                        Text(
                            text = "₫$priceFormatted",
                            style = MaterialTheme.typography.titleLarge,
                            color = ErrorRed,
                            fontWeight = FontWeight.Bold
                        )

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Kho: ${product.stock}",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextMuted
                            )
                            Text(
                                text = "Đã bán: ${product.soldCount}",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedButton(
                        onClick = onViewFeed,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Xem bài đăng thảo luận trong Feed", color = SmartPickColor)
                    }

                    // Đã xóa 2 nút Add/Buy ở đây để chuyển xuống đáy (BottomBar)

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 24.dp),
                        color = SurfaceCard
                    )
                }
            }

            if (canReview) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Viết đánh giá của bạn",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row {
                            repeat(5) { index ->
                                IconButton(
                                    onClick = { reviewRating = index + 1 },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (index < reviewRating) Color(0xFFFFC107) else TextMuted
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = reviewContent,
                            onValueChange = { reviewContent = it },
                            placeholder = { Text("Cảm nhận của bạn về sản phẩm...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                if (reviewContent.isNotBlank()) {
                                    onSubmitReview(reviewRating, reviewContent)
                                    reviewContent = ""
                                }
                            },
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(containerColor = SmartPickColor)
                        ) {
                            Text("Gửi đánh giá", color = White)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            item {
                Text(
                    text = "Đánh giá từ khách hàng (${reviews.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (reviews.isEmpty()) {
                item {
                    Text(
                        text = "Chưa có đánh giá nào cho sản phẩm này.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            } else {
                items(reviews) { review ->
                    ReviewCard(review)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ReviewCard(review: ReviewResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, SurfaceCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = review.user?.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SurfaceCard),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.user?.fullName ?: "Người dùng SmartPick",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = review.createdAt.split("T").firstOrNull() ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (index < review.rating) Color(0xFFFFC107) else TextMuted
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = review.content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}