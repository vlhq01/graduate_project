package com.example.graduateproject.data.remote

import com.example.graduateproject.domain.model.Product
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WorkspaceApiService {
    @GET("api/workspace")
    suspend fun loadWorkspace(): Response<List<Product>>

    @POST("api/workspace/{id}")
    suspend fun addToWorkspace(@Path("id") productId: String): Response<Unit>

    @DELETE("api/workspace/{id}")
    suspend fun removeFromWorkspace(@Path("id") productId: String): Response<Unit>
}