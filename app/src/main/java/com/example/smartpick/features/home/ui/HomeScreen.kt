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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.ui.theme.SmartPickColor
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.core.ui.theme.White
import com.example.smartpick.R
import com.example.smartpick.features.cart.ui.CartBottomSheet
import com.example.smartpick.features.cart.viewmodel.CartViewModel
import com.example.smartpick.features.home.ui.components.ProductGridCard
import com.example.smartpick.features.home.ui.components.SearchBar
import com.example.smartpick.features.home.viewmodel.HomeUiState
import com.example.smartpick.features.home.viewmodel.HomeViewModel
import com.example.smartpick.features.product_detail.ui.components.ProductDetailContent
import com.example.smartpick.navigation.Routes

@Composable
fun HomeScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalCartCount by cartViewModel.totalCartCount.collectAsState()

    var showCart by rememberSaveable { mutableStateOf(false) }
    var selectedProduct by rememberSaveable { mutableStateOf<Product?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val freshSelectedProduct = remember(selectedProduct, uiState) {
        if (uiState is HomeUiState.Success && selectedProduct != null) {
            (uiState as HomeUiState.Success).products.find { it.id == selectedProduct?.id }
                ?: selectedProduct
        } else selectedProduct
    }

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

    // Mỗi khi HomeScreen được quay lại, làm mới giỏ hàng để cập nhật trạng thái dữ liệu ổn định
    LaunchedEffect(Unit) {
        cartViewModel.refreshCart()
    }

    HomeContent(
        paddingValues = paddingValues,
        uiState = uiState,
        cartItems = cartItems,
        totalCartCount = totalCartCount,
        searchQuery = searchQuery,
        selectedProduct = freshSelectedProduct,
        showCart = showCart,
        onSearchQueryChange = { searchQuery = it; viewModel.searchProducts(it) },
        onMicClick = {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
            }
            try {
                speechLauncher.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(context, context.getString(R.string.ThietBiKhongHoTro), Toast.LENGTH_SHORT).show()
            }
        },
        onCartClick = { showCart = true },
        onProductClick = { selectedProduct = it },
        onAddToCart = { product ->
            viewModel.addToCart(
                product = product,
                onSuccess = {
                    Toast.makeText(context, context.getString(R.string.DaThemVaoGioHang), Toast.LENGTH_SHORT).show()
                    cartViewModel.refreshCart()
                },
                onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
            )
        },
        onDismissSheet = { selectedProduct = null },
        onViewFeed = { postId ->
            selectedProduct = null
            navController.navigate(Routes.PostDetail.createRoute(postId))
        },
        onBuyNow = {
            viewModel.addToCart(
                product = freshSelectedProduct!!,
                onSuccess = {
                    selectedProduct = null
                    cartViewModel.refreshCart()
                    navController.navigate(Routes.Checkout.route)
                },
                onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            )
        },
        onDismissCart = { showCart = false },
        onIncrease = { item -> cartViewModel.increaseQuantity(item) },
        onDecrease = { item -> cartViewModel.decreaseQuantity(item) },
        onCheckout = { showCart = false; navController.navigate(Routes.Checkout.route) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    paddingValues: PaddingValues,
    uiState: HomeUiState,
    cartItems: List<CartItem>,
    totalCartCount: Int,
    searchQuery: String,
    selectedProduct: Product?,
    showCart: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onMicClick: () -> Unit,
    onCartClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onDismissSheet: () -> Unit,
    onViewFeed: (String) -> Unit,
    onBuyNow: () -> Unit,
    onDismissCart: () -> Unit,
    onIncrease: (CartItem) -> Unit,
    onDecrease: (CartItem) -> Unit,
    onCheckout: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCartClick,
                containerColor = SmartPickColor,
                contentColor = White
            ) {
                if (totalCartCount > 0) BadgedBox(badge = { Badge { Text(totalCartCount.toString()) } }) {
                    Icon(Icons.Default.ShoppingCart, null)
                }
                else Icon(Icons.Default.ShoppingCart, null)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    top = paddingValues.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onMicClick = onMicClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp)
                    .padding(top = 4.dp, bottom = 4.dp)
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
                            start = 8.dp,
                            end = 8.dp,
                            top = 0.dp,
                            bottom = 16.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.products) { product ->
                            ProductGridCard(
                                product = product,
                                onProductClick = { onProductClick(product) },
                                onAddToCart = { onAddToCart(product) }
                            )
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    if (selectedProduct != null) {
        ModalBottomSheet(
            onDismissRequest = onDismissSheet,
            modifier = Modifier.fillMaxHeight(0.9f)
        ) {
            ProductDetailContent(
                product = selectedProduct,
                onViewFeed = onViewFeed,
                onAddToCart = { onAddToCart(selectedProduct) },
                onBuyNow = onBuyNow
            )
        }
    }

    if (showCart) {
        CartBottomSheet(
            cartItems = cartItems,
            onDismiss = onDismissCart,
            onIncrease = onIncrease,
            onDecrease = onDecrease,
            onCheckout = onCheckout
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Trạng Thái Danh Sách Sản Phẩm")
@Composable
fun HomeContentSuccessPreview() {
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
            soldCount = 1250 // Kiểm tra hiển thị định dạng rút gọn (1k)
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
            paddingValues = PaddingValues(top = 56.dp), // Giả lập lề TopBar hệ thống
            uiState = HomeUiState.Success(products = mockProducts),
            cartItems = emptyList(),
            totalCartCount = 3, // Giả lập Badge giỏ hàng hiển thị số 3
            searchQuery = "",
            selectedProduct = null,
            showCart = false,
            onSearchQueryChange = {},
            onMicClick = {},
            onCartClick = {},
            onProductClick = {},
            onAddToCart = {},
            onDismissSheet = {},
            onViewFeed = {},
            onBuyNow = {},
            onDismissCart = {},
            onIncrease = {},
            onDecrease = {},
            onCheckout = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Trạng Thái Đang Tải (Loading)")
@Composable
fun HomeContentLoadingPreview() {
    SmartPickTheme {
        HomeContent(
            paddingValues = PaddingValues(top = 56.dp),
            uiState = HomeUiState.Loading,
            cartItems = emptyList(),
            totalCartCount = 0,
            searchQuery = "",
            selectedProduct = null,
            showCart = false,
            onSearchQueryChange = {},
            onMicClick = {},
            onCartClick = {},
            onProductClick = {},
            onAddToCart = {},
            onDismissSheet = {},
            onViewFeed = {},
            onBuyNow = {},
            onDismissCart = {},
            onIncrease = {},
            onDecrease = {},
            onCheckout = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Trạng Thái Không Có Dữ Liệu")
@Composable
fun HomeContentEmptyPreview() {
    SmartPickTheme {
        HomeContent(
            paddingValues = PaddingValues(top = 56.dp),
            uiState = HomeUiState.Success(products = emptyList()),
            cartItems = emptyList(),
            totalCartCount = 0,
            searchQuery = "Sản phẩm không tồn tại", // Giả lập từ khóa tìm kiếm không ra kết quả
            selectedProduct = null,
            showCart = false,
            onSearchQueryChange = {},
            onMicClick = {},
            onCartClick = {},
            onProductClick = {},
            onAddToCart = {},
            onDismissSheet = {},
            onViewFeed = {},
            onBuyNow = {},
            onDismissCart = {},
            onIncrease = {},
            onDecrease = {},
            onCheckout = {}
        )
    }
}