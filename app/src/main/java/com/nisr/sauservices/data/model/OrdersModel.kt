package com.nisr.sauservices.data.model

data class OrderModel(
    val orderId: String = "",
    val serviceName: String = "",
    val category: String = "",
    val subcategory: String = "",
    val scheduleDate: String? = null,
    val scheduleTime: String? = null,
    val items: List<CartModel>? = null,
    val amount: Double = 0.0,
    val paymentMethod: String = "",
    val address: String = "",
    val status: String = "success",
    val timestamp: Long = System.currentTimeMillis()
)
