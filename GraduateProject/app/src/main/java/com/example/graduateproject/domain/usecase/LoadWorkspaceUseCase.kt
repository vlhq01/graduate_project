package com.example.graduateproject.domain.usecase

import com.example.graduateproject.domain.model.Product
import com.example.graduateproject.domain.repository.WorkspaceRepository
import javax.inject.Inject

class LoadWorkspaceUseCase @Inject constructor(
    private val repo: WorkspaceRepository
) {
    suspend operator fun invoke(): Result<List<Product>> {
        return repo.loadWorkspaceProducts()
    }
}