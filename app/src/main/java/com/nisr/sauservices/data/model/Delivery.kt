package com.nisr.sauservices.data.model

data class Delivery(
    val deliveryId: String,
    val customerName: String,
    val pickupAddress: String,
    val dropAddress: String,
    val distance: String,
    var status: String, // Assigned, Accepted, Picked, On The Way, Delivered
    val cartAddedTime: String = "3:15 PM",
    val items: String = "2 Items",
    val otp: String = "7193",
    val paymentMode: String = "Prepaid",
    val pickupShop: String = "FreshMart Express",
    val eta: String = "15 min",
    val customerPhone: String = "+91 9876543210",
    val orderAmount: String = "₹370"
)
