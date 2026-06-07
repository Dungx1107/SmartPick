package com.example.smartpick.features.profile.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smartpick.core.model.Post
import com.example.smartpick.core.model.Product
import com.example.smartpick.core.model.ReactionType
import com.example.smartpick.core.model.User
import com.example.smartpick.core.ui.components.post.PostItem

@Composable
fun ProfileContent(
    user: User?,
    posts: List<Triple<Post, User, Product?>>,
    likedPosts: List<Triple<Post, User, Product?>>,
    sellerProducts: List<Product>,
    isLoading: Boolean,
    onEditProfile: () -> Unit,
    onSettingsClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onDeletePost: (String) -> Unit,
    onReactionClick: (String, ReactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    if (isLoading && posts.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
        ) {
            // 1. Nhúng Header cá nhân
            item {
                CustomProfileHeader(
                    user = user,
                    onEditProfile = onEditProfile,
                    onSettingsClick = onSettingsClick
                )
                HorizontalDivider(
                    modifier = Modifier.padding(top = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )
            }

            // 2. Nhúng Thanh điều hướng Tab ngang TikTok
            item {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color.Transparent
                ) {
                    Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }) {
                        Icon(
                            Icons.Default.GridOn,
                            contentDescription = "Bài đăng",
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }) {
                        Icon(
                            Icons.Default.FavoriteBorder,
                            contentDescription = "Đã thích",
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Tab(selected = selectedTabIndex == 3, onClick = { selectedTabIndex = 3 }) {
                        Icon(
                            Icons.Default.Storefront,
                            contentDescription = "Cửa hàng",
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // 3. Phẳng hóa dữ liệu các Tab tránh crash lồng cuộn
            when (selectedTabIndex) {
                0 -> {
                    if (posts.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Bạn chưa đăng bài viết nào.",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        items(posts, key = { "post_${it.first.id}" }) { (post, author, product) ->
                            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                                PostItem(
                                    post = post,
                                    user = author,
                                    product = product,
                                    onPostClick = { clickedPostId ->
                                        if (clickedPostId.isNotEmpty()) onPostClick(clickedPostId)
                                    },
                                    onDeleteClick = { onDeletePost(post.id ?: "") },
                                    onReactionClick = onReactionClick,
                                    onProductClick = { p -> p.id?.let(onProductClick) }
                                )
                            }
                        }
                    }
                }

                1 -> {
                    if (likedPosts.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Bạn chưa thích bài đăng nào.",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        items(
                            likedPosts,
                            key = { "liked_${it.first.id}" }) { (post, author, product) ->
                            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                                PostItem(
                                    post = post,
                                    user = author,
                                    product = product,
                                    // ĐÃ CẬP NHẬT: Tương tự đối với các bài viết đã thích
                                    onPostClick = { clickedPostId ->
                                        if (clickedPostId.isNotEmpty()) onPostClick(clickedPostId)
                                    },
                                    onReactionClick = onReactionClick,
                                    onProductClick = { p -> p.id?.let(onProductClick) }
                                )
                            }
                        }
                    }
                }

                2 -> {
                    if (sellerProducts.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Bạn chưa đăng tải sản phẩm nào.",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        items(sellerProducts, key = { "product_${it.id}" }) { product ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { product.id?.let(onProductClick) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = product.imageUrls.firstOrNull()
                                                ?: "https://via.placeholder.com/150",
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Column {
                                            Text(
                                                text = product.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "Kho: ${product.stock} | Giá: ${
                                                    String.format(
                                                        "%,.0f đ",
                                                        product.price
                                                    ).replace(",", ".")
                                                }",
                                                fontSize = 13.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}