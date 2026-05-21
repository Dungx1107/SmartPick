package com.example.smartpick.features.auth.ui

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.R
import com.example.smartpick.core.ui.components.AuthPrimaryButton
import com.example.smartpick.core.ui.components.FieldLabel
import com.example.smartpick.core.ui.components.FullScreenLoadingOverlay
import com.example.smartpick.core.ui.components.PasswordTextFieldLight
import com.example.smartpick.core.ui.components.SocialAuthButton
import com.example.smartpick.core.ui.components.StandardTextFieldLight
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.core.ui.theme.TextMuted
import com.example.smartpick.core.utils.Constants.PROVIDER_GOOGLE
import com.example.smartpick.core.utils.Constants.WEB_CLIENT_ID
import com.example.smartpick.features.auth.data.performGoogleSignIn
import com.example.smartpick.features.auth.viewmodel.AuthState
import com.example.smartpick.features.auth.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(authState, currentUser) {
        if (currentUser != null) {
            onNavigateToHome()
            return@LaunchedEffect
        }
        when (authState) {
            is AuthState.Success -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.dangNhapThanhCong), Toast.LENGTH_SHORT
                ).show()
                onNavigateToHome()
            }
            is AuthState.Error -> {
                val errorMessage = (authState as AuthState.Error).message
                Toast.makeText(
                    context,
                    context.getString(R.string.loi, errorMessage), Toast.LENGTH_LONG
                ).show()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LoginContent(
            onSignIn = { email, password -> authViewModel.signInManual(email, password) },
            onNavigateToSignUp = onNavigateToSignUp,
            isLoading = authState is AuthState.Loading,
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

        if (authState is AuthState.Loading) {
            FullScreenLoadingOverlay()
        }
    }
}

@Composable
fun LoginContent(
    onSignIn: (String, String) -> Unit,
    onNavigateToSignUp: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    isLoading: Boolean,
) {
    // Sử dụng rememberSaveable để giữ thông tin nhập liệu khi xoay màn hình
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }

    fun validateAndSubmit() {
        emailError = null
        passwordError = null
        var isValid = true

        if (email.isBlank()) {
            emailError = "Vui lòng nhập email"
            isValid = false
        }
        if (password.isBlank()) {
            passwordError = "Vui lòng nhập mật khẩu"
            isValid = false
        }

        if (isValid) {
            onSignIn(email, password)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = stringResource(R.string.welcome_back),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = stringResource(R.string.continue_your_journey_of_curated_discovery),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        FieldLabel(stringResource(R.string.email))
        StandardTextFieldLight(
            value = email,
            onValueChange = {
                email = it
                emailError = null
            },
            placeholder = stringResource(R.string.name_domain_com),
            leadingIcon = Icons.Filled.Email,
            isError = emailError != null,
            errorMessage = emailError
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.password), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(
                stringResource(R.string.forgot_password),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.clickable { }
            )
        }
        PasswordTextFieldLight(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            passwordVisible = passwordVisible,
            onPasswordToggle = { passwordVisible = !passwordVisible },
            isError = passwordError != null,
            errorMessage = passwordError
        )

        Spacer(modifier = Modifier.height(32.dp))

        AuthPrimaryButton(
            text = stringResource(R.string.login),
            showArrow = !isLoading,
            onClick = { validateAndSubmit() },
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(48.dp))

        AuthDivider()
        Spacer(modifier = Modifier.height(32.dp))

        SocialAuthButton(
            text = stringResource(R.string.continue_with_google),
            brand = PROVIDER_GOOGLE,
            onClick = onGoogleSignInClick,
            loading = isLoading,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.don_t_have_an_account),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.sign_up_now),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onNavigateToSignUp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
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
    SmartPickTheme {
        LoginContent(
            onNavigateToSignUp = {},
            onGoogleSignInClick = {},
            onSignIn = { _, _ -> },
            isLoading = false
        )
    }
}