package com.nisr.sauservices.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.model.LiveLocation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val dbUrl = "https://sau-services-default-rtdb.asia-southeast1.firebasedatabase.app/"
    private val database = FirebaseDatabase.getInstance(dbUrl)
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    // --- CUSTOMER ACTIONS ---

    suspend fun saveBooking(booking: BookingModel): Result<String> = try {
        val ref = database.getReference("bookings").push()
        val id = ref.key ?: throw Exception("Failed to generate key")
        val finalBooking = booking.copy(bookingId = id, userId = getCurrentUserId() ?: "anonymous")
        ref.setValue(finalBooking).await()
        Result.success(id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun saveOrder(order: OrderModel): Result<String> = try {
        val ref = database.getReference("orders").push()
        val id = ref.key ?: throw Exception("Failed to generate key")
        val finalOrder = order.copy(orderId = id, userId = getCurrentUserId() ?: "anonymous")
        ref.setValue(finalOrder).await()
        Result.success(id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // --- SERVICE WORKER DASHBOARD ---

    fun observePendingBookings(): Flow<List<BookingModel>> = callbackFlow {
        val ref = database.getReference("bookings").orderByChild("status").equalTo("pending")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(BookingModel::class.java) }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun updateWorkerBookingStatus(bookingId: String, status: String, workerStatus: String, workerId: String): Result<Unit> = try {
        val updates = mapOf(
            "status" to status,
            "assignedWorker" to workerId,
            "workerStatus" to workerStatus
        )
        database.getReference("bookings").child(bookingId).updateChildren(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // --- SHOPKEEPER DASHBOARD ---

    fun observePendingOrders(): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("orders").orderByChild("status").equalTo("pending")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(OrderModel::class.java) }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun shopkeeperAcceptOrder(orderId: String, shopkeeperId: String): Result<Unit> = try {
        val updates = mapOf(
            "status" to "accepted",
            "assignedShopkeeper" to shopkeeperId
        )
        database.getReference("orders").child(orderId).updateChildren(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun assignDeliveryBoy(orderId: String, deliveryBoyId: String): Result<Unit> = try {
        val updates = mapOf(
            "assignedDeliveryBoy" to deliveryBoyId,
            "deliveryStatus" to "assigned"
        )
        database.getReference("orders").child(orderId).updateChildren(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // --- DELIVERY BOY DASHBOARD ---

    fun observeMyDeliveries(deliveryBoyId: String): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("orders").orderByChild("assignedDeliveryBoy").equalTo(deliveryBoyId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(OrderModel::class.java) }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun updateDeliveryStatus(orderId: String, deliveryStatus: String, overallStatus: String? = null): Result<Unit> = try {
        val updates = mutableMapOf<String, Any>("deliveryStatus" to deliveryStatus)
        overallStatus?.let { updates["status"] = it }
        database.getReference("orders").child(orderId).updateChildren(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // --- LIVE LOCATION ---

    suspend fun updateLiveLocation(deliveryBoyId: String, location: LiveLocation): Result<Unit> = try {
        database.getReference("liveLocation").child(deliveryBoyId).setValue(location).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun observeLiveLocation(deliveryBoyId: String): Flow<LiveLocation?> = callbackFlow {
        val ref = database.getReference("liveLocation").child(deliveryBoyId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(LiveLocation::class.java))
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
