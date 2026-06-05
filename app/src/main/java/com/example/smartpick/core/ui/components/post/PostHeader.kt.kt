package com.example.smartpick.core.ui.components.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smartpick.R
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.theme.TextMuted

@Composable
fun PostHeader(
    user: User,
    createdAt: String,
    currentUserId: String? = null,
    postOwnerId: String = "",
    modifier: Modifier = Modifier,
    isShared: Boolean = false,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onMoreClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            if (isShared) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        ) { append(user.fullName ?: stringResource(R.string.NguoiDung)) }
                        append(stringResource(R.string.daChiaSeBaiViet))
                    },
                    fontSize = 15.sp, color = TextMuted
                )
            } else {
                Text(
                    text = user.fullName ?: stringResource(R.string.smartpick_user),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = createdAt.split(stringResource(R.string.t)).firstOrNull() ?: "",
                fontSize = 12.sp,
                color = TextMuted
            )
        }

        // CHỈ HIỆN 3 CHẤM (SỬA/XÓA) NẾU LÀ BÀI CỦA MÌNH
        if (currentUserId == postOwnerId && currentUserId != null) {
            Box {
                IconButton(onClick = { expanded = true }) { Icon(Icons.Default.MoreVert, null, tint = TextMuted) }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, containerColor = Color.White) {
                    DropdownMenuItem(
                        text = { Text("Chỉnh sửa bài viết") },
                        onClick = { expanded = false; onEditClick() },
                        leadingIcon = { Icon(Icons.Outlined.Edit, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Xóa bài viết", color = MaterialTheme.colorScheme.error) },
                        onClick = { expanded = false; onDeleteClick() },
                        leadingIcon = { Icon(Icons.Outlined.Delete, null, tint = MaterialTheme.colorScheme.error) }
                    )
                }
            }
        } else {
            // Hiện nút 3 chấm mặc định cho bài người khác
            IconButton(onClick = onMoreClick) { Icon(Icons.Default.MoreVert, null, tint = TextMuted) }
        }
    }
}