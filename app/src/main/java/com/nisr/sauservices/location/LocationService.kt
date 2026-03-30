package com.nisr.sauservices.location

import android.app.*
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.nisr.sauservices.R

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val database = FirebaseDatabase.getInstance("https://sau-services-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        createNotificationChannel()
        startForeground(1, createNotification())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    updateLocationToFirebase(location)
                }
            }
        }
        
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(2000)
            .setMaxUpdateDelayMillis(5000)
            .build()

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (unlikely: SecurityException) {
            stopSelf()
        }
    }

    private fun updateLocationToFirebase(location: Location) {
        val userId = auth.currentUser?.uid ?: return
        
        val locationData = mapOf(
            "lat" to location.latitude,
            "lng" to location.longitude,
            "timestamp" to ServerValue.TIMESTAMP
        )

        // Update global delivery boy location
        database.child("deliveryLocations").child(userId).setValue(locationData)
        
        // Update location for all orders assigned to this delivery boy
        database.child("orders").orderByChild("assignedDeliveryBoy").equalTo(userId)
            .get().addOnSuccessListener { snapshot ->
                val updates = mutableMapOf<String, Any>()
                snapshot.children.forEach { orderSnapshot ->
                    val orderId = orderSnapshot.key
                    val currentStatus = orderSnapshot.child("orderStatus").getValue(String::class.java)
                    if (orderId != null && currentStatus != "delivered") {
                        updates["orders/$orderId/liveLocation"] = locationData
                    }
                }
                if (updates.isNotEmpty()) {
                    database.updateChildren(updates)
                }
            }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "location_channel",
                "Location Tracking Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "location_channel")
            .setContentTitle("Live Delivery Tracking")
            .setContentText("Your location is being shared for real-time tracking.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
