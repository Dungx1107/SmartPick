package com.example.smartpick.core.model
data class CartItem(
    val id: String? = null,
    val userId: String,
    val productId: String,
    val quantity: Int = 1,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val product: Product? = null
)