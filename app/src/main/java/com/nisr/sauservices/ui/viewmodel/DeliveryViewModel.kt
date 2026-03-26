package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.Delivery
import com.nisr.sauservices.data.model.FirestoreBooking
import com.nisr.sauservices.data.repository.DashboardRepository
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeliveryViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val dashboardRepository = DashboardRepository()

    val deliveries: LiveData<List<Delivery>> = dashboardRepository.getDeliveries()

    private val _availableDeliveries = MutableStateFlow<List<FirestoreBooking>>(emptyList())
    val availableDeliveries = _availableDeliveries.asStateFlow()

    private val _myDeliveries = MutableStateFlow<List<FirestoreBooking>>(emptyList())
    val myDeliveries = _myDeliveries.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun loadAvailableDeliveries() {
        viewModelScope.launch {
            repository.observeAvailableBookings("delivery").collect {
                _availableDeliveries.value = it
            }
        }
    }

    fun loadMyDeliveries(userId: String) {
        viewModelScope.launch {
            repository.observeMyBookings("delivery", userId).collect {
                _myDeliveries.value = it
            }
        }
    }

    fun acceptDelivery(bookingId: String, userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.acceptBooking(bookingId, "delivery", userId).onSuccess {
                _errorMessage.value = null
            }.onFailure {
                _errorMessage.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun updateDeliveryStatus(bookingId: String, status: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBookingStatus(bookingId, status).onSuccess {
                _errorMessage.value = null
            }.onFailure {
                _errorMessage.value = it.message
            }
            _isLoading.value = false
        }
    }
}
