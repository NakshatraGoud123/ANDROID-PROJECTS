package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ServiceWorkerViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val workerId = repository.getCurrentUserId() ?: ""

    private val _pendingBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val pendingBookings = _pendingBookings.asStateFlow()

    private val _acceptedBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val acceptedBookings = _acceptedBookings.asStateFlow()

    private val _completedBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val completedBookings = _completedBookings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        if (workerId.isNotEmpty()) {
            observeBookings()
        }
    }

    private fun observeBookings() {
        viewModelScope.launch {
            repository.listenToBookings().collect { allBookings ->
                _pendingBookings.value = allBookings.filter { it.status == "pending" }
                _acceptedBookings.value = allBookings.filter { it.status == "accepted" && it.workerId == workerId }
                _completedBookings.value = allBookings.filter { it.status == "completed" && it.workerId == workerId }
            }
        }
    }

    fun acceptBooking(bookingId: String) {
        if (workerId.isEmpty()) return
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, "accepted", workerId)
        }
    }

    fun completeBooking(bookingId: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, "completed", workerId)
        }
    }
}
