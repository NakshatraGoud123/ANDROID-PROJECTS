package com.nisr.sauservices.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nisr.sauservices.data.model.PLSBooking
import com.nisr.sauservices.data.model.PLSService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PropertyLifestyleRepository {
    private val dbUrl = "https://sau-services-default-rtdb.asia-southeast1.firebasedatabase.app/"
    private val database = FirebaseDatabase.getInstance(dbUrl)
    private val servicesRef = database.getReference("pls_services")
    private val bookingsRef = database.getReference("pls_bookings")

    // --- SERVICES ---

    fun getServices(subcategory: String? = null): Flow<List<PLSService>> = callbackFlow {
        val query = if (subcategory != null) {
            servicesRef.orderByChild("subcategory").equalTo(subcategory)
        } else {
            servicesRef
        }
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val services = snapshot.children.mapNotNull { it.getValue(PLSService::class.java) }
                trySend(services)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    suspend fun addService(service: PLSService): Result<Unit> = try {
        val ref = servicesRef.push()
        val id = ref.key ?: throw Exception("Failed to generate service ID")
        ref.setValue(service.copy(id = id)).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun updateService(service: PLSService): Result<Unit> = try {
        servicesRef.child(service.id).setValue(service).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    // --- BOOKINGS ---

    suspend fun placeBooking(booking: PLSBooking): Result<String> = try {
        val ref = bookingsRef.push()
        val id = ref.key ?: throw Exception("Failed to generate booking ID")
        val finalBooking = booking.copy(id = id)
        ref.setValue(finalBooking).await()
        Result.success(id)
    } catch (e: Exception) { Result.failure(e) }

    fun getBookings(userId: String? = null): Flow<List<PLSBooking>> = callbackFlow {
        val query = if (userId != null) {
            bookingsRef.orderByChild("userId").equalTo(userId)
        } else {
            bookingsRef
        }
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(PLSBooking::class.java) }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    suspend fun updateBookingStatus(bookingId: String, status: String): Result<Unit> = try {
        bookingsRef.child(bookingId).child("status").setValue(status).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }
}
