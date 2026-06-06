// FILE: com/example/smartpick/features/product_detail/ui/ProductDetailContent.kt
package com.example.smartpick.features.product_detail.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.ui.theme.*
import com.example.smartpick.R
import androidx.compose.ui.res.stringResource
import com.example.smartpick.features.product_detail.viewmodel.ProductDetailViewModel
import com.example.smartpick.features.review.ui.components.ReviewCard
import com.example.smartpick.features.review.ui.components.ReviewInputForm
import com.example.smartpick.features.review.viewmodel.ReviewViewModel
import com.example.smartpick.navigation.Routes

/**
 * THÊM MỚI: Hàm bọc đóng vai trò Màn hình độc lập (Screen) để AppNavigation gọi đến.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    navController: NavController,
    viewModel: ProductDetailViewModel = hiltViewModel(),
    reviewViewModel: ReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState() // Trạng thái nạp dữ liệu sản phẩm từ ViewModel
    val context = LocalContext.current

    // Tự động tải thông tin sản phẩm và đánh giá tương ứng từ Supabase theo productId nhận từ Route
    LaunchedEffect(productId) {
        viewModel.loadProductDetail(productId) // Sử dụng hàm nạp chi tiết trong ViewModel của bạn
        reviewViewModel.loadReviewData(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ChiTietSanPham), fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                    // Gọi Layout nội dung gốc khi dữ liệu Product đã sẵn sàng nạp về
                    ProductDetailContent(
                        product = uiState.product!!,
                        onViewFeed = { postId -> navController.navigate(Routes.PostDetail.createRoute(postId)) },
                        onAddToCart = {
                            viewModel.addToCart(
                                product = uiState.product!!,
                                onSuccess = { Toast.makeText(context, context.getString(R.string.DaThemVaoGioHang), Toast.LENGTH_SHORT).show() },
                                onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
                            )
                        },
                        onBuyNow = {
                            // Luồng mua ngay: Lưu sản phẩm vào DB và chuyển hướng thẳng sang màn hình Thanh toán riêng biệt
                            viewModel.addToCart(
                                product = uiState.product!!,
                                onSuccess = { navController.navigate(Routes.Checkout.route) },
                                onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
                            )
                        },
                        viewModel = viewModel,
                        reviewViewModel = reviewViewModel
                    )
                }
            }
        }
    }
}

/**
 * COMPOSABLE GỐC: Giữ nguyên cấu trúc hiển thị thông tin sản phẩm và danh sách Review của bạn.
 */
@Composable
fun ProductDetailContent(
    product: Product,
    onViewFeed: (String) -> Unit,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel(),
    reviewViewModel: ReviewViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val postId by viewModel.postId.collectAsState()

    // Review State
    val reviews by reviewViewModel.productReviews.collectAsState()
    val canReview by reviewViewModel.canReview.collectAsState()
    val isSubmitting by reviewViewModel.isSubmitting.collectAsState()

    // Khởi tạo dữ liệu
    LaunchedEffect(product.id) {
        product.id?.let {
            viewModel.fetchPostId(it)
            reviewViewModel.loadReviewData(it)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        bottomBar = {
            Surface(shadowElevation = 16.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAddToCart,
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        enabled = viewModel.isProductAvailable(product)
                    ) {
                        Text(stringResource(R.string.ThemGioHang), color = MaterialTheme.colorScheme.onPrimary)
                    }
                    Button(
                        onClick = onBuyNow,
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(12.dp),
                        enabled = viewModel.isProductAvailable(product)
                    ) {
                        Text(stringResource(R.string.mua_ngay), color = MaterialTheme.colorScheme.onSecondary)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // 1. Hình ảnh sản phẩm
            item {
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
            }

            // 2. Thông tin tên và giá
            item {
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
                    val priceFormatted = String.format("%,.0f đ", product.price).replace(",", ".")
                    Text(
                        text = priceFormatted,
                        style = MaterialTheme.typography.titleLarge,
                        color = AccentBlue,
                        fontWeight = FontWeight.Bold
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Kho: ${product.stock}", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                        Text("Đã bán: ${product.soldCount}", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 3. Nút xem bài đăng Feed
            item {
                OutlinedButton(
                    onClick = {
                        if (postId != null) onViewFeed(postId!!)
                        else Toast.makeText(context, context.getString(R.string.ChuaCoBaiDangThaoLuan), Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.XemBaiDangTrongFeed), color = MaterialTheme.colorScheme.primary)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp), color = MaterialTheme.colorScheme.outlineVariant)
            }

            // 4. Form viết đánh giá (Nếu đủ điều kiện)
            if (canReview) {
                item {
                    ReviewInputForm(
                        isSubmitting = isSubmitting,
                        onSubmitReview = { rating, content ->
                            reviewViewModel.submitProductReview(
                                productId = product.id!!,
                                rating = rating,
                                content = content,
                                onSuccess = { Toast.makeText(context, context.getString(R.string.DaGuiDanhGia), Toast.LENGTH_SHORT).show() },
                                onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // 5. Danh sách đánh giá từ khách hàng
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
                    ReviewCard(review = review)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}