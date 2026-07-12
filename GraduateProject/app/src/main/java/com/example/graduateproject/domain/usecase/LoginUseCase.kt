package com.example.graduateproject.domain.usecase

import com.example.graduateproject.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, pass: String) = repository.login(email, pass)
}