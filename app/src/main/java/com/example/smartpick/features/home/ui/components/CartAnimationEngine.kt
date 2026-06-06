package com.example.smartpick.features.home.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlin.math.roundToInt

/**
 * Cấu trúc dữ liệu đại diện cho một vật thể đang bay vào giỏ hàng
 */
data class FlyingProductState(
    val id: Long,
    val imageUrl: String?,
    val startOffset: Offset,
    val endOffset: Offset
)

@Composable
fun FlyingProductItem(
    state: FlyingProductState,
    onAnimationEnd: (Long) -> Unit
) {
    // Khởi tạo tỷ lệ tiến trình chạy từ 0.0 (điểm đầu) đến 1.0 (điểm đích)
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Chạy hiệu ứng mượt mà trong 600ms
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = LinearEasing)
        )
        // Gọi callback để xóa vật thể khỏi danh sách quản lý khi bay xong
        onAnimationEnd(state.id)
    }

    val currentProgress = progress.value

    // Tính toán tọa độ bay theo đường cong Bezier bậc 2 để tạo hiệu ứng vòng cung nhô lên rồi chui xuống giỏ hàng
    val controlPoint = Offset(
        x = (state.startOffset.x + state.endOffset.x) / 2,
        y = minOf(state.startOffset.y, state.endOffset.y) - 300f // Đẩy điểm uốn lên cao 300px
    )

    // Công thức tính toán vị trí Bezier: B(t) = (1-t)^2 * P0 + 2*(1-t)*t * P1 + t^2 * P2
    val t = currentProgress
    val currentX = (1 - t) * (1 - t) * state.startOffset.x + 2 * (1 - t) * t * controlPoint.x + t * t * state.endOffset.x
    val currentY = (1 - t) * (1 - t) * state.startOffset.y + 2 * (1 - t) * t * controlPoint.y + t * t * state.endOffset.y

    // Thu nhỏ dần vật thể khi bay gần đến đích (từ size 48dp về 12dp)
    val currentSize = (48 - (36 * currentProgress)).dp

    Box(
        modifier = Modifier
            .offset { IntOffset(currentX.roundToInt(), currentY.roundToInt()) }
            .size(currentSize)
            .clip(CircleShape)
            .background(Color.White)
    ) {
        if (!state.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = state.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Fallback nếu sản phẩm không có ảnh thì bay một chấm tròn màu đỏ
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFE2C55))
            )
        }
    }
}

// ======================= COMPOSE ANIMATION PREVIEW GENERATOR =======================
@Preview(showBackground = true, showSystemUi = true, name = "Xem Trước Hiệu Ứng Bay Giỏ Hàng")
@Composable
fun CartAnimationPreview() {
    // Quản lý danh sách các vật thể đang bay trong môi trường Preview thử nghiệm
    var previewFlyingList by remember { mutableStateOf(listOf<FlyingProductState>()) }
    var idCounter by remember { mutableLongStateOf(0L) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A)) // Nền tối để nhìn rõ quỹ đạo chấm tròn bay
    ) {
        // Giả lập vị trí Giỏ hàng (Đích đến) ghim cố định ở góc trên bên phải màn hình
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 20.dp)
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFFE2C55)),
            contentAlignment = Alignment.Center
        ) {
            Text("🛒", color = Color.White, fontSize = 20.sp)
        }

        // Khu vực nút bấm kích hoạt hiệu ứng nằm ở góc dưới màn hình
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    // Tạo một vật thể bay mới có điểm đầu xuất phát từ chính vị trí nút bấm
                    // và điểm đích hướng thẳng về vùng chứa Icon giỏ hàng phía trên
                    val newFlyingItem = FlyingProductState(
                        id = idCounter++,
                        imageUrl = null, // Để null để render chấm đỏ cho dễ quan sát quỹ đạo
                        startOffset = Offset(x = 450f, y = 1500f), // Giả lập tọa độ pixel nút bấm thô
                        endOffset = Offset(x = 900f, y = 150f)     // Giả lập tọa độ pixel icon giỏ hàng
                    )
                    previewFlyingList = previewFlyingList + newFlyingItem
                }
            ) {
                Text("Bấm Thêm Vào Giỏ (Thử Nghiệm)")
            }
        }

        // Render toàn bộ các phần tử đang chạy hiệu ứng trên màn hình layer
        previewFlyingList.forEach { flyingState ->
            FlyingProductItem(
                state = flyingState,
                onAnimationEnd = { endedId ->
                    // Loại bỏ khỏi danh sách ngay khi vòng đời hiệu ứng đạt tiến trình 1.0
                    previewFlyingList = previewFlyingList.filter { it.id != endedId }
                }
            )
        }
    }
}
