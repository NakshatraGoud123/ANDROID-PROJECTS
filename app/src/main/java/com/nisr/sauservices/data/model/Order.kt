package com.nisr.sauservices.data.model

data class Order(
    val orderId: String,
    val customerName: String,
    val amount: String,
    var status: String // Pending -> Accepted -> Completed
)
