package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class FoodCartItem(
    val id: String,
    val name: String,
    val price: Int,
    var quantity: Int,
    val description: String = ""
)

class FoodCartViewModel : ViewModel() {
    private val _cartItems = mutableStateListOf<FoodCartItem>()
    val cartItems: List<FoodCartItem> get() = _cartItems

    fun addItem(id: String, name: String, price: Int, description: String = "") {
        val existingItem = _cartItems.find { it.id == id }
        if (existingItem != null) {
            existingItem.quantity++
            // Trigger recomposition by replacing the item
            val index = _cartItems.indexOf(existingItem)
            _cartItems[index] = existingItem.copy()
        } else {
            _cartItems.add(FoodCartItem(id, name, price, 1, description))
        }
    }

    fun removeItem(id: String) {
        _cartItems.removeAll { it.id == id }
    }

    fun increaseQty(id: String) {
        val index = _cartItems.indexOfFirst { it.id == id }
        if (index != -1) {
            _cartItems[index] = _cartItems[index].copy(quantity = _cartItems[index].quantity + 1)
        }
    }

    fun decreaseQty(id: String) {
        val index = _cartItems.indexOfFirst { it.id == id }
        if (index != -1) {
            if (_cartItems[index].quantity > 1) {
                _cartItems[index] = _cartItems[index].copy(quantity = _cartItems[index].quantity - 1)
            } else {
                _cartItems.removeAt(index)
            }
        }
    }

    fun getItemQuantity(id: String): Int {
        return _cartItems.find { it.id == id }?.quantity ?: 0
    }

    fun clearCart() {
        _cartItems.clear()
    }

    fun getTotal(): Int {
        return _cartItems.sumOf { it.price * it.quantity }
    }
}
