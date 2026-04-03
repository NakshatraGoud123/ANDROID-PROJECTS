package com.nisr.sauservices.service.location

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object WorkerNavigator {
    fun navigateToCustomer(context: Context, lat: Double, lng: Double) {
        val gmmIntentUri = Uri.parse("google.navigation:q=$lat,$lng")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            Toast.makeText(context, "Google Maps app not found", Toast.LENGTH_SHORT).show()
        }
    }
}
