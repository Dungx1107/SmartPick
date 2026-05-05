package com.example.smartpick.core.model
data class Product(
    val id: String = "",
    val ownerId: String,
    val name: String,
    val brand: String? = null,
    val price: Double = 0.0,
    val category: String,
    val imageUrls: List<String> = emptyList(),
    val videoUrl: String? = null,
    val status: String = "active"
)

