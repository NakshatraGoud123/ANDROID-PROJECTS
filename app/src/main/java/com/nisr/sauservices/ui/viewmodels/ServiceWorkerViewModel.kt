package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ServiceWorkerViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _pendingBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val pendingBookings = _pendingBookings.asStateFlow()

    private val _acceptedBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val acceptedBookings = _acceptedBookings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        observePendingBookings()
        observeAcceptedBookings()
    }

    private fun observePendingBookings() {
        viewModelScope.launch {
            repository.getPendingBookings().collect { _pendingBookings.value = it }
        }
    }

    private fun observeAcceptedBookings() {
        viewModelScope.launch {
            repository.getAcceptedBookings().collect { _acceptedBookings.value = it }
        }
    }

    fun acceptBooking(bookingId: String, workerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBookingStatus(bookingId, "accepted", workerId)
            _isLoading.value = false
        }
    }

    fun completeBooking(bookingId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBookingStatus(bookingId, "completed")
            _isLoading.value = false
        }
    }
}
