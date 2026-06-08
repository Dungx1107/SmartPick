package com.example.smartpick.core.data.dto

@kotlinx.serialization.Serializable
data class PostProductInsertDto(
    val id: String,
    @kotlinx.serialization.SerialName("owner_id") val ownerId: String,
    val name: String,
    val brand: String?,
    val category: String?,
    val price: Double,
    val stock: Int,
    @kotlinx.serialization.SerialName("image_urls") val imageUrls: List<String>,
    @kotlinx.serialization.SerialName("video_url") val videoUrl: String?
)