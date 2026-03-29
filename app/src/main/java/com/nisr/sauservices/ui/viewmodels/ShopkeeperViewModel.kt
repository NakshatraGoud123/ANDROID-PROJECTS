package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShopkeeperViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _pendingOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val pendingOrders = _pendingOrders.asStateFlow()

    private val _acceptedOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val acceptedOrders = _acceptedOrders.asStateFlow()

    private val _assignedOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val assignedOrders = _assignedOrders.asStateFlow()

    private val _deliveryBoyLocation = MutableStateFlow<LatLng?>(null)
    val deliveryBoyLocation = _deliveryBoyLocation.asStateFlow()

    init {
        observePendingOrders()
        observeAcceptedOrders()
        observeAssignedOrders()
    }

    private fun observePendingOrders() {
        viewModelScope.launch {
            repository.getPendingOrders().collect { _pendingOrders.value = it }
        }
    }

    private fun observeAcceptedOrders() {
        viewModelScope.launch {
            repository.getAcceptedOrders().collect { _acceptedOrders.value = it }
        }
    }

    private fun observeAssignedOrders() {
        viewModelScope.launch {
            repository.getAssignedOrdersForShop().collect { _assignedOrders.value = it }
        }
    }

    fun acceptOrder(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "accepted")
        }
    }

    fun updateOrderStatus(orderId: String, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
        }
    }

    fun assignDeliveryBoy(orderId: String, deliveryBoyId: String) {
        viewModelScope.launch {
            repository.assignDeliveryBoy(orderId, deliveryBoyId)
        }
    }

    fun observeDeliveryBoyLocation(deliveryBoyId: String) {
        viewModelScope.launch {
            repository.observeDeliveryBoyLocation(deliveryBoyId).collect { location ->
                location?.let {
                    _deliveryBoyLocation.value = LatLng(it.lat, it.lng)
                }
            }
        }
    }
}
