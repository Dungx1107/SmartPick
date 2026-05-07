package com.example.smartpick.features.auth.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartpick.R
import com.example.smartpick.core.ui.components.AuthPrimaryButton
import com.example.smartpick.core.ui.components.FieldLabel
import com.example.smartpick.core.ui.components.PasswordTextFieldLight
import com.example.smartpick.core.ui.components.SocialAuthButton
import com.example.smartpick.core.ui.components.StandardTextFieldLight
import com.example.smartpick.core.ui.theme.BrightBackground
import com.example.smartpick.core.ui.theme.LoginBlue
import com.example.smartpick.core.ui.theme.TextPrimary
import com.example.smartpick.core.ui.theme.TextSecondary
import com.example.smartpick.core.utils.Constants
import com.example.smartpick.core.utils.Constants.WEB_CLIENT_ID
import com.example.smartpick.core.utils.Validator
import com.example.smartpick.features.auth.data.performGoogleSignIn
import com.example.smartpick.features.auth.viewmodel.AuthState
import com.example.smartpick.features.auth.viewmodel.AuthViewModel

/**
 * STATEFUL COMPOSABLE
 * Kết nối với ViewModel, xử lý Side Effects (Toast, Navigation)
 */
@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onLoginClick: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isGoogleLogin by rememberSaveable { mutableStateOf(false) }

    // Xử lý các thông báo đẩy ra từ Server (Supabase) dựa trên trạng thái authState
    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            val message = (authState as AuthState.Error).message
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        } else if (authState is AuthState.Success) {
            Toast.makeText(
                context,
                context.getString(R.string.DangKiThanhCong),
                Toast.LENGTH_LONG
            ).show()
            if (isGoogleLogin) {
                onNavigateToHome()
            } else {
                onLoginClick()
            }


        }
    }

    // Gọi phần nội dung UI (Stateless)
    SignUpContent(
        isLoading = authState is AuthState.Loading,
        onSignUp = { email, pass, name, user, phone ->
            isGoogleLogin = false // Reset về false khi đăng ký bằng email
            authViewModel.onSignUp(email, pass, name, user, phone)
        },
        onLoginClick = onLoginClick,
        onGoogleClick = {
            isGoogleLogin = true
            performGoogleSignIn(
                context = context,
                coroutineScope = coroutineScope,
                webClientId = WEB_CLIENT_ID,
                onTokenReceived = { token -> authViewModel.signInWithGoogleToken(token) },
                onError = { it.printStackTrace() }
            )
        },
    )
}

/**
 * STATELESS COMPOSABLE (UI CONTENT)
 * Hiển thị giao diện, quản lý trạng thái nhập liệu tạm thời và Validation
 */
@Composable
fun SignUpContent(
    isLoading: Boolean,
    onSignUp: (String, String, String, String, String) -> Unit,
    onLoginClick: () -> Unit,
    onGoogleClick: () -> Unit,
) {
    // Các biến lưu trữ dữ liệu người dùng nhập
    var fullName by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    /* phần biến kiểm tra lỗi nhập của người dùng để hiển thị Inline Error */
    var fullNameError by rememberSaveable { mutableStateOf<String?>(null) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var usernameError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmPasswordError by rememberSaveable { mutableStateOf<String?>(null) }
    var phoneNumberError by rememberSaveable { mutableStateOf<String?>(null) }

    // Hàm xử lý kiểm tra tính hợp lệ trước khi gửi dữ liệu đi
    fun validateAndSubmit() {
        /* Reset toàn bộ lỗi cũ về null */
        fullNameError = null
        emailError = null
        usernameError = null
        passwordError = null
        confirmPasswordError = null
        phoneNumberError = null

        var isValid = true

        // Kiểm tra Họ tên
        if (fullName.isBlank()) {
            fullNameError = Constants.ValidationError.FULL_NAME_EMPTY
            isValid = false
        }

        // Kiểm tra Email
        if (email.isBlank()) {
            emailError = Constants.ValidationError.EMAIL_EMPTY
            isValid = false
        } else if (!Validator.isValidEmail(email)) {
            emailError = Constants.ValidationError.EMAIL_INVALID
            isValid = false
        }

        // Kiểm tra Số điện thoại
        if (!Validator.isValidPhone(phoneNumber)) {
            phoneNumberError = Constants.ValidationError.PHONE_INVALID
            isValid = false
        }

        // Kiểm tra Username
        if (username.isBlank()) {
            usernameError = Constants.ValidationError.USERNAME_EMPTY
            isValid = false
        } else if (!Validator.isValidUsername(username)) {
            usernameError = Constants.ValidationError.USERNAME_INVALID
            isValid = false
        }

        // Kiểm tra Mật khẩu
        if (password.isBlank()) {
            passwordError = Constants.ValidationError.PASSWORD_EMPTY
            isValid = false
        } else if (!Validator.isTestValidPassword(password)) {
            passwordError = Constants.ValidationError.PASSWORD_INVALID
            isValid = false
        } else if (password != confirmPassword) {
            confirmPasswordError = Constants.ValidationError.PASSWORD_MISMATCH
            isValid = false
        }

        if (isValid) {
            onSignUp(email, password, fullName, username, phoneNumber)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BrightBackground)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            BulbIconLight()

            Text(
                text = stringResource(R.string.app_name),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Ô nhập Họ tên
            FieldLabel(stringResource(R.string.full_name))
            StandardTextFieldLight(
                value = fullName,
                onValueChange = { fullName = it; fullNameError = null },
                placeholder = stringResource(R.string.vd_full_name),
                leadingIcon = Icons.Default.Person,
                isError = fullNameError != null,
                errorMessage = fullNameError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ô nhập Username
            FieldLabel(stringResource(R.string.username))
            StandardTextFieldLight(
                value = username,
                onValueChange = { username = it; usernameError = null },
                placeholder = stringResource(R.string.nguyenvana123),
                leadingIcon = Icons.Default.Person,
                isError = usernameError != null,
                errorMessage = usernameError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ô nhập Email
            FieldLabel(stringResource(R.string.email))
            StandardTextFieldLight(
                value = email,
                onValueChange = { email = it; emailError = null },
                placeholder = stringResource(R.string.name_domain_com),
                leadingIcon = Icons.Default.Email,
                isError = emailError != null,
                errorMessage = emailError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ô nhập Số điện thoại
            FieldLabel(stringResource(R.string.phone_number))
            StandardTextFieldLight(
                value = phoneNumber,
                onValueChange = { phoneNumber = it; phoneNumberError = null },
                placeholder = stringResource(R.string.sdt),
                leadingIcon = Icons.Default.Phone,
                isError = phoneNumberError != null,
                errorMessage = phoneNumberError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ô nhập Mật khẩu
            FieldLabel(stringResource(R.string.password))
            PasswordTextFieldLight(
                value = password,
                onValueChange = { password = it; passwordError = null },
                passwordVisible = passwordVisible,
                onPasswordToggle = { passwordVisible = !passwordVisible },
                isError = passwordError != null,
                errorMessage = passwordError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ô Xác nhận mật khẩu
            FieldLabel(stringResource(R.string.confirm_password))
            PasswordTextFieldLight(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; confirmPasswordError = null },
                passwordVisible = confirmPasswordVisible,
                onPasswordToggle = { confirmPasswordVisible = !confirmPasswordVisible },
                isError = confirmPasswordError != null,
                errorMessage = confirmPasswordError
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Nút tạo tài khoản
            AuthPrimaryButton(
                text = stringResource(R.string.create_account),
                onClick = { validateAndSubmit() },
                enabled = !isLoading // Vô hiệu hóa nút khi đang gửi dữ liệu
            )

            Spacer(modifier = Modifier.height(24.dp))
            AuthDivider()
            Spacer(modifier = Modifier.height(24.dp))

            // Nút đăng nhập Google
            SocialAuthButton(
                text = stringResource(R.string.continue_with_google),
                brand = stringResource(R.string.google),
                onClick = onGoogleClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Chuyển sang màn hình Đăng nhập
            Row {
                Text(text = stringResource(R.string.already_have_an_account), color = TextSecondary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.login),
                    color = LoginBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Hiển thị lớp phủ Loading khi isLoading = true
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = LoginBlue)
            }
        }
    }
}

/**
 * PHẦN 3: PREVIEW
 * Chỉ gọi vào SignUpContent để không phụ thuộc vào Hilt/ViewModel
 */
@Preview(showBackground = true, name = "SignUp - Bình thường")
@Composable
fun PreviewSignUpNormal() {
    MaterialTheme {
        SignUpContent(
            isLoading = false,
            onSignUp = { _, _, _, _, _ -> },
            onLoginClick = {},
            onGoogleClick = {},
        )
    }
}

@Preview(showBackground = true, name = "SignUp - Đang xử lý")
@Composable
fun PreviewSignUpLoading() {
    MaterialTheme {
        SignUpContent(
            isLoading = true,
            onSignUp = { _, _, _, _, _ -> },
            onLoginClick = {},
            onGoogleClick = {},
        )
    }
}