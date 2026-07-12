package com.example.graduateproject.domain.usecase

import com.example.graduateproject.domain.model.Product
import com.example.graduateproject.domain.repository.ProductRepository
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(private val repo: ProductRepository) {
    suspend operator fun invoke(query: String): Result<List<Product>> =
        repo.getSearchProducts(query)
}