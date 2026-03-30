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
            repository.listenToCustomerOrder(orderId).collect { order ->
                _order.value = order
                order?.liveLocation?.let {
                    _deliveryBoyLocation.value = LatLng(it.lat, it.lng)
                }
            }
        }
    }
}
