package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.models.*
import com.nisr.sauservices.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*

class AppRepository {
    fun getMockBookings(): List<Booking> = listOf<Booking>(
        Booking("1", "John Doe", "123 Main St, Hyderabad", "Plumbing", "2024-03-01", "10:00 AM", "Pending"),
        Booking("2", "Jane Smith", "456 Park Ave, Hyderabad", "Electrician", "2024-03-01", "02:00 PM", "Accepted"),
        Booking("3", "Mike Johnson", "789 Broadway, Hyderabad", "Cleaning", "2024-03-02", "11:00 AM", "Pending")
    )

    fun getMockProducts(): List<Product> = listOf<Product>(
        Product("1", "AC Repair", R.drawable.ac_repair, 4.6, "Expert AC repair and maintenance", 499.0, "Service", "home", 1),
        Product("2", "Bathroom Cleaning", R.drawable.bathroom_cleaning, 4.8, "Professional deep bathroom cleaning", 699.0, "Service", "cleaning", 1),
        Product("3", "Plumbing Checkup", R.drawable.ac_repair, 4.5, "Standard plumbing inspection", 299.0, "Service", "home", 1)
    )

    fun getMockOrders(): List<Order> = listOf<Order>(
        Order("ORD101", "Alice Brown", "101 Maple St", 499.0, "2024-02-26 04:30 PM", "Pending"),
        Order("ORD102", "Bob Wilson", "202 Oak St", 699.0, "2024-02-27 09:15 AM", "Confirmed")
    )

    fun getMockDeliveries(): List<DeliveryItem> = listOf<DeliveryItem>(
        DeliveryItem("D1", "ORD101", "Alice Brown", "Service Center A", "101 Maple St", "2.5 km", "10 AM - 12 PM", "Pending"),
        DeliveryItem("D2", "ORD103", "Charlie Davis", "Service Center B", "303 Pine St", "1.2 km", "12 PM - 02 PM", "Pending")
    )

    fun getMockCategories(): List<CategoryModel> = listOf(
        CategoryModel("1", "Home & Lifestyle", Icons.Outlined.Lightbulb),
        CategoryModel("2", "Tech Services", Icons.Outlined.LaptopMac),
        CategoryModel("3", "Men's Grooming", Icons.Outlined.Face),
        CategoryModel("4", "Women's Beauty", Icons.Outlined.AutoAwesome),
        CategoryModel("5", "Healthcare", Icons.Outlined.FavoriteBorder),
        CategoryModel("6", "Cleaning", Icons.Outlined.CleaningServices)
    )
}
