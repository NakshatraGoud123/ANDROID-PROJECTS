package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.nisr.sauservices.data.model.Product

data class CartItem(
    val product: Product,
    var quantity: Int
)

class CartViewModel : ViewModel() {
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    fun addToCart(product: Product) {
        val existingItem = _cartItems.find { it.product.id == product.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            _cartItems.add(CartItem(product, 1))
        }
    }

    fun removeFromCart(productId: String) {
        _cartItems.removeAll { it.product.id == productId }
    }

    fun updateQuantity(productId: String, increment: Boolean) {
        val index = _cartItems.indexOfFirst { it.product.id == productId }
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

    fun getItemQuantity(productId: String): Int {
        return _cartItems.find { it.product.id == productId }?.quantity ?: 0
    }

    fun clearCart() {
        _cartItems.clear()
    }

    val itemTotal: Double get() = _cartItems.sumOf { it.product.price * it.quantity }
    val deliveryFee: Double get() = if (_cartItems.isEmpty()) 0.0 else 40.0
    val taxes: Double get() = itemTotal * 0.05
    val grandTotal: Double get() = itemTotal + deliveryFee + taxes
}
