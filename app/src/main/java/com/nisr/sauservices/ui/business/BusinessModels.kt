package com.nisr.sauservices.ui.business

data class BusinessService(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val subcategory: String
)

data class BusinessCartItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val category: String
)
