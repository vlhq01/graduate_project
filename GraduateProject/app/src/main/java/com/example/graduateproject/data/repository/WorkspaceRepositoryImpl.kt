package com.example.graduateproject.data.repository

import com.example.graduateproject.data.remote.WorkspaceApiService
import com.example.graduateproject.domain.model.Product
import com.example.graduateproject.domain.repository.WorkspaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class WorkspaceRepositoryImpl @Inject constructor(
    private val api: WorkspaceApiService
) : WorkspaceRepository {

    private val _workspaceProducts = MutableStateFlow<List<Product>>(emptyList())
    override val workspaceProductsFlow: Flow<List<Product>>
        get() = _workspaceProducts.asStateFlow()

    override suspend fun loadWorkspaceProducts(): Result<List<Product>> {
        return try {
            val response = api.loadWorkspace()
            if (response.isSuccessful) {
                val products = response.body() ?: emptyList()
                _workspaceProducts.value = products
                Result.success(products)
            } else {
                Result.failure(Exception("Lỗi tải giỏ hàng: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addToWorkspace(productId: String): Result<Boolean> {
        return try {
            val response = api.addToWorkspace(productId)
            if (response.isSuccessful) {
                loadWorkspaceProducts()
                Result.success(true)
            } else {
                Result.failure(Exception("Không thêm được: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromWorkspace(productId: String): Result<Boolean> {
        return try {
            val response = api.removeFromWorkspace(productId)
            if (response.isSuccessful) {
                loadWorkspaceProducts()
                Result.success(true)
            } else {
                Result.failure(Exception("Không xóa được: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}