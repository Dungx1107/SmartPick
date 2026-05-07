package com.example.smartpick.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.ui.theme.SocialButtonLightColor
import com.example.smartpick.core.ui.theme.TextPrimary
import com.example.smartpick.core.ui.theme.TextSecondary

/**
 * Nhãn tiêu đề phía trên các ô nhập liệu.
 */
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

/**
 * Ô nhập liệu tiêu chuẩn với icon dẫn đầu.
 */
@Composable
fun StandardTextFieldLight(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .background(SocialButtonLightColor, RoundedCornerShape(12.dp)),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = TextPrimary
        ),
        value = value,
        onValueChange = onValueChange,
        textStyle = LocalTextStyle.current.copy(color = TextPrimary),
        leadingIcon = { Icon(leadingIcon, null, tint = TextSecondary) },
        placeholder = { Text(placeholder, color = TextSecondary.copy(alpha = 0.5f)) },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        isError = isError,
    )

    if (isError && errorMessage != null) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error, // Màu đỏ mặc định của hệ thống
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}

/**
 * Ô nhập mật khẩu có hỗ trợ ẩn/hiện nội dung.
 */
@Composable
fun PasswordTextFieldLight(
    value: String,
    onValueChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordToggle: () -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .background(SocialButtonLightColor, RoundedCornerShape(12.dp)),
        textStyle = LocalTextStyle.current.copy(color = TextPrimary),
        leadingIcon = { Icon(Icons.Filled.Lock, null, tint = TextSecondary) },
        trailingIcon = {
            val icon =
                if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = onPasswordToggle) {
                Icon(
                    icon,
                    null,
                    tint = TextSecondary
                )
            }
        },
        placeholder = {
            Text(
                stringResource(R.string.pass_hidden),
                color = TextSecondary.copy(alpha = 0.5f)
            )
        },
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = TextPrimary,
        ),
        singleLine = true,
        isError = isError,
    )

    if (isError && errorMessage != null) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error, // Màu đỏ mặc định của hệ thống
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}


/**
 * Preview
 */

@Preview(showBackground = true)
@Composable
fun PreviewFieldLabel() {
    FieldLabel(text = "Email")
}

@Preview(showBackground = true)
@Composable
fun PreviewStandardTextFieldLight() {
    Column(modifier = Modifier.padding(16.dp)) {

        // Normal
        StandardTextFieldLight(
            value = "nguyenvana@gmail.com",
            onValueChange = {},
            placeholder = "Nhập email",
            leadingIcon = Icons.Default.Person
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Error state
        StandardTextFieldLight(
            value = "abc",
            onValueChange = {},
            placeholder = "Nhập email",
            leadingIcon = Icons.Default.Person,
            isError = true,
            errorMessage = "Email không hợp lệ"
        )
    }
}

