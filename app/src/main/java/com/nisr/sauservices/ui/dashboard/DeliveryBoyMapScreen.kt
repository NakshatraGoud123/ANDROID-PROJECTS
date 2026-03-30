package com.nisr.sauservices.ui.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nisr.sauservices.data.model.OrderModel

@Composable
fun DeliveryBoyMapScreen(
    order: OrderModel,
    currentLocation: LatLng?
) {
    val customerLatLng = LatLng(order.customerLocation.lat, order.customerLocation.lng)
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation ?: customerLatLng, 16f)
    }

    // Smoothly animate camera to current location
    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.animate(
                com.google.android.gms.maps.CameraUpdateFactory.newLatLng(it),
                1000
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(myLocationButtonEnabled = true, zoomControlsEnabled = false)
        ) {
            // Customer Destination Marker
            Marker(
                state = MarkerState(position = customerLatLng),
                title = "Delivery Location",
                snippet = order.address,
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            )

            // Current Location Marker (Manual marker for smoother visualization if needed, 
            // otherwise isMyLocationEnabled handles it)
            currentLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "My Position",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }
            
            // Note: Polyline route can be added here using Polyline() if route coordinates are available
        }
    }
}
