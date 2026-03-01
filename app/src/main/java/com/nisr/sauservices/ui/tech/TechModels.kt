package com.nisr.sauservices.ui.tech

data class TechServiceItem(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val subcategory: String
)

data class TechCartItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val category: String
)
