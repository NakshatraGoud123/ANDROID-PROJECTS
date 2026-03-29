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

    // --- CUSTOMER SIDE ---

    suspend fun placeOrder(order: OrderModel): Result<String> = try {
        val ref = database.getReference("orders").push()
        val orderId = ref.key ?: throw Exception("Failed to generate order ID")
        val finalOrder = order.copy(
            orderId = orderId,
            customerId = getCurrentUserId() ?: "",
            timestamp = System.currentTimeMillis(),
            orderStatus = "pending",
            liveLocation = LiveLocation()
        )
        ref.setValue(finalOrder).await()
        Result.success(orderId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun bookService(booking: BookingModel): Result<String> = try {
        val ref = database.getReference("bookings").push()
        val bookingId = ref.key ?: throw Exception("Failed to generate booking ID")
        val finalBooking = booking.copy(
            bookingId = bookingId,
            customerId = getCurrentUserId() ?: "",
            timestamp = System.currentTimeMillis(),
            status = "pending",
            workerId = ""
        )
        ref.setValue(finalBooking).await()
        Result.success(bookingId)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun observeOrderTracking(orderId: String): Flow<OrderModel?> = callbackFlow {
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

    // --- SHOPKEEPER SIDE ---

    fun getPendingOrders(): Flow<List<OrderModel>> = observeOrdersByStatus(listOf("pending"))
    fun getAcceptedOrders(): Flow<List<OrderModel>> = observeOrdersByStatus(listOf("accepted"))
    fun getAssignedOrdersForShop(): Flow<List<OrderModel>> = observeOrdersByStatus(listOf("assigned"))

    private fun observeOrdersByStatus(statuses: List<String>): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("orders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(OrderModel::class.java) }
                    .filter { it.orderStatus in statuses }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
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

    // --- SERVICE WORKER SIDE ---

    fun getPendingBookings(): Flow<List<BookingModel>> = observeBookingsByStatus(listOf("pending"))
    fun getAcceptedBookings(): Flow<List<BookingModel>> = observeBookingsByStatus(listOf("accepted"))

    private fun observeBookingsByStatus(statuses: List<String>): Flow<List<BookingModel>> = callbackFlow {
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

    suspend fun updateBookingStatus(bookingId: String, status: String, workerId: String? = null): Result<Unit> = try {
        val updates = mutableMapOf<String, Any>("status" to status)
        workerId?.let { updates["workerId"] = it }
        database.getReference("bookings").child(bookingId).updateChildren(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // --- DELIVERY BOY SIDE ---

    fun observeAssignedOrders(deliveryBoyId: String): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("orders").orderByChild("assignedDeliveryBoy").equalTo(deliveryBoyId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.children.mapNotNull { it.getValue(OrderModel::class.java) })
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun updateDeliveryLocation(userId: String, lat: Double, lng: Double): Result<Unit> = try {
        val location = LiveLocation(lat, lng, System.currentTimeMillis())
        database.getReference("delivery_locations").child(userId).setValue(location).await()
        
        // Also update all orders assigned to this delivery boy
        val ordersSnapshot = database.getReference("orders").orderByChild("assignedDeliveryBoy").equalTo(userId).get().await()
        val updates = mutableMapOf<String, Any>()
        ordersSnapshot.children.forEach { orderSnapshot ->
            updates["orders/${orderSnapshot.key}/liveLocation"] = location
        }
        if (updates.isNotEmpty()) {
            database.getReference().updateChildren(updates).await()
        }
        
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun observeDeliveryBoyLocation(deliveryBoyId: String): Flow<LiveLocation?> = callbackFlow {
        val ref = database.getReference("delivery_locations").child(deliveryBoyId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(LiveLocation::class.java))
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // --- SHARED / COMMON ---

    fun observeMyBookings(role: String, userId: String): Flow<List<BookingModel>> = callbackFlow {
        val field = if (role == "customer") "customerId" else "workerId"
        val ref = database.getReference("bookings").orderByChild(field).equalTo(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.children.mapNotNull { it.getValue(BookingModel::class.java) })
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun observeAllBookings(): Flow<List<FirestoreBooking>> = callbackFlow {
        val ref = database.getReference("bookings")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.children.mapNotNull { it.getValue(FirestoreBooking::class.java) })
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

    // --- SHIMS FOR COMPATIBILITY ---

    fun getBookingsByStatus(statuses: List<String>): Flow<List<BookingModel>> = observeBookingsByStatus(statuses)
    fun observeOrder(orderId: String): Flow<OrderModel?> = observeOrderTracking(orderId)
    fun observeAvailableBookings(type: String): Flow<List<FirestoreBooking>> = observeAllBookings()
    fun observeWorkerBookings(workerId: String): Flow<List<BookingModel>> = observeMyBookings("worker", workerId)
    suspend fun deleteUser(userId: String): Result<Unit> = try {
        database.getReference("users").child(userId).removeValue().await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }
    fun observeBookingStatus(bookingId: String): Flow<String?> = callbackFlow {
        val ref = database.getReference("bookings").child(bookingId).child("status")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { trySend(snapshot.getValue(String::class.java)) }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
    suspend fun updateOrderLiveLocation(orderId: String, lat: Double, lng: Double): Result<Unit> = try {
        val location = LiveLocation(lat, lng, System.currentTimeMillis())
        database.getReference("orders").child(orderId).child("liveLocation").setValue(location).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }
    suspend fun registerUser(user: FirebaseUser): Result<Unit> = try {
        database.getReference("users").child(user.userId).setValue(user).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }
    suspend fun getUserProfile(userId: String): Result<FirebaseUser?> = try {
        val snapshot = database.getReference("users").child(userId).get().await()
        Result.success(snapshot.getValue(FirebaseUser::class.java))
    } catch (e: Exception) { Result.failure(e) }
    suspend fun acceptBooking(bookingId: String, type: String, workerId: String): Result<Unit> = updateBookingStatus(bookingId, "accepted", workerId)
}
