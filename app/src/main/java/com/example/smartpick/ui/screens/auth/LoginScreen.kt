// File: ui/theme/Color.kt (Bạn cần thêm các màu này vào file màu của theme app)
package com.example.smartpick.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.ui.theme.CardDark
import com.example.smartpick.ui.theme.DeepBlack
import com.example.smartpick.ui.theme.DividerColor
import com.example.smartpick.ui.theme.LoginBlue
import com.example.smartpick.ui.theme.LoginBlueGradientEnd
import com.example.smartpick.ui.theme.SmartPickTheme
import com.example.smartpick.ui.theme.SocialButtonDark
import com.example.smartpick.ui.theme.TextPrimary
import com.example.smartpick.ui.theme.TextSecondary

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    // Quản lý trạng thái của các ô nhập liệu
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()), // Thêm scroll để không bị khuất khi hiện bàn phím
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // --- Header Section ---
        // Tiêu đề và Logo bo góc, có hiệu ứng bóng (mô phỏng)
        BulbIcon()
        Text(
            text = "LUMINA",
            fontSize = 20.sp,
            fontWeight = FontWeight.W600,
            color = TextPrimary,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Welcome Back",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Continue your journey of curated discovery.",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // --- Input Fields ---
        // Ô nhập Email
        FieldLabel(text = "Email")
        StandardTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "name@domain.com",
            leadingIcon = Icons.Filled.Email
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Ô nhập Mật khẩu và Hàng nhãn/link "Quên mật khẩu?"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Password",
                fontSize = 14.sp,
                color = TextSecondary
            )
            Text(
                text = "Forgot Password?",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.clickable { /* Xử lý Quên mật khẩu */ }
            )
        }
        PasswordTextField(
            value = password,
            onValueChange = { password = it },
            passwordVisible = passwordVisible,
            onPasswordToggle = { passwordVisible = !passwordVisible }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Login Button ---
        // Nút bấm chính với hiệu ứng gradient và icon
        LoginButton(onClick = onNavigateToHome)

        Spacer(modifier = Modifier.height(48.dp))

        // --- Social Login Section ---
        // Dải phân cách "OR CONNECT WITH"
        OrConnectWithDivider()
        Spacer(modifier = Modifier.height(32.dp))
        // Nút Google và Facebook bo góc
        SocialButton(text = "Continue with Google", brand = "Google", onClick = { /* Xử lý đăng nhập Google */ })
        Spacer(modifier = Modifier.height(16.dp))
        SocialButton(text = "Continue with Facebook", brand = "Facebook", onClick = { /* Xử lý đăng nhập Facebook */ })

        Spacer(modifier = Modifier.height(48.dp))

        // --- Footer Section ---
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Don't have an account?", fontSize = 14.sp, color = TextPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            // Chữ "Sign Up Now" được in đậm và có thể bấm được
            Text(
                text = "Sign Up Now",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.clickable(onClick = onNavigateToSignUp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// --- Các Composable nhỏ dùng chung cho màn hình này ---

@Composable
fun BulbIcon() {
    Surface(
        modifier = Modifier
            .size(64.dp)
            .background(CardDark, shape = RoundedCornerShape(16.dp))
            .border(1.dp, DividerColor, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            // Mô phỏng hiệu ứng glossy nhỏ
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(top = 4.dp, end = 4.dp)
                    .background(
                        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)),
                        shape = CircleShape
                    )
            )
            Icon(
                imageVector = Icons.Filled.Lightbulb,
                contentDescription = "Bulb Icon",
                modifier = Modifier.size(32.dp),
                tint = TextPrimary
            )
        }
    }
}

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

@Composable
fun StandardTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .background(CardDark, shape = RoundedCornerShape(12.dp)),
        textStyle = LocalTextStyle.current.copy(color = TextPrimary),
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = "Field Icon",
                tint = TextSecondary,
                modifier = Modifier.padding(start = 16.dp)
            )
        },
        placeholder = { Text(text = placeholder, color = TextSecondary.copy(alpha = 0.5f)) },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = TextPrimary
        ),
        singleLine = true
    )
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordToggle: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, shape = RoundedCornerShape(12.dp)),
        textStyle = LocalTextStyle.current.copy(color = TextPrimary),
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Lock Icon",
                tint = TextSecondary,
                modifier = Modifier.padding(start = 16.dp)
            )
        },
        trailingIcon = {
            val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = onPasswordToggle) {
                Icon(imageVector = icon, contentDescription = "Password Toggle", tint = TextSecondary)
            }
        },
        placeholder = { Text(text = "••••••••", color = TextSecondary.copy(alpha = 0.5f)) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = TextPrimary
        ),
        singleLine = true
    )
}

@Composable
fun LoginButton(onClick: () -> Unit) {
    // Định nghĩa hiệu ứng gradient cho nền nút
    val gradientBrush = Brush.linearGradient(listOf(LoginBlue, LoginBlueGradientEnd))
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onClick)
            .border(1.dp, DividerColor, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent // Nền nút thực tế là trong suốt, dùng Box ở trong để hiện gradient
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Login",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Login Arrow",
                tint = TextPrimary
            )
        }
    }
}

@Composable
fun OrConnectWithDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Dải phân cách bên trái
        HorizontalDivider(modifier = Modifier.weight(1f).padding(end = 16.dp), color = DividerColor)
        Text(text = "OR CONNECT WITH", fontSize = 12.sp, color = DividerColor)
        // Dải phân cách bên phải
        HorizontalDivider(modifier = Modifier.weight(1f).padding(start = 16.dp), color = DividerColor)
    }
}

@Composable
fun SocialButton(text: String, brand: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onClick)
            .background(SocialButtonDark, shape = RoundedCornerShape(12.dp))
            .border(1.dp, DividerColor, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Icon Google/Facebook bo góc tròn như hình
            if (brand == "Google") {
                Surface(shape = CircleShape, color = Color.White, modifier = Modifier.size(24.dp)) {
                    Text(text = "G", fontSize = 16.sp, color = DeepBlack, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.wrapContentSize())
                }
            } else if (brand == "Facebook") {
                Surface(shape = CircleShape, color = Color(0xFF1877F2), modifier = Modifier.size(24.dp)) {
                    Text(text = "f", fontSize = 16.sp, color = TextPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.wrapContentSize())
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                color = TextPrimary
            )
        }
    }
}

// --- Hàm Preview ---
@Preview(name = "Màn hình Đăng nhập (Dark Mode)", showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenPreview() {
    SmartPickTheme {
        LoginScreen(onNavigateToHome = {}, onNavigateToSignUp = {})
    }
}