package com.example.graduateproject.domain.repository

import com.example.graduateproject.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface WorkspaceRepository {
    val workspaceProductsFlow: Flow<List<Product>>

    suspend fun addToWorkspace(productId: String): Result<Boolean>

    suspend fun loadWorkspaceProducts(): Result<List<Product>>

    suspend fun removeFromWorkspace(productId: String): Result<Boolean>
}