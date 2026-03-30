package com.example.smartpick.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Trạng thái của màn hình đăng nhập
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun signInWithGoogleToken(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading // Báo cho UI hiện vòng xoay xoay
            try {
                // Nhờ Repository xử lý logic
                authRepository.signInWithGoogleAndSaveUser(idToken)

                _authState.value = AuthState.Success// Trả về không có lỗi gì -> Thành công!

            } catch (e: Exception) {
                // Bắt lỗi từ Repository ném ra và báo cho UI
                _authState.value = AuthState.Error(e.message ?: "Lỗi đăng nhập không xác định")
            }
        }
    }
}