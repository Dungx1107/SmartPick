package com.example.smartpick.features.profile.ui.saved

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.Order
import com.example.smartpick.core.ui.components.PostItem
import com.example.smartpick.core.ui.theme.*
import com.example.smartpick.features.cart.viewmodel.CartViewModel
import com.example.smartpick.features.checkout.viewmodel.CheckoutViewModel
import com.example.smartpick.features.feed.viewmodel.FeedViewModel
import com.example.smartpick.navigation.Routes

@Composable
fun SavedCollectionScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    initialCategory: String = "Giỏ hàng",
    cartViewModel: CartViewModel = hiltViewModel(),
    checkoutViewModel: CheckoutViewModel = hiltViewModel(),
    feedViewModel: FeedViewModel = hiltViewModel()
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val orders by checkoutViewModel.cartItems.collectAsState()
    val reactedPosts by feedViewModel.reactedPosts.collectAsState()
    val isReactedLoading by feedViewModel.isReactedLoading.collectAsState()

    var selectedCategory by rememberSaveable { mutableStateOf(initialCategory) }

    LaunchedEffect(initialCategory) { selectedCategory = initialCategory }

    LaunchedEffect(selectedCategory) {
        if (selectedCategory == "Bài viết đã thích") feedViewModel.loadReactedPosts()
        if (selectedCategory == "Giỏ hàng") cartViewModel.refreshCart()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (selectedCategory == "Giỏ hàng" && cartItems.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 16.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val total = cartItems.sumOf { (it.product?.price ?: 0.0) * it.quantity }
                        Column {
                            Text("Tổng cộng", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                            val totalFormatted = String.format("%,.0f đ", total).replace(",", ".")
                            Text(totalFormatted, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = AccentBlue)
                        }
                        Button(
                            onClick = { navController.navigate(Routes.Checkout.route) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SmartPickColor)
                        ) {
                            val totalCount = cartItems.sumOf { it.quantity }
                            Text("Thanh toán ($totalCount)", color = White)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // FIX: Cú pháp chuẩn hóa span cho cấu trúc đơn phần tử 'item'
            item(span = { GridItemSpan(maxLineSpan) }) {
                CategorySection(selectedCategory = selectedCategory, onCategorySelected = { selectedCategory = it })
            }

            if (selectedCategory == "Giỏ hàng") {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text("Giỏ hàng của bạn", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                }

                if (cartItems.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text("Giỏ hàng trống", color = TextMuted)
                        }
                    }
                } else {
                    // Đối với lưới thường (2 cột), hiển thị Card dạng lưới không cần truyền thuộc tính span full-width
                    items(items = cartItems, key = { it.id ?: "" }) { item ->
                        CartGridCard(
                            item = item,
                            onIncrease = { cartViewModel.increaseQuantity(it) },
                            onDecrease = { cartViewModel.decreaseQuantity(it) }
                        )
                    }
                }
            } else if (selectedCategory == "Lịch sử mua hàng") {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text("Lịch sử mua hàng", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                }

                if (orders.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text("Chưa có đơn hàng nào", color = TextMuted)
                        }
                    }
                } else {
                    // FIX: Cú pháp Lambda nhận 2 tham số chuẩn signature: LazyGridItemSpanScope.(T) -> GridItemSpan
                    items(
                        items = orders,
                        span = { _ -> GridItemSpan(maxLineSpan) }
                    ) { item ->
                        if (item is Order) {
                            OrderCard(order = item)
                        }
                    }
                }
            } else if (selectedCategory == "Bài viết đã thích") {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text("Bài viết bạn đã thích", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                }

                if (isReactedLoading) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = SmartPickColor, strokeWidth = 3.dp)
                        }
                    }
                } else if (reactedPosts.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text("Chưa có bài viết nào được lưu", color = TextMuted)
                        }
                    }
                } else {
                    // FIX: Cú pháp Lambda nhận 2 tham số giúp compiler suy luận chính xác kiểu để destructuring
                    items(
                        items = reactedPosts,
                        span = { _ -> GridItemSpan(maxLineSpan) }
                    ) { triple ->
                        val (post, user, product) = triple
                        PostItem(
                            post = post,
                            user = user,
                            product = product,
                            onPostClick = { navController.navigate(Routes.PostDetail.createRoute(post.id.toString())) },
                            onReactionClick = { id, reactionType -> feedViewModel.toggleReaction(id, reactionType) }
                        )
                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Đơn hàng: #${order.id.take(8).uppercase()}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(12.dp)) {
                    Text(
                        text = "Thành công",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val date = order.createdAt.split("T").firstOrNull() ?: order.createdAt
                Text(text = date, style = MaterialTheme.typography.bodySmall, color = TextMuted)

                Text(
                    text = String.format("%,.0f đ", order.totalAmount).replace(",", "."),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = AccentBlue
                )
            }
        }
    }
}

@Composable
fun CartGridCard(item: CartItem, onIncrease: (CartItem) -> Unit, onDecrease: (CartItem) -> Unit) {
    val product = item.product ?: return
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = product.imageUrls.firstOrNull(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(product.name, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)

                Text(
                    text = String.format("%,.0f đ", product.price).replace(",", "."),
                    color = AccentBlue,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        IconButton(onClick = { onDecrease(item) }, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = if (item.quantity > 1) Icons.Default.Remove else Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (item.quantity > 1) MaterialTheme.colorScheme.onSurfaceVariant else AccentBlue
                            )
                        }
                        Text(item.quantity.toString(), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        IconButton(onClick = { onIncrease(item) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategorySection(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Quản lý mua sắm", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            item { CategoryItem("Giỏ hàng", Icons.Default.ShoppingCart, isSelected = selectedCategory == "Giỏ hàng", onClick = { onCategorySelected("Giỏ hàng") }) }
            item { CategoryItem("Lịch sử mua hàng", Icons.Default.History, isSelected = selectedCategory == "Lịch sử mua hàng", onClick = { onCategorySelected("Lịch sử mua hàng") }) }
            item { CategoryItem("Bài viết đã thích", Icons.Default.Favorite, isSelected = selectedCategory == "Bài viết đã thích", onClick = { onCategorySelected("Bài viết đã thích") }) }
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
            Icon(icon, null, tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, style = MaterialTheme.typography.labelMedium, color = if (isSelected) MaterialTheme.colorScheme.primary else TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}