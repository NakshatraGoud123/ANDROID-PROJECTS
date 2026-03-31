package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomerViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val userId = repository.getCurrentUserId() ?: ""

    private val _myBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val myBookings = _myBookings.asStateFlow()

    private val _myOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val myOrders = _myOrders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        if (userId.isNotEmpty()) {
            observeData()
        }
    }

    private fun observeData() {
        viewModelScope.launch {
            repository.observeMyBookings("customer", userId).collect {
                _myBookings.value = it
            }
        }
        viewModelScope.launch {
            repository.listenToCustomerOrders(userId).collect {
                _myOrders.value = it
            }
        }
    }

    fun placeBooking(booking: BookingModel) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.bookSrv(booking)
            _isLoading.value = false
        }
    }
}
