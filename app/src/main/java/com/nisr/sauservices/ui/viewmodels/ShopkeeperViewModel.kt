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

    private val _pendingOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val pendingOrders = _pendingOrders.asStateFlow()

    private val _acceptedOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val acceptedOrders = _acceptedOrders.asStateFlow()

    private val _assignedOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val assignedOrders = _assignedOrders.asStateFlow()

    private val _deliveryBoys = MutableStateFlow<List<FirebaseUser>>(emptyList())
    val deliveryBoys = _deliveryBoys.asStateFlow()

    private val _deliveryBoyLocation = MutableStateFlow<LatLng?>(null)
    val deliveryBoyLocation = _deliveryBoyLocation.asStateFlow()

    init {
        observeAllOrders()
        loadDeliveryBoys()
    }

    private fun observeAllOrders() {
        viewModelScope.launch {
            repository.listenToShopkeeperOrders().collect { allOrders ->
                _pendingOrders.value = allOrders.filter { it.orderStatus == "pending" }
                _acceptedOrders.value = allOrders.filter { it.orderStatus == "accepted" }
                _assignedOrders.value = allOrders.filter { it.orderStatus == "assigned" || it.orderStatus == "out_for_delivery" }
            }
        }
    }

    private fun loadDeliveryBoys() {
        viewModelScope.launch {
            _deliveryBoys.value = repository.getDeliveryBoys()
        }
    }

    fun acceptOrder(orderId: String) {
        val shopkeeperId = repository.getCurrentUserId() ?: return
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
            repository.observeOrderTracking(deliveryBoyId).collect { order ->
                order?.liveLocation?.let {
                    _deliveryBoyLocation.value = LatLng(it.lat, it.lng)
                }
            }
        }
    }
}
