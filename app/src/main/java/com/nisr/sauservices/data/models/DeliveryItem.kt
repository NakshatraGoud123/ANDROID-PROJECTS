package com.nisr.sauservices.data.models

data class DeliveryItem(
    val id: String,
    val orderId: String,
    val customerName: String,
    val pickupLocation: String,
    val dropLocation: String,
    val distance: String,
    val timeSlot: String,
    val status: String
)
