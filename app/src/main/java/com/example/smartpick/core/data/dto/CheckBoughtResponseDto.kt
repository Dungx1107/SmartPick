package com.example.smartpick.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckBoughtResponseDto(
    @SerialName("id") val id: String,
    @SerialName("orders") val orders: OrderInnerDto? = null
)