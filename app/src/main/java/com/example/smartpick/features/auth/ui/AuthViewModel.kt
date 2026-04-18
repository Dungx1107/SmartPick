package com.example.smartpick.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.User
import com.example.smartpick.features.auth.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Trạng thái của màn hình đăng nhập
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    // 1. Quản lý luồng xử lý (Loading, Error...)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // 2. Quản lý DỮ LIỆU người dùng (User Session)
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // kiểm soát trạng thái bắt đầu
    private val _isInitializing = MutableStateFlow(true)
    val isInitializing = _isInitializing.asStateFlow()

    init {
        // Tự động kiểm tra xem người dùng đã login từ trước chưa khi mở App
        checkExistingSession()
    }

    private fun checkExistingSession() {
        viewModelScope.launch {
            try {
                // Giả sử repository có hàm lấy user hiện tại từ Supabase
                val user = authRepository.getCurrentUser()
                _currentUser.value = user
            } catch (e: Exception) {
                _currentUser.value = null
                e.printStackTrace()
            } finally {
                _isInitializing.value = false
            }
        }
    }

    fun signInWithGoogleToken(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading // Báo cho UI hiện vòng xoay xoay
            try {
                //Repository xử lý logic nhan user từ repo
                val user = authRepository.signInWithGoogleAndSaveUser(idToken)

                _currentUser.value = user // dùng chung cho toàn App
                _authState.value = AuthState.Success//Thành công

            } catch (e: Exception) {
                // Bắt lỗi từ Repository ném ra và báo cho UI
                _authState.value = AuthState.Error(e.message ?: "Lỗi đăng nhập không xác định")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
            _currentUser.value = null
            _authState.value = AuthState.Idle
        }
    }
}