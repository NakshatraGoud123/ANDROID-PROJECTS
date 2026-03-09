package com.nisr.sauservices.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.Order
import com.nisr.sauservices.data.model.Product
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomerViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _myOrders = MutableStateFlow<List<Order>>(emptyList())
    val myOrders = _myOrders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadProducts(category: String? = null) {
        viewModelScope.launch {
            repository.getProducts(category).collect {
                _products.value = it
            }
        }
    }

    fun loadMyOrders(userId: String) {
        viewModelScope.launch {
            repository.observeUserOrders(userId).collect {
                _myOrders.value = it
            }
        }
    }

    fun placeOrder(order: Order) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createOrder(order).onSuccess {
                // Success
            }.onFailure {
                // Handle error
            }
            _isLoading.value = false
        }
    }
}
