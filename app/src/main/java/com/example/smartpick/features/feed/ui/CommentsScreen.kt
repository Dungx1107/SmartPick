package com.example.smartpick.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.compareTo

// Data model cho Bình luận
data class Comment(
    val id: String,
    val authorName: String,
    val timeAgo: String,
    val content: String,
    val likesCount: Int,
    val isLiked: Boolean = false,
    val isAuthor: Boolean = false // Để highlight nếu người bình luận là chủ bài viết
)

@Composable
fun CommentsScreen(
    paddingValues: PaddingValues, // Nhận padding từ Scaffold cha (MainTopBar & MainBottomBar)
    modifier: Modifier = Modifier
) {
    var commentText by remember { mutableStateOf("") }

    // Dữ liệu mẫu
    val comments = listOf(
        Comment(
            "1",
            "Nguyễn Minh Quang",
            "1 giờ trước",
            "Bàn phím này gõ êm không bác? Đang tính xúc một em về code đêm.",
            12
        ),
        Comment(
            "2",
            "Lê Hải An",
            "45 phút trước",
            "Gõ cực êm nha bác, build nhôm đầm tay lắm. Nên mua switch red nhen! 🔥",
            5,
            isAuthor = true
        ),
        Comment(
            "3",
            "Trần Thu Hà",
            "30 phút trước",
            "Setup đẹp quá, xin link mua cái giá đỡ màn hình với ạ.",
            2
        ),
        Comment(
            "4",
            "Hoàng Nam",
            "15 phút trước",
            "Mình cũng đang dùng combo y hệt, công nhận làm việc năng suất hẳn.",
            0
        )
    )

    // Sử dụng Column thay vì Scaffold
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(paddingValues) // Áp dụng padding để không lẹm Top/Bottom Bar
    ) {
        // LazyColumn chiếm phần không gian còn lại (weight = 1f)
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(comments) { comment ->
                CommentItem(comment)
            }
        }

        // Khung nhập bình luận sẽ tự động nằm ở dưới cùng (ngay trên MainBottomBar)
        CommentInputField(
            commentText = commentText,
            onCommentChange = { commentText = it },
            onSend = {
                /* TODO: Xử lý gửi bình luận lên Supabase */
                commentText = "" // Clear text sau khi gửi
            }
        )
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Avatar",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Nội dung bình luận
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.authorName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1E3A8A)
                )

                // Badge "Tác giả"
                if (comment.isAuthor) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFD6E4FF), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "Tác giả",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))
                Text(text = comment.timeAgo, fontSize = 12.sp, color = Color(0xFF94A3B8))
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = comment.content,
                fontSize = 14.sp,
                color = Color(0xFF334155),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nút Thích & Trả lời
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Trả lời",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF64748B),
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }
        }

        // Cột Thả tim
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = "Like",
                tint = Color(0xFF94A3B8),
                modifier = Modifier
                    .size(16.dp)
                    .clickable { /* TODO */ }
            )
            if (comment.likesCount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comment.likesCount.toString(),
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentInputField(
    commentText: String,
    onCommentChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Cần WindowInsets padding (ime) để khung chat nổi lên khi bật bàn phím
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding() // Đẩy UI lên khi bàn phím xuất hiện
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar user hiện tại
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "My Avatar",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Input field
            TextField(
                value = commentText,
                onValueChange = onCommentChange,
                placeholder = {
                    Text(
                        "Thêm bình luận...",
                        color = Color(0xFF94A3B8),
                        fontSize = 14.sp
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F5F9),
                    unfocusedContainerColor = Color(0xFFF1F5F9),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 40.dp, max = 100.dp), // Tự động giãn chiều cao tối đa
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Nút Send
            IconButton(
                onClick = onSend,
                enabled = commentText.isNotBlank(),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (commentText.isNotBlank()) Color(0xFF1E3A8A) else Color(
                            0xFFE2E8F0
                        )
                    )
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (commentText.isNotBlank()) Color.White else Color(0xFF94A3B8),
                    modifier = Modifier
                        .size(18.dp)
                        .padding(start = 2.dp) // Dịch icon send ra giữa một chút
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommentsScreenPreview() {
    CommentsScreen(
        paddingValues = PaddingValues(0.dp),
        modifier = Modifier
    )
}