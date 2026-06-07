package com.example.smartpick.features.profile.ui.saved

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartpick.core.model.Order
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.ReactionType
import com.example.smartpick.core.ui.components.post.PostItem
import com.example.smartpick.core.ui.theme.*
import com.example.smartpick.features.checkout.viewmodel.CheckoutViewModel
import com.example.smartpick.features.feed.viewmodel.FeedViewModel
import com.example.smartpick.navigation.Routes
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun SavedCollectionScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    initialCategory: String = "Bài viết đã thích",
    checkoutViewModel: CheckoutViewModel = hiltViewModel(),
    feedViewModel: FeedViewModel = hiltViewModel()
) {
    val orders by checkoutViewModel.orders.collectAsState()
    val reactedPosts by feedViewModel.reactedPosts.collectAsState()
    val isReactedLoading by feedViewModel.isReactedLoading.collectAsState()

    var selectedCategory by rememberSaveable { mutableStateOf(initialCategory) }

    LaunchedEffect(initialCategory) { selectedCategory = initialCategory }

    LaunchedEffect(Unit) {
        checkoutViewModel.loadOrderHistory()
        feedViewModel.loadReactedPosts()
    }

    LaunchedEffect(selectedCategory) {
        if (selectedCategory == "Bài viết đã thích") feedViewModel.loadReactedPosts()
        if (selectedCategory == "Lịch sử mua hàng") checkoutViewModel.loadOrderHistory()
    }

    SavedCollectionContent(
        paddingValues = paddingValues,
        selectedCategory = selectedCategory,
        orders = orders,
        reactedPosts = reactedPosts,
        isReactedLoading = isReactedLoading,
        onCategorySelected = { selectedCategory = it },
        onNavigateToPostDetail = { postId -> navController.navigate(Routes.PostDetail.createRoute(postId)) },
        onProductClick = { productId -> navController.navigate(Routes.ProductDetail.createRoute(productId)) },
        onToggleReaction = { id, reactionType -> feedViewModel.toggleReaction(id, reactionType) }
    )
}

@Composable
fun SavedCollectionContent(
    paddingValues: PaddingValues,
    selectedCategory: String,
    orders: List<Order>,
    reactedPosts: List<Triple<Post, User, Product?>>,
    isReactedLoading: Boolean,
    onCategorySelected: (String) -> Unit,
    onNavigateToPostDetail: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onToggleReaction: (String, ReactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("Bài viết đã thích", "Lịch sử mua hàng")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)) // Đổi nền sang màu xám nhẹ sang trọng hơn
            .padding(
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                top = paddingValues.calculateTopPadding()
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // TIÊU ĐỀ GIAO DIỆN MỚI
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalMall,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Quản lý mua sắm",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = Color(0xFF1A1A1A)
                )
            }

            // BƯỚC 4 KHẮC PHỤC: THANH TAB CAPSULE CUSTOM SIÊU ĐẸP
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE9ECEF))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    val backgroundColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        animationSpec = tween(durationMillis = 250),
                        label = "tabBg"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) Color.White else Color(0xFF495057),
                        animationSpec = tween(durationMillis = 250),
                        label = "tabText"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(backgroundColor)
                            .clickable { onCategorySelected(category) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (category == "Bài viết đã thích") Icons.Default.Favorite else Icons.Default.Assignment,
                                contentDescription = null,
                                tint = textColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = category,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = textColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // DANH SÁCH HIỂN THỊ NỘI DUNG
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                when (selectedCategory) {
                    "Bài viết đã thích" -> {
                        if (isReactedLoading) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 3.dp)
                                }
                            }
                        } else if (reactedPosts.isEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                                    Text("Chưa có bài viết nào được thích", color = Color(0xFF6C757D), fontWeight = FontWeight.Medium)
                                }
                            }
                        } else {
                            items(
                                items = reactedPosts,
                                span = { _ -> GridItemSpan(maxLineSpan) }
                            ) { triple ->
                                val (post, user, product) = triple
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    PostItem(
                                        post = post,
                                        user = user,
                                        product = product,
                                        onPostClick = { onNavigateToPostDetail(post.id.toString()) },
                                        onReactionClick = onToggleReaction,
                                        onProductClick = { productId -> onProductClick(productId.toString()) }
                                    )
                                }
                            }
                        }
                    }

                    "Lịch sử mua hàng" -> {
                        if (orders.isEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                                    Text("Chưa có đơn hàng nào trong lịch sử", color = Color(0xFF6C757D), fontWeight = FontWeight.Medium)
                                }
                            }
                        } else {
                            // BƯỚC 3 KHẮC PHỤC: RENDER GIAO DIỆN CARD ĐƠN HÀNG LỊCH SỬ SIÊU ĐẸP, CÓ ĐỒ HỌA TRỰC QUAN
                            items(
                                items = orders,
                                key = { it.id },
                                span = { _ -> GridItemSpan(maxLineSpan) }
                            ) { orderItem ->
                                CustomOrderCard(order = orderItem)
                            }
                        }
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

/**
 * COMPONENT CUSTOM ĐƯỢC THIẾT KẾ LẠI: Đẹp mắt, có hình họa gradient đại diện trực quan, đầy đủ thông tin chi tiết
 */
@Composable
fun CustomOrderCard(order: Order, modifier: Modifier = Modifier) {
    // Định dạng hiển thị thời gian mua hàng từ chuỗi ISO sang định dạng Việt Nam
    val formattedDate = remember(order.createdAt) {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val outputFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
            val date = inputFormat.parse(order.createdAt)
            if (date != null) outputFormat.format(date) else order.createdAt
        } catch (e: Exception) {
            order.createdAt.substringBefore("T")
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // KHẮC PHỤC BƯỚC 3: Thay thế vùng text trống bằng Khối Đồ hoạ Gradient Trực quan, sắc nét đại diện cho đơn hàng
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF4EA8DE), Color(0xFF5390D9))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // PHẦN THÔNG TIN CHI TIẾT ĐƠN HÀNG
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mã đơn: #${order.id.take(8).uppercase()}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212529)
                    )

                    // Nhãn trạng thái đổ màu nổi bật
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFD1E7DD),
                        contentColor = Color(0xFF0F5132)
                    ) {
                        Text(
                            text = order.status.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = "Thời gian: $formattedDate",
                    fontSize = 12.sp,
                    color = Color(0xFF6C757D)
                )

                Text(
                    text = "Thanh toán: ${order.paymentMethod ?: "COD"}",
                    fontSize = 12.sp,
                    color = Color(0xFF6C757D)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tổng tiền:",
                        fontSize = 13.sp,
                        color = Color(0xFF495057),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    val totalFormatted = String.format("%,.0f đ", order.totalAmount).replace(",", ".")
                    Text(
                        text = totalFormatted,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE63946) // Màu đỏ làm điểm nhấn nổi bật cho tổng chi phí
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Tab Bài Viết Đã Thích - Đã Tinh Gọn")
@Composable
fun SavedCollectionContentReactedPreview() {
    val mockPost = Post(
        id = "101",
        userId = "u2",
        content = "Gợi ý cho anh em con tai nghe chống ồn đỉnh cao này dùng cực thích luôn nhé!",
        mediaUrls = emptyList(),
        createdAt = "2026-05-27T12:00:00Z",
        reactionCount = 1,
        currentUserReaction = ReactionType.LIKE
    )
    val mockUser = User(id = "u2", fullName = "Nguyễn Xuân Dũng", avatarUrl = "")
    val mockProduct = Product(id = "p1", ownerId = "o1", name = "Tai nghe Sony WH-1000XM5", brand = "Sony", category = "Tech", price = 6500000.0, imageUrls = listOf(""), stock = 10, soldCount = 10)

    val mockReactedPosts = listOf(
        Triple(mockPost, mockUser, mockProduct as Product?)
    )

    SmartPickTheme {
        SavedCollectionContent(
            paddingValues = PaddingValues(),
            selectedCategory = "Bài viết đã thích",
            orders = emptyList(),
            reactedPosts = mockReactedPosts,
            isReactedLoading = false,
            onCategorySelected = {},
            onNavigateToPostDetail = {},
            onProductClick = {},
            onToggleReaction = { _, _ -> }
        )
    }
}