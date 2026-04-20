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

@Composable
fun SignUpScreen(
    onCreateAccount: () -> Unit,
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
            onValueChange = { fullName = it },
            placeholder = stringResource(R.string.vd_full_name),
            leadingIcon = Icons.Default.Person
        )

        Spacer(modifier = Modifier.height(16.dp))

        FieldLabel(stringResource(R.string.username))
        StandardTextFieldLight(
            value = username,
            onValueChange = { username = it },
            placeholder = stringResource(R.string.nguyenvana123),
            leadingIcon = Icons.Default.Person
        )

        Spacer(modifier = Modifier.height(16.dp))

        FieldLabel(stringResource(R.string.email))
        StandardTextFieldLight(
            value = email,
            onValueChange = { email = it },
            placeholder = stringResource(R.string.name_domain_com),
            leadingIcon = Icons.Default.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        FieldLabel(stringResource(R.string.phone_number))
        StandardTextFieldLight(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            placeholder = stringResource(R.string.sdt),
            leadingIcon = Icons.Default.Phone
        )

        Spacer(modifier = Modifier.height(16.dp))

        FieldLabel(stringResource(R.string.password))
        PasswordTextFieldLight(
            value = password,
            onValueChange = { password = it },
            passwordVisible = passwordVisible,
            onPasswordToggle = { passwordVisible = !passwordVisible }
        )

        Spacer(modifier = Modifier.height(16.dp))

        FieldLabel(stringResource(R.string.confirm_password))
        PasswordTextFieldLight(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            passwordVisible = confirmPasswordVisible,
            onPasswordToggle = { confirmPasswordVisible = !confirmPasswordVisible }
        )

        Spacer(modifier = Modifier.height(32.dp))

        AuthPrimaryButton(
            text = stringResource(R.string.create_account),
            onClick = {
                // Thêm logic kiểm tra password == confirmPassword trước khi gọi API
                if (password == confirmPassword) {
                    onCreateAccount()
                } else {
                    // Hiển thị thông báo lỗi
                }
            }
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
                color = TextSecondary)
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
        SignUpScreen({}, {}, {}, {})
    }
}
