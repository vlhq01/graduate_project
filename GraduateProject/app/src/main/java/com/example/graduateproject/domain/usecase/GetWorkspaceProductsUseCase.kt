package com.example.graduateproject.domain.usecase

import com.example.graduateproject.domain.model.Product
import com.example.graduateproject.domain.repository.WorkspaceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetWorkspaceProductsUseCase @Inject constructor(private val repo: WorkspaceRepository) {
    operator fun invoke(): Flow<List<Product>> = repo.workspaceProductsFlow
}