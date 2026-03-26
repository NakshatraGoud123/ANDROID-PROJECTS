package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.nisr.sauservices.data.model.FirebaseUser
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseAuthViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _userState = MutableStateFlow<FirebaseUser?>(null)
    val userState = _userState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun register(email: String, pass: String, name: String, phone: String, role: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
                val uid = authResult.user?.uid ?: ""
                val newUser = FirebaseUser(
                    userId = uid,
                    name = name,
                    email = email,
                    phone = phone,
                    role = role,
                    status = if (role == "CUSTOMER") "APPROVED" else "PENDING"
                )
                
                repository.registerUser(newUser).fold(
                    onSuccess = {
                        _userState.value = newUser
                    },
                    onFailure = { throwable ->
                        _errorMessage.value = throwable.message
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val authResult = auth.signInWithEmailAndPassword(email, pass).await()
                val uid = authResult.user?.uid ?: ""
                
                repository.getUserProfile(uid).fold(
                    onSuccess = { user ->
                        _userState.value = user
                    },
                    onFailure = { throwable ->
                        _errorMessage.value = throwable.message
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        auth.signOut()
        _userState.value = null
    }
}
