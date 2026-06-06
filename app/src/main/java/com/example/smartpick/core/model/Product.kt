package com.example.smartpick.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String? = null,
    val ownerId: String,
    val name: String,
    val brand: String? = null,
    val category: String? = null,
    val price: Double = 0.0,
    val imageUrls: List<String> = emptyList(),
    val videoUrl: String? = null,
    val status: String = "available",
    val stock: Int = 0,
    val soldCount: Int = 0,
    val createdAt: String? = null,
    val postId: String? = null,
    val ownerName: String? = null
) : Parcelable