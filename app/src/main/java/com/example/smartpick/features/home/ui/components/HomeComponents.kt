package com.example.smartpick.features.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smartpick.R
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.ui.theme.AccentBlue
import com.example.smartpick.core.ui.theme.SmartPickColor
import com.example.smartpick.core.ui.theme.TextMuted

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onMicClick: () -> Unit,
    totalCartCount: Int,         // Thêm tham số nhận số lượng hàng trong giỏ
    onCartClick: () -> Unit,      // Thêm sự kiện click xe đẩy
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp) // Khoảng cách giữa ô tìm kiếm và xe đẩy
    ) {
        // Khối ô tìm kiếm bên trái (Tự động co dãn chiếm không gian chính)
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, null, tint = TextMuted, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        stringResource(R.string.TimKiemSanPham),
                        color = TextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            IconButton(
                onClick = onMicClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Mic,
                    contentDescription = stringResource(R.string.TimKiemBangGiongNoi),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Khối Xe đẩy hàng (Ghim cố định ở góc bên phải)
        IconButton(
            onClick = onCartClick,
            modifier = Modifier.size(40.dp)
        ) {
            if (totalCartCount > 0) {
                // Hiển thị chấm đỏ số lượng nếu giỏ hàng có đồ
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ) {
                            Text(totalCartCount.toString(), fontSize = 10.sp)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Giỏ hàng",
                        tint = SmartPickColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
            } else {
                // Chỉ hiển thị icon trống nếu không có hàng
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Giỏ hàng",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
fun ProductGridCard(
    product: Product,
    onProductClick: (Product) -> Unit, // Nhận thực thể Product đầy đủ
    onAddToCart: (Product) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp) // Thêm padding nhẹ để các thẻ không dính sát nhau khi lên Grid
            .clickable { onProductClick(product) }, // THỰC THI: Truyền ngược đối tượng ra màn hình cha để chuyển cảnh
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                AsyncImage(
                    model = product.imageUrls.firstOrNull(),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = { onAddToCart(product) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(30.dp)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = "Add to Cart",
                        tint = AccentBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                    modifier = Modifier.height(36.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val priceFormatted = String.format("%,.0f đ", product.price).replace(",", ".")
                    Text(
                        text = "$priceFormatted",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )

                    Text(
                        text = "Đã bán ${if(product.soldCount > 1000) "${product.soldCount/1000}k" else product.soldCount}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}