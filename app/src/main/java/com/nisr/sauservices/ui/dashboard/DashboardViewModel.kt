package com.nisr.sauservices.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.repository.CartRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val repository = CartRepository()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId: String get() = auth.currentUser?.uid ?: ""

    private val _globalOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val globalOrders: StateFlow<List<OrderModel>> = _globalOrders

    private val _globalBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val globalBookings: StateFlow<List<BookingModel>> = _globalBookings

    init {
        observeGlobalData()
    }

    private fun observeGlobalData() {
        viewModelScope.launch {
            repository.getGlobalOrders().collect {
                _globalOrders.value = it
            }
        }
        viewModelScope.launch {
            repository.getGlobalBookings().collect {
                _globalBookings.value = it
            }
        }
    }

    // --- SERVICE WORKER ACTIONS ---
    fun updateBookingStatus(booking: BookingModel, newStatus: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(booking.bookingId, newStatus, currentUserId)
        }
    }

    // --- SHOPKEEPER ACTIONS ---
    fun updateOrderStatus(order: OrderModel, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(order.orderId, newStatus, currentUserId)
        }
    }
}
