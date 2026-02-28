package com.nisr.sauservices.data.model

data class Delivery(
    val deliveryId: String,
    val customerName: String,
    val pickupAddress: String,
    val dropAddress: String,
    val distance: String,
    var status: String // Pending -> Picked -> On The Way -> Delivered
)
