package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.PLSBooking
import com.nisr.sauservices.data.model.PLSService
import com.nisr.sauservices.data.repository.PropertyLifestyleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PropertyLifestyleViewModel : ViewModel() {
    private val repository = PropertyLifestyleRepository()

    private val _services = MutableStateFlow<List<PLSService>>(emptyList())
    val services = _services.asStateFlow()

    private val _userBookings = MutableStateFlow<List<PLSBooking>>(emptyList())
    val userBookings = _userBookings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _bookingResult = MutableStateFlow<Result<String>?>(null)
    val bookingResult = _bookingResult.asStateFlow()

    private val _currentBooking = MutableStateFlow<PLSBooking?>(null)
    val currentBooking = _currentBooking.asStateFlow()

    fun loadServices(subcategory: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getServices(subcategory).collect {
                _services.value = it
                _isLoading.value = false
            }
        }
    }

    fun loadUserBookings(userId: String) {
        viewModelScope.launch {
            repository.getBookings(userId).collect {
                _userBookings.value = it
            }
        }
    }

    fun setCurrentBooking(booking: PLSBooking) {
        _currentBooking.value = booking
    }

    fun placeBooking(booking: PLSBooking) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.placeBooking(booking)
            _bookingResult.value = result
            _isLoading.value = false
        }
    }

    fun resetBookingResult() {
        _bookingResult.value = null
    }
}
