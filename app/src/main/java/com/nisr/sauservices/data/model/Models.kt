package com.nisr.sauservices.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val user_id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val status: String,
    val token: String? = null
)

data class Product(
    val product_id: Int,
    val shop_id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val stock: Int,
    val image_url: String
)

data class ServiceItem(
    val service_id: Int,
    val worker_id: Int,
    val name: String,
    val category: String,
    val base_price: Double,
    val duration: String,
    val description: String
)

data class Order(
    val order_id: Int,
    val customer_id: Int,
    val item_type: String,
    val total_price: Double,
    val order_status: String,
    val created_at: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: User?,
    val token: String?
)
