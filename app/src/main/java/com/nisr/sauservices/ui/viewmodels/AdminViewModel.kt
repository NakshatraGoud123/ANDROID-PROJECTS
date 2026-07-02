package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.model.FirebaseUser
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _allBookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val allBookings = _allBookings.asStateFlow()

    private val _allOrders = MutableStateFlow<List<OrderModel>>(emptyList())
    val allOrders = _allOrders.asStateFlow()

    private val _allUsers = MutableStateFlow<List<FirebaseUser>>(emptyList())
    val allUsers = _allUsers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.listenToAllBookings().collect { list ->
                _allBookings.value = list
                _isLoading.value = false
            }
        }
        viewModelScope.launch {
            repository.listenToAllOrders().collect { list ->
                _allOrders.value = list
            }
        }
        viewModelScope.launch {
            repository.listenToAllUsers().collect { list ->
                _allUsers.value = list
            }
        }
    }

    fun updateBookingStatus(bookingId: String, status: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, status)
        }
    }

    fun updateOrderStatus(orderId: String, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
        }
    }

    fun reassignWorker(bookingId: String, workerId: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, "assigned", workerId = workerId)
        }
    }

    fun deleteUser(uid: String) {
        viewModelScope.launch {
            repository.deleteUser(uid)
        }
    }

    fun deleteOrder(orderId: String) {
        viewModelScope.launch {
            repository.deleteOrder(orderId)
        }
    }

    fun deleteBooking(bookingId: String) {
        viewModelScope.launch {
            repository.deleteBooking(bookingId)
        }
    }
}
