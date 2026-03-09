package com.nisr.sauservices.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.Order
import com.nisr.sauservices.data.model.Product
import com.nisr.sauservices.data.model.ServiceItem
import com.nisr.sauservices.data.repository.SauRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DashboardState<out T> {
    object Idle : DashboardState<Nothing>()
    object Loading : DashboardState<Nothing>()
    data class Success<T>(val data: T) : DashboardState<T>()
    data class Error(val message: String) : DashboardState<Nothing>()
}

class DashboardViewModel : ViewModel() {
    private val repository = SauRepository()

    // Shopkeeper State
    private val _shopOrders = MutableStateFlow<DashboardState<List<Order>>>(DashboardState.Idle)
    val shopOrders: StateFlow<DashboardState<List<Order>>> = _shopOrders

    // Worker State
    private val _workerJobs = MutableStateFlow<DashboardState<List<Order>>>(DashboardState.Idle)
    val workerJobs: StateFlow<DashboardState<List<Order>>> = _workerJobs

    // Customer States
    private val _products = MutableStateFlow<DashboardState<List<Product>>>(DashboardState.Idle)
    val products: StateFlow<DashboardState<List<Product>>> = _products

    private val _services = MutableStateFlow<DashboardState<List<ServiceItem>>>(DashboardState.Idle)
    val services: StateFlow<DashboardState<List<ServiceItem>>> = _services

    // --- SHOPKEEPER ACTIONS ---
    fun fetchShopOrders() {
        viewModelScope.launch {
            _shopOrders.value = DashboardState.Loading
            try {
                val response = repository.getShopOrders()
                if (response.isSuccessful) {
                    _shopOrders.value = DashboardState.Success(response.body() ?: emptyList())
                } else {
                    _shopOrders.value = DashboardState.Error(response.message())
                }
            } catch (e: Exception) {
                _shopOrders.value = DashboardState.Error(e.message ?: "Unknown error")
            }
        }
    }

    // --- WORKER ACTIONS ---
    fun fetchWorkerJobs() {
        viewModelScope.launch {
            _workerJobs.value = DashboardState.Loading
            try {
                val response = repository.getWorkerJobs()
                if (response.isSuccessful) {
                    _workerJobs.value = DashboardState.Success(response.body() ?: emptyList())
                } else {
                    _workerJobs.value = DashboardState.Error(response.message())
                }
            } catch (e: Exception) {
                _workerJobs.value = DashboardState.Error(e.message ?: "Unknown error")
            }
        }
    }

    // --- CUSTOMER ACTIONS ---
    fun fetchProducts(category: String? = null) {
        viewModelScope.launch {
            _products.value = DashboardState.Loading
            try {
                val response = repository.getProducts(category)
                if (response.isSuccessful) {
                    _products.value = DashboardState.Success(response.body() ?: emptyList())
                } else {
                    _products.value = DashboardState.Error(response.message())
                }
            } catch (e: Exception) {
                _products.value = DashboardState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateOrderStatus(orderId: Int, newStatus: String, role: String) {
        viewModelScope.launch {
            try {
                val response = repository.updateOrderStatus(orderId, newStatus)
                if (response.isSuccessful) {
                    // Refresh data after update
                    if (role == "shopkeeper") fetchShopOrders()
                    else if (role == "worker") fetchWorkerJobs()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
