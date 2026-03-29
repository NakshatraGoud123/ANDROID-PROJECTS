package com.nisr.sauservices.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nisr.sauservices.ui.viewmodels.CustomerTrackingViewModel

@Composable
fun CustomerTrackOrderScreen(
    orderId: String,
    viewModel: CustomerTrackingViewModel
) {
    val order by viewModel.trackedOrder.collectAsState()
    val deliveryLocation by viewModel.deliveryLocation.collectAsState()
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(deliveryLocation ?: LatLng(0.0, 0.0), 15f)
    }

    LaunchedEffect(orderId) {
        viewModel.trackOrder(orderId)
    }

    LaunchedEffect(deliveryLocation) {
        deliveryLocation?.let {
            cameraPositionState.animate(
                com.google.android.gms.maps.CameraUpdateFactory.newLatLng(it)
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Status Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Tracking Order #${orderId.takeLast(6)}",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Status: ${order?.orderStatus?.replaceFirstChar { it.uppercase() } ?: "Pending"}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = false)
            ) {
                // Customer's delivery address marker
                order?.let {
                    val customerLatLng = LatLng(it.customerLocation.lat, it.customerLocation.lng)
                    Marker(
                        state = MarkerState(position = customerLatLng),
                        title = "Delivery Address",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )
                }

                // Moving Delivery Boy marker
                deliveryLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Delivery Partner",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
                }
            }
        }

        // Order Summary Bottom Sheet Style
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Delivery Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Address: ${order?.address ?: "N/A"}")
                Text(text = "Estimated Time: 15-20 mins")
            }
        }
    }
}
