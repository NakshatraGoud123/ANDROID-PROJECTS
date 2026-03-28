package com.nisr.sauservices.data.model

import com.google.firebase.database.IgnoreExtraProperties

data class OperationResult(
    val isSuccess: Boolean,
    val message: String? = null,
    val exception: Throwable? = null
) {
    fun getOrNull(): String? = message
}

@IgnoreExtraProperties
data class BookingModel(
    val bookingId: String = "",
    val customerId: String = "",
    val serviceId: String = "",
    val serviceName: String = "",
    val scheduledDate: String = "",
    val scheduledTime: String = "",
    val status: String = "pending", // pending, accepted, completed
    val workerId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val address: String = ""
)

@IgnoreExtraProperties
data class OrderModel(
    val orderId: String = "",
    val customerId: String = "",
    val items: List<CartModel> = emptyList(),
    val totalPrice: Double = 0.0,
    val address: String = "",
    val customerLocation: LiveLocation = LiveLocation(),
    val paymentStatus: String = "pending",
    val orderStatus: String = "pending", // pending, accepted, assigned, delivered, rejected
    val assignedDeliveryBoy: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val liveLocation: LiveLocation = LiveLocation(),
    
    // Fields for Service Bookings / Direct Orders
    val serviceName: String = "",
    val category: String = "",
    val subcategory: String = "",
    val scheduleDate: String? = null,
    val scheduleTime: String? = null,
    val amount: Double = 0.0,
    val paymentMethod: String = "",
    val status: String = ""
)

@IgnoreExtraProperties
data class LiveLocation(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

@IgnoreExtraProperties
data class FirebaseUser(
    val userId: String = "",
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "",
    val status: String = "active"
)
