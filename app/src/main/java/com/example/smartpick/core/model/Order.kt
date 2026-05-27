package com.example.smartpick.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val id: String,
    val userId: String? = null,
    val totalAmount: Double = 0.0,
    val shippingAddress: String? = null,
    val phoneNumber: String? = null,
    val paymentMethod: String? = null,
    val status: String = "completed",
    val createdAt: String = ""
) : Parcelable

@Parcelize
data class OrderItem(
    val id: String? = null,
    val orderId: String,
    val productId: String,
    val quantity: Int,
    val priceAtPurchase: Double
) : Parcelable
