package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.CartModel
import com.nisr.sauservices.data.model.HomeProduct
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val repository = CartRepository()

    private val _dbCartItems = MutableStateFlow<List<CartModel>>(emptyList())
    val dbCartItems = _dbCartItems.asStateFlow()

    private val _orderStatus = MutableStateFlow<Result<String>?>(null)
    val orderStatus = _orderStatus.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getCartItems().collect {
                _dbCartItems.value = it
            }
        }
    }

    fun addItemToCart(name: String, price: Double, category: String, subcategory: String, unit: String, productId: String = "") {
        val existingItem = _dbCartItems.value.find { 
            (productId.isNotEmpty() && it.productId == productId) || (it.itemName == name && it.unit == unit) 
        }
        
        if (existingItem != null) {
            updateQuantity(existingItem.itemId, existingItem.quantity + 1)
        } else {
            val item = CartModel(
                productId = productId,
                itemName = name,
                price = price,
                quantity = 1,
                category = category,
                subcategory = subcategory,
                unit = unit,
                totalPrice = price
            )
            repository.addToCart(item)
        }
    }

    fun updateQuantity(itemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            repository.removeItem(itemId)
        } else {
            val item = _dbCartItems.value.find { it.itemId == itemId }
            item?.let {
                val updatedItem = it.copy(
                    quantity = newQuantity,
                    totalPrice = it.price * newQuantity
                )
                repository.updateItem(updatedItem)
            }
        }
    }

    fun removeItem(itemId: String) {
        repository.removeItem(itemId)
    }

    fun placeOrder(address: String, paymentMethod: String) {
        viewModelScope.launch {
            if (_dbCartItems.value.isEmpty()) {
                _orderStatus.value = Result.failure(Exception("Cart is empty"))
                return@launch
            }

            val total = _dbCartItems.value.sumOf { it.totalPrice } + 30.0 // Delivery charge
            val order = OrderModel(
                serviceName = "Essential Supplies",
                items = _dbCartItems.value,
                amount = total,
                address = address,
                paymentMethod = paymentMethod,
                status = "success",
                timestamp = System.currentTimeMillis()
            )
            
            val result = repository.placeOrder(order)
            if (result.isSuccess) {
                repository.clearCart()
            }
            _orderStatus.value = result
        }
    }

    fun resetOrderStatus() {
        _orderStatus.value = null
    }

    val cartCount get() = _dbCartItems.value.sumOf { it.quantity }
    val itemTotal: Double get() = _dbCartItems.value.sumOf { it.totalPrice }
    
    // --- Compatibility with HomeEssentials ---
    
    data class HomeCartItemCompat(
        val product: HomeProduct,
        val quantity: Int
    )

    val homeCartItems: List<HomeCartItemCompat> get() = _dbCartItems.value.map {
        HomeCartItemCompat(
            product = HomeProduct(
                id = it.productId.ifEmpty { it.itemId },
                name = it.itemName,
                price = it.price.toInt(),
                subcategoryId = it.subcategory,
                unit = it.unit,
                category = it.category
            ),
            quantity = it.quantity
        )
    }

    fun getHomeItemQuantity(productId: String): Int {
        return _dbCartItems.value.find { it.productId == productId || it.itemId == productId }?.quantity ?: 0
    }

    fun addHomeProduct(product: HomeProduct) {
        addItemToCart(product.name, product.price.toDouble(), product.category, product.subcategoryId, product.unit, product.id)
    }

    fun removeHomeProduct(productId: String) {
        val item = _dbCartItems.value.find { it.productId == productId || it.itemId == productId }
        item?.let { 
            if (it.quantity > 1) {
                updateQuantity(it.itemId, it.quantity - 1)
            } else {
                repository.removeItem(it.itemId)
            }
        }
    }

    fun clearHomeCart() {
        repository.clearCart()
    }
}
