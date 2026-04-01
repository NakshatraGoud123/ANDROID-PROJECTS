package com.nisr.sauservices.ui.location

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.nisr.sauservices.ui.viewmodel.CustomerTrackingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    navController: NavController,
    orderId: String,
    viewModel: CustomerTrackingViewModel = viewModel()
) {
    val order by viewModel.trackedOrder.collectAsState()
    val deliveryLocation by viewModel.deliveryLocation.collectAsState()
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.5937, 78.9629), 10f)
    }

    LaunchedEffect(orderId) {
        viewModel.trackOrder(orderId)
    }

    // Smart Camera Update: Fits both markers in view
    LaunchedEffect(order?.customerLocation, deliveryLocation) {
        val dest = order?.customerLocation?.let { if (it.lat != 0.0) LatLng(it.lat, it.lng) else null }
        val partner = deliveryLocation?.let { if (it.latitude != 0.0) it else null }

        if (dest != null && partner != null) {
            val bounds = LatLngBounds.builder()
                .include(dest)
                .include(partner)
                .build()
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 200), 1000)
        } else if (dest != null) {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(dest, 15f), 1000)
        } else if (partner != null) {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(partner, 15f), 1000)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Track Order", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("#${orderId.takeLast(6).uppercase()}", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Recenter Button
                    IconButton(onClick = {
                        val dest = order?.customerLocation?.let { LatLng(it.lat, it.lng) }
                        if (dest != null) {
                            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(dest, 15f))
                        }
                    }) {
                        Icon(Icons.Rounded.MyLocation, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false),
                properties = MapProperties(isMyLocationEnabled = false)
            ) {
                // Customer Marker (Destination)
                order?.customerLocation?.let { loc ->
                    if (loc.lat != 0.0) {
                        Marker(
                            state = MarkerState(position = LatLng(loc.lat, loc.lng)),
                            title = "Delivery Destination",
                            snippet = order?.address,
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                        )
                    }
                }

                // Delivery Partner Marker (Live Moving)
                deliveryLocation?.let { loc ->
                    if (loc.latitude != 0.0) {
                        Marker(
                            state = MarkerState(position = loc),
                            title = "Delivery Partner",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                        )
                    }
                }
            }

            // Status Card
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    val status = order?.orderStatus ?: "processing"
                    
                    Text(
                        text = when(status.lowercase()) {
                            "pending" -> "Preparing your order"
                            "accepted" -> "Order confirmed"
                            "assigned" -> "Partner assigned"
                            "out_for_delivery" -> "Partner is on the way"
                            "arriving" -> "Almost there!"
                            "delivered" -> "Delivered"
                            else -> "Processing..."
                        },
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color(0xFF1A1C1E)
                    )
                    
                    Text(
                        text = order?.address?.ifEmpty { "Address fetching..." } ?: "Fetching details...",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    
                    val progress = when(status.lowercase()) {
                        "pending" -> 0.1f
                        "accepted" -> 0.3f
                        "assigned" -> 0.5f
                        "out_for_delivery" -> 0.7f
                        "arriving" -> 0.9f
                        "delivered" -> 1.0f
                        else -> 0.05f
                    }
                    
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = Color(0xFF0FA3A3),
                        trackColor = Color(0xFFE0F2F2)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (status.lowercase() != "delivered") {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFF0FA3A3)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Live Tracking Active",
                                fontSize = 12.sp,
                                color = Color(0xFF0FA3A3),
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text(
                                "Delivery Complete",
                                fontSize = 12.sp,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
