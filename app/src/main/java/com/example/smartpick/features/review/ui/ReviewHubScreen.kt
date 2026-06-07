package com.example.smartpick.features.review.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.smartpick.R
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.Review
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.features.review.ui.components.CompletedReviewList
import com.example.smartpick.features.review.ui.components.PendingReviewList
import com.example.smartpick.features.review.viewmodel.ReviewHubViewModel

@Composable
fun ReviewHubScreen(
    viewModel: ReviewHubViewModel,
    onNavigateToWriteReview: (String) -> Unit,
    onNavigateToProductDetail: (String) -> Unit, // BỔ SUNG: Lambda điều hướng tới trang chi tiết sản phẩm
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val productsToReview by viewModel.productsToReview.collectAsState()
    val reviewedProducts by viewModel.reviewedProducts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Tự động làm mới dữ liệu mỗi khi màn hình này được mở ra
    LaunchedEffect(Unit) {
        viewModel.fetchReviewData()
    }

    ReviewHubContent(
        productsToReview = productsToReview,
        reviewedProducts = reviewedProducts,
        isLoading = isLoading,
        onNavigateToWriteReview = onNavigateToWriteReview,
        onNavigateToProductDetail = onNavigateToProductDetail, // Chuyển tiếp xuống Content tầng dưới
        modifier = modifier.padding(
            start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
            end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
            bottom = paddingValues.calculateBottomPadding()
        )
    )
}

@Composable
fun ReviewHubContent(
    productsToReview: List<Product>,
    reviewedProducts: List<Review>,
    isLoading: Boolean,
    onNavigateToWriteReview: (String) -> Unit,
    onNavigateToProductDetail: (String) -> Unit, // Nhận lambda điều hướng
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Chờ đánh giá", "Đã đánh giá")

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (selectedTabIndex) {
                        0 -> PendingReviewList(
                            products = productsToReview,
                            onReviewClick = onNavigateToWriteReview
                        )
                        1 -> CompletedReviewList(
                            reviews = reviewedProducts,
                            onProductClick = onNavigateToProductDetail // KẾT NỐI: Truyền lambda xuống danh sách đã đánh giá
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewHubContentPreview() {
    val mockProducts = listOf(
        Product(
            id = "prod_01",
            ownerId = "user_01",
            name = "Tai nghe Bluetooth Không Dây Chống Ồn Chủ Động Sony WH-1000XM5",
            brand = "Sony",
            category = "Phụ kiện",
            price = 6490000.0,
            imageUrls = listOf("https://via.placeholder.com/150"),
            stock = 5,
            soldCount = 2
        )
    )

    val mockReviews = listOf(
        Review(
            id = "rev_01",
            userId = "user_01",
            productId = "prod_01",
            rating = 4,
            content = "Tai nghe dùng rất thích, chống ồn đỉnh cao đúng như mô tả sản phẩm.",
            createdAt = "2026-06-07T12:00:00",
            product = mockProducts[0]
        )
    )

    SmartPickTheme {
        ReviewHubContent(
            productsToReview = mockProducts,
            reviewedProducts = mockReviews,
            isLoading = false,
            onNavigateToWriteReview = {},
            onNavigateToProductDetail = {}
        )
    }
}