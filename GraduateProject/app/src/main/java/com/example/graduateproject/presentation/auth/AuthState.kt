package com.example.graduateproject.presentation.auth

import com.example.graduateproject.domain.model.User

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val loggedInUser: User? = null,

    val nameInput: String = "",
    val emailInput: String = "",
    val phoneInput: String = "",
    val passwordInput: String = "",
    val confirmPasswordInput: String = "",
    val isTermsAccepted: Boolean = false
)