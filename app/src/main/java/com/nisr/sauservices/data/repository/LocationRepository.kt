package com.nisr.sauservices.data.repository

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocationRepository {
    private val database = FirebaseDatabase.getInstance().reference

    fun updateUserLocation(userId: String, latLng: LatLng, role: String) {
        val updates = mapOf(
            "latitude" to latLng.latitude,
            "longitude" to latLng.longitude,
            "role" to role,
            "lastUpdated" to System.currentTimeMillis()
        )
        database.child("locations").child(userId).updateChildren(updates)
    }

    fun observeUserLocation(userId: String): Flow<LatLng?> = callbackFlow {
        val ref = database.child("locations").child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lat = snapshot.child("latitude").getValue(Double::class.java)
                val lng = snapshot.child("longitude").getValue(Double::class.java)
                if (lat != null && lng != null) {
                    trySend(LatLng(lat, lng))
                }
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun observeOrderTracking(orderId: String): Flow<TrackingData> = callbackFlow {
        val ref = database.child("orders").child(orderId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val deliveryBoy = snapshot.child("deliveryBoyLocation").let {
                    val lat = it.child("latitude").getValue(Double::class.java)
                    val lng = it.child("longitude").getValue(Double::class.java)
                    if (lat != null && lng != null) LatLng(lat, lng) else null
                }
                val customer = snapshot.child("customerLocation").let {
                    val lat = it.child("latitude").getValue(Double::class.java)
                    val lng = it.child("longitude").getValue(Double::class.java)
                    if (lat != null && lng != null) LatLng(lat, lng) else null
                }
                val shop = snapshot.child("shopLocation").let {
                    val lat = it.child("latitude").getValue(Double::class.java)
                    val lng = it.child("longitude").getValue(Double::class.java)
                    if (lat != null && lng != null) LatLng(lat, lng) else null
                }
                trySend(TrackingData(deliveryBoy, customer, shop))
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}

data class TrackingData(
    val deliveryBoy: LatLng? = null,
    val customer: LatLng? = null,
    val shop: LatLng? = null
)
