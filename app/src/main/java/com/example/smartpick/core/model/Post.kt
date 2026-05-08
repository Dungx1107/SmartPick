package com.example.smartpick.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Post(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String? = null,
    val content: String? = null,
    @SerialName("media_urls") val mediaUrls: List<String> = emptyList(),

    @kotlinx.serialization.Transient
    val status: String? = "available",

    @SerialName("created_at") val createdAt: String? = null
) : Parcelable