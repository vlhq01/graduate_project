package com.example.graduateproject.domain.usecase

import com.example.graduateproject.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.getCurrentUser()
}