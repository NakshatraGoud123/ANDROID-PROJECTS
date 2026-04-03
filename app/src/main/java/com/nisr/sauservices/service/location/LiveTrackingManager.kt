package com.nisr.sauservices.service.location

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LiveTrackingManager(
    private val id: String, 
    private val isWorker: Boolean,
    private val map: GoogleMap,
    private var marker: Marker?
) {
    private val database = FirebaseDatabase.getInstance(FirebasePaths.DB_URL)
    private val path = if (isWorker) FirebasePaths.WORKER_LOCATIONS else FirebasePaths.DELIVERY_LOCATIONS

    fun startListening() {
        database.getReference(path).child(id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lat = snapshot.child("lat").getValue(Double::class.java) ?: return
                    val lng = snapshot.child("lng").getValue(Double::class.java) ?: return
                    val newPos = LatLng(lat, lng)

                    marker?.let {
                        MarkerAnimator.animateMarker(it, newPos)
                    }
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(newPos, 16f))
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
