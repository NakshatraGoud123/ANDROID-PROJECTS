package com.nisr.sauservices.data.model

data class Booking(
    val bookingId: String,
    val customerName: String,
    val serviceType: String,
    val address: String,
    val timeSlot: String,
    var status: String // Assigned -> On The Way -> Started -> Completed
)
