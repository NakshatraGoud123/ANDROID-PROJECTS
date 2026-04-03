package com.nisr.sauservices.service.location

import com.google.firebase.database.FirebaseDatabase

class DeliveryLocationUpdater(private val id: String, private val isWorker: Boolean = false) {
    private val database = FirebaseDatabase.getInstance(FirebasePaths.DB_URL)
    private val path = if (isWorker) FirebasePaths.WORKER_LOCATIONS else FirebasePaths.DELIVERY_LOCATIONS

    fun updateLocation(lat: Double, lng: Double) {
        val locationMap = mapOf(
            "lat" to lat,
            "lng" to lng,
            "timestamp" to System.currentTimeMillis()
        )
        database.getReference(path).child(id).setValue(locationMap)
    }
}
