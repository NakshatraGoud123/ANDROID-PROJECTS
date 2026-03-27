package com.nisr.sauservices.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.nisr.sauservices.R
import com.nisr.sauservices.data.model.LiveLocation
import com.nisr.sauservices.data.repository.FirebaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val repository = FirebaseRepository()
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startForeground(1, createNotification())
        requestLocationUpdates()
    }

    private fun createNotification(): Notification {
        val channelId = "location_channel"
        val channel = NotificationChannel(channelId, "Delivery Tracking", NotificationManager.IMPORTANCE_LOW)
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Delivery in Progress")
            .setContentText("Your location is being shared for delivery tracking.")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(2000)
            .build()

        fusedLocationClient.requestLocationUpdates(request, object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    val userId = repository.getCurrentUserId() ?: return
                    serviceScope.launch {
                        repository.updateLiveLocation(
                            userId,
                            LiveLocation(location.latitude, location.longitude)
                        )
                    }
                }
            }
        }, Looper.getMainLooper())
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY
}
