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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeliveryBoyViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _assignedOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val assignedOrders = _assignedOrders.asStateFlow()

    private val _availableOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val availableOrders = _availableOrders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation = _currentLocation.asStateFlow()

    init {
        observeOrders()
    }

    private fun observeOrders() {
        val userId = repository.getCurrentUserId() ?: return
        viewModelScope.launch {
            repository.listenToDeliveryBoyOrders(userId).collect {
                _assignedOrders.value = it
            }
        }
        viewModelScope.launch {
            repository.listenToAvailableOrders().collect {
                _availableOrders.value = it
            }
        }
    }

    // For legacy Activity support
    fun observeAssignedOrders() {
        val userId = repository.getCurrentUserId() ?: return
        viewModelScope.launch {
            repository.listenToDeliveryBoyOrders(userId).collect {
                _assignedOrders.value = it
            }
        }
    }

    fun updateLocation(lat: Double, lng: Double) {
        _currentLocation.value = LatLng(lat, lng)
        val userId = repository.getCurrentUserId() ?: return
        viewModelScope.launch {
            repository.updateDeliveryLocation(userId, lat, lng)
        }
    }

    fun updateOrderStatus(orderId: String, status: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateOrderStatus(orderId, status)
            _isLoading.value = false
        }
    }

    fun markDelivered(orderId: String) {
        updateOrderStatus(orderId, "delivered")
    }

    fun startDelivery(context: Context, orderId: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, "out_for_delivery")
            startLocationTracking(context)
        }
    }

    fun startLocationTracking(context: Context) {
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
