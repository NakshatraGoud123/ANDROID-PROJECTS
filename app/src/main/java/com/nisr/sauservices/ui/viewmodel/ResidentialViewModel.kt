package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.nisr.sauservices.data.model.BookingDetails
import com.nisr.sauservices.data.model.ResidentialCartItem
import com.nisr.sauservices.data.model.ResidentialServiceItem

class ResidentialViewModel : ViewModel() {
    private val _cartItems = mutableStateListOf<ResidentialCartItem>()
    val cartItems: List<ResidentialCartItem> get() = _cartItems

    var bookingDetails = mutableStateOf(BookingDetails())
        private set

    fun addToCart(service: ResidentialServiceItem) {
        val existingItem = _cartItems.find { it.service.id == service.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            _cartItems.add(ResidentialCartItem(service, 1))
        }
    }

    fun removeFromCart(serviceId: String) {
        _cartItems.removeAll { it.service.id == serviceId }
    }

    fun updateQty(serviceId: String, increment: Boolean) {
        val index = _cartItems.indexOfFirst { it.service.id == serviceId }
        if (index != -1) {
            val item = _cartItems[index]
            if (increment) {
                _cartItems[index] = item.copy(quantity = item.quantity + 1)
            } else {
                if (item.quantity > 1) {
                    _cartItems[index] = item.copy(quantity = item.quantity - 1)
                } else {
                    _cartItems.removeAt(index)
                }
            }
        }
    }

    fun getItemQuantity(serviceId: String): Int {
        return _cartItems.find { it.service.id == serviceId }?.quantity ?: 0
    }

    fun calculateTotal(): Double {
        return _cartItems.sumOf { it.service.price * it.quantity }
    }

    fun setDate(date: String) {
        bookingDetails.value = bookingDetails.value.copy(date = date)
    }

    fun setTimeSlot(slot: String) {
        bookingDetails.value = bookingDetails.value.copy(timeSlot = slot)
    }

    fun setAddress(address: String) {
        bookingDetails.value = bookingDetails.value.copy(address = address)
    }

    fun setPhone(phone: String) {
        bookingDetails.value = bookingDetails.value.copy(phone = phone)
    }

    fun setPaymentMethod(method: String) {
        bookingDetails.value = bookingDetails.value.copy(paymentMethod = method)
    }

    fun clearCart() {
        _cartItems.clear()
        bookingDetails.value = BookingDetails()
    }
}
