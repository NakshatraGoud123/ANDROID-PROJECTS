package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.SupplyOrder
import com.nisr.sauservices.data.model.SupplyOrderItem
import com.nisr.sauservices.data.repository.RealtimeDatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EssentialSuppliesViewModel : ViewModel() {
    private val repository = RealtimeDatabaseRepository()
    
    private val _cartItems = mutableStateListOf<SupplyOrderItem>()
    val cartItems: List<SupplyOrderItem> get() = _cartItems

    private val _orderStatus = MutableStateFlow<Result<String>?>(null)
    val orderStatus = _orderStatus.asStateFlow()

    fun addToCart(name: String, price: String) {
        val existing = _cartItems.find { it.name == name }
        if (existing != null) {
            val index = _cartItems.indexOf(existing)
            _cartItems[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            _cartItems.add(SupplyOrderItem(name, price, 1))
        }
    }

    fun removeFromCart(name: String) {
        val existing = _cartItems.find { it.name == name }
        if (existing != null) {
            if (existing.quantity > 1) {
                val index = _cartItems.indexOf(existing)
                _cartItems[index] = existing.copy(quantity = existing.quantity - 1)
            } else {
                _cartItems.remove(existing)
            }
        }
    }

    fun clearCart() {
        _cartItems.clear()
    }

    fun checkout(location: String) {
        viewModelScope.launch {
            val total = _cartItems.sumOf { 
                val priceVal = it.price.replace("₹", "").split("–").first().trim().toIntOrNull() ?: 0
                priceVal * it.quantity
            }
            val order = SupplyOrder(
                items = _cartItems.toList(),
                totalPrice = "₹$total",
                location = location
            )
            val result = repository.placeSupplyOrder(order)
            _orderStatus.value = result
            if (result.isSuccess) {
                clearCart()
            }
        }
    }
}
