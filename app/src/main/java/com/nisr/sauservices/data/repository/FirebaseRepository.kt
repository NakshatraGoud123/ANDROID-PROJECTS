package com.nisr.sauservices.data.repository

import android.util.Log
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

    // --- AUTH LOGIC ---
    suspend fun registerUser(user: FirebaseUser): Result<Unit> = try {
        database.getReference("users").child(user.uid).setValue(user).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getUserProfile(userId: String): Result<FirebaseUser> = try {
        val snapshot = database.getReference("users").child(userId).get().await()
        val user = snapshot.getValue(FirebaseUser::class.java) ?: throw Exception("User not found")
        Result.success(user)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getAllUsers(): Result<List<FirebaseUser>> = try {
        val snapshot = database.getReference("users").get().await()
        Result.success(snapshot.children.mapNotNull { it.getValue(FirebaseUser::class.java) })
    } catch (e: Exception) { Result.failure(e) }

    fun listenToAllUsers(): Flow<List<FirebaseUser>> = callbackFlow {
        val ref = database.getReference("users")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.children.mapNotNull { it.getValue(FirebaseUser::class.java) })
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun deleteUser(userId: String): Result<Unit> = try {
        database.getReference("users").child(userId).removeValue().await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    // --- CUSTOMER LOGIC ---

    suspend fun bookService(booking: BookingModel): Result<String> = try {
        val ref = database.getReference("bookings").push()
        val bookingId = ref.key ?: throw Exception("Failed to get booking key")
        val finalBooking = booking.copy(bookingId = bookingId)
        ref.setValue(finalBooking).await()
        Result.success(bookingId)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun placeOrder(order: OrderModel): Result<String> = try {
        val ref = database.getReference("orders").push()
        val orderId = ref.key ?: throw Exception("Failed to get order key")
        val finalOrder = order.copy(orderId = orderId)
        ref.setValue(finalOrder).await()
        Result.success(orderId)
    } catch (e: Exception) { Result.failure(e) }

    fun observeMyBookings(role: String, userId: String): Flow<List<BookingModel>> = callbackFlow {
        val ref = database.getReference("bookings")
        val query = if (role == "customer") {
            ref.orderByChild("customerId").equalTo(userId)
        } else {
            ref.orderByChild("workerId").equalTo(userId)
        }
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.children.mapNotNull { it.getValue(BookingModel::class.java) })
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    fun observeBookingStatus(bookingId: String): Flow<String?> = callbackFlow {
        val ref = database.getReference("bookings").child(bookingId).child("status")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(String::class.java))
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun listenToCustomerOrder(orderId: String): Flow<OrderModel?> = callbackFlow {
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

    fun observeOrder(orderId: String): Flow<OrderModel?> = listenToCustomerOrder(orderId)
    fun observeOrderTracking(orderId: String): Flow<OrderModel?> = listenToCustomerOrder(orderId)

    // --- SHOPKEEPER LOGIC ---

    fun listenToShopkeeperOrders(shopkeeperId: String? = null): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("orders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.children.mapNotNull { it.getValue(OrderModel::class.java) })
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getPendingOrders(): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("orders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(OrderModel::class.java) }.filter { it.orderStatus == "pending" }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getAcceptedOrders(): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("orders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(OrderModel::class.java) }.filter { it.orderStatus == "accepted" }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getAssignedOrdersForShop(shopId: String? = null): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("orders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(OrderModel::class.java) }.filter { it.orderStatus == "assigned" || it.orderStatus == "out_for_delivery" }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun observeOrdersByStatus(statuses: List<String>): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("orders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(OrderModel::class.java) }.filter { it.orderStatus in statuses }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun getDeliveryBoys(): List<FirebaseUser> = try {
        val snapshot = database.getReference("users")
            .orderByChild("role")
            .equalTo("delivery")
            .get().await()
        snapshot.children.mapNotNull { it.getValue(FirebaseUser::class.java) }
    } catch (e: Exception) { emptyList() }

    suspend fun assignDeliveryBoy(orderId: String, deliveryBoyId: String): Result<Unit> = try {
        val updates = mapOf(
            "assignedDeliveryBoy" to deliveryBoyId,
            "orderStatus" to "assigned",
            "assignedAt" to ServerValue.TIMESTAMP
        )
        database.getReference("orders").child(orderId).updateChildren(updates).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun acceptOrder(orderId: String, shopkeeperId: String): Result<Unit> = try {
        val updates = mapOf(
            "orderStatus" to "accepted",
            "acceptedBy" to shopkeeperId,
            "acceptedAt" to ServerValue.TIMESTAMP
        )
        database.getReference("orders").child(orderId).updateChildren(updates).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    // --- SERVICE WORKER LOGIC ---

    fun getPendingBookings(): Flow<List<BookingModel>> = callbackFlow {
        val ref = database.getReference("bookings")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(BookingModel::class.java) }.filter { it.status == "pending" }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun listenToWorkerBookings(workerId: String): Flow<List<BookingModel>> = callbackFlow {
        val ref = database.getReference("bookings")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.children.mapNotNull { it.getValue(BookingModel::class.java) })
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun observeWorkerBookings(workerId: String, statuses: List<String>? = null): Flow<List<BookingModel>> = callbackFlow {
        val ref = database.getReference("bookings")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(BookingModel::class.java) }
                if (statuses != null) {
                    trySend(list.filter { it.status in statuses && (it.workerId == workerId || it.status == "pending") })
                } else {
                    trySend(list.filter { it.workerId == workerId })
                }
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
    
    fun observeWorkerBookings(workerId: String, status: String): Flow<List<BookingModel>> = observeWorkerBookings(workerId, listOf(status))

    suspend fun updateBookingStatus(bookingId: String, status: String, workerId: String? = null): Result<Unit> = try {
        val updates = mutableMapOf<String, Any>("status" to status)
        workerId?.let { updates["workerId"] = it }
        database.getReference("bookings").child(bookingId).updateChildren(updates).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    // --- DELIVERY BOY LOGIC ---

    fun listenToAvailableOrders(): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("orders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(OrderModel::class.java) }.filter { it.orderStatus == "accepted" }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun listenToDeliveryBoyOrders(deliveryBoyId: String): Flow<List<OrderModel>> = callbackFlow {
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

    fun observeAssignedOrders(deliveryBoyId: String): Flow<List<OrderModel>> = listenToDeliveryBoyOrders(deliveryBoyId)

    suspend fun updateDeliveryLocation(deliveryBoyId: String, lat: Double, lng: Double): Result<Unit> = try {
        val locationData = mapOf(
            "lat" to lat,
            "lng" to lng,
            "timestamp" to ServerValue.TIMESTAMP
        )
        database.getReference("deliveryLocations").child(deliveryBoyId).setValue(locationData).await()
        
        val ordersSnapshot = database.getReference("orders")
            .orderByChild("assignedDeliveryBoy")
            .equalTo(deliveryBoyId)
            .get().await()
            
        val updates = mutableMapOf<String, Any>()
        ordersSnapshot.children.forEach { orderSnapshot ->
            val orderId = orderSnapshot.key
            if (orderId != null) {
                updates["orders/$orderId/liveLocation"] = locationData
            }
        }
        if (updates.isNotEmpty()) {
            database.getReference().updateChildren(updates).await()
        }
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> = try {
        database.getReference("orders").child(orderId).child("orderStatus").setValue(status).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    // --- MAP \u0026 LOCATION ---

    fun observeDeliveryBoyLocation(deliveryBoyId: String): Flow<LiveLocation?> = callbackFlow {
        val ref = database.getReference("deliveryLocations").child(deliveryBoyId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(LiveLocation::class.java))
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun observeDeliveryBoyLiveLocation(deliveryBoyId: String): Flow<LiveLocation?> = observeDeliveryBoyLocation(deliveryBoyId)

    // --- ADMIN LOGIC ---

    fun listenToAllBookings(): Flow<List<BookingModel>> = callbackFlow {
        val ref = database.getReference("bookings")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.children.mapNotNull { it.getValue(BookingModel::class.java) })
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun listenToAllOrders(): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("orders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.children.mapNotNull { it.getValue(OrderModel::class.java) })
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun observeAllBookings(): Flow<List<BookingModel>> = listenToAllBookings()

    fun observeAvailableBookings(type: String): Flow<List<BookingModel>> = callbackFlow {
        val ref = database.getReference("bookings").orderByChild("status").equalTo("pending")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.children.mapNotNull { it.getValue(BookingModel::class.java) })
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun acceptBooking(bookingId: String, type: String, userId: String): Result<Unit> = try {
        updateBookingStatus(bookingId, "accepted", workerId = userId)
    } catch (e: Exception) { Result.failure(e) }
}
