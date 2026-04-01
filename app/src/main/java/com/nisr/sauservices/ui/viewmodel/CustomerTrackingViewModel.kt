package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomerTrackingViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _trackedOrder = MutableStateFlow<OrderModel?>(null)
    val trackedOrder = _trackedOrder.asStateFlow()

    private val _deliveryLocation = MutableStateFlow<LatLng?>(null)
    val deliveryLocation = _deliveryLocation.asStateFlow()

    private var locationJob: Job? = null

    fun trackOrder(orderId: String) {
        viewModelScope.launch {
            repository.listenToCustomerOrder(orderId).collect { order ->
                _trackedOrder.value = order
                
                // If order is assigned to a delivery boy, track their specific live location
                if (order != null && order.assignedDeliveryBoy.isNotEmpty()) {
                    startTrackingDeliveryBoy(order.assignedDeliveryBoy)
                } else if (order?.liveLocation != null) {
                    // Fallback to order's internal liveLocation if no delivery boy is assigned yet
                    _deliveryLocation.value = LatLng(order.liveLocation.lat, order.liveLocation.lng)
                }
            }
        }
    }

    private fun startTrackingDeliveryBoy(deliveryBoyId: String) {
        // Stop previous job if tracking a different boy
        locationJob?.cancel()
        locationJob = viewModelScope.launch {
            repository.observeDeliveryBoyLocation(deliveryBoyId).collect { location ->
                location?.let {
                    _deliveryLocation.value = LatLng(it.lat, it.lng)
                }
            }
        }
    }
}
