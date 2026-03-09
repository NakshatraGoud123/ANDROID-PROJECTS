package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.Order
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkerViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _assignedJobs = MutableStateFlow<List<Order>>(emptyList())
    val assignedJobs = _assignedJobs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadJobs(workerId: String) {
        viewModelScope.launch {
            repository.observeWorkerJobs(workerId).collect {
                _assignedJobs.value = it
            }
        }
    }

    fun updateJobStatus(orderId: String, status: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateOrderStatus(orderId, status).onSuccess {
                // Status updated
            }.onFailure {
                // Handle error
            }
            _isLoading.value = false
        }
    }

    // Additional logic for availability can be added to User profile
    fun setAvailability(workerId: String, isAvailable: Boolean) {
        viewModelScope.launch {
            // Update 'status' or a new 'isAvailable' field in users collection
        }
    }
}
