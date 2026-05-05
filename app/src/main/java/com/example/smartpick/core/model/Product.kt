package com.example.smartpick.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String? = null,
    @SerialName("owner_id") val ownerId: String,
    val name: String,
    val brand: String? = null,
    val category: String? = null,
    val price: Double = 0.0,
    @SerialName("image_urls") val imageUrls: List<String> = emptyList(),
    @SerialName("video_url") val videoUrl: String? = null,
    val status: String = "available"
)


