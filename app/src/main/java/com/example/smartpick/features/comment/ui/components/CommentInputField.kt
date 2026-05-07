package com.example.smartpick.features.comment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.R
import com.example.smartpick.core.ui.components.ProfileAvatar

@Composable
fun CommentInputField(
    commentText: String,
    onCommentChange: (String) -> Unit,
    onSend: () -> Unit,
    avatarAuthorUrl: String?,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shadowElevation = 12.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.ime) // Tối ưu hơn imePadding() cho Layout
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileAvatar(
                avatarUrl = avatarAuthorUrl,
                size = 48.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            TextField(
                value = commentText,
                onValueChange = onCommentChange,
                placeholder = { Text(stringResource(R.string.VietBinhLuan), fontSize = 14.sp) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(max = 120.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F5F9),
                    unfocusedContainerColor = Color(0xFFF1F5F9),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Nút gửi với hiệu ứng màu sắc
            val sendEnabled = commentText.isNotBlank()
            IconButton(
                onClick = onSend,
                enabled = sendEnabled,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (sendEnabled) Color(0xFF1E3A8A) else Color(0xFFE2E8F0))
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = null,
                    tint = if (sendEnabled) Color.White else Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
