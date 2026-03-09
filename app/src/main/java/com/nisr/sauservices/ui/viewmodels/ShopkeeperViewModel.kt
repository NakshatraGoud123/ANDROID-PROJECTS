package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.Order
import com.nisr.sauservices.data.model.Product
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShopkeeperViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadOrders(shopId: String) {
        viewModelScope.launch {
            repository.observeShopOrders(shopId).collect {
                _orders.value = it
            }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.addProduct(product).onSuccess {
                // Handle success
            }.onFailure {
                // Handle failure
            }
            _isLoading.value = false
        }
    }

    fun updateStatus(orderId: String, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
        }
    }
}
