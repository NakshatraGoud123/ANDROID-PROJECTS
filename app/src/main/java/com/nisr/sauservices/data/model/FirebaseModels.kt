package com.nisr.sauservices.data.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class BookingModel(
    val bookingId: String = "",
    val userId: String = "",
    val category: String = "",
    val subCategory: String = "",
    val price: String = "",
    val date: String = "",
    val time: String = "",
    val address: String = "",
    val status: String = "pending", // pending, accepted, rejected, completed
    val assignedWorker: String = "",
    val workerStatus: String = "waiting" // waiting, on_the_way, completed
)

@IgnoreExtraProperties
data class OrderModel(
    val orderId: String = "",
    val userId: String = "",
    val items: List<CartModel>? = null,
    val totalAmount: String = "",
    val address: String = "",
    val deliveryDate: String = "",
    val deliveryTime: String = "",
    val paymentStatus: String = "pending", // pending, success, failed
    val status: String = "pending", // pending, accepted, rejected, completed
    val assignedShopkeeper: String = "",
    val assignedDeliveryBoy: String = "",
    val deliveryStatus: String = "order_placed" // order_placed, assigned, out_for_delivery, delivered
)

data class LiveLocation(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val lastUpdated: Long = System.currentTimeMillis()
)
