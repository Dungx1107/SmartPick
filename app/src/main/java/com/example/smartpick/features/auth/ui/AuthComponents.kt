package com.example.smartpick.features.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.ui.theme.CardLight
import com.example.smartpick.core.ui.theme.DividerColor
import com.example.smartpick.core.ui.theme.LoginBlue

/**
 * Biểu tượng bóng đèn đặc trưng của ứng dụng SmartPick.
 */
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
                contentDescription = stringResource(R.string.bulb_icon),
                modifier = Modifier.size(32.dp),
                tint = LoginBlue
            )
        }
    }
}


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


