package com.example.smartpick.features.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.theme.*
import com.example.smartpick.core.utils.Constants.PROVIDER_GOOGLE

/**
 * Biểu tượng bóng đèn đặc trưng của ứng dụng SmartPick.
 */
@Composable
fun BulbIconLight() {
    Surface(
        modifier = Modifier
            .size(64.dp)
            .background(CardLight, shape = RoundedCornerShape(16.dp))
            .border(1.dp, DividerColor, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Filled.Lightbulb,
                contentDescription = "Bulb Icon",
                modifier = Modifier.size(32.dp),
                tint = LoginBlue
            )
        }
    }
}

/**
 * Nhãn tiêu đề phía trên các ô nhập liệu.
 */
@Composable
fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = TextSecondary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        textAlign = TextAlign.Start
    )
}

/**
 * Ô nhập liệu tiêu chuẩn với icon dẫn đầu.
 */
@Composable
fun StandardTextFieldLight(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .background(SocialButtonLightColor, RoundedCornerShape(12.dp)),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = TextPrimary
        ),
        value = value,
        onValueChange = onValueChange,
        textStyle = LocalTextStyle.current.copy(color = TextPrimary),
        leadingIcon = { Icon(leadingIcon, null, tint = TextSecondary) },
        placeholder = { Text(placeholder, color = TextSecondary.copy(alpha = 0.5f)) },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        isError = isError,
    )

    if (isError && errorMessage != null) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error, // Màu đỏ mặc định của hệ thống
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}

/**
 * Ô nhập mật khẩu có hỗ trợ ẩn/hiện nội dung.
 */
@Composable
fun PasswordTextFieldLight(
    value: String,
    onValueChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordToggle: () -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .background(SocialButtonLightColor, RoundedCornerShape(12.dp)),
        textStyle = LocalTextStyle.current.copy(color = TextPrimary),
        leadingIcon = { Icon(Icons.Filled.Lock, null, tint = TextSecondary) },
        trailingIcon = {
            val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = onPasswordToggle) { Icon(icon, null, tint = TextSecondary) }
        },
        placeholder = {
            Text(
                stringResource(R.string.pass_hidden),
                color = TextSecondary.copy(alpha = 0.5f)
            )
        },
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = TextPrimary,
        ),
        singleLine = true,
        isError = isError,
    )

    if (isError && errorMessage != null) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error, // Màu đỏ mặc định của hệ thống
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}

/**
 * Nút bấm chính có hiệu ứng Gradient màu xanh.
 * @param text Văn bản hiển thị trên nút.
 * @param showArrow Hiển thị icon mũi tên ở cuối nút.
 * @param onClick Sự kiện khi bấm nút.
 */
@Composable
fun AuthPrimaryButton(
    text: String,
    showArrow: Boolean = true,
    onClick: () -> Unit,
    enabled: Boolean = true // Thêm tham số này
) {
    // 1. Định nghĩa màu sắc dựa trên trạng thái enabled
    val gradientBrush = if (enabled) {
        Brush.linearGradient(listOf(LoginBlue, LoginBlueGradientEnd))
    } else {
        // Khi disable thì dùng một màu xám cố định thay vì gradient
        SolidColor(Color.LightGray)
    }

    val contentColor = if (enabled) Color.White else Color.Gray

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            // 2. Quan trọng: Truyền enabled vào clickable để chặn click khi đang load
            .clickable(enabled = enabled, onClick = onClick)
            .border(
                width = 1.dp,
                color = if (enabled) DividerColor else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor // Đổi màu chữ khi disable
            )
            if (showArrow) {
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    tint = contentColor // Đổi màu icon khi disable
                )
            }
        }
    }
}

/**
 * Nút đăng nhập qua mạng xã hội.
 * @param text Văn bản hiển thị.
 * @param brand Loại thương hiệu (Google/Facebook) để hiển thị logo tương ứng.
 */
@Composable
fun SocialAuthButton(
    text: String,
    brand: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onClick)
            .background(SocialButtonLightColor, RoundedCornerShape(12.dp))
            .border(1.dp, DividerColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconRes = when (brand.lowercase()) {
                PROVIDER_GOOGLE -> R.drawable.ic_google_logo
                else -> null
            }

            iconRes?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = stringResource(R.string.logo, brand),
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Đường kẻ phân cách với văn bản ở giữa.
 */
@Composable
fun AuthDivider(text: String = stringResource(R.string.or_connect_with)) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Divider(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            color = DividerColor
        )
        Text(
            text = text.uppercase(),
            fontSize = 12.sp,
            color = DividerColor
        )
        Divider(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            color = DividerColor
        )
    }
}
