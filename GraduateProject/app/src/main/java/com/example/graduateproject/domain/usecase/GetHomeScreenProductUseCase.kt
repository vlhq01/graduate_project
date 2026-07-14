package com.example.graduateproject.domain.usecase

import com.example.graduateproject.domain.model.Product
import com.example.graduateproject.domain.repository.RecommendationRepository
import javax.inject.Inject

class GetHomeScreenProductsUseCase @Inject constructor(
    private val repository: RecommendationRepository
) {
    suspend operator fun invoke(
        category: String,
        page: Int,
        pageSize: Int
    ): Result<List<Product>> {
        val filter = if (category == "All") null else category
        return repository.getHomeScreenProducts(filter, page, pageSize)
    }
}