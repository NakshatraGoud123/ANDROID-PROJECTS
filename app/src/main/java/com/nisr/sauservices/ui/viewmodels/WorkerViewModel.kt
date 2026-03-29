package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkerViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _assignedJobs = MutableStateFlow<List<BookingModel>>(emptyList())
    val assignedJobs = _assignedJobs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadJobs(workerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.observeMyBookings("worker", workerId).collect { list ->
                    _assignedJobs.value = list
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun updateJobStatus(bookingId: String, status: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBookingStatus(bookingId, status).onSuccess {
                _error.value = null
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun markJobCompleted(bookingId: String) {
        updateJobStatus(bookingId, "completed")
    }

    fun startJob(bookingId: String) {
        updateJobStatus(bookingId, "in_progress")
    }
}
