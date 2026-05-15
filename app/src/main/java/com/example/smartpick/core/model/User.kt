package com.example.smartpick.core.model

data class User(
    val id: String,
    val email: String? = null,
    val username: String? = null,
    val fullName: String? = null,
    val avatarUrl: String? = null,
    val phoneNumber: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)