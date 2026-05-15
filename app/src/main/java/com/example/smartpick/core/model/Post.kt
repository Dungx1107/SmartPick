package com.example.smartpick.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: String? = null,
    val userId: String,
    val productId: String? = null,
    val content: String? = null,
    val mediaUrls: List<String> = emptyList(),
    val status: String? = "available",
    val createdAt: String? = null
) : Parcelable