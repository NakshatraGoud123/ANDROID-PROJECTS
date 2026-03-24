package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.nisr.sauservices.data.model.FirebaseUser
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
            try {
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = task.result?.user?.uid ?: ""
                        val newUser = FirebaseUser(
                            userId = uid,
                            name = name,
                            email = email,
                            phone = phone,
                            role = role,
                            status = if (role == "CUSTOMER") "APPROVED" else "PENDING"
                        )
                        viewModelScope.launch {
                            repository.registerUser(newUser).onSuccess {
                                _userState.value = newUser
                            }.onFailure {
                                _errorMessage.value = it.message
                            }
                        }
                    } else {
                        _errorMessage.value = task.exception?.message
                    }
                }
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
            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid ?: ""
                    viewModelScope.launch {
                        repository.getUserProfile(uid).onSuccess {
                            _userState.value = it
                        }.onFailure {
                            _errorMessage.value = it.message
                        }
                    }
                } else {
                    _errorMessage.value = task.exception?.message
                }
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        auth.signOut()
        _userState.value = null
    }
}
