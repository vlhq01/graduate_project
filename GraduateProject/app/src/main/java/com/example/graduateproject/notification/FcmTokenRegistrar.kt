package com.example.graduateproject.notification

import com.example.graduateproject.data.remote.NotificationApiService
import com.example.graduateproject.data.remote.dto.FcmTokenDto
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FcmTokenRegistrar @Inject constructor(
    private val notificationApi: NotificationApiService
) {
    suspend fun registerCurrentToken() {
        val token = FirebaseMessaging.getInstance().token.await()
        notificationApi.saveFcmToken(FcmTokenDto(token))
    }
}