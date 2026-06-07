package com.example.smartpick.features.checkout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.model.CartItem
import com.example.smartpick.core.ui.theme.AccentBlue
import com.example.smartpick.core.ui.theme.TextMuted

@Composable
fun CustomCheckoutTopBar(
    isProcessing: Boolean,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Nút quay lại sử dụng Box + Icon (Bọc ngoài bằng Box để tăng vùng tương tác)
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .clickable(enabled = !isProcessing, onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = if (isProcessing) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                else MaterialTheme.colorScheme.onSurface
            )
        }

        // Tiêu đề căn giữa toàn bộ TopBar
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.ThanhToan),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun DeliveryInfoSection(
    phone: String,
    address: String,
    isProcessing: Boolean,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Thông tin nhận hàng",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                label = { Text("Địa chỉ nhận hàng") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing
            )
        }
    }
}

@Composable
fun PaymentMethodSection(
    paymentMethod: String,
    isProcessing: Boolean,
    onPaymentMethodChange: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Tiêu đề khối
            Text(
                text = "Phương thức thanh toán",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(16.dp))

            // Lựa chọn 1: COD
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isProcessing) { onPaymentMethodChange("COD") }
                    .padding(vertical = 12.dp) // Tăng khoảng trống dòng để dễ bấm
            ) {
                // Khử vùng đệm 48.dp mặc định của RadioButton để icon dịch sát ra lề trái, thẳng hàng với tiêu đề
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                    RadioButton(
                        selected = paymentMethod == "COD",
                        onClick = { onPaymentMethodChange("COD") },
                        enabled = !isProcessing
                    )
                }
                Spacer(Modifier.width(12.dp)) // Khoảng cách chuẩn từ Icon đến chữ
                Text(
                    text = "Thanh toán khi nhận hàng (COD)",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Lựa chọn 2: CARD
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isProcessing) { onPaymentMethodChange("CARD") }
                    .padding(vertical = 12.dp)
            ) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                    RadioButton(
                        selected = paymentMethod == "CARD",
                        onClick = { onPaymentMethodChange("CARD") },
                        enabled = !isProcessing
                    )
                }
                Spacer(Modifier.width(12.dp)) // Khoảng cách chuẩn từ Icon đến chữ
                Text(
                    text = "Thẻ tín dụng / Ghi nợ",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun ProductSummarySection(cartItems: List<CartItem>) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Tóm tắt sản phẩm",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            cartItems.forEach { item ->
                val itemTotal = (item.product?.price ?: 0.0) * item.quantity
                val itemTotalFormatted = String.format("%,.0f đ", itemTotal).replace(",", ".")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${item.product?.name ?: "Sản phẩm"} x${item.quantity}",
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(itemTotalFormatted, fontWeight = FontWeight.Medium, color = AccentBlue)
                }
            }
        }
    }
}

@Composable
fun CheckoutBottomBar(
    totalFormatted: String,
    isProcessing: Boolean,
    isCartNotEmpty: Boolean,
    onOrderClick: () -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Tổng thanh toán", fontSize = 12.sp, color = TextMuted)
                Text(
                    text = totalFormatted,
                    style = MaterialTheme.typography.titleLarge,
                    color = AccentBlue,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = onOrderClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .height(50.dp)
                    .width(150.dp),
                enabled = !isProcessing && isCartNotEmpty
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("ĐẶT HÀNG", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}