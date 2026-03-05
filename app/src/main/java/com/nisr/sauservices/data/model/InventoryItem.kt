package com.nisr.sauservices.data.model

data class InventoryItem(
    val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val mrp: Double,
    val stockCount: Int,
    val status: String, // In Stock, Low Stock, Out of Stock
    val imageUrl: String = ""
)
