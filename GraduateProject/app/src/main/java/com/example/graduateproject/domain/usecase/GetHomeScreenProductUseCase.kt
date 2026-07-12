package com.example.graduateproject.domain.usecase

import com.example.graduateproject.domain.model.Product
import com.example.graduateproject.domain.repository.RecommendationRepository
import javax.inject.Inject

class GetHomeScreenProductsUseCase @Inject constructor(
    private val repository: RecommendationRepository
) {
    suspend operator fun invoke(category: String): Result<List<Product>> {
        val filter = if (category == "All") null else category

        // Gọi repository
        val result = repository.getHomeScreenProducts(filter)

        return result.map { products ->
            products.sortedByDescending { it.rating }
        }
    }
}