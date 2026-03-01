package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.nisr.sauservices.ui.business.BusinessCartItem
import com.nisr.sauservices.ui.business.BusinessService

class BusinessViewModel : ViewModel() {
    private val _cartItems = mutableStateListOf<BusinessCartItem>()
    val cartItems: List<BusinessCartItem> get() = _cartItems

    var selectedDate = mutableStateOf("")
    var selectedTime = mutableStateOf("")
    var customerAddress = mutableStateOf("")
    var phoneNumber = mutableStateOf("")

    fun addToCart(service: BusinessService) {
        val existing = _cartItems.find { it.id == service.id }
        if (existing != null) {
            val index = _cartItems.indexOf(existing)
            _cartItems[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            _cartItems.add(
                BusinessCartItem(
                    id = service.id,
                    name = service.name,
                    price = service.price,
                    quantity = 1,
                    category = service.category
                )
            )
        }
    }

    fun removeFromCart(serviceId: String) {
        _cartItems.removeAll { it.id == serviceId }
    }

    fun increaseQty(serviceId: String) {
        val index = _cartItems.indexOfFirst { it.id == serviceId }
        if (index != -1) {
            _cartItems[index] = _cartItems[index].copy(quantity = _cartItems[index].quantity + 1)
        }
    }

    fun decreaseQty(serviceId: String) {
        val index = _cartItems.indexOfFirst { it.id == serviceId }
        if (index != -1) {
            if (_cartItems[index].quantity > 1) {
                _cartItems[index] = _cartItems[index].copy(quantity = _cartItems[index].quantity - 1)
            } else {
                _cartItems.removeAt(index)
            }
        }
    }

    fun getTotalPrice(): Double {
        return _cartItems.sumOf { it.price * it.quantity }
    }

    fun clearCart() {
        _cartItems.clear()
        selectedDate.value = ""
        selectedTime.value = ""
        customerAddress.value = ""
        phoneNumber.value = ""
    }
}
