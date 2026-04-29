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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.smartpick.core.theme.*
import com.example.smartpick.core.utils.Validator
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
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current

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

            onLoginClick()

        }
    }

    // Gọi phần nội dung UI (Stateless)
    SignUpContent(
        isLoading = authState is AuthState.Loading,
        onSignUp = { email, pass, name, user, phone ->
            authViewModel.onSignUp(email, pass, name, user, phone)
        },
        onLoginClick = onLoginClick,
        onGoogleClick = onGoogleClick,
        onFacebookClick = onFacebookClick
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
    onFacebookClick: () -> Unit
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
        /* Reset toàn bộ lỗi cũ về null trước khi kiểm tra mới */
        fullNameError = null
        emailError = null
        usernameError = null
        passwordError = null
        confirmPasswordError = null
        phoneNumberError = null

        var isValid = true

        if (fullName.isBlank()) {
            fullNameError = "Họ tên không được để trống"
            isValid = false
        }

        if (email.isBlank()) {
            emailError = "Email không được bỏ trống !!!"
            isValid = false
        } else if (!Validator.isValidEmail(email)) {
            emailError = "Email không đúng định dạng"
            isValid = false
        }

        if (!Validator.isValidPhone(phoneNumber)) {
            phoneNumberError = "Số điện thoại không đúng định dạng"
            isValid = false
        }

        if (username.isBlank()) {
            usernameError = "Username không được bỏ trống !!!"
            isValid = false
        } else if (!Validator.isValidUsername(username)) {
            usernameError = "Username từ 3-20 ký tự, không chứa ký tự đặc biệt"
            isValid = false
        }

        if (password.isBlank()) {
            passwordError = "Mật khẩu không được để trống !!!"
            isValid = false
        } else if (!Validator.isTestValidPassword(password)) {
            passwordError = "Mật khẩu phải có ít nhất 6 ký tự"
            isValid = false
        } else if (password != confirmPassword) {
            confirmPasswordError = "Mật khẩu xác nhận không khớp"
            isValid = false
        }

        // Nếu tất cả các trường đều hợp lệ, thực thi callback onSignUp
        if (isValid) {
            onSignUp(
                email.trim(),
                password.trim(),
                fullName.trim(),
                username.trim(),
                phoneNumber.trim()
            )
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
                modifier = Modifier
                    .fillMaxSize()
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
            onFacebookClick = {}
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
            onFacebookClick = {}
        )
    }
}