package com.nisr.sauservices.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nisr.sauservices.data.model.*

class DashboardRepository {

    fun getOrders(): LiveData<List<Order>> {
        val orders = MutableLiveData<List<Order>>()
        orders.value = listOf(
            Order(
                orderId = "ORD001",
                customerName = "Aarav Mehta",
                customerPhone = "+91 98765 00001",
                amount = "₹660",
                status = "placed",
                paymentMode = "COD",
                items = listOf(
                    OrderItem("Basmati Rice 5kg", 1, 380.0),
                    OrderItem("Amul Butter 500g", 1, 280.0)
                ),
                createdAt = "2024-03-04 14:30"
            ),
            Order(
                orderId = "ORD002",
                customerName = "Priya Sharma",
                customerPhone = "+91 98765 00002",
                amount = "₹370",
                status = "accepted",
                paymentMode = "Prepaid",
                items = listOf(
                    OrderItem("Toor Dal 1kg", 2, 280.0),
                    OrderItem("Fresh Paneer 200g", 1, 90.0)
                ),
                createdAt = "2024-03-04 14:45"
            )
        )
        return orders
    }

    fun getInventory(): LiveData<List<InventoryItem>> {
        val inventory = MutableLiveData<List<InventoryItem>>()
        inventory.value = listOf(
            InventoryItem("1", "Basmati Rice 5kg", "Groceries", 380.0, 420.0, 45, "In Stock"),
            InventoryItem("2", "Amul Butter 500g", "Dairy", 280.0, 290.0, 22, "In Stock"),
            InventoryItem("3", "Toor Dal 1kg", "Groceries", 140.0, 160.0, 3, "Low Stock"),
            InventoryItem("4", "Coca Cola 2L", "Beverages", 95.0, 100.0, 8, "Low Stock"),
            InventoryItem("5", "Fresh Paneer 200g", "Dairy", 90.0, 100.0, 15, "In Stock"),
            InventoryItem("6", "Paracetamol Strips", "Pharmacy", 35.0, 38.0, 60, "In Stock")
        )
        return inventory
    }

    fun getBookings(): LiveData<List<Booking>> {
        val bookings = MutableLiveData<List<Booking>>()
        bookings.value = listOf(
            Booking(
                bookingId = "B001",
                customerName = "Aarav Mehta",
                customerPhone = "+91 98765 00001",
                serviceType = "AC Repair",
                address = "42, MG Road, Sector 15",
                timeSlot = "10:00 AM - 12:00 PM",
                status = "placed",
                price = "₹799",
                description = "Split AC not cooling properly",
                otp = "6743"
            ),
            Booking(
                bookingId = "B002",
                customerName = "Priya Sharma",
                customerPhone = "+91 98765 00002",
                serviceType = "Wiring Repair",
                address = "78, Lakeview Apartments",
                timeSlot = "2:00 PM - 4:00 PM",
                status = "accepted",
                price = "₹399",
                description = "Kitchen wiring sparking",
                otp = "2198"
            )
        )
        return bookings
    }

    fun getDeliveries(): LiveData<List<Delivery>> {
        val deliveries = MutableLiveData<List<Delivery>>()
        deliveries.value = listOf(
            Delivery(
                deliveryId = "D101",
                customerName = "Priya Sharma",
                pickupAddress = "78, Lakeview Apartments",
                dropAddress = "FreshMart Express",
                distance = "3.2 km",
                status = "Assigned",
                cartAddedTime = "04:45 PM",
                items = "Toor Dal 1kg, Fresh Paneer 200g",
                otp = "7193",
                paymentMode = "Prepaid",
                pickupShop = "FreshMart Express",
                eta = "15 min"
            )
        )
        return deliveries
    }
}
