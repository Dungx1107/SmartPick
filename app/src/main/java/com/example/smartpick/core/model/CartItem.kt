// File: app/src/main/java/com/example/smartpick/core/model/CartItem.kt
package com.example.smartpick.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String,
    val quantity: Int = 1,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,

    // Thuộc tính này sẽ chứa dữ liệu khi join với bảng products
    val products: Product? = null
)