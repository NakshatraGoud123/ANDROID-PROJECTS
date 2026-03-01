package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class MensGroomingService(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val subcategory: String
)

data class MensCartItem(
    val id: String,
    val name: String,
    val price: Double,
    var quantity: Int = 1
)

class MensGroomingViewModel : ViewModel() {
    private val _cartItems = mutableStateListOf<MensCartItem>()
    val cartItems: List<MensCartItem> = _cartItems

    // Booking Fields
    val selectedDate = mutableStateOf("")
    val selectedTime = mutableStateOf("")
    val customerAddress = mutableStateOf("")
    val phoneNumber = mutableStateOf("")

    fun addToCart(service: MensGroomingService) {
        val existingItem = _cartItems.find { it.id == service.id }
        if (existingItem != null) {
            existingItem.quantity++
            // Force recomposition
            val index = _cartItems.indexOf(existingItem)
            _cartItems[index] = existingItem.copy()
        } else {
            _cartItems.add(MensCartItem(service.id, service.name, service.price))
        }
    }

    fun removeFromCart(itemId: String) {
        _cartItems.removeAll { it.id == itemId }
    }

    fun increaseQty(itemId: String) {
        val index = _cartItems.indexOfFirst { it.id == itemId }
        if (index != -1) {
            _cartItems[index] = _cartItems[index].copy(quantity = _cartItems[index].quantity + 1)
        }
    }

    fun decreaseQty(itemId: String) {
        val index = _cartItems.indexOfFirst { it.id == itemId }
        if (index != -1) {
            if (_cartItems[index].quantity > 1) {
                _cartItems[index] = _cartItems[index].copy(quantity = _cartItems[index].quantity - 1)
            } else {
                _cartItems.removeAt(index)
            }
        }
    }

    fun clearCart() {
        _cartItems.clear()
        selectedDate.value = ""
        selectedTime.value = ""
        customerAddress.value = ""
        phoneNumber.value = ""
    }

    fun getTotalPrice(): Double {
        return _cartItems.sumOf { it.price * it.quantity }
    }
}
