package com.example.smartpick.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItem(
    val id: String? = null,
    val orderId: String,
    val productId: String,
    val quantity: Int,
    val priceAtPurchase: Double
) : Parcelable

