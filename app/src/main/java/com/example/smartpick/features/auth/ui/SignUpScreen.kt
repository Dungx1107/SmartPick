package com.example.smartpick.features.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
    var fullName by remember { mutableStateOf("") }
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
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = stringResource(R.string.curating_excellence_for_you),
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        FieldLabel(stringResource(R.string.full_name))
        StandardTextFieldLight(
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = stringResource(R.string.enter_your_name),
            leadingIcon = Icons.Default.Person
        )

        Spacer(modifier = Modifier.height(20.dp))

        FieldLabel(stringResource(R.string.email))
        StandardTextFieldLight(
            value = email,
            onValueChange = { email = it },
            placeholder = stringResource(R.string.name_domain_com),
            leadingIcon = Icons.Default.Email
        )

        Spacer(modifier = Modifier.height(20.dp))

        FieldLabel(stringResource(R.string.password))
        PasswordTextFieldLight(
            value = password,
            onValueChange = { password = it },
            passwordVisible = passwordVisible,
            onPasswordToggle = { passwordVisible = !passwordVisible }
        )

        Spacer(modifier = Modifier.height(32.dp))

        AuthPrimaryButton(
            text = stringResource(R.string.create_account),
            onClick = onCreateAccount
        )

        Spacer(modifier = Modifier.height(32.dp))

        AuthDivider()

        Spacer(modifier = Modifier.height(24.dp))

        SocialAuthButton(
            text = stringResource(R.string.continue_with_google),
            brand = stringResource(R.string.google),
            onClick = onGoogleClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        SocialAuthButton(
            text = stringResource(R.string.continue_with_facebook),
            brand = stringResource(R.string.facebook),
            onClick = onFacebookClick
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row {
            Text(
                text = stringResource(R.string.already_have_an_account),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.log_in),
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
