package com.example.smartpick.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Router
import androidx.compose.material.icons.outlined.Speaker
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material.icons.outlined.TabletMac
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartpick.features.auth.ui.TextPrimary
import com.example.smartpick.features.auth.ui.TextSecondary
import com.example.smartpick.core.theme.AICyan
import com.example.smartpick.core.theme.AccentBlue
import com.example.smartpick.core.theme.BadgeOrange
import com.example.smartpick.core.theme.PageBg
import com.example.smartpick.core.theme.SurfaceCard
import com.example.smartpick.core.theme.TextMuted
import com.example.smartpick.core.theme.White


// ─── Data Models ──────────────────────────────────────────────────────────────
data class Product(
    val id: Int,
    val brand: String,
    val name: String,
    val price: String,
    val badge: String? = null,          // "AI CHOICE" | "TRENDING" | null
    val imageDescription: String = "",   // placeholder label
    val badgeColor: Color = AccentBlue,
    val bgColor: Color = SurfaceCard,
)

data class Category(
    val icon: ImageVector,
    val label: String
)

// ─── Sample Data ──────────────────────────────────────────────────────────────
val sampleProducts = listOf(
    Product(
        1,
        "AUDIO MASTER",
        "Tai nghe chống ồn Pro",
        "5.490.000đ",
        "AI CHOICE",
        badgeColor = AccentBlue,
        bgColor = Color(0xFF1E3A5F)
    ),
    Product(
        2,
        "LUMINA CORE",
        "Tablet Đồ Họa AI",
        "12.900.000đ",
        "TRENDING",
        badgeColor = BadgeOrange,
        bgColor = Color(0xFF0F2035)
    ),
    Product(
        3,
        "HEALTH LAB",
        "Đồng Hồ Lumina S2",
        "4.150.000đ",
        "AI CHOICE",
        badgeColor = AccentBlue,
        bgColor = SurfaceCard
    ),
    Product(
        4,
        "SMART HOME",
        "Loa Trợ Lý Aura G1",
        "2.890.000đ", null,
        badgeColor = AccentBlue,
        bgColor = SurfaceCard
    ),
)

val sampleCategories = listOf(
    Category(Icons.Outlined.Home, "SMART HOME"),
    Category(Icons.Outlined.Headphones, "AUDIO"),
    Category(Icons.Outlined.Watch, "WEARABLE"),
    Category(Icons.Outlined.Tv, "TV & DISPLAY"),
    Category(Icons.Outlined.Router, "NETWORK"),
)

// ─── Main Screen ──────────────────────────────────────────────────────────────
@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(PageBg)
    ) {
        Spacer(Modifier.height(12.dp))
        SearchBar()

        Spacer(Modifier.height(16.dp))

        HeroBanner()

        Spacer(Modifier.height(20.dp))

        CategoryRow(sampleCategories)

        Spacer(Modifier.height(24.dp))

        RecommendedSection(sampleProducts)

        Spacer(Modifier.height(24.dp))

        AICuratorBanner()

        Spacer(Modifier.height(24.dp))
    }
}

// ─── Search Bar ───────────────────────────────────────────────────────────────
@Composable
fun SearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(White)
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(28.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.Default.Search, contentDescription = null,
                tint = TextMuted, modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Tìm kiếm sản phẩm thông minh...",
                color = TextMuted, fontSize = 14.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            Icons.Default.CameraAlt, contentDescription = "Camera search",
            tint = TextSecondary, modifier = Modifier.size(20.dp)
        )
    }
}

// ─── Hero Banner ──────────────────────────────────────────────────────────────
@Composable
fun HeroBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0D2137), Color(0xFF1E5F99), Color(0xFF0A3D6B))
                )
            )
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(140.dp)
                .offset(x = 200.dp, y = (-30).dp)
                .background(
                    Brush.radialGradient(listOf(Color(0x40009FFF), Color.Transparent)),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .offset(x = 240.dp, y = 60.dp)
                .background(
                    Brush.radialGradient(listOf(Color(0x3000C8FF), Color.Transparent)),
                    CircleShape
                )
        )

        // Text content
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp, end = 140.dp)
        ) {
            Text(
                "Kỷ Nguyên\nÂm Thanh AI",
                color = White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 28.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Trải nghiệm chất âm tinh tế được tinh chỉnh bởi trí tuệ nhân tạo Lumina Pro.",
                color = Color(0xCCFFFFFF),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(White)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    "Khám phá ngay", color = AccentBlue,
                    fontSize = 12.sp, fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Right side placeholder for product image
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .size(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x33FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Headphones, contentDescription = null,
                tint = Color(0x99FFFFFF), modifier = Modifier.size(48.dp)
            )
        }
    }
}

// ─── Category Row ─────────────────────────────────────────────────────────────
@Composable
fun CategoryRow(categories: List<Category>) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        categories.forEach { cat ->
            CategoryItem(cat)
        }
    }
}

@Composable
fun CategoryItem(category: Category) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(64.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFEBF3FF))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                category.icon, contentDescription = category.label,
                tint = AccentBlue, modifier = Modifier.size(26.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            category.label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ─── Recommended Section ──────────────────────────────────────────────────────
@Composable
fun RecommendedSection(products: List<Product>) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Gợi ý cho bạn",
                    fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary
                )
                Text(
                    "Dựa trên sở thích của bạn với trợ lý AI",
                    fontSize = 12.sp, color = TextSecondary
                )
            }
            Text(
                "Xem tất cả",
                fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AccentBlue
            )
        }

        Spacer(Modifier.height(16.dp))

        // 2-column grid
        val rows = products.chunked(2)
        rows.forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { product ->
                    ProductCard(product, modifier = Modifier.weight(1f))
                }
                // Fill empty slot if odd count
                if (rowItems.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun ProductCard(product: Product, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column {
            // Image area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        product.bgColor,
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Badge
                product.badge?.let { badge ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(product.badgeColor)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (badge == "AI CHOICE") {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(AICyan, CircleShape)
                                )
                                Spacer(Modifier.width(3.dp))
                            } else {
                                Icon(
                                    Icons.Default.Bolt, contentDescription = null,
                                    tint = White, modifier = Modifier.size(10.dp)
                                )
                                Spacer(Modifier.width(2.dp))
                            }
                            Text(
                                badge, color = White, fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Product image placeholder
                val icon = when {
                    product.brand.contains("AUDIO") -> Icons.Outlined.Headphones
                    product.brand.contains("HEALTH") -> Icons.Outlined.Watch
                    product.brand.contains("SMART") -> Icons.Outlined.Speaker
                    else -> Icons.Outlined.TabletMac
                }
                Icon(
                    icon, contentDescription = product.name,
                    tint = if (product.bgColor == SurfaceCard) Color(0xFF9CA3AF) else Color(
                        0x99FFFFFF
                    ),
                    modifier = Modifier.size(64.dp)
                )
            }

            // Product info
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    product.brand,
                    fontSize = 9.sp, fontWeight = FontWeight.SemiBold,
                    color = TextMuted, letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    product.name,
                    fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                    color = TextPrimary, maxLines = 2, lineHeight = 18.sp
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        product.price,
                        fontSize = 14.sp, fontWeight = FontWeight.Bold,
                        color = AccentBlue
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEBF3FF))
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.AddShoppingCart, contentDescription = "Add to cart",
                            tint = AccentBlue, modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── AI Curator Banner ────────────────────────────────────────────────────────
@Composable
fun AICuratorBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFEBF3FF))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Tìm kiếm bằng AI\nCurator?",
                fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                color = AccentBlue, lineHeight = 26.sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Hãy để trí tuệ nhân tạo của Lumina giúp bạn tìm thấy sản phẩm hoàn hảo chỉ qua một cuộc trò chuyện ngắn.",
                fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp
            )
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF1A4FA0), AccentBlue))
                    )
                    .clickable { }
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AutoAwesome, contentDescription = null,
                        tint = White, modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Bắt đầu ngay", color = White,
                        fontSize = 14.sp, fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Star field placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF071629), Color(0xFF0D2B4A), Color(0xFF071629))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Stars, contentDescription = null,
                    tint = AICyan, modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}


// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen()
    }
}