package com.nisr.sauservices.data.models

data class Booking(
    val id: String,
    val customerName: String,
    val address: String,
    val serviceType: String,
    val date: String,
    val time: String,
    val status: String
)
