package com.example.graduateproject.domain.usecase

import com.example.graduateproject.domain.repository.RecommendationRepository
import javax.inject.Inject

class GetSimilarProductsUseCase @Inject constructor(private val repo: RecommendationRepository) {
    suspend operator fun invoke(id: String) = repo.getSimilarProducts(id)
}