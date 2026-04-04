package com.nisr.sauservices.data.model

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

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
    
    @get:PropertyName("scheduledDate") @set:PropertyName("scheduledDate")
    var scheduledDate: String = "",
    
    @get:PropertyName("scheduledTime") @set:PropertyName("scheduledTime")
    var scheduledTime: String = "",
    
    val status: String = "pending", // pending, accepted, completed
    val workerId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val address: String = "",

    // Fallback fields for compatibility with BookingRequest
    val location: String = "",
    val date: String = "",
    val time: String = "",
    val category: String = "",
    val subcategory: String = ""
) {
    // Helper to get consistent values
    val displayAddress: String get() = address.ifEmpty { location }.ifEmpty { "No address provided" }
    val displayDate: String get() = scheduledDate.ifEmpty { date }.ifEmpty { "TBD" }
    val displayTime: String get() = scheduledTime.ifEmpty { time }.ifEmpty { "" }
    val displayService: String get() = serviceName.ifEmpty { subcategory }.ifEmpty { category }.ifEmpty { "Service Request" }
}

@IgnoreExtraProperties
data class OrderModel(
    val orderId: String = "",
    val customerId: String = "",
    val items: List<CartModel> = emptyList(),
    val totalPrice: Double = 0.0,
    val address: String = "",
    val customerLocation: LiveLocation = LiveLocation(),
    val paymentStatus: String = "pending",
    val orderStatus: String = "pending", // pending, accepted, assigned, out_for_delivery, delivered, rejected
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
) {
    val displayStatus: String get() = orderStatus.ifEmpty { status }.ifEmpty { "pending" }
    val displayAddress: String get() = address.ifEmpty { "Address not specified" }
}

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
    val fullName: String = "", // Fallback
    val email: String = "",
    val phone: String = "",
    val phoneNumber: String = "", // Fallback
    val role: String = "customer",
    val status: String = "active"
) {
    val displayName: String get() = name.ifEmpty { fullName }.ifEmpty { "Anonymous User" }
    val displayEmail: String get() = email.ifEmpty { "No email provided" }
    val displayPhone: String get() = phone.ifEmpty { phoneNumber }.ifEmpty { "No phone" }
}
