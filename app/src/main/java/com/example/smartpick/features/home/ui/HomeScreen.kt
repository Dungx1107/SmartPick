package com.example.smartpick.features.home.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartpick.R
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.features.cart.viewmodel.CartViewModel
import com.example.smartpick.features.home.ui.components.ProductGridCard
import com.example.smartpick.features.home.ui.components.SearchBar
import com.example.smartpick.features.home.viewmodel.HomeUiState
import com.example.smartpick.features.home.viewmodel.HomeViewModel
import com.example.smartpick.navigation.Routes
import com.example.smartpick.features.home.ui.components.FlyingProductItem
import com.example.smartpick.features.home.ui.components.FlyingProductState

@Composable
fun HomeScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(), // Khôi phục lại để lấy số lượng xe đẩy
    onProductClick: (Product) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val totalCartCount by cartViewModel.totalCartCount.collectAsState() // Thu thập số lượng item đang có
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // Quản lý danh sách các gói tin sản phẩm đang bay trên màn hình HomeScreen
    var flyingProducts by remember { mutableStateOf(listOf<FlyingProductState>()) }
    var animationIdCounter by remember { mutableLongStateOf(0L) }

    var cartIconOffset by remember { mutableStateOf(Offset.Zero) }// Tọa độ đích đến

    val context = LocalContext.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val speechLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val text =
                    result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
                        ?: ""
                searchQuery = text
                viewModel.searchProducts(text)
            }
        }

    // Làm mới trạng thái số lượng xe đẩy liên tục khi quay lại màn hình
    LaunchedEffect(Unit) {
        cartViewModel.refreshCart()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomeContent(
            paddingValues = paddingValues,
            uiState = uiState,
            searchQuery = searchQuery,
            totalCartCount = totalCartCount,
            onSearchQueryChange = { searchQuery = it; viewModel.searchProducts(it) },
            onMicClick = { /* ... giữ nguyên logic mic cũ của bạn */ },
            onCartClick = { navController.navigate(Routes.Cart.route) },
            onProductClick = onProductClick,
            // Lắng nghe tọa độ icon giỏ hàng truyền lên từ cấu trúc SearchBar
            onCartIconPositioned = { offset ->
                cartIconOffset = offset
            },
            onAddToCart = { product ,touchOffset->
                if (product.id != null) {
                    val endPoint =
                        if (cartIconOffset != Offset.Zero) cartIconOffset else Offset(930f, 140f)
                    val newItem = FlyingProductState(
                        id = animationIdCounter++,
                        imageUrl = product.imageUrls.firstOrNull(),
                        startOffset = touchOffset,
                        endOffset = endPoint
                    )
                    flyingProducts = flyingProducts + newItem
                }

                viewModel.addToCart(
                    product = product,
                    onSuccess = {
                        Toast.makeText(
                            context,
                            context.getString(R.string.DaThemVaoGioHang),
                            Toast.LENGTH_SHORT
                        ).show()
                        cartViewModel.refreshCart()
                    },
                    onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
                )
            }
        )

        flyingProducts.forEach { flyingState ->
            FlyingProductItem(
                state = flyingState,
                onAnimationEnd = { endedId ->
                    flyingProducts = flyingProducts.filter { it.id != endedId }
                }
            )
        }
    }
}

@Composable
fun HomeContent(
    paddingValues: PaddingValues,
    uiState: HomeUiState,
    searchQuery: String,
    totalCartCount: Int,
    onSearchQueryChange: (String) -> Unit,
    onMicClick: () -> Unit,
    onCartClick: () -> Unit,
    onCartIconPositioned: (Offset) -> Unit,        // Callback nhận tọa độ giỏ hàng
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product,Offset) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                top = paddingValues.calculateTopPadding(),      // Đẩy SearchBar xuống dưới StatusBar chuẩn
                bottom = paddingValues.calculateBottomPadding() // Đẩy Grid sản phẩm lên trên MainBottomBar chuẩn
            )
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            onMicClick = onMicClick,
            totalCartCount = totalCartCount,
            onCartClick = onCartClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp) // Thu hẹp lề hai bên thanh search từ 12.dp xuống 8.dp
                .padding(top = 4.dp, bottom = 4.dp) // Thu hẹp lề trên dưới của search bar
                .onGloballyPositioned { coordinates ->
                    // Lấy tọa độ góc trên bên phải thanh tìm kiếm (gần vị trí xe hàng)
                    val position = coordinates.positionInWindow()
                    onCartIconPositioned(
                        Offset(
                            position.x + coordinates.size.width - 100f,
                            position.y + 30f
                        )
                    )
                }
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

                is HomeUiState.Success -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        start = 6.dp,
                        end = 6.dp,
                        top = 2.dp,
                        bottom = 8.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(state.products, key = { it.id ?: "" }) { product ->
                        ProductGridCard(
                            product = product,
                            onProductClick = { onProductClick(product) },
                            onAddToCart = { prod, offset -> onAddToCart(prod, offset) }
                        )
                    }
                }

                else -> Unit
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Giao Diện Trang Chủ Có Hàng Trong Giỏ")
@Composable
fun HomeContentSuccessWithBadgePreview() {
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
            soldCount = 1250
        ),
        Product(
            id = "prod_02",
            ownerId = "user_01",
            name = "Bàn phím cơ Custom Keychron K2 V2 Nhôm Hot-swappable",
            brand = "Keychron",
            category = "Phụ kiện",
            price = 1850000.0,
            imageUrls = listOf("https://via.placeholder.com/150"),
            stock = 12,
            soldCount = 45
        ),
        Product(
            id = "prod_03",
            ownerId = "user_02",
            name = "Điện thoại Apple iPhone 15 Pro Max 256GB Chính hãng VN/A",
            brand = "Apple",
            category = "Điện thoại",
            price = 29990000.0,
            imageUrls = listOf("https://via.placeholder.com/150"),
            stock = 8,
            soldCount = 89
        ),
        Product(
            id = "prod_04",
            ownerId = "user_02",
            name = "Củ sạc nhanh Anker GaNPrime 65W 3 cổng (2 C, 1 A) nhỏ gọn",
            brand = "Anker",
            category = "Phụ kiện",
            price = 850000.0,
            imageUrls = listOf("https://via.placeholder.com/150"),
            stock = 50,
            soldCount = 2100
        )
    )

    SmartPickTheme {
        HomeContent(
            paddingValues = PaddingValues(top = 56.dp), // Giả lập khoảng trống TopBar hệ thống
            uiState = HomeUiState.Success(products = mockProducts),
            searchQuery = "",
            totalCartCount = 5, // Giả lập hiển thị số 5 màu đỏ trên xe đẩy hàng đỉnh đầu
            onSearchQueryChange = {},
            onMicClick = {},
            onCartClick = {},
            onProductClick = {},
            onAddToCart = { _, _ -> },
            onCartIconPositioned = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Giao Diện Trang Chủ Giỏ Hàng Trống")
@Composable
fun HomeContentEmptyCartPreview() {
    val mockProducts = listOf(
        Product(
            id = "prod_02",
            ownerId = "user_01",
            name = "Bàn phím cơ Custom Keychron K2 V2 Nhôm Hot-swappable",
            brand = "Keychron",
            category = "Phụ kiện",
            price = 1850000.0,
            imageUrls = listOf("https://via.placeholder.com/150"),
            stock = 12,
            soldCount = 45
        )
    )

    SmartPickTheme {
        HomeContent(
            paddingValues = PaddingValues(top = 56.dp),
            uiState = HomeUiState.Success(products = mockProducts),
            searchQuery = "",
            totalCartCount = 0, // Giỏ hàng trống, icon xe đẩy hiển thị dạng mờ không có chấm đỏ số lượng
            onSearchQueryChange = {},
            onMicClick = {},
            onCartClick = {},
            onProductClick = {},
            onAddToCart = { _, _ -> },
            onCartIconPositioned = {},
        )
    }
}