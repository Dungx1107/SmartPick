// File: app/src/main/java/com/example/smartpick/core/model/OrderModels.kt
package com.example.smartpick.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    @SerialName("user_id") val userId: String,
    @SerialName("total_amount") val totalAmount: Double,
    @SerialName("shipping_address") val shippingAddress: String,
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("payment_method") val paymentMethod: String,
    val status: String = "completed"
)

@Serializable
data class OrderResponse(
    val id: String,
    @SerialName("total_amount") val totalAmount: Double = 0.0,
    val status: String = "completed",
    @SerialName("created_at") val createdAt: String = ""
)

@Serializable
data class OrderItemRequest(
    @SerialName("order_id") val orderId: String,
    @SerialName("product_id") val productId: String,
    val quantity: Int,
    @SerialName("price_at_purchase") val priceAtPurchase: Double
)
