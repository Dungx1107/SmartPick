package com.example.smartpick.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String,
    @SerialName("quantity") val quantity: Int = 1,
    @SerialName("post_id") val postId: String? = null, // Đồng bộ chuẩn xác với cột post_id trong Database
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,

    // Phục vụ câu lệnh Postgrest Join query [products(*)] khi lấy dữ liệu giỏ hàng về từ Supabase
    @SerialName("products") val products: ProductDto? = null
)