package com.nisr.sauservices.data.model

import com.google.firebase.database.IgnoreExtraProperties

data class PLSCategory(
    val id: String = "",
    val name: String = "",
    val icon: Int? = null,
    val subcategories: List<String> = emptyList()
)

@IgnoreExtraProperties
data class PLSService(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val unit: String = "", // e.g., /month, /day, /night, Custom Quote, sqft
    val category: String = "",
    val subcategory: String = "",
    val description: String = "",
    val isEnabled: Boolean = true
)

@IgnoreExtraProperties
data class PLSBooking(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhone: String = "",
    val userAddress: String = "",
    val serviceId: String = "",
    val serviceName: String = "",
    val category: String = "",
    val subcategory: String = "",
    val date: String = "",
    val timeSlot: String = "",
    val status: String = "Pending", // Pending, Confirmed, Assigned, In Progress, Completed, Cancelled
    val timestamp: Long = System.currentTimeMillis(),
    val totalPrice: Double = 0.0,
    val paymentMethod: String = "", // Cash, UPI, Card
    
    // Dynamic Fields
    val guestsCount: Int? = null,
    val duration: Int? = null, // days or months
    val areaSqft: Double? = null,
    val requirements: String? = null,
    
    val assignedWorkerId: String = "",
    val assignedWorkerName: String = ""
)
