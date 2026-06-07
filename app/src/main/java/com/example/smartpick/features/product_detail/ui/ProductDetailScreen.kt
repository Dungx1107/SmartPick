package com.example.smartpick.features.product_detail.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.ui.theme.*
import com.example.smartpick.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.smartpick.features.product_detail.viewmodel.ProductDetailViewModel
import com.example.smartpick.features.review.ui.components.ReviewCard
import com.example.smartpick.features.review.viewmodel.ReviewViewModel
import com.example.smartpick.navigation.Routes
import com.example.smartpick.core.model.Review
import com.example.smartpick.core.model.ReviewUser
import com.example.smartpick.features.auth.viewmodel.AuthViewModel

@Composable
fun ProductDetailScreen(
    productId: String,
    navController: NavController,
    viewModel: ProductDetailViewModel = hiltViewModel(),
    reviewViewModel: ReviewViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(productId) {
        viewModel.loadProductDetail(productId)
        reviewViewModel.loadReviewData(productId)
    }

    val postId by viewModel.postId.collectAsState()
    val reviews by reviewViewModel.productReviews.collectAsState()
    val canReview by reviewViewModel.canReview.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            uiState.error != null -> {
                Text(
                    text = uiState.error ?: "Lỗi tải thông tin sản phẩm",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.product != null -> {
                val currentProduct = uiState.product!!
                val currentUserId = currentUser?.id ?: ""

                ProductDetailContent(
                    product = currentProduct,
                    postId = postId,
                    reviews = reviews,
                    canReview = canReview,
                    isProductAvailable = viewModel.isProductAvailable(currentProduct)
                            && currentProduct.ownerId != currentUserId,
                    onBackClick = { navController.popBackStack() },
                    onViewFeed = { id -> navController.navigate(Routes.PostDetail.createRoute(id)) },
                    onAddToCart = {
                        viewModel.addToCart(
                            product = currentProduct,
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.DaThemVaoGioHang),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onError = { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onBuyNow = { selectedQuantity ->
                        if (currentProduct.id != null) {
                            navController.navigate(
                                Routes.Checkout.createRoute(
                                    productId = currentProduct.id,
                                    quantity = selectedQuantity
                                )
                            )
                        }
                    },
                    // ĐỒNG BỘ UX: Khi người dùng đủ điều kiện đánh giá ấn nút, điều hướng họ sang ReviewHub quản lý
                    onNavigateToReviewHub = {
                        navController.navigate(Routes.ReviewHub.route)
                    }
                )
            }
        }
    }
}

@Composable
fun ProductDetailContent(
    product: Product,
    postId: String?,
    reviews: List<Review>,
    canReview: Boolean,
    isProductAvailable: Boolean,
    onBackClick: () -> Unit,
    onViewFeed: (String) -> Unit,
    onAddToCart: () -> Unit,
    onBuyNow: (Int) -> Unit,
    onNavigateToReviewHub: () -> Unit // THAY THẾ: Nhận lệnh điều hướng thay cho lambda submit review cũ
) {
    val context = LocalContext.current
    var quantity by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.ime)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // 1. Hình ảnh sản phẩm kèm nút Back
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledIconButton(
                            onClick = onBackClick,
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Quay lại"
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Chi tiết sản phẩm",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AsyncImage(
                        model = product.imageUrls.firstOrNull(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .padding(horizontal = 12.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // 2. Thông tin tên và giá
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
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
                        val priceFormatted =
                            String.format("%,.0f đ", product.price).replace(",", ".")
                        Text(
                            text = priceFormatted,
                            style = MaterialTheme.typography.titleLarge,
                            color = AccentBlue,
                            fontWeight = FontWeight.Bold
                        )

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "Kho: ${product.stock}",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextMuted
                            )
                            Text(
                                "Đã bán: ${product.soldCount}",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextMuted
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // UI CHỌN SỐ LƯỢNG MUA NGAY
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Số lượng mua:",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        TextButton(
                            onClick = { if (quantity > 1) quantity-- },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.width(40.dp)
                        ) {
                            Text(
                                "-",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = quantity.toString(),
                            modifier = Modifier.padding(horizontal = 12.dp),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        TextButton(
                            onClick = { if (quantity < product.stock) quantity++ },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.width(40.dp)
                        ) {
                            Text(
                                "+",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 3. Nút xem bài đăng Feed
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedButton(
                        onClick = {
                            if (postId != null) onViewFeed(postId)
                            else Toast.makeText(
                                context,
                                context.getString(R.string.ChuaCoBaiDangThaoLuan),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            stringResource(R.string.XemBaiDangTrongFeed),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 24.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            // 4. Nhắc nhở viết đánh giá chuẩn UX (Thay thế cho ô nhập liệu ReviewInputForm cũ)
            if (canReview) {
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Button(
                            onClick = onNavigateToReviewHub,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Bạn có đơn hàng chưa đánh giá. Viết nhận xét ngay!",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // 5. Danh sách đánh giá từ khách hàng
            item {
                Text(
                    text = "Đánh giá từ khách hàng (${reviews.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (reviews.isEmpty()) {
                item {
                    Text(
                        text = "Chưa có đánh giá nào cho sản phẩm này.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                items(reviews) { review ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        ReviewCard(review = review, onProductClick = {})
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 16.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding()
            ) {
                if (product.stock > 0 && !isProductAvailable) {
                    Text(
                        text = "Sản phẩm do bạn đăng bán. Không thể thực hiện mua hàng.",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        enabled = isProductAvailable
                    ) {
                        Text(
                            stringResource(R.string.ThemGioHang),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Button(
                        onClick = { onBuyNow(quantity) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(12.dp),
                        enabled = isProductAvailable
                    ) {
                        Text(
                            stringResource(R.string.mua_ngay),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Chi Tiết Sản Phẩm - Cấu Trúc Đáy Chuẩn"
)
@Composable
fun ProductDetailContentPreview() {
    val mockProduct = Product(
        id = "prod_sample_01",
        ownerId = "owner_01",
        name = "Bàn phím cơ Custom Keychron K2 V2 Nhôm Hot-swappable Chính Hãng",
        brand = "Keychron",
        category = "Phụ kiện",
        price = 1850000.0,
        imageUrls = listOf("https://via.placeholder.com/300"),
        stock = 15,
        soldCount = 142
    )

    val mockReviews = listOf(
        Review(
            id = "rev_1",
            userId = "user_01",
            productId = "prod_sample_01",
            rating = 5,
            content = "Bàn phím gõ rất mượt, kết nối Bluetooth ổn định, giao hàng nhanh chóng.",
            createdAt = "2026-06-06T12:00:00Z",
            user = ReviewUser(id = "user_01", fullName = "Nguyễn Xuân Dũng", avatarUrl = null)
        )
    )

    SmartPickTheme {
        ProductDetailContent(
            product = mockProduct,
            postId = "feed_post_123",
            reviews = mockReviews,
            canReview = true,
            isProductAvailable = true,
            onBackClick = {},
            onViewFeed = {},
            onAddToCart = {},
            onBuyNow = {},
            onNavigateToReviewHub = {}
        )
    }
}