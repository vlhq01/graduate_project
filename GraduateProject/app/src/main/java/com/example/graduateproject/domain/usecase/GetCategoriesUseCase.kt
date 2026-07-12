package com.example.graduateproject.domain.usecase

import com.example.graduateproject.domain.repository.CategoriesRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoriesRepository
) {
    suspend operator fun invoke(): Result<List<String>> {
        return repository.getCategories()
    }
}