package com.example.graduateproject.data.remote

import com.example.graduateproject.data.remote.dto.FcmTokenDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificationApiService {
    @POST("api/notifications/fcm-token")
    suspend fun saveFcmToken(@Body dto: FcmTokenDto): Response<Unit>

    @POST("api/notifications/test")
    suspend fun sendTestNotification(): Response<Unit>
}