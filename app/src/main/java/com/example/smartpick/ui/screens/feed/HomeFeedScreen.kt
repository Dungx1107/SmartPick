package com.example.smartpick.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.ui.screens.profile.EditProfileScreen

// Data class mô phỏng dữ liệu bài đăng
data class Post(
    val id: String,
    val authorName: String,
    val authorRole: String,
    val timeAgo: String,
    val content: String,
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false
)

@Composable
fun HomeFeedScreen(
    paddingValues: PaddingValues, // Nhận padding từ Scaffold cha chứa TopBar/BottomBar
    modifier: Modifier = Modifier
) {
    // Dữ liệu mẫu (Dummy Data)
    val posts = listOf(
        Post("1", "Lê Hải An", "Tech Reviewer", "2 giờ trước", "Góc setup làm việc tối giản với chiếc bàn nâng hạ và màn hình Ultrawide. Rất recommend anh em dùng thử giá đỡ màn hình của Human Motion nhé, siêu chắc chắn! 🖥️✨", 124, 18),
        Post("2", "Trần Minh", "DevOps Engineer", "5 giờ trước", "Vừa sắm em bàn phím cơ Keychron Q1 Pro. Cảm giác gõ phím quá đã, build nhôm nguyên khối đầm tay. Anh em nào hay code đêm thì nên thử switch red cho êm. ⌨️🔥", 89, 5),
        Post("3", "Nguyễn Ngọc", "UI/UX Designer", "1 ngày trước", "Bộ sưu tập ly gốm sứ mới về cho góc pha cafe tại nhà. Thiết kế minimalism cực hợp với phong cách Japandi. ☕🌿", 256, 42)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(paddingValues), // Áp dụng padding để không lẹm Top/Bottom Bar
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Phần header gợi ý các tag/danh mục ở trên cùng (Tùy chọn)
        item {
            CategoryFilterRow()
        }

        // Danh sách các bài đăng
        items(posts) { post ->
            PostCard(post = post)
        }
    }
}

@Composable
fun CategoryFilterRow() {
    val categories = listOf("Dành cho bạn", "Đang theo dõi", "Setup & Đồ công nghệ", "Nhà cửa", "Lifestyle")
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) Color(0xFF1E3A8A) else Color.White,
                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)) else null,
                modifier = Modifier.clickable { selectedCategory = category }
            ) {
                Text(
                    text = category,
                    color = if (isSelected) Color.White else Color(0xFF64748B),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun PostCard(post: Post) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Avatar, Name, Time, More Options
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar Placeholder
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Avatar", tint = Color.Gray, modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = post.authorName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E3A8A))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = post.authorRole, fontSize = 12.sp, color = Color(0xFF64748B))
                        Text(text = " • ${post.timeAgo}", fontSize = 12.sp, color = Color(0xFF94A3B8))
                    }
                }

                IconButton(onClick = { /* TODO: Show options menu */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = Color(0xFF64748B))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content Text
            Text(
                text = post.content,
                fontSize = 14.sp,
                color = Color(0xFF334155),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Image Placeholder (Sử dụng AsyncImage của Coil ở đây khi có data thật)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Image, contentDescription = "Post Image", tint = Color(0xFFCBD5E1), modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer: Actions (Like, Comment, Share, Bookmark)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Like Button
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
                        Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Like", tint = Color(0xFF64748B), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${post.likesCount}", fontSize = 13.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                    }

                    // Comment Button
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
                        Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Comment", tint = Color(0xFF64748B), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${post.commentsCount}", fontSize = 13.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                    }

                    // Share Button
                    Icon(Icons.Outlined.Share, contentDescription = "Share", tint = Color(0xFF64748B), modifier = Modifier.size(20.dp).clickable { })
                }

                // Bookmark Button
                Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Save", tint = Color(0xFF64748B), modifier = Modifier.size(24.dp).clickable { })
            }
        }
    }
}
