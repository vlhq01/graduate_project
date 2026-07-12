package com.example.graduateproject.data.repository

import com.example.graduateproject.data.remote.ProductApiService
import com.example.graduateproject.domain.model.Product
import com.example.graduateproject.domain.repository.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val apiService: ProductApiService
) : ProductRepository {
    override suspend fun getProductById(productId: String?): Result<Product> {
        if (productId == null) return Result.failure(Exception("ID trống"))

        return try {
            val response = apiService.getProductById(productId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Body rỗng"))
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSearchProducts(query: String?): Result<List<Product>> {

        val searchQuery = query?.trim() ?: ""
        if (searchQuery.isEmpty()) {
            return Result.success(emptyList())
        }

        return try {
            val response = apiService.getSearchProducts(searchQuery)

            if (response.isSuccessful) {
                val networkProducts = response.body() ?: emptyList()

                Result.success(networkProducts)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"

                Result.failure(Exception("Lỗi API sếp ơi: ${response.code()} - $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}