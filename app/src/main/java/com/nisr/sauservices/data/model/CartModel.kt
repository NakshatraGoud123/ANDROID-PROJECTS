package com.nisr.sauservices.data.model

data class CartModel(
    val itemId: String = "", // Firebase unique key
    val productId: String = "", // Product ID or Service ID
    val itemName: String = "",
    val price: Double = 0.0,
    val unit: String = "",
    val quantity: Int = 1,
    val category: String = "",
    val subcategory: String = "",
    val totalPrice: Double = 0.0,
    val date: String? = null, // For scheduled bookings
    val time: String? = null, // For scheduled bookings
    val timestamp: Long = System.currentTimeMillis()
)
