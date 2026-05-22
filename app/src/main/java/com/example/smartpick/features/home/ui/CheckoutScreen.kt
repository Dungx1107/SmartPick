package com.example.smartpick.features.home.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.ui.theme.*
import com.example.smartpick.features.home.viewmodel.HomeViewModel

@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onNavigateToSuccess: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val context = LocalContext.current

    var phone by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var paymentMethod by rememberSaveable { mutableStateOf("COD") }

    CheckoutContent(
        cartItems = cartItems,
        phone = phone,
        address = address,
        paymentMethod = paymentMethod,
        onPhoneChange = { phone = it },
        onAddressChange = { address = it },
        onPaymentMethodChange = { paymentMethod = it },
        onBack = onBack,
        onOrderClick = {
            if (phone.isBlank() || address.isBlank()) {
                Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@CheckoutContent
            }
            viewModel.processCheckout(
                address = address,
                phone = phone,
                paymentMethod = paymentMethod,
                onSuccess = {
                    Toast.makeText(context, "Đặt hàng thành công!", Toast.LENGTH_LONG).show()
                    onNavigateToSuccess()
                },
                onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutContent(
    cartItems: List<CartItem>,
    phone: String,
    address: String,
    paymentMethod: String,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPaymentMethodChange: (String) -> Unit,
    onBack: () -> Unit,
    onOrderClick: () -> Unit
) {
    val total = cartItems.sumOf { (it.product?.price ?: 0.0) * it.quantity }
    // Format tổng tiền chuẩn: 20.000 đ
    val totalFormatted = String.format("%,.0f đ", total).replace(",", ".")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Tổng thanh toán", fontSize = 12.sp, color = TextMuted)
                        Text(
                            totalFormatted,
                            style = MaterialTheme.typography.titleLarge,
                            color = AccentBlue, // Đã đổi từ ErrorRed sang AccentBlue
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = onOrderClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SmartPickColor),
                        modifier = Modifier.height(50.dp).width(150.dp)
                    ) {
                        Text("ĐẶT HÀNG", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Thông tin nhận hàng", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = onPhoneChange,
                            label = { Text("Số điện thoại") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = address,
                            onValueChange = onAddressChange,
                            label = { Text("Địa chỉ nhận hàng") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Phương thức thanh toán", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = paymentMethod == "COD", onClick = { onPaymentMethodChange("COD") })
                            Text("Thanh toán khi nhận hàng (COD)", color = MaterialTheme.colorScheme.onSurface)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = paymentMethod == "CARD", onClick = { onPaymentMethodChange("CARD") })
                            Text("Thẻ tín dụng / Ghi nợ", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tóm tắt sản phẩm", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(8.dp))
                        cartItems.forEach { item ->
                            val itemTotal = (item.product?.price ?: 0.0) * item.quantity
                            val itemTotalFormatted = String.format("%,.0f đ", itemTotal).replace(",", ".")

                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${item.product?.name ?: "Sản phẩm"} x${item.quantity}", modifier = Modifier.weight(1f), maxLines = 1, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(itemTotalFormatted, fontWeight = FontWeight.Medium, color = AccentBlue) // Đã đổi sang AccentBlue
                            }
                        }
                    }
                }
            }
        }
    }
}