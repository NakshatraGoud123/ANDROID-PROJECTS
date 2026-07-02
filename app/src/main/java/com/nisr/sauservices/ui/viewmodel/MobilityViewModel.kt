package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.nisr.sauservices.data.model.MobilityServiceType

data class MobilityBookingState(
    val serviceType: MobilityServiceType? = null,
    val pickupLocation: String = "",
    val destinationLocation: String = "",
    val fareEstimate: Double = 0.0,
    val selectedDriver: String = "",
    val paymentMethod: String = "Cash",
    val isTracking: Boolean = false
)

class MobilityViewModel : ViewModel() {
    private val _uiState = mutableStateOf(MobilityBookingState())
    val uiState: State<MobilityBookingState> = _uiState

    fun selectServiceType(type: MobilityServiceType) {
        _uiState.value = _uiState.value.copy(serviceType = type)
    }

    fun setLocations(pickup: String, destination: String) {
        // Mock fare estimation based on "distance"
        val distance = 5.0 // Mock distance
        val base = _uiState.value.serviceType?.baseFare ?: 0.0
        val perKm = _uiState.value.serviceType?.perKmFare ?: 0.0
        val estimate = base + (perKm * distance)
        
        _uiState.value = _uiState.value.copy(
            pickupLocation = pickup,
            destinationLocation = destination,
            fareEstimate = estimate
        )
    }

    fun updatePaymentMethod(method: String) {
        _uiState.value = _uiState.value.copy(paymentMethod = method)
    }

    fun startTracking() {
        _uiState.value = _uiState.value.copy(isTracking = true)
    }
}
