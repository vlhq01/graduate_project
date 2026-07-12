package com.example.graduateproject.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val name: String,
    val brand: String,
    val category: String,
    val images: List<String>,
    val specs: Map<String, String>,
    val price: Price,
    val rating: Double,
    val ratingCount: Int,
    val sold: Int = 0,
    val stock: Int = 0,
    val reviews: List<Review> = emptyList()
)

@Serializable
data class Price(
    val usd: Double,
    val original: Double? = null
)

@Serializable
data class Review(
    val user: String,
    val rating: Int,
    val comment: String,
    @SerialName("created_at")
    val createdAt: String
)