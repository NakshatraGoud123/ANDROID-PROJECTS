package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.Delivery
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.repository.DashboardRepository
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeliveryViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val dashboardRepository = DashboardRepository()

    private val _deliveries = MutableStateFlow<List<Delivery>>(emptyList())
    val deliveries: StateFlow<List<Delivery>> = _deliveries.asStateFlow()

    private val _availableDeliveries = MutableStateFlow<List<BookingModel>>(emptyList())
    val availableDeliveries = _availableDeliveries.asStateFlow()

    private val _myDeliveries = MutableStateFlow<List<BookingModel>>(emptyList())
    val myDeliveries = _myDeliveries.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        loadAllDeliveries()
    }

    private fun loadAllDeliveries() {
        viewModelScope.launch {
            dashboardRepository.listenToAvailableDeliveries().collect { list ->
                _deliveries.value = list
            }
        }
    }

    fun loadAvailableDeliveries() {
        viewModelScope.launch {
            repository.observeAvailableBookings("delivery").collect { list ->
                _availableDeliveries.value = list
            }
        }
    }

    fun loadMyDeliveries(userId: String) {
        viewModelScope.launch {
            repository.observeMyBookings("delivery", userId).collect { list ->
                _myDeliveries.value = list
            }
        }
    }

    fun acceptDelivery(bookingId: String, userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBookingStatus(bookingId, "accepted", userId).onSuccess {
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
