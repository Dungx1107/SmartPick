package com.example.smartpick.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize // Thêm để đồng bộ với Post và tránh lỗi Saveable
data class Product(
    val id: String? = null,
    @SerialName("owner_id") val ownerId: String,
    val name: String,
    val brand: String? = null,
    val category: String? = null,
    val price: Double = 0.0,
    @SerialName("image_urls") val imageUrls: List<String> = emptyList(),
    @SerialName("video_url") val videoUrl: String? = null,
    val status: String = "available",

    @SerialName("created_at") val createdAt: String? = null // Bắt buộc có để nhận data từ Supabase
) : Parcelable