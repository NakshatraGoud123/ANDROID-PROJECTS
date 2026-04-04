package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.model.OrderModel
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
        observeAllServiceRequests()
    }

    private fun observeAllServiceRequests() {
        viewModelScope.launch {
            // Combine data from /bookings and /orders (that have schedules)
            combine(
                repository.listenToBookings(),
                repository.listenToOrders()
            ) { bookings, orders ->
                // Convert relevant orders to BookingModel format
                val scheduledOrders = orders.filter { it.scheduleDate != null }.map { it.toBookingModel() }
                bookings + scheduledOrders
            }.collect { allRequests ->
                _pendingBookings.value = allRequests.filter { 
                    it.status.lowercase() == "pending" || it.status.lowercase() == "placed" 
                }
                
                if (workerId.isNotEmpty()) {
                    _acceptedBookings.value = allRequests.filter { 
                        it.status.lowercase() == "accepted" && it.workerId == workerId 
                    }
                    _completedBookings.value = allRequests.filter { 
                        it.status.lowercase() == "completed" && it.workerId == workerId 
                    }
                }
            }
        }
    }

    private fun OrderModel.toBookingModel() = BookingModel(
        bookingId = orderId,
        customerId = customerId,
        serviceName = serviceName.ifEmpty { "Service Booking" },
        scheduledDate = scheduleDate ?: "",
        scheduledTime = scheduleTime ?: "",
        status = orderStatus.ifEmpty { "pending" },
        address = address,
        workerId = assignedDeliveryBoy // Reusing field for technician mapping
    )

    fun acceptBooking(bookingId: String) {
        if (workerId.isEmpty() || bookingId.isEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            // Update both possible paths
            repository.updateBookingStatus(bookingId, "accepted", workerId)
            repository.updateOrderStatus(bookingId, "accepted")
            _isLoading.value = false
        }
    }

    fun completeBooking(bookingId: String) {
        if (bookingId.isEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBookingStatus(bookingId, "completed", workerId)
            repository.updateOrderStatus(bookingId, "completed")
            _isLoading.value = false
        }
    }
}
