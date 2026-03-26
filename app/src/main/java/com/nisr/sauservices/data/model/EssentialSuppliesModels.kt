package com.nisr.sauservices.data.model

data class SupplyCategory(
    val id: String = "",
    val name: String = "",
    val subcategories: List<SupplySubcategory> = emptyList()
)

data class SupplySubcategory(
    val id: String = "",
    val name: String = "",
    val priceRange: String = "",
    val itemType: String = ""
)

data class SupplyOrder(
    val orderId: String = "",
    val userId: String = "",
    val items: List<SupplyOrderItem> = emptyList(),
    val totalPrice: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "pending",
    val location: String = ""
)

data class SupplyOrderItem(
    val name: String = "",
    val price: String = "",
    val quantity: Int = 1
)
