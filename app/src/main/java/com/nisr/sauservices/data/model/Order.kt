package com.nisr.sauservices.data.model

data class Order(
    val orderId: String,
    val customerName: String,
    val customerPhone: String = "+91 98765 00001",
    val amount: String,
    var status: String, // placed, accepted, ready, completed, rejected
    val paymentMode: String = "COD", // COD, Prepaid
    val items: List<OrderItem> = emptyList(),
    val createdAt: String = "2024-03-04 14:30",
    val cartAddedTime: String = "14:15"
)

data class OrderItem(
    val name: String,
    val quantity: Int,
    val price: Double
)
