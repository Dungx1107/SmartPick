package com.example.smartpick.features.auth.ui

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.utils.Constants.WEB_CLIENT_ID
import com.example.smartpick.features.auth.data.performGoogleSignIn
import androidx.hilt.navigation.compose.hiltViewModel

// --- Màu chủ đạo trắng + xanh dương ---
val BrightBackground = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF1A73E8)
val TextSecondary = Color(0xFF4A4A4A)
val DividerColor = Color(0xFFB0B0B0)
val LoginBlue = Color(0xFF1A73E8)
val LoginBlueGradientEnd = Color(0xFF0D47A1)
val SocialButtonLight = Color(0xFFF5F9FF)
val CardLight = Color(0xFFE3F2FD)

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                onNavigateToHome()
            }

            is AuthState.Error -> {
                val errorMessage = (authState as AuthState.Error).message
                Toast.makeText(context, "Lỗi: $errorMessage", Toast.LENGTH_LONG).show()
            }

            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrightBackground)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        BulbIconLight()

        Text(
            text = stringResource(R.string.app_name),
            fontSize = 20.sp,
            fontWeight = FontWeight.W600,
            color = TextPrimary,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = stringResource(R.string.welcome_back),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = stringResource(R.string.continue_your_journey_of_curated_discovery),
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        FieldLabel("Email")
        StandardTextFieldLight(
            value = email,
            onValueChange = { email = it },
            placeholder = stringResource(R.string.name_domain_com),
            leadingIcon = Icons.Filled.Email
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.password), fontSize = 14.sp, color = TextSecondary)
            Text(
                stringResource(R.string.forgot_password),
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.clickable { }
            )
        }
        PasswordTextFieldLight(
            value = password,
            onValueChange = { password = it },
            passwordVisible = passwordVisible,
            onPasswordToggle = { passwordVisible = !passwordVisible }
        )

        Spacer(modifier = Modifier.height(32.dp))

        LoginButtonLight(onClick = onNavigateToHome)

        Spacer(modifier = Modifier.height(48.dp))

        OrConnectWithDividerLight()
        Spacer(modifier = Modifier.height(32.dp))

        SocialButtonLight(
            text = stringResource(R.string.continue_with_google),
            brand = stringResource(R.string.google),
            onClick = {
                performGoogleSignIn(
                    context = context,
                    coroutineScope = coroutineScope,
                    webClientId = WEB_CLIENT_ID,
                    onTokenReceived = { token -> authViewModel.signInWithGoogleToken(token) },
                    onError = { it.printStackTrace() }
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SocialButtonLight(
            text = stringResource(R.string.continue_with_facebook),
            brand = stringResource(R.string.facebook),
            onClick = { }
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.don_t_have_an_account),
                fontSize = 14.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.sign_up_now),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.clickable(onClick = onNavigateToSignUp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// --- Các Composable phụ dùng màu sáng + xanh ---
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
fun StandardTextFieldLight(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .background(SocialButtonLight, RoundedCornerShape(12.dp)),
        textStyle = LocalTextStyle.current.copy(color = TextPrimary),
        leadingIcon = { Icon(leadingIcon, null, tint = TextSecondary) },
        placeholder = { Text(placeholder, color = TextSecondary.copy(alpha = 0.5f)) },
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
fun PasswordTextFieldLight(
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
            .background(SocialButtonLight, RoundedCornerShape(12.dp)),
        textStyle = LocalTextStyle.current.copy(color = TextPrimary),
        leadingIcon = { Icon(Icons.Filled.Lock, null, tint = TextSecondary) },
        trailingIcon = {
            val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = onPasswordToggle) { Icon(icon, null, tint = TextSecondary) }
        },
        placeholder = { Text("••••••••", color = TextSecondary.copy(alpha = 0.5f)) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        shape = RoundedCornerShape(12.dp),

        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = TextPrimary,
        ),
        singleLine = true
    )
}

@Composable
fun LoginButtonLight(onClick: () -> Unit) {
    val gradientBrush = Brush.linearGradient(listOf(LoginBlue, LoginBlueGradientEnd))
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onClick)
            .border(1.dp, DividerColor, RoundedCornerShape(12.dp)),
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
                stringResource(R.string.login),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(Icons.Filled.ArrowForward, null, tint = Color.White)
        }
    }
}

@Composable
fun OrConnectWithDividerLight() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Divider(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp), color = DividerColor
        )
        Text(stringResource(R.string.or_connect_with), fontSize = 12.sp, color = DividerColor)
        Divider(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp), color = DividerColor
        )
    }
}

@Composable
fun SocialButtonLight(
    text: String,
    brand: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onClick)
            .background(SocialButtonLight, RoundedCornerShape(12.dp))
            .border(1.dp, DividerColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (brand == "Google") {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google_logo), // icon vector Google
                    contentDescription = stringResource(R.string.google_logo),
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified// giữ màu gốc của logo
                )
            } else if (brand == "Facebook") {
                Icon(
                    painter = painterResource(id = R.drawable.ic_facebook_logo), // icon vector Facebook
                    contentDescription = stringResource(R.string.facebook_logo),
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

@Preview(
    name = "Login Screen - White Blue Theme",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun LoginScreenWhiteBluePreview() {
    LoginScreen(onNavigateToHome = {}, onNavigateToSignUp = {})

}