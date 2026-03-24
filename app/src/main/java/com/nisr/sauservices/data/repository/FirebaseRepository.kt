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

    // --- AUTHENTICATION & PROFILE ---
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun getUserProfile(uid: String): Result<FirebaseUser?> {
        return try {
            val snapshot = db.collection("users").document(uid).get().await()
            Result.success(snapshot.toObject(FirebaseUser::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerUser(user: FirebaseUser): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            db.collection("users").document(uid).set(user.copy(userId = uid)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- CUSTOMER BOOKING ACTIONS ---
    suspend fun createBooking(booking: FirestoreBooking): Result<Unit> {
        return try {
            val docRef = db.collection("bookings").document()
            val finalBooking = booking.copy(
                bookingId = docRef.id,
                status = "pending",
                assigned = false,
                timestamp = com.google.firebase.Timestamp.now()
            )
            docRef.set(finalBooking).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- REALTIME DASHBOARD QUERIES (Available Jobs) ---
    fun observeAvailableBookings(roleTarget: String, serviceType: String? = null): Flow<List<FirestoreBooking>> = callbackFlow {
        var query = db.collection("bookings")
            .whereEqualTo("roleTarget", roleTarget)
            .whereEqualTo("assigned", false)

        if (roleTarget == "worker" && serviceType != null) {
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

    // --- REALTIME PERSONAL DASHBOARD (Accepted Jobs) ---
    fun observeMyBookings(role: String, userId: String): Flow<List<FirestoreBooking>> = callbackFlow {
        val field = when (role) {
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

    // --- DASHBOARD ACTIONS ---
    suspend fun acceptBooking(bookingId: String, role: String, userId: String): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "assigned" to true,
                "status" to "accepted"
            )
            when (role) {
                "worker" -> updates["workerId"] = userId
                "shopkeeper" -> updates["shopkeeperId"] = userId
                "delivery" -> updates["deliveryBoyId"] = userId
            }
            db.collection("bookings").document(bookingId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBookingStatus(bookingId: String, status: String): Result<Unit> {
        return try {
            db.collection("bookings").document(bookingId).update("status", status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
