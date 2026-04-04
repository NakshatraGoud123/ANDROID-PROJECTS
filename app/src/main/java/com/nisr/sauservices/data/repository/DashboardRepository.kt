package com.nisr.sauservices.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.model.Delivery
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class DashboardRepository {
    private val dbUrl = "https://sau-services-default-rtdb.asia-southeast1.firebasedatabase.app/"
    private val database = FirebaseDatabase.getInstance(dbUrl)

    // --- SERVICE WORKER LOGIC ---

    fun listenToAssignedBookings(workerId: String): Flow<List<BookingModel>> = callbackFlow {
        val ref = database.getReference("workers").child(workerId).child("assignedBookings")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookingIds = snapshot.children.mapNotNull { it.key }
                fetchBookingsByIds(bookingIds) { trySend(it) }
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    private fun fetchBookingsByIds(ids: List<String>, onResult: (List<BookingModel>) -> Unit) {
        val bookingsRef = database.getReference("service_bookings")
        bookingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(BookingModel::class.java)?.copy(bookingId = it.key ?: "") }
                    .filter { ids.contains(it.bookingId) }
                    .sortedWith(compareBy({ it.scheduledDate }, { it.scheduledTime }))
                onResult(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // --- SHOPKEEPER LOGIC ---

    fun listenToShopOrders(shopId: String): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("shops").child(shopId).child("incomingOrders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderIds = snapshot.children.mapNotNull { it.key }
                fetchOrdersByIds(orderIds) { trySend(it) }
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    private fun fetchOrdersByIds(ids: List<String>, onResult: (List<OrderModel>) -> Unit) {
        val ordersRef = database.getReference("orders")
        ordersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(OrderModel::class.java)?.copy(orderId = it.key ?: "") }
                    .filter { ids.contains(it.orderId) }
                    .filter { it.category == "home_essentials" || it.category == "food_beverages" }
                    .sortedByDescending { it.timestamp }
                onResult(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // --- DELIVERY LOGIC ---

    fun listenToAvailableDeliveries(): Flow<List<Delivery>> = callbackFlow {
        val ref = database.getReference("deliveries")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(Delivery::class.java)?.copy(deliveryId = it.key ?: "") }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // --- AUTOMATION LINKING ---

    suspend fun linkBookingToWorker(workerId: String, bookingId: String) {
        database.getReference("workers").child(workerId).child("assignedBookings").child(bookingId).setValue(true)
    }

    suspend fun linkOrderToShop(shopId: String, orderId: String) {
        database.getReference("shops").child(shopId).child("incomingOrders").child(orderId).setValue(true)
    }
}
