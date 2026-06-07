package com.example.smartpick.features.checkout.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.R
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.features.checkout.viewmodel.CheckoutViewModel

@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onNavigateToSuccess: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val address by viewModel.address.collectAsState()
    val paymentMethod by viewModel.paymentMethod.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()

    val context = LocalContext.current

    CheckoutContent(
        cartItems = cartItems,
        phone = phone,
        address = address,
        paymentMethod = paymentMethod,
        isProcessing = isProcessing,
        onPhoneChange = { viewModel.updatePhone(it) },
        onAddressChange = { viewModel.updateAddress(it) },
        onPaymentMethodChange = { viewModel.updatePaymentMethod(it) },
        onBack = onBack,
        onOrderClick = {
            viewModel.placeOrder(
                onSuccess = {
                    Toast.makeText(context, context.getString(R.string.DatHangThanhCong), Toast.LENGTH_LONG).show()
                    onNavigateToSuccess()
                },
                onError = { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            )
        }
    )
}

@Composable
fun CheckoutContent(
    cartItems: List<CartItem>,
    phone: String,
    address: String,
    paymentMethod: String,
    isProcessing: Boolean,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPaymentMethodChange: (String) -> Unit,
    onBack: () -> Unit,
    onOrderClick: () -> Unit
) {
    val total = cartItems.sumOf { (it.product?.price ?: 0.0) * it.quantity }
    val totalFormatted = String.format("%,.0f đ", total).replace(",", ".")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
    ) {
        // 1. Thanh công cụ tự chế bằng Box + Row
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .statusBarsPadding()
        ) {
            CustomCheckoutTopBar(
                isProcessing = isProcessing,
                onBack = onBack
            )
        }

        // 2. Nội dung cuộn ở giữa
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                DeliveryInfoSection(
                    phone = phone,
                    address = address,
                    isProcessing = isProcessing,
                    onPhoneChange = onPhoneChange,
                    onAddressChange = onAddressChange
                )
            }

            item {
                PaymentMethodSection(
                    paymentMethod = paymentMethod,
                    isProcessing = isProcessing,
                    onPaymentMethodChange = onPaymentMethodChange
                )
            }

            item {
                ProductSummarySection(
                    cartItems = cartItems
                )
            }
        }

        // 3. Thanh thanh toán dưới đáy
        CheckoutBottomBar(
            totalFormatted = totalFormatted,
            isProcessing = isProcessing,
            isCartNotEmpty = cartItems.isNotEmpty(),
            onOrderClick = onOrderClick
        )
    }
}

@Preview(showBackground = true, name = "Giao diện Thanh toán - Chế độ sáng")
@Composable
fun CheckoutContentPreview() {
    // Khởi tạo dữ liệu Mock đúng theo Constructor của Product.kt
    val mockProducts = listOf(
        Product(
            id = "prod_001",
            ownerId = "owner_shop_a", // Thuộc tính bắt buộc
            name = "Áo Sơ Mi Nam Công Sở Cao Cấp", // Thuộc tính bắt buộc
            price = 250000.0,
            brand = "SmartPick Brand"
        ),
        Product(
            id = "prod_002",
            ownerId = "owner_shop_b", // Thuộc tính bắt buộc
            name = "Quần Tây Âu Dáng Hàn Quốc", // Thuộc tính bắt buộc
            price = 320000.0,
            brand = "SmartPick Fashion"
        )
    )

    val mockCartItems = listOf(
        CartItem(
            id = "cart_01",
            userId = "user_dungnx",
            productId = "prod_001",
            quantity = 2,
            product = mockProducts[0]
        ),
        CartItem(
            id = "cart_02",
            userId = "user_dungnx",
            productId = "prod_002",
            quantity = 1,
            product = mockProducts[1]
        )
    )

    SmartPickTheme {
        CheckoutContent(
            cartItems = mockCartItems,
            phone = "0987654321",
            address = "144 Xuân Thủy, Cầu Giấy, Hà Nội",
            paymentMethod = "COD",
            isProcessing = false,
            onPhoneChange = {},
            onAddressChange = {},
            onPaymentMethodChange = {},
            onBack = {},
            onOrderClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Giao diện Thanh toán - Đang xử lý (Loading)")
@Composable
fun CheckoutContentProcessingPreview() {
    val mockCartItems = listOf(
        CartItem(
            id = "cart_01",
            userId = "user_dungnx",
            productId = "prod_001",
            quantity = 1,
            product = Product(
                id = "prod_001",
                ownerId = "owner_shop_a",
                name = "Giày Thể Thao Sneaker",
                price = 450000.0
            )
        )
    )

    SmartPickTheme {
        CheckoutContent(
            cartItems = mockCartItems,
            phone = "0123456789",
            address = "Số 1 Đại Cồ Việt, Hai Bà Trưng, Hà Nội",
            paymentMethod = "CARD",
            isProcessing = true, // Trạng thái này sẽ làm nút ĐẶT HÀNG hiển thị CircularProgressIndicator
            onPhoneChange = {},
            onAddressChange = {},
            onPaymentMethodChange = {},
            onBack = {},
            onOrderClick = {}
        )
    }
}
