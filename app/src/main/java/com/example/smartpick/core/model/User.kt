package com.example.smartpick.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,  // ID từ hệ thống Auth của Supabase
    val username: String? = null,
    val email: String? = null,

    @SerialName("fullname")
    val fullName: String? = null,

    @SerialName("avatar_url")
    val avatarUrl: String? = null
)
