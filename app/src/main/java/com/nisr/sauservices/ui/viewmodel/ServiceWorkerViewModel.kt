package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nisr.sauservices.data.model.Booking
import com.nisr.sauservices.data.repository.DashboardRepository

class ServiceWorkerViewModel : ViewModel() {
    private val repository = DashboardRepository()
    
    private val _bookings = repository.getBookings() as MutableLiveData<List<Booking>>
    val bookings: LiveData<List<Booking>> = _bookings

    fun updateBookingStatus(bookingId: String, newStatus: String) {
        val currentBookings = _bookings.value?.toMutableList() ?: return
        val index = currentBookings.indexOfFirst { it.bookingId == bookingId }
        if (index != -1) {
            currentBookings[index] = currentBookings[index].copy(status = newStatus)
            _bookings.value = currentBookings
        }
    }
}
