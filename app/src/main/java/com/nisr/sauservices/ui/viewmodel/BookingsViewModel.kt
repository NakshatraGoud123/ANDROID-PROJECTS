package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.Booking
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BookingItem(
    val id: String,
    val serviceName: String,
    val date: String,
    val time: String,
    val status: String, // Upcoming, Completed
    val price: String
)

class BookingsViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val _bookings = mutableStateListOf<BookingItem>()
    val bookings: List<BookingItem> get() = _bookings

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _bookingResult = MutableStateFlow<Result<String>?>(null)
    val bookingResult = _bookingResult.asStateFlow()

    fun confirmBooking(
        customerName: String,
        serviceType: String,
        address: String,
        timeSlot: String,
        price: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val newBooking = Booking(
                bookingId = "", // Will be set by repository
                customerName = customerName,
                serviceType = serviceType,
                address = address,
                timeSlot = timeSlot,
                status = "placed",
                price = price
            )
            val result = repository.createBooking(newBooking)
            _bookingResult.value = result
            if (result.isSuccess) {
                // Optionally add to local list or refresh from DB
                _bookings.add(0, BookingItem(
                    id = result.getOrThrow(),
                    serviceName = serviceType,
                    date = "Today", // Simplified
                    time = timeSlot,
                    status = "Upcoming",
                    price = price
                ))
            }
            _isLoading.value = false
        }
    }

    fun addBooking(booking: BookingItem) {
        _bookings.add(0, booking)
    }

    fun cancelBooking(bookingId: String) {
        _bookings.removeAll { it.id == bookingId }
    }
}
