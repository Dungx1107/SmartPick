package com.example.smartpick.features.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smartpick.core.theme.CardLight
import com.example.smartpick.core.theme.DividerColor
import com.example.smartpick.core.theme.LoginBlue

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
                contentDescription = "Bulb Icon",
                modifier = Modifier.size(32.dp),
                tint = LoginBlue
            )
        }
    }
}

