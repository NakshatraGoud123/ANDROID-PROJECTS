package com.nisr.sauservices.data.models

data class Order(
    val id: String,
    val customerName: String,
    val address: String,
    val total: Double,
    val dateTime: String,
    val status: String
)
