package com.example.graduateproject.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface CategoriesApiService {
    @GET("api/products/categories")
    suspend fun getCategories(): Response<List<String>>
}