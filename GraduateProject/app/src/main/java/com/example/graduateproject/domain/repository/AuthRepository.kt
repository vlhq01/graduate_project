package com.example.graduateproject.domain.repository

import com.example.graduateproject.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>

    suspend fun register(name: String, email: String, phone: String, password: String): Result<User>

    suspend fun signInWithGoogle(googleIdToken: String): Result<User>

    suspend fun getCurrentUser(): Result<User?>

    suspend fun logout()
}