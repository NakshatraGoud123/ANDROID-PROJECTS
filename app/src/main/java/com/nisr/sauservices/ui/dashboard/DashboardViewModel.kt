package com.nisr.sauservices.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.OrderModel
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

    private val _globalBookings = MutableStateFlow<List<OrderModel>>(emptyList())
    val globalBookings: StateFlow<List<OrderModel>> = _globalBookings

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
    fun updateBookingStatus(order: OrderModel, newStatus: String) {
        viewModelScope.launch {
            // In a real app, 'order.userId' should be the customer's ID
            // Here we assume OrderModel contains the customer's userId or we find it
            repository.updateBookingStatus("anonymous", order.orderId, newStatus, currentUserId)
        }
    }

    // --- SHOPKEEPER ACTIONS ---
    fun updateOrderStatus(order: OrderModel, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus("anonymous", order.orderId, newStatus, currentUserId)
        }
    }
}
