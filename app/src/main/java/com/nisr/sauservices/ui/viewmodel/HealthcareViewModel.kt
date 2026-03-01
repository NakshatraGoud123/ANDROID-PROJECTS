package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class HealthcareService(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val subcategory: String,
    val requiresPrescription: Boolean = false
)

data class HealthcareCartItem(
    val id: String,
    val name: String,
    val price: Double,
    var quantity: Int = 1,
    val requiresPrescription: Boolean = false
)

class HealthcareViewModel : ViewModel() {
    private val _cartItems = mutableStateListOf<HealthcareCartItem>()
    val cartItems: List<HealthcareCartItem> = _cartItems

    // Booking State
    var selectedDate = mutableStateOf("")
    var selectedTimeSlot = mutableStateOf("")
    var patientName = mutableStateOf("")
    var customerAddress = mutableStateOf("")
    var phoneNumber = mutableStateOf("")
    var selectedPaymentMethod = mutableStateOf("")
    var prescriptionImageUri = mutableStateOf<String?>(null)

    fun addToCart(service: HealthcareService) {
        val existingItem = _cartItems.find { it.id == service.id }
        if (existingItem != null) {
            existingItem.quantity++
            val index = _cartItems.indexOf(existingItem)
            _cartItems[index] = existingItem.copy()
        } else {
            _cartItems.add(HealthcareCartItem(service.id, service.name, service.price, requiresPrescription = service.requiresPrescription))
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

    fun needsPrescription(): Boolean {
        return _cartItems.any { it.requiresPrescription }
    }

    fun clearCart() {
        _cartItems.clear()
        selectedDate.value = ""
        selectedTimeSlot.value = ""
        patientName.value = ""
        customerAddress.value = ""
        phoneNumber.value = ""
        selectedPaymentMethod.value = ""
        prescriptionImageUri.value = null
    }
}
