package com.example.graduateproject.data.repository

import com.example.graduateproject.data.remote.CategoriesApiService
import com.example.graduateproject.domain.repository.CategoriesRepository
import javax.inject.Inject


class CategoriesRepositoryImpl @Inject constructor(
    private val apiService: CategoriesApiService
) : CategoriesRepository {
    override suspend fun getCategories(): Result<List<String>> {
        return try {
            val response = apiService.getCategories()
            if (response.isSuccessful) {
                return Result.success(response.body() ?: emptyList())
            }
            return Result.failure(Exception("Lỗi API: ${response.code()}"))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}