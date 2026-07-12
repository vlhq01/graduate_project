package com.example.graduateproject.domain.repository

import com.example.graduateproject.domain.model.Product

interface RecommendationRepository {
    suspend fun getSimilarProducts(productId: String): Result<List<Product>>
    suspend fun getHomeScreenProducts(category: String?): Result<List<Product>>

}