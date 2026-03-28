package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeliveryBoyViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _assignedOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val assignedOrders = _assignedOrders.asStateFlow()

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation = _currentLocation.asStateFlow()

    fun observeAssignedOrders() {
        val userId = repository.getCurrentUserId() ?: return
        viewModelScope.launch {
            repository.getAssignedOrders(userId).collect {
                _assignedOrders.value = it
            }
        }
    }

    fun updateLocation(lat: Double, lng: Double) {
        val userId = repository.getCurrentUserId() ?: return
        _currentLocation.value = LatLng(lat, lng)
        viewModelScope.launch {
            repository.updateDeliveryLocation(userId, lat, lng)
            // Update live location for all assigned orders
            _assignedOrders.value.forEach { order ->
                repository.updateOrderLiveLocation(order.orderId, lat, lng)
            }
        }
    }

    fun markDelivered(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "delivered")
        }
    }
}
