package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.FirestoreBooking
import com.nisr.sauservices.data.model.InventoryItem
import com.nisr.sauservices.data.model.Order
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShopkeeperViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _availableOrders = MutableStateFlow<List<FirestoreBooking>>(emptyList())
    val availableOrders = _availableOrders.asStateFlow()

    private val _myOrders = MutableStateFlow<List<FirestoreBooking>>(emptyList())
    val myOrders = _myOrders.asStateFlow()

    private val _orders = MutableLiveData<List<Order>>(emptyList())
    val orders: LiveData<List<Order>> = _orders

    private val _inventory = MutableStateFlow<List<InventoryItem>>(emptyList())
    val inventory = _inventory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun loadAvailableOrders() {
        viewModelScope.launch {
            repository.observeAvailableBookings("shopkeeper").collect {
                _availableOrders.value = it
            }
        }
    }

    fun loadMyOrders(userId: String) {
        viewModelScope.launch {
            repository.observeMyBookings("shopkeeper", userId).collect {
                _myOrders.value = it
            }
        }
    }

    fun acceptOrder(bookingId: String, userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.acceptBooking(bookingId, "shopkeeper", userId).onSuccess {
                _errorMessage.value = null
            }.onFailure {
                _errorMessage.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun updateOrderStatus(bookingId: String, status: String) {
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
