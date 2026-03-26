package com.nisr.sauservices.data.model

data class CartModel(
    val itemId: String = "", // Firebase unique key
    val productId: String = "", // Product ID from catalog
    val itemName: String = "",
    val price: Double = 0.0,
    val unit: String = "",
    val quantity: Int = 1,
    val category: String = "",
    val subcategory: String = "",
    val totalPrice: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
