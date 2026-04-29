package com.example.smartpick.features.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.theme.*
import com.example.smartpick.core.utils.Validator

@Composable
fun SignUpScreen(
    onSignUp: (String, String, String, String, String) -> Unit,
    onLoginClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    /* phần biến kiểm tra lỗi nhập của người dùng */
    var fullNameError by rememberSaveable { mutableStateOf<String?>(null) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var usernameError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmPasswordError by rememberSaveable { mutableStateOf<String?>(null) }
    var phoneNumberError by rememberSaveable { mutableStateOf<String?>(null) }

    fun validateAndSubmit() {
        /* Reset toàn bộ lỗi cũ về null trước khi kiểm tra */
        fullNameError = null
        emailError = null
        usernameError = null
        passwordError = null
        confirmPasswordError = null

        var isValid = true

        if (fullName.isBlank()) {
            fullNameError = "Họ tên không được để trống"
            isValid = false
        }

        if (email.isBlank()) {
            emailError = "Email không được bỏ trống !!!"
            isValid = false
        } else {
            if (!Validator.isValidEmail(email)) {
                emailError = "Email không đúng định dạng"
                isValid = false
            }
        }

        if (!Validator.isValidPhone(phoneNumber)) {
            phoneNumberError = "Số điện thoại không đúng định dạng"
            isValid = false
        }

        if (username.isBlank()) {
            usernameError = "Username không được bỏ trống !!!"
            isValid = false
        } else {
            if (!Validator.isValidUsername(username)) {
                usernameError = "Username từ 3-20 ký tự, không chứa ký tự đặc biệt"
                isValid = false
            }
        }
        if (password.isBlank()) {
            passwordError = "Mật khẩu không được để trống !!!"
            isValid = false
        } else {
            if (!Validator.isTestValidPassword(password)) {
                passwordError = "Mật khẩu phải có ít nhất 6 ký tự"
                isValid = false
            } else if (password != confirmPassword) {
                confirmPasswordError = "Mật khẩu xác nhận không khớp"
                isValid = false
            }
        }

//         if (!Validator.isStrongPassword(password)) {
//            passwordError = "Mật khẩu phải có ít nhất 8 ký tự, 1 chữ hoa, 1 chữ số , 1 kí tự đặc biệt"
//            isValid = false
//        } else if (password != confirmPassword) {
//            confirmPasswordError = "Mật khẩu xác nhận không khớp"
//            isValid = false
//        }


        // 3. Nếu tất cả đều đúng (isValid vẫn là true) thì mới gọi onSignUp
        if (isValid) {
            onSignUp(email, password, fullName, username, phoneNumber)
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

        FieldLabel(stringResource(R.string.full_name))
        StandardTextFieldLight(
            value = fullName,
            onValueChange = {
                fullName = it
                fullNameError = null
            },
            placeholder = stringResource(R.string.vd_full_name),
            leadingIcon = Icons.Default.Person,
            isError = fullNameError != null,
            errorMessage = fullNameError
        )

        Spacer(modifier = Modifier.height(16.dp))

        FieldLabel(stringResource(R.string.username))
        StandardTextFieldLight(
            value = username,
            onValueChange = {
                username = it
                usernameError = null
            },
            placeholder = stringResource(R.string.nguyenvana123),
            leadingIcon = Icons.Default.Person,
            isError = usernameError != null,
            errorMessage = usernameError
        )

        Spacer(modifier = Modifier.height(16.dp))

        FieldLabel(stringResource(R.string.email))
        StandardTextFieldLight(
            value = email,
            onValueChange = {
                email = it
                emailError = null
            },
            placeholder = stringResource(R.string.name_domain_com),
            leadingIcon = Icons.Default.Email,
            isError = emailError != null,
            errorMessage = emailError
        )

        Spacer(modifier = Modifier.height(16.dp))

        FieldLabel(stringResource(R.string.phone_number))
        StandardTextFieldLight(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
                phoneNumberError = null
            },
            placeholder = stringResource(R.string.sdt),
            leadingIcon = Icons.Default.Phone,
            isError = phoneNumberError != null,
            errorMessage = phoneNumberError
        )

        Spacer(modifier = Modifier.height(16.dp))

        FieldLabel(stringResource(R.string.password))
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

        Spacer(modifier = Modifier.height(16.dp))

        FieldLabel(stringResource(R.string.confirm_password))
        PasswordTextFieldLight(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = null
            },
            passwordVisible = confirmPasswordVisible,
            onPasswordToggle = { confirmPasswordVisible = !confirmPasswordVisible },
            isError = confirmPasswordError != null,
            errorMessage = confirmPasswordError
        )

        Spacer(modifier = Modifier.height(32.dp))

        AuthPrimaryButton(
            text = stringResource(R.string.create_account),
            onClick = { validateAndSubmit() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuthDivider()

        Spacer(modifier = Modifier.height(24.dp))

        SocialAuthButton(
            text = stringResource(R.string.continue_with_google),
            brand = stringResource(R.string.google),
            onClick = onGoogleClick
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row {
            Text(
                text = stringResource(R.string.already_have_an_account),
                color = TextSecondary
            )
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

}

@Preview(showBackground = true, name = "SignUp Preview")
@Composable
fun PreviewSignUp() {
    MaterialTheme {
        SignUpScreen(
            onSignUp = { email, pass, name, user, phone -> },
            onLoginClick = {},
            onGoogleClick = {},
            onFacebookClick = {},
        )
    }
}
