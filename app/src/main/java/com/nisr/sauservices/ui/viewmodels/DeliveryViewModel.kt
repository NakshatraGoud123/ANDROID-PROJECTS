package com.nisr.sauservices.ui.viewmodels

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.repository.FirebaseRepository
import com.nisr.sauservices.service.LocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeliveryViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _assignedOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val assignedOrders = _assignedOrders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun observeAssignedOrders(deliveryBoyId: String) {
        viewModelScope.launch {
            repository.observeAssignedOrders(deliveryBoyId).collect {
                _assignedOrders.value = it
            }
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

    fun startLocationUpdates(context: Context, userId: String) {
        val intent = Intent(context, LocationService::class.java).apply {
            putExtra("USER_ID", userId)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopLocationUpdates(context: Context) {
        context.stopService(Intent(context, LocationService::class.java))
    }
}
