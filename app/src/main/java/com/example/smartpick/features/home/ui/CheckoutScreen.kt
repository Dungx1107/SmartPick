package com.example.smartpick.features.home.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.core.ui.theme.ErrorRed
import com.example.smartpick.core.ui.theme.PageBg
import com.example.smartpick.core.ui.theme.SmartPickColor
import com.example.smartpick.core.ui.theme.TextMuted
import com.example.smartpick.core.ui.theme.White
import com.example.smartpick.features.home.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onNavigateToSuccess: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val context = LocalContext.current

    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("COD") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = White) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val total = cartItems.sumOf { (it.product?.price ?: 0.0) * it.quantity }
                    Column {
                        Text("Tổng thanh toán", fontSize = 12.sp, color = TextMuted)
                        Text("${total}đ", style = MaterialTheme.typography.titleLarge, color = ErrorRed, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            if (phone.isBlank() || address.isBlank()) {
                                Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                                return@Button
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
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SmartPickColor),
                        modifier = Modifier.height(50.dp).width(150.dp)
                    ) {
                        Text("ĐẶT HÀNG", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(PageBg),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Khối 1: Thông tin nhận hàng
            item {
                Card(colors = CardDefaults.cardColors(containerColor = White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Thông tin nhận hàng", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Số điện thoại") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Địa chỉ nhận hàng") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Khối 2: Phương thức thanh toán
            item {
                Card(colors = CardDefaults.cardColors(containerColor = White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Phương thức thanh toán", fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = paymentMethod == "COD", onClick = { paymentMethod = "COD" })
                            Text("Thanh toán khi nhận hàng (COD)")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = paymentMethod == "CARD", onClick = { paymentMethod = "CARD" })
                            Text("Thẻ tín dụng / Ghi nợ")
                        }
                    }
                }
            }

            // Khối 3: Tóm tắt đơn hàng
            item {
                Card(colors = CardDefaults.cardColors(containerColor = White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tóm tắt sản phẩm", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        cartItems.forEach { item ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${item.product?.name} x${item.quantity}", modifier = Modifier.weight(1f), maxLines = 1)
                                Text("${(item.product?.price ?: 0.0) * item.quantity}đ", fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}