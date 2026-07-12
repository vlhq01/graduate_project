package com.example.graduateproject.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduateproject.domain.usecase.GetCurrentUserUseCase
import com.example.graduateproject.domain.usecase.GoogleSignInUseCase
import com.example.graduateproject.domain.usecase.LoginUseCase
import com.example.graduateproject.domain.usecase.LogoutUseCase
import com.example.graduateproject.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    init {
        restoreSession()
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            // --- 1. Xử lý cập nhật dữ liệu form ---
            is AuthEvent.NameChanged -> {
                _state.update { it.copy(nameInput = event.name) }
            }

            is AuthEvent.EmailChanged -> {
                _state.update { it.copy(emailInput = event.email) }
            }

            is AuthEvent.PhoneChanged -> {
                _state.update { it.copy(phoneInput = event.phone) }
            }

            is AuthEvent.PasswordChanged -> {
                _state.update { it.copy(passwordInput = event.pass) }
            }

            is AuthEvent.ConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPasswordInput = event.pass) }
            }

            is AuthEvent.TermsAcceptedChanged -> {
                _state.update { it.copy(isTermsAccepted = event.isAccepted) }
            }

            // --- 2. Xử lý các nút bấm hành động ---
            is AuthEvent.LoginClicked -> {
                // Đọc giá trị trực tiếp từ state hiện tại
                val email = _state.value.emailInput
                val pass = _state.value.passwordInput

                // (Tùy chọn) Validate nhẹ ở đây
                if (email.isBlank() || pass.isBlank()) {
                    _state.update { it.copy(error = "Vui lòng nhập email và mật khẩu") }
                    return
                }

                login(email, pass)
            }

            is AuthEvent.RegisterClicked -> {
                val currentState = _state.value

                // Validate form đăng ký
                if (currentState.passwordInput != currentState.confirmPasswordInput) {
                    _state.update { it.copy(error = "Mật khẩu xác nhận không khớp!") }
                    return
                }
                if (!currentState.isTermsAccepted) {
                    _state.update { it.copy(error = "Bạn cần đồng ý với các điều khoản!") }
                    return
                }
                if (currentState.nameInput.isBlank() || currentState.emailInput.isBlank()) {
                    _state.update { it.copy(error = "Vui lòng điền đầy đủ thông tin!") }
                    return
                }

                register(
                    name = currentState.nameInput,
                    email = currentState.emailInput,
                    phone = currentState.phoneInput,
                    pass = currentState.passwordInput
                )
            }

            is AuthEvent.GoogleSignInSuccess -> {
                signInWithGoogle(event.googleIdToken)
            }

            is AuthEvent.LogoutClicked -> {
                logout()
            }

            is AuthEvent.ErrorDismissed -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    // --- 3. Gọi UseCase (Thực hiện gọi API / Firebase) ---
    private fun login(email: String, pass: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = loginUseCase(email, pass)

            result.onSuccess { user ->
                _state.update { it.copy(isLoading = false, loggedInUser = user) }
            }.onFailure { exception ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Đăng nhập thất bại"
                    )
                }
            }
        }
    }

    private fun register(name: String, email: String, phone: String, pass: String) {
        viewModelScope.launch {
            Log.d("zzz", "register: running")
            _state.update { it.copy(isLoading = true, error = null) }

            val result = registerUseCase(name, email, phone, pass)

            result.onSuccess { user ->
                _state.update { it.copy(isLoading = false, loggedInUser = user) }
            }.onFailure { exception ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Đăng ký thất bại"
                    )
                }
            }
        }
    }

    private fun signInWithGoogle(googleIdToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = googleSignInUseCase(googleIdToken)

            result.onSuccess { user ->
                _state.update { it.copy(isLoading = false, loggedInUser = user) }
            }.onFailure { exception ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Đăng nhập Google thất bại"
                    )
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _state.update {
                it.copy(
                    loggedInUser = null,
                    emailInput = "",
                    passwordInput = "",
                    nameInput = "",
                    phoneInput = "",
                    confirmPasswordInput = "",
                    isTermsAccepted = false
                )
            }
        }
    }

    private fun restoreSession() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getCurrentUserUseCase()
                .onSuccess { user ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            loggedInUser = user
                        )
                    }
                }
                .onFailure {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            loggedInUser = null
                        )
                    }
                }
        }
    }
}