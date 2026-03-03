package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.nisr.sauservices.data.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository = UserRepository()) : ViewModel() {

    private val _authState = mutableStateOf<AuthState>(AuthState.Idle)
    val authState: State<AuthState> = _authState

    val currentUser: FirebaseUser?
        get() = userRepository.getCurrentUser()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.signIn(email, password)
            _authState.value = result.fold(
                onSuccess = { user ->
                    if (user != null) {
                        val dataResult = userRepository.getUserData(user.uid)
                        dataResult.fold(
                            onSuccess = { AuthState.Success(user, it) },
                            onFailure = { AuthState.Error(it.message ?: "Failed to fetch user data") }
                        )
                    } else {
                        AuthState.Error("User not found")
                    }
                },
                onFailure = { AuthState.Error(it.message ?: "Login failed") }
            )
        }
    }

    fun signInWithGoogle(credential: AuthCredential, role: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.signInWithCredential(credential)
            _authState.value = result.fold(
                onSuccess = { user ->
                    if (user != null) {
                        // Check if user data exists, if not create it with the role
                        val dataResult = userRepository.getUserData(user.uid)
                        dataResult.fold(
                            onSuccess = { existingData ->
                                if (existingData == null) {
                                    val newData = mapOf(
                                        "fullName" to (user.displayName ?: ""),
                                        "email" to (user.email ?: ""),
                                        "role" to role,
                                        "phoneNumber" to (user.phoneNumber ?: "")
                                    )
                                    viewModelScope.launch {
                                        userRepository.saveUserData(user.uid, newData)
                                    }
                                    AuthState.Success(user, newData)
                                } else {
                                    AuthState.Success(user, existingData)
                                }
                            },
                            onFailure = { AuthState.Error(it.message ?: "Failed to fetch user data") }
                        )
                    } else {
                        AuthState.Error("Google Sign-In failed")
                    }
                },
                onFailure = { AuthState.Error(it.message ?: "Google Sign-In failed") }
            )
        }
    }

    fun signUp(email: String, password: String, userData: Map<String, Any>) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.signUp(email, password, userData)
            _authState.value = result.fold(
                onSuccess = { AuthState.Success(it, userData) },
                onFailure = { AuthState.Error(it.message ?: "Registration failed") }
            )
        }
    }

    fun logout() {
        userRepository.logout()
        _authState.value = AuthState.Idle
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser?, val userData: Map<String, Any>? = null) : AuthState()
    data class Error(val message: String) : AuthState()
}
