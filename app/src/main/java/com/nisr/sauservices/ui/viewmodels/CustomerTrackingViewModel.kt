package com.nisr.sauservices.ui.viewmodels

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

    private val _trackedOrder = MutableStateFlow<OrderModel?>(null)
    val trackedOrder = _trackedOrder.asStateFlow()

    private val _deliveryLocation = MutableStateFlow<LatLng?>(null)
    val deliveryLocation = _deliveryLocation.asStateFlow()

    fun trackOrder(orderId: String) {
        viewModelScope.launch {
            repository.listenToCustomerOrder(orderId).collect { order ->
                _trackedOrder.value = order
                order?.liveLocation?.let {
                    _deliveryLocation.value = LatLng(it.lat, it.lng)
                }
            }
        }
    }
}
