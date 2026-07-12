package com.example.graduateproject.data.remote

import com.example.graduateproject.domain.model.Product
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {

    @GET("api/products") // Sửa ở đây
    suspend fun getProducts(
        @Query("category") category: String? = null
    ): Response<List<Product>>

    @GET("api/products/{id}") // Sửa ở đây
    suspend fun getProductById(@Path("id") id: String): Response<Product>

    @GET("api/products/search") // Sửa ở đây
    suspend fun getSearchProducts(@Query("q") query: String?): Response<List<Product>>


}