package com.nisr.sauservices.ui.viewmodels

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.repository.FirebaseRepository
import com.nisr.sauservices.location.LocationService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DeliveryBoyViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val userId = repository.getCurrentUserId() ?: ""

    private val _assignedOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val assignedOrders = _assignedOrders.asStateFlow()

    private val _availableOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val availableOrders = _availableOrders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation = _currentLocation.asStateFlow()

    init {
        if (userId.isNotEmpty()) {
            observeOrders()
        }
    }

    private fun observeOrders() {
        viewModelScope.launch {
            repository.listenToDeliveryBoyOrders(userId).collect {
                _assignedOrders.value = it
            }
        }
        viewModelScope.launch {
            repository.listenToOrders().collect { allOrders ->
                _availableOrders.value = allOrders.filter { it.orderStatus == "accepted" }
            }
        }
    }

    fun updateLocation(lat: Double, lng: Double) {
        _currentLocation.value = LatLng(lat, lng)
        if (userId.isEmpty()) return
        viewModelScope.launch {
            repository.updateDeliveryLocation(userId, lat, lng)
        }
    }

    fun startDelivery(context: Context, orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "out_for_delivery")
            startLocationTracking(context)
        }
    }

    fun markDelivered(orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "delivered")
        }
    }

    private fun startLocationTracking(context: Context) {
        val intent = Intent(context, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopLocationTracking(context: Context) {
        context.stopService(Intent(context, LocationService::class.java))
    }
}
