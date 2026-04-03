package com.nisr.sauservices.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.nisr.sauservices.data.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val dbUrl = "https://sau-services-default-rtdb.asia-southeast1.firebasedatabase.app/"
    private val database = FirebaseDatabase.getInstance(dbUrl)
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    private fun <T> DataSnapshot.toModelList(clazz: Class<T>): List<T> {
        return children.mapNotNull { snapshot ->
            try {
                val model = snapshot.getValue(clazz)
                // Inject key if it's a model with an ID field
                when (model) {
                    is BookingModel -> model.copy(bookingId = snapshot.key ?: "")
                    is OrderModel -> model.copy(orderId = snapshot.key ?: "")
                    is FirebaseUser -> model.copy(userId = snapshot.key ?: "")
                    else -> model
                } as? T
            } catch (e: Exception) {
                Log.e("FirebaseRepository", "Error parsing ${clazz.simpleName}", e)
                null
            }
        }
    }

    // --- GENERIC LISTENERS ---

    fun listenToOrders(): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("orders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { trySend(snapshot.toModelList(OrderModel::class.java)) }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun listenToBookings(): Flow<List<BookingModel>> = callbackFlow {
        val ref = database.getReference("bookings")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { trySend(snapshot.toModelList(BookingModel::class.java)) }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun listenToUsers(): Flow<List<FirebaseUser>> = callbackFlow {
        val ref = database.getReference("users")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { trySend(snapshot.toModelList(FirebaseUser::class.java)) }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // --- SPECIFIC LISTENERS ---

    fun observeMyBookings(role: String, userId: String): Flow<List<BookingModel>> = callbackFlow {
        val ref = database.getReference("bookings")
        val query = if (role == "customer") {
            ref.orderByChild("customerId").equalTo(userId)
        } else {
            ref.orderByChild("workerId").equalTo(userId)
        }
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.toModelList(BookingModel::class.java))
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    fun listenToCustomerOrders(customerId: String): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("orders").orderByChild("customerId").equalTo(customerId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { trySend(snapshot.toModelList(OrderModel::class.java)) }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun listenToDeliveryBoyOrders(deliveryBoyId: String): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("orders").orderByChild("assignedDeliveryBoy").equalTo(deliveryBoyId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { trySend(snapshot.toModelList(OrderModel::class.java)) }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
    
    fun observeAssignedOrders(deliveryBoyId: String): Flow<List<OrderModel>> = listenToDeliveryBoyOrders(deliveryBoyId)

    fun listenToAvailableOrders(): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("orders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.toModelList(OrderModel::class.java).filter { it.orderStatus == "accepted" }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun observeAvailableBookings(type: String): Flow<List<BookingModel>> = callbackFlow {
        val ref = database.getReference("bookings").orderByChild("status").equalTo("pending")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { trySend(snapshot.toModelList(BookingModel::class.java)) }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun listenToWorkerBookings(workerId: String): Flow<List<BookingModel>> = callbackFlow {
        val ref = database.getReference("bookings")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { trySend(snapshot.toModelList(BookingModel::class.java)) }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getPendingOrders(): Flow<List<OrderModel>> = callbackFlow {
        val ref = database.getReference("orders")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.toModelList(OrderModel::class.java).filter { it.orderStatus == "pending" }
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
                val list = snapshot.toModelList(OrderModel::class.java).filter { it.orderStatus == "accepted" }
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
                val list = snapshot.toModelList(OrderModel::class.java).filter { it.orderStatus == "assigned" || it.orderStatus == "out_for_delivery" }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun listenToAllBookings(): Flow<List<BookingModel>> = listenToBookings()
    fun listenToAllOrders(): Flow<List<OrderModel>> = listenToOrders()
    fun listenToAllUsers(): Flow<List<FirebaseUser>> = listenToUsers()
    fun listenToShopkeeperOrders(): Flow<List<OrderModel>> = listenToOrders()

    fun listenToDeliveryLocations(): Flow<Map<String, LiveLocation>> = callbackFlow {
        val ref = database.getReference("deliveryLocations")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val locations = snapshot.children.associate { it.key!! to it.getValue(LiveLocation::class.java)!! }
                trySend(locations)
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
                val model = snapshot.getValue(OrderModel::class.java)
                trySend(model?.copy(orderId = snapshot.key ?: ""))
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

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

    // --- ACTIONS ---

    suspend fun bookService(booking: BookingModel): Result<String> = try {
        val ref = database.getReference("bookings").push()
        val bookingId = ref.key ?: throw Exception("Failed to get booking key")
        ref.setValue(booking.copy(bookingId = bookingId)).await()
        Result.success(bookingId)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun bookSrv(booking: BookingModel): Result<String> = bookService(booking)

    suspend fun placeOrder(order: OrderModel): Result<String> = try {
        val ref = database.getReference("orders").push()
        val orderId = ref.key ?: throw Exception("Failed to get order key")
        ref.setValue(order.copy(orderId = orderId)).await()
        Result.success(orderId)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> = try {
        database.getReference("orders").child(orderId).child("orderStatus").setValue(status).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun updateBookingStatus(bookingId: String, status: String, workerId: String? = null): Result<Unit> = try {
        if (bookingId.isEmpty()) throw Exception("Booking ID is empty")
        val updates = mutableMapOf<String, Any>("status" to status)
        workerId?.let { updates["workerId"] = it }
        database.getReference("bookings").child(bookingId).updateChildren(updates).await()
        Result.success(Unit)
    } catch (e: Exception) { 
        Log.e("FirebaseRepository", "Error updating booking $bookingId", e)
        Result.failure(e) 
    }

    suspend fun acceptBooking(bookingId: String, type: String, userId: String): Result<Unit> = try {
        updateBookingStatus(bookingId, "accepted", userId)
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

    suspend fun assignDeliveryBoy(orderId: String, deliveryBoyId: String): Result<Unit> = try {
        val updates = mapOf(
            "assignedDeliveryBoy" to deliveryBoyId,
            "orderStatus" to "assigned",
            "assignedAt" to ServerValue.TIMESTAMP
        )
        database.getReference("orders").child(orderId).updateChildren(updates).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun updateDeliveryLocation(deliveryBoyId: String, lat: Double, lng: Double): Result<Unit> = try {
        val locationData = LiveLocation(lat, lng)
        database.getReference("deliveryLocations").child(deliveryBoyId).setValue(locationData).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun registerUser(user: FirebaseUser): Result<Unit> = try {
        database.getReference("users").child(user.userId).setValue(user).await()
        // Also save to Firestore for unified data
        firestore.collection("users").document(user.userId).set(user).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getUserProfile(userId: String): Result<FirebaseUser> = try {
        // Try Realtime DB first
        val snapshot = database.getReference("users").child(userId).get().await()
        var user = snapshot.getValue(FirebaseUser::class.java)
        
        if (user == null) {
            // Try Firestore
            val doc = firestore.collection("users").document(userId).get().await()
            if (doc.exists()) {
                user = FirebaseUser(
                    userId = doc.id,
                    uid = doc.id,
                    name = doc.getString("fullName") ?: doc.getString("name") ?: "",
                    email = doc.getString("email") ?: "",
                    phone = doc.getString("phoneNumber") ?: doc.getString("phone") ?: "",
                    role = doc.getString("role") ?: ""
                )
            }
        }
        
        if (user != null) Result.success(user) else throw Exception("User not found")
    } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteUser(userId: String): Result<Unit> = try {
        database.getReference("users").child(userId).removeValue().await()
        firestore.collection("users").document(userId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getDeliveryBoys(): List<FirebaseUser> = try {
        // Fetch from Firestore as primary for users
        val snapshot = firestore.collection("users")
            .whereIn("role", listOf("delivery", "DELIVERY"))
            .get()
            .await()
        
        val list = snapshot.documents.mapNotNull { doc ->
            FirebaseUser(
                userId = doc.id,
                uid = doc.id,
                name = doc.getString("fullName") ?: doc.getString("name") ?: "No Name",
                email = doc.getString("email") ?: "",
                phone = doc.getString("phoneNumber") ?: doc.getString("phone") ?: "No Phone",
                role = "delivery"
            )
        }
        
        if (list.isEmpty()) {
            // Fallback to RTDB
            val rtdbSnapshot = database.getReference("users").get().await()
            rtdbSnapshot.children.mapNotNull { it.getValue(FirebaseUser::class.java) }
                .filter { it.role.equals("delivery", ignoreCase = true) }
        } else {
            list
        }
    } catch (e: Exception) { 
        Log.e("FirebaseRepository", "Error fetching delivery boys", e)
        emptyList() 
    }
}
