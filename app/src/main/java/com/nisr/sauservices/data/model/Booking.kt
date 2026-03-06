package com.nisr.sauservices.data.model

data class Booking(
    val bookingId: String,
    val customerName: String,
    val customerPhone: String = "+91 98765 00001",
    val serviceType: String,
    val address: String,
    val timeSlot: String,
    var status: String, // placed, accepted, on_the_way, arrived, started, completed
    val price: String = "₹799",
    val description: String = "Split AC not cooling properly",
    val otp: String = "6743",
    val checklist: List<String> = listOf("Multimeter", "Insulation tape", "Wire stripper", "Circuit tester"),
    val preWorkPhoto: String? = null,
    val postWorkPhoto: String? = null,
    val notes: String? = null,
    val rating: Float? = null
)
