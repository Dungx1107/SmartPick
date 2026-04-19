package com.example.smartpick.features.auth.ui

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.R
import com.example.smartpick.core.theme.BrightBackground
import com.example.smartpick.core.theme.DividerColor
import com.example.smartpick.core.theme.LoginBlue
import com.example.smartpick.core.theme.LoginBlueGradientEnd
import com.example.smartpick.core.theme.TextPrimary
import com.example.smartpick.core.theme.TextSecondary
import com.example.smartpick.core.utils.Constants.WEB_CLIENT_ID
import com.example.smartpick.features.auth.data.performGoogleSignIn
import com.example.smartpick.features.auth.viewmodel.AuthState
import com.example.smartpick.features.auth.viewmodel.AuthViewModel


// 1. Stateful Composable (Được gọi từ AppNavigation)
@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel() // Hilt chỉ chạy ở đây
) {
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                Toast.makeText(context,
                    context.getString(R.string.dangNhapThanhCong), Toast.LENGTH_SHORT).show()
                onNavigateToHome()
            }

            is AuthState.Error -> {
                val errorMessage = (authState as AuthState.Error).message
                Toast.makeText(context,
                    context.getString(R.string.loi, errorMessage), Toast.LENGTH_LONG).show()
            }

            else -> {}
        }
    }

    // Truyền event xuống Stateless Composable
    LoginContent(
        onNavigateToHome = onNavigateToHome,
        onNavigateToSignUp = onNavigateToSignUp,
        onGoogleSignInClick = {
            performGoogleSignIn(
                context = context,
                coroutineScope = coroutineScope,
                webClientId = WEB_CLIENT_ID,
                onTokenReceived = { token -> authViewModel.signInWithGoogleToken(token) },
                onError = { it.printStackTrace() }
            )
        }
    )
}

// 2. Stateless Composable (Chỉ vẽ UI, không phụ thuộc thư viện ngoài)
@Composable
fun LoginContent(
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onGoogleSignInClick: () -> Unit // Bắt event click
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

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

        FieldLabel(stringResource(R.string.email))
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

        AuthPrimaryButton(
            text = stringResource(R.string.login),
            showArrow = true,
            onClick = onNavigateToHome
        )

        Spacer(modifier = Modifier.height(48.dp))

        AuthDivider()
        Spacer(modifier = Modifier.height(32.dp))

        SocialAuthButton(
            text = stringResource(R.string.continue_with_google),
            brand = stringResource(R.string.google),
            onClick = onGoogleSignInClick // Thực thi hàm được truyền vào
        )

        Spacer(modifier = Modifier.height(16.dp))

        SocialAuthButton(
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


// 3. Cập nhật hàm Preview
@Preview(
    name = "Login Screen - White Blue Theme",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun LoginScreenWhiteBluePreview() {
    LoginContent(
        onNavigateToHome = {},
        onNavigateToSignUp = {},
        onGoogleSignInClick = {}
    )
}