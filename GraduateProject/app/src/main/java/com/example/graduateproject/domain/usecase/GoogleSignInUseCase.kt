package com.example.graduateproject.domain.usecase

import com.example.graduateproject.domain.repository.AuthRepository
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(googleIdToken: String) = repository.signInWithGoogle(googleIdToken)
}