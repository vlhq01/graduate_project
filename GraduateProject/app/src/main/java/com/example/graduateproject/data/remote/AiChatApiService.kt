package com.example.graduateproject.data.remote

import com.example.graduateproject.data.remote.dto.ChatMessageDTO
import com.example.graduateproject.data.remote.dto.ChatRequestDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AiChatApiService {
    @GET("api/chat/history")
    suspend fun getChatHistory(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<List<ChatMessageDTO>>

    // Gửi tin nhắn
    @POST("api/chat/send")
    suspend fun sendMessage(@Body request: ChatRequestDTO): Response<ChatMessageDTO>
}