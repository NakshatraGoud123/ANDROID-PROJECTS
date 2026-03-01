package com.nisr.sauservices.ui.lifestyle

data class LifestyleServiceItem(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val subcategory: String
)

data class LifestyleCartItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val category: String
)
