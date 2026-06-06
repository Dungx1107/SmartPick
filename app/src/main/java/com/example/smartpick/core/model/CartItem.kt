package com.example.smartpick.core.model

data class CartItem(
    val id: String? = null,         // ID duy nhất của dòng mặt hàng này trong database
    val userId: String,             // ID của người mua (người sở hữu giỏ hàng này)
    val productId: String,          // ID của sản phẩm để mapping database ngoại quan
    val quantity: Int = 1,          // Số lượng chọn mua của RIÊNG sản phẩm này (Ví dụ: mua 2 cái áo)
    val originPostId: String? = null, // Vết ID bài đăng gốc chứa sản phẩm lúc ấn mua
    val product: Product? = null,    // Thực thể Product chứa thông tin chi tiết (tên, giá, ảnh, kho stock) để hiển thị lên UI

    val createdAt: String? = null,
    val updatedAt: String? = null,
)