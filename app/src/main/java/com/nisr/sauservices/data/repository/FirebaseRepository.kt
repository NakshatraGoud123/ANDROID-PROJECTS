package com.nisr.sauservices.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.nisr.sauservices.data.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val dbUrl = "https://sau-services-default-rtdb.asia-southeast1.firebasedatabase.app/"
    private val database = FirebaseDatabase.getInstance(dbUrl)
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    // --- AUTH & PROFILE ---

    suspend fun registerUser(user: FirebaseUser): Result<Unit> = try {
        database.getReference("users").child(user.userId).setValue(user).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getUserProfile(userId: String): Result<FirebaseUser?> = try {
        val snapshot = database.getReference("users").child(userId).get().await()
        Result.success(snapshot.getValue(FirebaseUser::class.java))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // --- CUSTOMER SIDE ---

    suspend fun createBooking(booking: BookingModel): Result<String> = try {
        val ref = database.getReference("bookings").push()
        val bookingId = ref.key ?: throw Exception("Failed to generate booking ID")
        val finalBooking = booking.copy(
            bookingId = bookingId,
            customerId = getCurrentUserId() ?: "",
            timestamp = System.currentTimeMillis()
        )
        ref.setValue(finalBooking).await()
        Result.success(bookingId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun bookService(booking: BookingModel): Result<String> = createBooking(booking)

    suspend fun placeOrder(order: OrderModel): Result<String> = try {
        val ref = database.getReference("orders").push()
        val orderId = ref.key ?: throw Exception("Failed to generate order ID")
        val finalOrder = order.copy(
            orderId = orderId,
            customerId = getCurrentUserId() ?: "",
            timestamp = System.currentTimeMillis()
        )
        ref.setValue(finalOrder).await()
        Result.success(orderId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun observeBookingStatus(bookingId: String): Flow<BookingModel?> = callbackFlow {
        val ref = database.getReference("bookings").child(bookingId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(BookingModel::class.java))
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun observeOrder(orderId: String): Flow<OrderModel?> = callbackFlow {
        val ref = database.getReference("orders").child(orderId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(OrderModel::class.java))
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // --- WORKER / DELIVERY SIDE ---

    fun observeAvailableBookings(type: String, serviceType: String? = null): Flow<List<FirestoreBooking>> = callbackFlow {
        val ref = database.getReference("bookings").orderByChild("status").equalTo("pending")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(FirestoreBooking::class.java) }
                    .filter { booking ->
                        val matchesType = when(type) {
                            "delivery" -> booking.serviceType?.contains("delivery", ignoreCase = true) == true
                            "shopkeeper" -> booking.serviceType?.contains("shop", ignoreCase = true) == true
                            else -> true
                        }
                        val matchesServiceType = serviceType == null || booking.serviceType == serviceType
                        matchesType && matchesServiceType
                    }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun acceptBooking(bookingId: String, type: String, workerId: String): Result<Unit> = try {
        val updates = mapOf(
            "status" to "accepted",
            "workerId" to workerId
        )
        database.getReference("bookings").child(bookingId).updateChildren(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun getAssignedOrders(deliveryBoyId: String): Flow<List<OrderModel>> = callbackFlow {
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

    suspend fun updateDeliveryLocation(userId: String, lat: Double, lng: Double): Result<Unit> = try {
        val updates = mapOf(
            "lat" to lat,
            "lng" to lng,
            "lastUpdated" to ServerValue.TIMESTAMP
        )
        database.getReference("delivery_locations").child(userId).setValue(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun observeDeliveryBoyLocation(deliveryBoyId: String): Flow<Map<String, Any>?> = callbackFlow {
        val ref = database.getReference("delivery_locations").child(deliveryBoyId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                trySend(snapshot.value as? Map<String, Any>)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // --- ADMIN ---

    fun observeAllBookings(): Flow<List<FirestoreBooking>> = callbackFlow {
        val ref = database.getReference("bookings")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(FirestoreBooking::class.java) }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun getAllUsers(): List<FirebaseUser> = try {
        val snapshot = database.getReference("users").get().await()
        snapshot.children.mapNotNull { it.getValue(FirebaseUser::class.java) }
    } catch (e: Exception) {
        emptyList()
    }

    suspend fun deleteUser(userId: String): Result<Unit> = try {
        database.getReference("users").child(userId).removeValue().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // --- COMMON UPDATES ---

    suspend fun updateBookingStatus(bookingId: String, status: String, workerId: String? = null): Result<Unit> = try {
        val updates = mutableMapOf<String, Any>("status" to status)
        workerId?.let { updates["workerId"] = it }
        database.getReference("bookings").child(bookingId).updateChildren(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> = try {
        database.getReference("orders").child(orderId).child("orderStatus").setValue(status).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun assignDeliveryBoy(orderId: String, deliveryBoyId: String): Result<Unit> = try {
        val updates = mapOf(
            "assignedDeliveryBoy" to deliveryBoyId,
            "orderStatus" to "assigned"
        )
        database.getReference("orders").child(orderId).updateChildren(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun getBookingsByStatus(status: String): Flow<List<BookingModel>> = callbackFlow {
        val ref = database.getReference("bookings").orderByChild("status").equalTo(status)
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

    fun getBookingsByStatus(statuses: List<String>): Flow<List<BookingModel>> = callbackFlow {
        val ref = database.getReference("bookings")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(BookingModel::class.java) }
                    .filter { it.status in statuses }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun observeMyBookings(type: String, userId: String): Flow<List<FirestoreBooking>> = callbackFlow {
        val ref = database.getReference("bookings").orderByChild("workerId").equalTo(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(FirestoreBooking::class.java) }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
