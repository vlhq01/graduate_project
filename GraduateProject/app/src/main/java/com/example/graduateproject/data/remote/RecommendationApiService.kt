package com.example.graduateproject.data.remote

import com.example.graduateproject.domain.model.Product
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecommendationApiService {
    @GET("api/products/{id}/similar")
    suspend fun getSimilarProducts(@Path("id") id: String): Response<List<Product>>

    @GET("api/products/homescreen")
    suspend fun getHomeScreenProducts(
        @Query("category") category: String?,
        @Query("page") page: Int,
        @Query("pageSize") limit: Int
    ): Response<List<Product>>
}