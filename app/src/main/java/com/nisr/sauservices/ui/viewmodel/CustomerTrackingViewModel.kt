package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomerTrackingViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _order = MutableStateFlow<OrderModel?>(null)
    val order = _order.asStateFlow()

    private val _deliveryBoyLocation = MutableStateFlow<LatLng?>(null)
    val deliveryBoyLocation = _deliveryBoyLocation.asStateFlow()

    fun trackOrder(orderId: String) {
        viewModelScope.launch {
            repository.observeOrder(orderId).collect { order ->
                _order.value = order
                order?.let {
                    // Update delivery boy marker from the order's liveLocation field
                    _deliveryBoyLocation.value = LatLng(it.liveLocation.lat, it.liveLocation.lng)
                    
                    // Alternatively, observe the delivery_locations node for higher frequency updates
                    if (it.assignedDeliveryBoy.isNotEmpty()) {
                        observeDeliveryBoy(it.assignedDeliveryBoy)
                    }
                }
            }
        }
    }

    private fun observeDeliveryBoy(deliveryBoyId: String) {
        viewModelScope.launch {
            repository.observeDeliveryBoyLocation(deliveryBoyId).collect { location ->
                location?.let {
                    _deliveryBoyLocation.value = LatLng(it.lat, it.lng)
                }
            }
        }
    }
}
