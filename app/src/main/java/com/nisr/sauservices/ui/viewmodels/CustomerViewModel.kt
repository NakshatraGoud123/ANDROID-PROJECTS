package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.model.FirestoreBooking
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomerViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _myBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val myBookings = _myBookings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _currentBookingStatus = MutableStateFlow<String?>(null)
    val currentBookingStatus = _currentBookingStatus.asStateFlow()

    fun loadMyBookings(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.observeMyBookings("customer", userId).collect {
                _myBookings.value = it
                _isLoading.value = false
            }
        }
    }

    fun placeBooking(booking: BookingModel) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.bookService(booking).onSuccess {
                // Success
            }.onFailure {
                // Handle error
            }
            _isLoading.value = false
        }
    }

    // Overload for compatibility with screens passing FirestoreBooking
    fun placeBooking(booking: FirestoreBooking) {
        val model = BookingModel(
            serviceName = booking.serviceType ?: "",
            address = booking.address,
            status = "pending"
        )
        placeBooking(model)
    }

    fun observeBookingStatus(bookingId: String) {
        viewModelScope.launch {
            repository.observeBookingStatus(bookingId).collect { status ->
                _currentBookingStatus.value = status
            }
        }
    }
}
