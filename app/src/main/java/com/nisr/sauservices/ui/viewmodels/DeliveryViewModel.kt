package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.Order
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeliveryViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _assignedDeliveries = MutableStateFlow<List<Order>>(emptyList())
    val assignedDeliveries = _assignedDeliveries.asStateFlow()

    private val _isOnline = MutableStateFlow(false)
    val isOnline = _isOnline.asStateFlow()

    fun observeDeliveries(partnerId: String) {
        viewModelScope.launch {
            repository.observeDeliveryTasks(partnerId).collect {
                _assignedDeliveries.value = it
            }
        }
    }

    fun toggleOnlineStatus(partnerId: String, online: Boolean) {
        viewModelScope.launch {
            // Update online status in Firestore 'users' collection
            _isOnline.value = online
        }
    }

    fun updateDeliveryStatus(orderId: String, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
        }
    }
}
