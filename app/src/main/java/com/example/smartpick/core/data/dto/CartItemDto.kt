package com.example.smartpick.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemRequest(
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String,
    val quantity: Int = 1
)