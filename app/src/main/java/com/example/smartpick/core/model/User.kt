package com.example.smartpick.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String, // UUID từ auth.users
    val email: String? = null,
    val username: String? = null,

    @SerialName("fullname")
    val fullName: String? = null,

    @SerialName("avatar_url")
    val avatarUrl: String? = null,

    @SerialName("phone_number")
    val phoneNumber: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)