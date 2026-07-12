package com.example.graduateproject.presentation.auth

sealed class AuthEvent {
    // --- Sự kiện thay đổi giá trị trên Form ---
    data class NameChanged(val name: String) : AuthEvent()
    data class EmailChanged(val email: String) : AuthEvent()
    data class PhoneChanged(val phone: String) : AuthEvent()
    data class PasswordChanged(val pass: String) : AuthEvent()
    data class ConfirmPasswordChanged(val pass: String) : AuthEvent()
    data class TermsAcceptedChanged(val isAccepted: Boolean) : AuthEvent()

    object LoginClicked : AuthEvent()
    object RegisterClicked : AuthEvent()
    object LogoutClicked : AuthEvent()

    data class GoogleSignInSuccess(val googleIdToken: String) : AuthEvent()


    object ErrorDismissed : AuthEvent()
}