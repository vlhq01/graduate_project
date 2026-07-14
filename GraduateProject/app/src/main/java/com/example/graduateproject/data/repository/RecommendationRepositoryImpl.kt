package com.example.graduateproject.data.repository

import com.example.graduateproject.data.remote.RecommendationApiService
import com.example.graduateproject.domain.model.Product
import com.example.graduateproject.domain.repository.RecommendationRepository
import javax.inject.Inject

class RecommendationRepositoryImpl @Inject constructor(
    private val recommendationApi: RecommendationApiService
) : RecommendationRepository {
    override suspend fun getHomeScreenProducts(
        category: String?,
        page: Int,
        pageSize: Int
    ): Result<List<Product>> {
        return try {
            val serverCategory = if (category == "All") null else category

            // Truyền page và pageSize vào hàm gọi API
            val response = recommendationApi.getHomeScreenProducts(
                category = serverCategory,
                page = page,
                limit = pageSize
            )

            if (response.isSuccessful) {
                val products = response.body() ?: emptyList()
                Result.success(products)
            } else {
                Result.failure(Exception("Lỗi API sếp ơi: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSimilarProducts(productId: String): Result<List<Product>> {
        return try {
            val response = recommendationApi.getSimilarProducts(productId)
            if (response.isSuccessful) {
                val similarProducts = response.body() ?: emptyList()
//                val similar = allProducts.filter { it.id != productId }.take(4)
                Result.success(similarProducts)
            } else {
                Result.failure(Exception("Lỗi API"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}