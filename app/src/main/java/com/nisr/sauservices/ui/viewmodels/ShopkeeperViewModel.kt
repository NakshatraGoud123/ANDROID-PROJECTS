package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.nisr.sauservices.data.model.FirebaseUser
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShopkeeperViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val shopkeeperId = repository.getCurrentUserId() ?: ""

    private val _pendingOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val pendingOrders = _pendingOrders.asStateFlow()

    private val _acceptedOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val acceptedOrders = _acceptedOrders.asStateFlow()

    private val _assignedOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val assignedOrders = _assignedOrders.asStateFlow()

    private val _completedOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val completedOrders = _completedOrders.asStateFlow()

    private val _deliveryBoys = MutableStateFlow<List<FirebaseUser>>(emptyList())
    val deliveryBoys = _deliveryBoys.asStateFlow()

    private val _deliveryBoyLocation = MutableStateFlow<LatLng?>(null)
    val deliveryBoyLocation = _deliveryBoyLocation.asStateFlow()

    init {
        if (shopkeeperId.isNotEmpty()) {
            observeOrders()
            loadDeliveryBoys()
        }
    }

    private fun observeOrders() {
        viewModelScope.launch {
            repository.listenToShopkeeperOrders().collect { allOrders ->
                // Filtering based on status. In a real app, maybe filter by shopkeeperId too.
                _pendingOrders.value = allOrders.filter { it.orderStatus == "pending" }
                _acceptedOrders.value = allOrders.filter { it.orderStatus == "accepted" }
                _assignedOrders.value = allOrders.filter { it.orderStatus == "assigned" || it.orderStatus == "out_for_delivery" }
                _completedOrders.value = allOrders.filter { it.orderStatus == "delivered" }
            }
        }
    }

    private fun loadDeliveryBoys() {
        viewModelScope.launch {
            _deliveryBoys.value = repository.getDeliveryBoys()
        }
    }

    fun acceptOrder(orderId: String) {
        if (shopkeeperId.isEmpty()) return
        viewModelScope.launch {
            repository.acceptOrder(orderId, shopkeeperId)
        }
    }

    fun assignDeliveryBoy(orderId: String, deliveryBoyId: String) {
        viewModelScope.launch {
            repository.assignDeliveryBoy(orderId, deliveryBoyId)
        }
    }

    fun updateOrderStatus(orderId: String, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
        }
    }

    fun observeDeliveryBoyLocation(deliveryBoyId: String) {
        viewModelScope.launch {
            repository.listenToDeliveryLocations().collect { locations ->
                locations[deliveryBoyId]?.let {
                    _deliveryBoyLocation.value = LatLng(it.lat, it.lng)
                }
            }
        }
    }
}
