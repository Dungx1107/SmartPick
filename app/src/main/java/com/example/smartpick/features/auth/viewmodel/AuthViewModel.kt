package com.example.smartpick.features.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpick.core.model.User
import com.example.smartpick.core.utils.Constants
import com.example.smartpick.features.auth.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.gotrue.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Trạng thái thực thi các tác vụ xác thực tại tầng giao diện (UI Layer).
 */
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * AuthViewModel quản lý trạng thái xác thực và vòng đời phiên làm việc của người dùng.
 *
 * Áp dụng nguyên tắc Single Source of Truth (SSOT), sử dụng trạng thái phiên từ
 * Supabase Auth làm nguồn dữ liệu gốc để đồng bộ hóa trạng thái ứng dụng.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Luồng trạng thái xử lý tác vụ xác thực (Login/Register flow)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /**
     * Dữ liệu người dùng hiện tại (Global User State).
     * Được quan sát bởi toàn bộ các thành phần UI để đảm bảo tính nhất quán dữ liệu.
     */
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Trạng thái khởi tạo và phục hồi phiên đăng nhập khi khởi động ứng dụng
    private val _isInitializing = MutableStateFlow(true)
    val isInitializing = _isInitializing.asStateFlow()

    // Kiểm soát logic hiển thị thông báo chào mừng trong một phiên làm việc
    private val _hasShownWelcomeToast = MutableStateFlow(false)
    val hasShownWelcomeToast = _hasShownWelcomeToast.asStateFlow()

    init {
        /**
         * Thiết lập cơ chế quan sát phiên ngay khi ViewModel được khởi tạo.
         */
        observeSession()
    }

    /**
     * Đồng bộ hóa trạng thái người dùng dựa trên dòng dữ liệu (Stream) từ Supabase Auth.
     *
     * Khi trạng thái session thay đổi (Authenticated/NotAuthenticated), hàm sẽ tự động
     * cập nhật giá trị cho [_currentUser], đảm bảo dữ liệu luôn được đồng bộ
     * mà không cần gọi hàm cập nhật thủ công từ các màn hình khác.
     */
    private fun observeSession() {
        viewModelScope.launch {
            authRepository.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        // Truy xuất thông tin định danh từ session và nạp dữ liệu chi tiết từ Database
                        val user = authRepository.getCurrentUser()
                        _currentUser.value = user
                        _isInitializing.value = false
                    }
                    is SessionStatus.NotAuthenticated -> {
                        // Reset trạng thái người dùng về null khi phiên kết thúc hoặc không tồn tại
                        _currentUser.value = null
                        _isInitializing.value = false
                    }
                    else -> {
                        // Xử lý các trạng thái trung gian khác từ SDK nếu cần
                    }
                }
            }
        }
    }

    /**
     * Thực hiện xác thực người dùng bằng phương thức Email/Password.
     *
     * @param email Địa chỉ email định danh.
     * @param pass Mật khẩu truy cập.
     */
    fun signInManual(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Vui lòng nhập đầy đủ thông tin")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signInManual(email, pass)
            result.onSuccess {
                _authState.value = AuthState.Success
                // Lưu ý: Không cập nhật _currentUser thủ công tại đây vì observeSession chịu trách nhiệm này
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Lỗi đăng nhập")
            }
        }
    }

    /**
     * Xác thực thông qua Google OAuth Provider bằng ID Token.
     */
    fun signInWithGoogleToken(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                authRepository.signInWithGoogleAndSaveUser(idToken)
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Lỗi đăng nhập Google")
            }
        }
    }

    /**
     * Cập nhật trạng thái đã hiển thị thông báo chào mừng.
     */
    fun markWelcomeToastShown() {
        _hasShownWelcomeToast.value = true
    }

    /**
     * Đăng ký tài khoản người dùng mới và khởi tạo dữ liệu metadata đi kèm.
     */
    fun onSignUp(email: String, pass: String, name: String, user: String, phone: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val availabilityResult = authRepository.checkAvailability(user, email)

            availabilityResult.onSuccess { response ->
                if (response.emailTaken) {
                    _authState.value = AuthState.Error(Constants.ValidationError.EMAIL_ALREADY_EXISTS)
                    return@launch
                }
                if (response.usernameTaken) {
                    _authState.value = AuthState.Error(Constants.ValidationError.USERNAME_ALREADY_EXISTS)
                    return@launch
                }

                val result = authRepository.signUpManual(email, pass, name, user, phone)
                result.onSuccess {
                    _authState.value = AuthState.Success
                }.onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Đăng ký thất bại")
                }
            }.onFailure {
                _authState.value = AuthState.Error("Lỗi kết nối hệ thống")
            }
        }
    }

    /**
     * Kết thúc phiên làm việc hiện tại và thu hồi các token xác thực.
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
            _authState.value = AuthState.Idle
            _hasShownWelcomeToast.value = false
        }
    }

    fun updateLocalAvatar(newAvatarUrl: String) {
        _currentUser.value = _currentUser.value?.copy(avatarUrl = newAvatarUrl)
    }

    /**
     * Cập nhật lại State cục bộ của User sau khi chỉnh sửa hồ sơ.
     * Hàm này giúp giao diện (Profile, Home...) phản ứng và tải lại dữ liệu mới ngay lập tức.
     */
    fun updateCurrentUser(updatedUser: User) {
        _currentUser.value = updatedUser
    }

}