package com.nisr.sauservices.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nisr.sauservices.data.model.Booking
import com.nisr.sauservices.data.model.Delivery
import com.nisr.sauservices.data.model.Order

class DashboardRepository {

    fun getOrders(): LiveData<List<Order>> {
        val orders = MutableLiveData<List<Order>>()
        orders.value = listOf(
            Order("101", "John Doe", "$25.00", "Pending"),
            Order("102", "Jane Smith", "$40.50", "Accepted"),
            Order("103", "Robert Brown", "$15.00", "Completed"),
            Order("104", "Emily Davis", "$60.00", "Pending"),
            Order("105", "Michael Wilson", "$32.00", "Accepted"),
            Order("106", "Sarah Miller", "$22.00", "Pending")
        )
        return orders
    }

    fun getBookings(): LiveData<List<Booking>> {
        val bookings = MutableLiveData<List<Booking>>()
        bookings.value = listOf(
            Booking("B001", "Alice Johnson", "Plumbing", "123 Maple St", "10:00 AM", "Assigned"),
            Booking("B002", "Bob Thompson", "Electrician", "456 Oak Ave", "02:00 PM", "On The Way"),
            Booking("B003", "Charlie Davis", "Cleaning", "789 Pine Ln", "09:00 AM", "Started"),
            Booking("B004", "Diana Prince", "AC Repair", "321 Birch Rd", "11:30 AM", "Completed"),
            Booking("B005", "Edward Norton", "Painting", "654 Cedar Ct", "04:00 PM", "Assigned")
        )
        return bookings
    }

    fun getDeliveries(): LiveData<List<Delivery>> {
        val deliveries = MutableLiveData<List<Delivery>>()
        deliveries.value = listOf(
            Delivery("D101", "Kevin Hart", "101 Alpha St", "202 Beta Rd", "3.5 km", "Pending"),
            Delivery("D102", "Justin Bieber", "50 Cent Ave", "8 Mile Blvd", "5.2 km", "Picked"),
            Delivery("D103", "Selena Gomez", "99 Sunset Strip", "101 Hollywood Hills", "2.1 km", "On The Way"),
            Delivery("D104", "Taylor Swift", "13 Folklore Ln", "1989 Pop St", "10.0 km", "Delivered"),
            Delivery("D105", "Dwayne Johnson", "1 Rock Solid Pl", "100 Gym Floor", "1.5 km", "Pending")
        )
        return deliveries
    }
}
