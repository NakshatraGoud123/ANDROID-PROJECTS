package com.nisr.sauservices.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nisr.sauservices.data.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun getUserProfile(uid: String): Result<FirebaseUser?> = try {
        val snapshot = db.collection("users").document(uid).get().await()
        Result.success(snapshot.toObject(FirebaseUser::class.java))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun registerUser(user: FirebaseUser): Result<Unit> = try {
        db.collection("users").document(user.userId).set(user).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun createBooking(booking: FirestoreBooking): Result<String> = try {
        val docRef = db.collection("bookings").document()
        val finalBooking = booking.copy(
            bookingId = docRef.id,
            status = "pending",
            assigned = false,
            timestamp = System.currentTimeMillis()
        )
        docRef.set(finalBooking).await()
        Result.success(docRef.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun observeMyBookings(role: String, userId: String): Flow<List<FirestoreBooking>> = callbackFlow {
        val field = when (role.lowercase()) {
            "worker" -> "workerId"
            "shopkeeper" -> "shopkeeperId"
            "delivery" -> "deliveryBoyId"
            else -> "customerId"
        }
        
        val listener = db.collection("bookings")
            .whereEqualTo(field, userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.toObjects(FirestoreBooking::class.java) ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    fun observeAvailableBookings(role: String, serviceType: String? = null): Flow<List<FirestoreBooking>> = callbackFlow {
        var query = db.collection("bookings")
            .whereEqualTo("roleTarget", role.lowercase())
            .whereEqualTo("assigned", false)

        if (role.lowercase() == "worker" && serviceType != null) {
            query = query.whereEqualTo("serviceType", serviceType)
        }

        val listener = query.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.toObjects(FirestoreBooking::class.java) ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    suspend fun acceptBooking(bookingId: String, role: String, userId: String): Result<Unit> = try {
        val updates = mutableMapOf<String, Any>(
            "assigned" to true,
            "status" to "accepted"
        )
        when (role.lowercase()) {
            "worker" -> updates["workerId"] = userId
            "shopkeeper" -> updates["shopkeeperId"] = userId
            "delivery" -> updates["deliveryBoyId"] = userId
        }
        db.collection("bookings").document(bookingId).update(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateBookingStatus(
        bookingId: String, 
        status: String, 
        workerId: String? = null,
        shopkeeperId: String? = null,
        deliveryBoyId: String? = null
    ): Result<Unit> = try {
        val updates = mutableMapOf<String, Any>("status" to status)
        workerId?.let { updates["workerId"] = it }
        shopkeeperId?.let { updates["shopkeeperId"] = it }
        deliveryBoyId?.let { updates["deliveryBoyId"] = it }
        
        if (status == "accepted" || workerId != null || shopkeeperId != null || deliveryBoyId != null) {
            updates["assigned"] = true
        }

        db.collection("bookings").document(bookingId).update(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun observeAllBookings(): Flow<List<FirestoreBooking>> = callbackFlow {
        val listener = db.collection("bookings")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.toObjects(FirestoreBooking::class.java) ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getAllUsers(): List<FirebaseUser> = try {
        val snapshot = db.collection("users").get().await()
        snapshot.toObjects(FirebaseUser::class.java)
    } catch (e: Exception) {
        emptyList()
    }

    suspend fun deleteUser(uid: String): Result<Unit> = try {
        db.collection("users").document(uid).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun observeBookingStatus(bookingId: String): Flow<String?> = callbackFlow {
        val listener = db.collection("bookings").document(bookingId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.getString("status"))
            }
        awaitClose { listener.remove() }
    }
}
