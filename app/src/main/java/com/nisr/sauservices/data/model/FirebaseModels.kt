package com.nisr.sauservices.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class FirebaseUser(
    @DocumentId val userId: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val role: String = "", // "customer", "worker", "shopkeeper", "delivery"
    val serviceType: String? = null, // e.g., "Electrician"
    @get:PropertyName("available") @set:PropertyName("available") var available: Boolean = true,
    val status: String = "PENDING",
    val createdAt: Timestamp = Timestamp.now()
)

data class FirestoreBooking(
    @DocumentId val bookingId: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val roleTarget: String = "", // "worker", "shopkeeper", "delivery"
    val serviceType: String? = null,
    @get:PropertyName("assigned") @set:PropertyName("assigned") var assigned: Boolean = false,
    val status: String = "pending", // "pending", "accepted", "on_the_way", "completed"
    val workerId: String? = null,
    val shopkeeperId: String? = null,
    val deliveryBoyId: String? = null,
    val address: String = "",
    val price: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
