package com.nisr.sauservices.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val userId: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val role: String = "", // CUSTOMER, SHOPKEEPER, WORKER, DELIVERY
    val address: String = "",
    val status: String = "PENDING", // PENDING, APPROVED
    val createdAt: Timestamp = Timestamp.now()
)

data class Product(
    @DocumentId val productId: String = "",
    val shopId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val category: String = "",
    val imageUrl: String = ""
)

data class ServiceItem(
    @DocumentId val serviceId: String = "",
    val workerId: String = "",
    val name: String = "",
    val basePrice: Double = 0.0,
    val category: String = "",
    val description: String = "",
    val duration: String = ""
)

data class CartItem(
    @DocumentId val cartId: String = "",
    val customerId: String = "",
    val itemId: String = "",
    val itemType: String = "", // product/service
    val quantity: Int = 1,
    val price: Double = 0.0,
    val timestamp: Timestamp = Timestamp.now()
)

data class Order(
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

data class Delivery(
    @DocumentId val deliveryId: String = "",
    val orderId: String = "",
    val deliveryPartnerId: String = "",
    val pickupLocation: String = "",
    val dropLocation: String = "",
    val deliveryStatus: String = "ASSIGNED" // ASSIGNED, PICKED_UP, COMPLETED
)
