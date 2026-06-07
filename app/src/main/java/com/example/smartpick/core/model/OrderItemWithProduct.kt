package com.example.smartpick.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItemWithProduct(
    val id: String?,
    val productId: String,
    val quantity: Int,
    val priceAtPurchase: Double,
    val productName: String,
    val productImageUrl: String?
) : Parcelable