package com.nisr.sauservices.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.CartModel
import com.nisr.sauservices.data.model.HomeProduct
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.repository.FirebaseRepository
import com.nisr.sauservices.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val cartRepository = CartRepository()
    private val firebaseRepository = FirebaseRepository()

    private val _dbCartItems = MutableStateFlow<List<CartModel>>(emptyList())
    val dbCartItems = _dbCartItems.asStateFlow()

    private val _orderStatus = MutableStateFlow<Result<String>?>(null)
    val orderStatus = _orderStatus.asStateFlow()

    init {
        viewModelScope.launch {
            cartRepository.getCartItems().collect {
                _dbCartItems.value = it
                Log.d("CartViewModel", "Cart updated: ${it.size} items")
            }
        }
    }

    fun addItemToCart(
        name: String, 
        price: Double, 
        category: String, 
        subcategory: String, 
        unit: String, 
        productId: String = "",
        date: String? = null,
        time: String? = null,
        quantity: Int = 1
    ) {
        viewModelScope.launch {
            val existingItem = _dbCartItems.value.find { 
                it.productId == productId && it.itemName == name && it.date == date && it.time == time
            }
            
            if (existingItem != null) {
                cartRepository.updateQuantity(existingItem.itemId, existingItem.quantity + quantity)
            } else {
                val item = CartModel(
                    productId = productId,
                    itemName = name,
                    price = price,
                    quantity = quantity,
                    category = category,
                    subcategory = subcategory,
                    unit = unit,
                    totalPrice = price * quantity,
                    date = date,
                    time = time
                )
                cartRepository.addToCart(item)
            }
        }
    }

    fun updateQuantity(itemId: String, newQuantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(itemId, newQuantity)
        }
    }

    fun removeItem(itemId: String) {
        viewModelScope.launch {
            cartRepository.removeItem(itemId)
        }
    }

    fun placeOrder(address: String, paymentMethod: String, deliveryDate: String = "", deliveryTime: String = "") {
        viewModelScope.launch {
            if (_dbCartItems.value.isEmpty()) {
                _orderStatus.value = Result.failure(Exception("Cart is empty"))
                return@launch
            }

            val itemTotal = _dbCartItems.value.sumOf { it.totalPrice }
            val deliveryCharge = 30.0
            val tax = itemTotal * 0.05
            val total = itemTotal + deliveryCharge + tax
            
            // Create Order as per required structure in Part 3
            val order = com.nisr.sauservices.data.model.OrderModel(
                userId = firebaseRepository.getCurrentUserId() ?: "",
                items = _dbCartItems.value,
                totalAmount = total.toString(),
                address = address,
                deliveryDate = deliveryDate,
                deliveryTime = deliveryTime,
                paymentStatus = if (paymentMethod == "COD") "pending" else "success",
                status = "pending",
                deliveryStatus = "order_placed"
            )
            
            val result = firebaseRepository.saveOrder(order)
            if (result.isSuccess) {
                cartRepository.clearCart()
            }
            _orderStatus.value = result
        }
    }

    fun resetOrderStatus() {
        _orderStatus.value = null
    }

    fun getHomeItemQuantity(productId: String): Int {
        return _dbCartItems.value.find { it.productId == productId }?.quantity ?: 0
    }

    fun addHomeProduct(product: HomeProduct) {
        addItemToCart(
            name = product.name,
            price = product.price.toDouble(),
            category = "Home Essentials",
            subcategory = product.subcategoryId,
            unit = product.unit,
            productId = product.id
        )
    }

    fun removeHomeProduct(productId: String) {
        val item = _dbCartItems.value.find { it.productId == productId }
        item?.let { 
            updateQuantity(it.itemId, it.quantity - 1)
        }
    }

    fun clearHomeCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }

    val cartCount get() = _dbCartItems.value.sumOf { it.quantity }
}
