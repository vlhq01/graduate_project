package com.example.graduateproject.data.remote

import com.example.graduateproject.data.remote.dto.UserDto
import com.example.graduateproject.data.remote.dto.UserSyncDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/sync")
    suspend fun syncFirebaseUser(
        @Body userData: UserSyncDTO
    ): UserDto
}