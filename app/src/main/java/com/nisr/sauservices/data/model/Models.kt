package com.nisr.sauservices.data.model

import com.google.gson.annotations.SerializedName

data class ApiUser(
    @SerializedName("user_id") val userId: Int,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val status: String,
    val token: String? = null
)

data class ApiProduct(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("shop_id") val shopId: Int,
    val name: String,
    val category: String,
    val price: Double,
    val stock: Int,
    @SerializedName("image_url") val imageUrl: String
)

data class ApiServiceItem(
    @SerializedName("service_id") val serviceId: Int,
    @SerializedName("worker_id") val workerId: Int,
    val name: String,
    val category: String,
    @SerializedName("base_price") val basePrice: Double,
    val duration: String,
    val description: String
)

data class ApiOrder(
    @SerializedName("order_id") val orderId: Int,
    @SerializedName("customer_id") val customerId: Int,
    @SerializedName("item_type") val itemType: String,
    @SerializedName("total_price") val totalPrice: Double,
    @SerializedName("order_status") val orderStatus: String,
    @SerializedName("created_at") val createdAt: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: ApiUser?,
    val token: String?
)
