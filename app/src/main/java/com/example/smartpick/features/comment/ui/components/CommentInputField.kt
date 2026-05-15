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
import com.example.smartpick.core.ui.theme.SmartPickTheme
import com.example.smartpick.core.ui.theme.TextMuted

@Composable
fun CommentInputField(
    commentText: String,
    onCommentChange: (String) -> Unit,
    onSend: () -> Unit,
    avatarAuthorUrl: String?,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
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
                placeholder = { 
                    Text(
                        stringResource(R.string.VietBinhLuan), 
                        fontSize = 14.sp,
                        color = TextMuted
                    ) 
                },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(max = 120.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.width(8.dp))

            val sendEnabled = commentText.isNotBlank()
            IconButton(
                onClick = onSend,
                enabled = sendEnabled,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (sendEnabled) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = null,
                    tint = if (sendEnabled) 
                        MaterialTheme.colorScheme.onPrimary 
                    else 
                        TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CommentInputFieldPreview() {
    SmartPickTheme {
        var comment by remember {
            mutableStateOf("Sản phẩm này nhìn ổn đấy!")
        }

        CommentInputField(
            commentText = comment,
            onCommentChange = { comment = it },
            onSend = {},
            avatarAuthorUrl = null
        )
    }
}

@Preview(showBackground = true, name = "Empty Comment")
@Composable
fun CommentInputFieldEmptyPreview() {
    SmartPickTheme {
        CommentInputField(
            commentText = "",
            onCommentChange = {},
            onSend = {},
            avatarAuthorUrl = null
        )
    }
}