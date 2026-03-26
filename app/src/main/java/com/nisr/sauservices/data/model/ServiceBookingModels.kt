package com.nisr.sauservices.data.model

data class BookingRequest(
    val bookingId: String = "",
    val category: String = "",
    val subcategory: String = "",
    val type: String = "",
    val price: String = "",
    val date: String = "",
    val time: String = "",
    val userId: String = "",
    val location: String = "",
    val bookingStatus: String = "pending",
    val paymentStatus: String = "pending",
    val assignedWorker: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class BookingCategory(
    val id: String = "",
    val name: String = "",
    val subcategories: List<BookingSubcategory> = emptyList()
)

data class BookingSubcategory(
    val id: String = "",
    val name: String = "",
    val items: List<BookingItem> = emptyList()
)

data class BookingItem(
    val name: String = "",
    val priceRange: String = ""
)
