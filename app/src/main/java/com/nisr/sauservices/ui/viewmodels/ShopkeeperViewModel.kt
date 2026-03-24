package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.Booking
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShopkeeperViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings = _bookings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadShopBookings(shopkeeperId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.observeBookingsByRole("SHOPKEEPER", shopkeeperId).collect {
                _bookings.value = it
                _isLoading.value = false
            }
        }
    }

    fun acceptBooking(bookingId: String, shopkeeperId: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, "accepted", shopkeeperId = shopkeeperId)
        }
    }

    fun assignWorker(bookingId: String, workerId: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, "assigned", workerId = workerId)
        }
    }
    
    fun assignDeliveryBoy(bookingId: String, deliveryBoyId: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, "assigned", deliveryBoyId = deliveryBoyId)
        }
    }
}
