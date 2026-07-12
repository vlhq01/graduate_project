package com.example.graduateproject.domain.repository

interface CategoriesRepository {
    suspend fun getCategories(): Result<List<String>>
}