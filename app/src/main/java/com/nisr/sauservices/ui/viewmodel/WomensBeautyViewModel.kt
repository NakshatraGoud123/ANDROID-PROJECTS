package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class BeautyService(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val subcategory: String,
    val durationMinutes: Int = 30
)

data class BeautyCartItem(
    val id: String,
    val name: String,
    val price: Double,
    var quantity: Int = 1
)

data class BeautyBookingDetails(
    val date: String = "",
    val timeSlot: String = "",
    val address: String = "",
    val phone: String = "",
    val paymentMethod: String = ""
)

class WomensBeautyViewModel : ViewModel() {
    private val _cartItems = mutableStateListOf<BeautyCartItem>()
    val cartItems: List<BeautyCartItem> = _cartItems

    // Booking State
    var selectedDate = mutableStateOf("")
    var selectedTimeSlot = mutableStateOf("")
    var customerAddress = mutableStateOf("")
    var phoneNumber = mutableStateOf("")
    var selectedPaymentMethod = mutableStateOf("")

    fun addToCart(service: BeautyService) {
        val existingItem = _cartItems.find { it.id == service.id }
        if (existingItem != null) {
            existingItem.quantity++
            val index = _cartItems.indexOf(existingItem)
            _cartItems[index] = existingItem.copy()
        } else {
            _cartItems.add(BeautyCartItem(service.id, service.name, service.price))
        }
    }

    fun removeFromCart(itemId: String) {
        _cartItems.removeAll { it.id == itemId }
    }

    fun updateQty(itemId: String, increase: Boolean) {
        val index = _cartItems.indexOfFirst { it.id == itemId }
        if (index != -1) {
            val currentQty = _cartItems[index].quantity
            if (increase) {
                _cartItems[index] = _cartItems[index].copy(quantity = currentQty + 1)
            } else {
                if (currentQty > 1) {
                    _cartItems[index] = _cartItems[index].copy(quantity = currentQty - 1)
                } else {
                    _cartItems.removeAt(index)
                }
            }
        }
    }

    fun calculateTotal(): Double {
        return _cartItems.sumOf { it.price * it.quantity }
    }

    fun clearCart() {
        _cartItems.clear()
        selectedDate.value = ""
        selectedTimeSlot.value = ""
        customerAddress.value = ""
        phoneNumber.value = ""
        selectedPaymentMethod.value = ""
    }
}
