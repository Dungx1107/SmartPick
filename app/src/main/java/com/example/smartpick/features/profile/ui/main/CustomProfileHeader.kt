package com.example.smartpick.features.profile.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.theme.SmartPickTheme

@Composable
fun CustomProfileHeader(
    user: User?,
    onEditProfile: () -> Unit,
    onSettingsClick: () -> Unit,
    onSellerDashboardClick: () -> Unit,
    onHistoryClick: () -> Unit, // Khớp nối lại tham số điều hướng lịch sử mua hàng
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 32.dp,
                bottom = 8.dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier.padding(bottom = 4.dp),
                contentAlignment = Alignment.BottomEnd // Đặt icon nằm ở góc dưới bên phải
            ) {
                AsyncImage(
                    model = user?.avatarUrl ?: "https://via.placeholder.com/150",
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )

                FilledIconButton(
                    onClick = onEditProfile,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(2.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Chỉnh sửa hồ sơ",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Khối bên phải: Hiển thị thông tin và nút Đơn hàng lớn
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = user?.fullName ?: "Người dùng SmartPick",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Cụm nút chức năng nhỏ gọn (Gian hàng & Cài đặt)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onSellerDashboardClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = "Gian hàng",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        IconButton(
                            onClick = onSettingsClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Cài đặt",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "@${user?.username ?: "smartpick_user"}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Biến đổi nút cũ thành nút Lịch sử mua hàng (Đơn hàng của bạn) dạng Container phẳng rộng rãi
                Button(
                    onClick = onHistoryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = PaddingValues(vertical = 0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ListAlt,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Đơn hàng của bạn",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Custom Profile Header"
)
@Composable
private fun CustomProfileHeaderPreview() {
    SmartPickTheme {
        CustomProfileHeader(
            user = User(
                id = "1",
                email = "dung@example.com",
                username = "dungx1107",
                fullName = "Nguyễn Xuân Dũng",
                avatarUrl = null
            ),
            onEditProfile = {},
            onSettingsClick = {},
            onSellerDashboardClick = {},
            onHistoryClick = {}, // Đồng bộ tham số cho khối Preview
            modifier = Modifier
                .statusBarsPadding()
                .background(MaterialTheme.colorScheme.background)
        )
    }
}