package com.nisr.sauservices.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class LocationViewModel : ViewModel() {

    var uiState by mutableStateOf(LocationUiState())
        private set

    private var geocodeJob: Job? = null
    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    data class LocationUiState(
        val centerLocation: LatLng = LatLng(20.5937, 78.9629), // Default India
        val address: String = "Fetching address...",
        val landmark: String = "",
        val isFetchingAddress: Boolean = false,
        val isLocationConfirmed: Boolean = false
    )

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                updateCenterLocation(latLng, context)
            }
        }
    }

    fun updateCenterLocation(latLng: LatLng, context: Context) {
        uiState = uiState.copy(centerLocation = latLng, isFetchingAddress = true)
        
        // Debounce geocoding to avoid excessive API calls while dragging
        geocodeJob?.cancel()
        geocodeJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            reverseGeocode(latLng, context)
        }
    }

    private fun reverseGeocode(latLng: LatLng, context: Context) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val fullAddress = address.getAddressLine(0)
                val landmark = address.featureName ?: ""
                
                viewModelScope.launch(Dispatchers.Main) {
                    uiState = uiState.copy(
                        address = fullAddress,
                        landmark = landmark,
                        isFetchingAddress = false
                    )
                }
            }
        } catch (e: Exception) {
            viewModelScope.launch(Dispatchers.Main) {
                uiState = uiState.copy(address = "Error fetching address", isFetchingAddress = false)
            }
        }
    }

    fun confirmLocation(onSuccess: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val locationData = mapOf(
            "address" to uiState.address,
            "latitude" to uiState.centerLocation.latitude,
            "longitude" to uiState.centerLocation.longitude,
            "lastUpdated" to System.currentTimeMillis()
        )

        database.child("users").child(userId).child("selectedLocation").setValue(locationData)
            .addOnSuccessListener {
                onSuccess()
            }
    }
}
