package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class BookingItem(
    val id: String,
    val serviceName: String,
    val date: String,
    val time: String,
    val status: String, // Upcoming, Completed
    val price: String
)

class BookingsViewModel : ViewModel() {
    private val _bookings = mutableStateListOf<BookingItem>()
    val bookings: List<BookingItem> get() = _bookings

    fun addBooking(booking: BookingItem) {
        _bookings.add(0, booking)
    }

    fun cancelBooking(bookingId: String) {
        _bookings.removeAll { it.id == bookingId }
    }
}
