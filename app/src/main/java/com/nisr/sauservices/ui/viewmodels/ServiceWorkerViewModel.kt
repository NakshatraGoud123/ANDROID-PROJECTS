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

    private val _myBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val myBookings = _myBookings.asStateFlow()

    init {
        observeBookings()
    }

    private fun observeBookings() {
        viewModelScope.launch {
            repository.getBookingsByStatus(listOf("pending")).collect {
                _pendingBookings.value = it
            }
        }
        
        repository.getCurrentUserId()?.let { userId ->
            viewModelScope.launch {
                repository.getBookingsByStatus(listOf("accepted")).collect { list ->
                    _myBookings.value = list.filter { it.workerId == userId }
                }
            }
        }
    }

    fun acceptBooking(bookingId: String) {
        val userId = repository.getCurrentUserId() ?: return
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, "accepted", workerId = userId)
        }
    }

    fun completeBooking(bookingId: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, "completed")
        }
    }
}
