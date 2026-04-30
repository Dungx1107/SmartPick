package com.example.smartpick.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.theme.DividerColor

/**
 * Đường kẻ phân cách với văn bản ở giữa.
 */
@Composable
fun AuthDivider(text: String = stringResource(R.string.or_connect_with)) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Divider(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            color = DividerColor
        )
        Text(
            text = text.uppercase(),
            fontSize = 12.sp,
            color = DividerColor
        )
        Divider(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            color = DividerColor
        )
    }
}
