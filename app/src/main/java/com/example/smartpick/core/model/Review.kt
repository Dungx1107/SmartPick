package com.example.smartpick.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReviewUser(
    val id: String,
    val fullName: String? = null,
    val avatarUrl: String? = null
) : Parcelable

@Parcelize
data class Review(
    val id: String,
    val userId: String,
    val productId: String,
    val orderItemId: String? = null,
    val rating: Int,
    val content: String,
    val createdAt: String,
    val product: Product? = null,
    val user: ReviewUser? = null
) : Parcelable
