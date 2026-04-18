package com.example.smartpick.core.model

data class Product(
    val id: String,
    val name: String,
    val branch: String,
    val price: Double? = 0.0,
    val category: String,
    val imageUrl: String? = null
)
