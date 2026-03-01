package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class EduCartItem(
    val id: String,
    val name: String,
    val price: Int,
    var quantity: Int,
    val category: String,
    val duration: String = "1 Month"
)

class EducationCartViewModel : ViewModel() {
    private val _cartItems = mutableStateListOf<EduCartItem>()
    val cartItems: List<EduCartItem> get() = _cartItems

    fun addItem(id: String, name: String, price: Int, category: String, duration: String) {
        val existingItem = _cartItems.find { it.id == id }
        if (existingItem != null) {
            existingItem.quantity++
            // Trigger recomposition by replacing the item
            val index = _cartItems.indexOf(existingItem)
            _cartItems[index] = existingItem.copy(quantity = existingItem.quantity)
        } else {
            _cartItems.add(EduCartItem(id, name, price, 1, category, duration))
        }
    }

    fun removeItem(id: String) {
        _cartItems.removeAll { it.id == id }
    }

    fun increaseQty(id: String) {
        val item = _cartItems.find { it.id == id }
        if (item != null) {
            val index = _cartItems.indexOf(item)
            _cartItems[index] = item.copy(quantity = item.quantity + 1)
        }
    }

    fun decreaseQty(id: String) {
        val item = _cartItems.find { it.id == id }
        if (item != null && item.quantity > 1) {
            val index = _cartItems.indexOf(item)
            _cartItems[index] = item.copy(quantity = item.quantity - 1)
        } else if (item != null && item.quantity == 1) {
            removeItem(id)
        }
    }

    fun getTotal(): Int {
        return _cartItems.sumOf { it.price * it.quantity }
    }
    
    fun clearCart() {
        _cartItems.clear()
    }
}
