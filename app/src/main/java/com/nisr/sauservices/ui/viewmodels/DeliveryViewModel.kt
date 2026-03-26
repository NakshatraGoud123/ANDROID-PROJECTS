package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.FirestoreBooking
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeliveryViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _assignedDeliveries = MutableStateFlow<List<FirestoreBooking>>(emptyList())
    val assignedDeliveries = _assignedDeliveries.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun observeDeliveries(deliveryBoyId: String) {
        viewModelScope.launch {
            repository.observeMyBookings("delivery", deliveryBoyId).collect {
                _assignedDeliveries.value = it
            }
        }
    }

    fun updateDeliveryStatus(bookingId: String, status: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBookingStatus(bookingId, status)
            _isLoading.value = false
        }
    }

    fun pickUpOrder(bookingId: String) {
        updateDeliveryStatus(bookingId, "out_for_delivery")
    }

    fun deliverOrder(bookingId: String) {
        updateDeliveryStatus(bookingId, "delivered")
    }
}
