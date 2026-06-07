package com.example.smartpick.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderRequestDto(
    @SerialName("user_id") val userId: String,
    @SerialName("total_amount") val totalAmount: Double,
    @SerialName("shipping_address") val shippingAddress: String,
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("payment_method") val paymentMethod: String,
    val status: String = "completed"
)

// 1. DTO thu nhỏ của Product để bóc tách Tên và Ảnh từ Supabase
@Serializable
data class ProductMinDto(
    val id: String? = null,
    val name: String = "",
    @SerialName("image_urls") val imageUrls: List<String> = emptyList()
)

// 2. KHẮC PHỤC LỖI THIẾU KHAI BÁO: Thêm cấu trúc lớp phản ánh bảng order_items khi nhận về
@Serializable
data class OrderItemWithProductDto(
    val id: String? = null,
    val quantity: Int,
    @SerialName("price_at_purchase") val priceAtPurchase: Double,
    val products: ProductMinDto? = null, // Nhúng object sản phẩm lồng bên trong
    @SerialName("product_id") val productId: String? = null,
)

// 3. DTO phản ánh bảng orders khi nhận về từ truy vấn SELECT
@Serializable
data class OrderResponseDto(
    val id: String,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("shipping_address") val shippingAddress: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("payment_method") val paymentMethod: String? = null,
    @SerialName("total_amount") val totalAmount: Double = 0.0,
    val status: String = "completed",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("order_items") val orderItems: List<OrderItemWithProductDto> = emptyList()
)