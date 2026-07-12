package com.example.graduateproject.domain.usecase

import com.example.graduateproject.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(productId: String? = null) = repository.getProductById(productId)
}