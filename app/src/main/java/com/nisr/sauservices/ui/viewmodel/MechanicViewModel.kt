package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.nisr.sauservices.data.model.MechanicServiceItem

data class MechanicBookingState(
    val categoryId: String = "",
    val subcategoryId: String = "",
    val vehicleType: String = "",
    val description: String = "",
    val location: String = "Current Location Detected",
    val mechanicId: String = "",
    val estimatedCost: Double = 0.0,
    val paymentMethod: String = "Cash",
    val selectedService: MechanicServiceItem? = null
)

class MechanicViewModel : ViewModel() {
    private val _bookingState = mutableStateOf(MechanicBookingState())
    val bookingState: State<MechanicBookingState> = _bookingState

    fun updateCategoryId(id: String) {
        _bookingState.value = _bookingState.value.copy(categoryId = id)
    }

    fun updateSubcategoryId(id: String) {
        _bookingState.value = _bookingState.value.copy(subcategoryId = id)
    }

    fun updateVehicleType(type: String) {
        _bookingState.value = _bookingState.value.copy(vehicleType = type)
    }

    fun updateDescription(desc: String) {
        _bookingState.value = _bookingState.value.copy(description = desc)
    }

    fun selectService(service: MechanicServiceItem) {
        _bookingState.value = _bookingState.value.copy(
            selectedService = service,
            estimatedCost = service.price
        )
    }

    fun updatePaymentMethod(method: String) {
        _bookingState.value = _bookingState.value.copy(paymentMethod = method)
    }
}
