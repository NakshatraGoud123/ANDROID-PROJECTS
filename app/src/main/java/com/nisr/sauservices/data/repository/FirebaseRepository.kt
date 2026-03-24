package com.nisr.sauservices.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
    private val rtdb = FirebaseDatabase.getInstance().getReference("bookings")

    // --- AUTHENTICATION & ROLE-BASED ACCESS ---
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

    // --- CUSTOMER BOOKING FLOW ---
    suspend fun createBooking(booking: Booking): Result<String> {
        return try {
            val docRef = db.collection("bookings").document()
            val finalBooking = booking.copy(bookingId = docRef.id)
            db.collection("bookings").document(docRef.id).set(finalBooking).await()
            
            // Push to Realtime Database for instant status updates
            rtdb.child(docRef.id).child("status").setValue(finalBooking.status).await()
            
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeBookingStatus(bookingId: String): Flow<String?> = callbackFlow {
        val listener = rtdb.child(bookingId).child("status").addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                trySend(snapshot.getValue(String::class.java))
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { rtdb.child(bookingId).child("status").removeEventListener(listener) }
    }

    // --- DASHBOARD OBSERVERS ---

    fun observeAllBookings(): Flow<List<Booking>> = callbackFlow {
        val listener = db.collection("bookings")
            .orderBy("bookingId", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.toObjects(Booking::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    fun observeBookingsByRole(role: String, id: String): Flow<List<Booking>> = callbackFlow {
        val field = when (role) {
            "SHOPKEEPER" -> "assignedShopkeeper"
            "WORKER" -> "assignedWorker"
            "DELIVERYBOY" -> "assignedDeliveryBoy"
            else -> "customerID"
        }
        val listener = db.collection("bookings")
            .whereEqualTo(field, id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.toObjects(Booking::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    // --- ACTIONS ---

    suspend fun updateBookingStatus(bookingId: String, status: String, workerId: String? = null, deliveryBoyId: String? = null, shopkeeperId: String? = null): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>("status" to status)
            workerId?.let { updates["assignedWorker"] = it }
            deliveryBoyId?.let { updates["assignedDeliveryBoy"] = it }
            shopkeeperId?.let { updates["assignedShopkeeper"] = it }
            
            db.collection("bookings").document(bookingId).update(updates).await()
            rtdb.child(bookingId).child("status").setValue(status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- ADMIN ACTIONS ---
    suspend fun getAllUsers(): List<FirebaseUser> {
        return try {
            db.collection("users").get().await().toObjects(FirebaseUser::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun deleteUser(uid: String): Result<Unit> {
        return try {
            db.collection("users").document(uid).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
