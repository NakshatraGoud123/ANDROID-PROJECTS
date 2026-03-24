package com.nisr.sauservices.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class FirebaseUser(
    @DocumentId val userId: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val role: String = "", // CUSTOMER, WORKER, SHOPKEEPER, DELIVERYBOY, ADMIN
    val address: String = "",
    val status: String = "PENDING", // PENDING, APPROVED, OFFLINE
    val createdAt: Timestamp = Timestamp.now()
)

data class FirestoreBooking(
    @DocumentId val bookingID: String = "",
    val customerID: String = "",
    val customerName: String = "",
    val serviceType: String = "",
    val description: String = "",
    val address: String = "",
    val bookingTime: Timestamp = Timestamp.now(),
    val status: String = "pending", // pending, accepted, assigned, in_progress, completed, out_for_delivery, delivered
    val assignedWorker: String? = null,
    val assignedShopkeeper: String? = null,
    val assignedDeliveryBoy: String? = null,
    val totalPrice: Double = 0.0
)

// For backward compatibility if needed, or we can just use Booking for everything
data class FirebaseOrder(
    @DocumentId val orderId: String = "",
    val customerId: String = "",
    val shopId: String? = null,
    val workerId: String? = null,
    val deliveryPartnerId: String? = null,
    val itemType: String = "", // product/service
    val itemsList: List<Map<String, Any>> = emptyList(),
    val totalPrice: Double = 0.0,
    val address: String = "",
    val timeSlot: String? = null,
    val orderStatus: String = "PENDING",
    val paymentStatus: String = "UNPAID",
    val createdAt: Timestamp = Timestamp.now()
)
