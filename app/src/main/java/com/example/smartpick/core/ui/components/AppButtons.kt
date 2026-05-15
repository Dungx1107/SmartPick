package com.example.smartpick.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.ui.theme.DividerColor
import com.example.smartpick.core.ui.theme.LoginBlue
import com.example.smartpick.core.ui.theme.LoginBlueGradientEnd
import com.example.smartpick.core.ui.theme.SocialButtonLightColor
import com.example.smartpick.core.ui.theme.TextMuted
import com.example.smartpick.core.utils.Constants.PROVIDER_GOOGLE


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
    enabled: Boolean = true
) {
    val gradientBrush = if (enabled) {
        // Giữ Gradient branding nhưng có thể cân nhắc dùng theme nếu cần chuyển hoàn toàn
        Brush.linearGradient(listOf(LoginBlue, LoginBlueGradientEnd))
    } else {
        SolidColor(MaterialTheme.colorScheme.surfaceVariant)
    }

    val contentColor = if (enabled) MaterialTheme.colorScheme.onPrimary else TextMuted

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(enabled = enabled, onClick = onClick)
            .border(
                width = 1.dp,
                color = if (enabled) DividerColor else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Row(modifier = Modifier.fillMaxSize()
                .background(gradientBrush),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            if (showArrow) {
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    tint = contentColor
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
    onClick: () -> Unit,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    val backgroundColor = if (enabled && !loading) SocialButtonLightColor else MaterialTheme.colorScheme.surfaceVariant
    val contentAlpha = if (enabled && !loading) 1f else 0.5f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(enabled = enabled && !loading, onClick = onClick)
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .border(1.dp, DividerColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
            } else {
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
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}