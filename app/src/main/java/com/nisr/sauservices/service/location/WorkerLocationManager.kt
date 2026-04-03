package com.nisr.sauservices.service.location

import android.content.Context
import android.content.Intent

object WorkerLocationManager {
    fun startTracking(context: Context, workerId: String) {
        val intent = Intent(context, DeliveryLocationService::class.java).apply {
            putExtra("ID_KEY", workerId)
            putExtra("IS_WORKER", true)
        }
        context.startForegroundService(intent)
    }

    fun stopTracking(context: Context) {
        context.stopService(Intent(context, DeliveryLocationService::class.java))
    }
}
