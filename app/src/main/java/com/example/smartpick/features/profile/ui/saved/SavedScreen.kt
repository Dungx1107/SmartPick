package com.example.smartpick.features.profile.ui.saved

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.Order
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.User
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.ReactionType
import com.example.smartpick.core.ui.components.post.PostItem
import com.example.smartpick.core.ui.components.shopping.CartGridCard
import com.example.smartpick.core.ui.components.shopping.CategorySection
import com.example.smartpick.core.ui.components.shopping.OrderCard
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

    SavedCollectionContent(
        paddingValues = paddingValues,
        selectedCategory = selectedCategory,
        cartItems = cartItems,
        orders = orders,
        reactedPosts = reactedPosts,
        isReactedLoading = isReactedLoading,
        onCategorySelected = { selectedCategory = it },
        onIncreaseQuantity = { cartViewModel.increaseQuantity(it) },
        onDecreaseQuantity = { cartViewModel.decreaseQuantity(it) },
        onNavigateToCheckout = { navController.navigate(Routes.Checkout.route) },
        onNavigateToPostDetail = { postId -> navController.navigate(Routes.PostDetail.createRoute(postId)) },
        onToggleReaction = { id, reactionType -> feedViewModel.toggleReaction(id, reactionType) } // Khớp chuẩn (String, ReactionType) -> Unit
    )
}

@Composable
fun SavedCollectionContent(
    paddingValues: PaddingValues,
    selectedCategory: String,
    cartItems: List<CartItem>,
    orders: List<Any>,
    reactedPosts: List<Triple<Post, User, Product?>>,
    isReactedLoading: Boolean,
    onCategorySelected: (String) -> Unit,
    onIncreaseQuantity: (CartItem) -> Unit,
    onDecreaseQuantity: (CartItem) -> Unit,
    onNavigateToCheckout: () -> Unit,
    onNavigateToPostDetail: (String) -> Unit,
    onToggleReaction: (String, ReactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                top = paddingValues.calculateTopPadding()
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding() + 16.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    CategorySection(selectedCategory = selectedCategory, onCategorySelected = onCategorySelected)
                }

                when (selectedCategory) {
                    "Giỏ hàng" -> {
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
                            items(items = cartItems, key = { it.id ?: "" }) { item ->
                                CartGridCard(
                                    item = item,
                                    onIncrease = onIncreaseQuantity,
                                    onDecrease = onDecreaseQuantity
                                )
                            }
                        }
                    }
                    "Lịch sử mua hàng" -> {
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
                            items(
                                items = orders,
                                span = { _ -> GridItemSpan(maxLineSpan) }
                            ) { item ->
                                if (item is Order) {
                                    OrderCard(order = item)
                                }
                            }
                        }
                    }
                    "Bài viết đã thích" -> {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text("Bài viết bạn đã thích", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                        }

                        if (isReactedLoading) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 3.dp)
                                }
                            }
                        } else if (reactedPosts.isEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                    Text("Chưa có bài viết nào được lưu", color = TextMuted)
                                }
                            }
                        } else {
                            items(
                                items = reactedPosts,
                                span = { _ -> GridItemSpan(maxLineSpan) }
                            ) { triple ->
                                val (post, user, product) = triple
                                PostItem(
                                    post = post,
                                    user = user,
                                    product = product,
                                    onPostClick = { onNavigateToPostDetail(post.id.toString()) },                                    onReactionClick = onToggleReaction
                                )
                            }
                        }
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(8.dp)) }
            }

            if (selectedCategory == "Giỏ hàng" && cartItems.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = paddingValues.calculateBottomPadding()),
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
                            onClick = onNavigateToCheckout,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            val totalCount = cartItems.sumOf { it.quantity }
                            Text("Thanh toán ($totalCount)", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Tab Bài Viết Đã Thích")
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
        Triple(mockPost, mockUser, mockProduct)
    )

    SmartPickTheme {
        SavedCollectionContent(
            paddingValues = PaddingValues(),
            selectedCategory = "Bài viết đã thích",
            cartItems = emptyList(),
            orders = emptyList(),
            reactedPosts = mockReactedPosts,
            isReactedLoading = false,
            onCategorySelected = {},
            onIncreaseQuantity = {},
            onDecreaseQuantity = {},
            onNavigateToCheckout = {},
            onNavigateToPostDetail = {},
            onToggleReaction = { _, _ -> }
        )
    }
}