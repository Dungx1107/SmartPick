package com.example.smartpick.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BoughtProductDto(
    @SerialName("product_id") val productId: String,
    @SerialName("products") val product: ProductDto? = null
)