package com.example.smartpick.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF3B82F6),          // Xanh sáng năng động
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E3A8A),   // Xanh thương hiệu SmartPick ở dạng tối
    onPrimaryContainer = Color(0xFF00C8FF), // Màu Cyan phát sáng cho AI
    secondary = Color(0xFF4A9EE8),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF1E293B), // Slate container tối
    onSecondaryContainer = Color(0xFFE2E8F0),
    background = Color(0xFF0F172A),       // Nền Slate đen-xanh cực sâu và dịu mắt
    onBackground = Color(0xFFF8FAFC),     // Chữ trắng sữa tương phản cao
    surface = Color(0xFF1E293B),          // Nền thẻ Card tối thanh lịch
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF334155),    // Slate nhạt hơn cho viền/thẻ con
    onSurfaceVariant = Color(0xFF94A3B8),  // Chữ xám mờ
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF334155),
    error = Color(0xFFEF4444),
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = SmartPickColor,
    onPrimary = White,
    primaryContainer = CardLight,
    onPrimaryContainer = LoginBlue,
    secondaryContainer = SocialButtonLightColor,
    onSecondaryContainer = TextSecondary,
    background = PageBg,
    onBackground = TextPrimary,
    surface = BrightBackground,
    onSurface = TextSecondary,
    surfaceVariant = SurfaceCard,
    outlineVariant = DividerColor,
    outline = DividerColor,
    error = ErrorRed
)

@Composable
fun SmartPickTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled dynamic color to prioritize SmartPick branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}