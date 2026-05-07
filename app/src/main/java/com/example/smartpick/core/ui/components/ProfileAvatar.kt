package com.example.smartpick.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.smartpick.R
import com.example.smartpick.core.ui.theme.SmartPickColor
import com.example.smartpick.core.ui.theme.White

@Composable
fun ProfileAvatar(
    avatarUrl: String?,
    selectedImage: Any? = null,
    size: Dp = 40.dp // Mặc định size nhỏ cho Feed/Comment
) {
    val hasImage = selectedImage != null || !avatarUrl.isNullOrEmpty()

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(if (hasImage) Color.White else Color(0xFFF1F5F9))
            .border(1.dp, SmartPickColor.copy(alpha = 0.1f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        val imageModel = selectedImage ?: avatarUrl

        if (imageModel != null) {
            AsyncImage(
                model = imageModel,
                contentDescription = stringResource(R.string.avatar),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(size * 0.6f),
                tint = Color(0xFF94A3B8)
            )
        }
    }
}
