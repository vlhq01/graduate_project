package com.example.graduateproject.domain.usecase

import com.example.graduateproject.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(name: String, email: String, phone: String, password: String) =
        repository.register(name, email, phone, password)
}