package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.CartModel
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.model.HomeProduct
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
            }
        }
    }

    fun addItemToCart(
        name: String,
        price: Double,
        category: String,
        subcategory: String,
        unit: String,
        productId: String,
        date: String? = null,
        time: String? = null,
        quantity: Int = 1
    ) {
        viewModelScope.launch {
            val item = CartModel(
                itemName = name,
                price = price,
                category = category,
                subcategory = subcategory,
                unit = unit,
                productId = productId,
                date = date,
                time = time,
                quantity = quantity,
                totalPrice = price * quantity
            )
            cartRepository.addToCart(item)
        }
    }

    fun getHomeItemQuantity(productId: String): Int {
        return _dbCartItems.value.find { it.productId == productId }?.quantity ?: 0
    }

    fun addHomeProduct(product: HomeProduct) {
        val existingItem = _dbCartItems.value.find { it.productId == product.id }
        if (existingItem != null) {
            updateQuantity(existingItem.itemId, existingItem.quantity + 1)
        } else {
            addItemToCart(
                name = product.name,
                price = product.price.toDouble(),
                category = product.category,
                subcategory = "", 
                unit = product.unit,
                productId = product.id
            )
        }
    }

    fun removeHomeProduct(productId: String) {
        val existingItem = _dbCartItems.value.find { it.productId == productId }
        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                updateQuantity(existingItem.itemId, existingItem.quantity - 1)
            } else {
                viewModelScope.launch {
                    cartRepository.removeItem(existingItem.itemId)
                }
            }
        }
    }

    fun updateQuantity(itemId: String, quantity: Int) {
        viewModelScope.launch {
            if (quantity <= 0) {
                cartRepository.removeItem(itemId)
            } else {
                cartRepository.updateQuantity(itemId, quantity)
            }
        }
    }

    fun clearHomeCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }

    fun placeOrder(address: String, paymentMethod: String = "Cash on Delivery") {
        viewModelScope.launch {
            val items = _dbCartItems.value
            if (items.isEmpty()) return@launch

            val total = items.sumOf { it.totalPrice }
            val order = OrderModel(
                customerId = firebaseRepository.getCurrentUserId() ?: "",
                items = items,
                totalPrice = total,
                address = address,
                orderStatus = "pending",
                paymentMethod = paymentMethod
            )

            firebaseRepository.placeOrder(order).onSuccess { orderId ->
                cartRepository.clearCart()
                _orderStatus.value = Result.success(orderId)
            }.onFailure {
                _orderStatus.value = Result.failure(it)
            }
        }
    }

    fun resetOrderStatus() {
        _orderStatus.value = null
    }
}
