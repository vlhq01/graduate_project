package com.example.graduateproject.domain.repository

import com.example.graduateproject.domain.model.Product

interface ProductRepository {
    suspend fun getProductById(productId: String? = null): Result<Product>

    suspend fun getSearchProducts(query: String?): Result<List<Product>>

}