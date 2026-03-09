package com.nisr.sauservices.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.LoginResponse
import com.nisr.sauservices.data.model.User
import com.nisr.sauservices.data.repository.SauRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val repository = SauRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = repository.login(mapOf("email" to email, "password" to password))
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.user
                    if (user != null) {
                        _authState.value = AuthState.Success(user)
                    } else {
                        _authState.value = AuthState.Error("User data not found")
                    }
                } else {
                    _authState.value = AuthState.Error(response.message() ?: "Login failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun register(userData: Map<String, String>) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = repository.register(userData)
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.user
                    if (user != null) {
                        _authState.value = AuthState.Success(user)
                    } else {
                        _authState.value = AuthState.Error("Registration successful but user data missing")
                    }
                } else {
                    _authState.value = AuthState.Error(response.message() ?: "Registration failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
