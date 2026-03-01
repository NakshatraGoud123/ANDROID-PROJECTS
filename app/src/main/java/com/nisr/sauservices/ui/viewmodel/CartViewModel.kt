package com.nisr.sauservices.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.nisr.sauservices.data.models.Product
import com.nisr.sauservices.data.model.HomeProduct

data class CartItem(
    val product: Product,
    var quantity: Int
)

data class HomeCartItem(
    val product: HomeProduct,
    var quantity: Int
)

class CartViewModel : ViewModel() {
    // Legacy Product Cart
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    // Home Essentials Cart
    private val _homeCartItems = mutableStateListOf<HomeCartItem>()
    val homeCartItems: List<HomeCartItem> get() = _homeCartItems

    // --- Legacy Methods ---
    fun addToCart(product: Product) {
        val existingItem = _cartItems.find { it.product.id == product.id }
        if (existingItem != null) {
            val index = _cartItems.indexOf(existingItem)
            _cartItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
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

    // --- Home Essentials Methods ---
    fun addHomeProduct(product: HomeProduct) {
        val existingItem = _homeCartItems.find { it.product.id == product.id }
        if (existingItem != null) {
            val index = _homeCartItems.indexOf(existingItem)
            _homeCartItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            _homeCartItems.add(HomeCartItem(product, 1))
        }
    }

    fun removeHomeProduct(productId: String) {
        val index = _homeCartItems.indexOfFirst { it.product.id == productId }
        if (index != -1) {
            val item = _homeCartItems[index]
            if (item.quantity > 1) {
                _homeCartItems[index] = item.copy(quantity = item.quantity - 1)
            } else {
                _homeCartItems.removeAt(index)
            }
        }
    }

    fun deleteHomeProduct(productId: String) {
        _homeCartItems.removeAll { it.product.id == productId }
    }

    fun getHomeItemQuantity(productId: String): Int {
        return _homeCartItems.find { it.product.id == productId }?.quantity ?: 0
    }

    fun clearHomeCart() {
        _homeCartItems.clear()
    }

    // --- Combined Totals (Example uses Home Cart for now) ---
    val itemTotal: Double get() = _homeCartItems.sumOf { (it.product.price * it.quantity).toDouble() }
    val deliveryFee: Double get() = if (_homeCartItems.isEmpty()) 0.0 else 30.0
    val taxes: Double get() = itemTotal * 0.05
    val grandTotal: Double get() = itemTotal + deliveryFee + taxes
}
