package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nisr.sauservices.data.model.Booking
import com.nisr.sauservices.data.repository.DashboardRepository

class ServiceWorkerViewModel : ViewModel() {
    private val repository = DashboardRepository()
    val bookings: LiveData<List<Booking>> = repository.getBookings()

    fun updateBookingStatus(booking: Booking, newStatus: String) {
        booking.status = newStatus
    }
}
