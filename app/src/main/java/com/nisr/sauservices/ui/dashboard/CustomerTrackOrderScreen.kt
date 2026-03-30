package com.nisr.sauservices.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ElectricBike
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
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
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 15f)
    }

    LaunchedEffect(orderId) {
        viewModel.trackOrder(orderId)
    }

    // Live Camera Update
    LaunchedEffect(deliveryLocation) {
        deliveryLocation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLng(it),
                1000
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Real-time Status Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp).statusBarsPadding()) {
                Text(
                    text = "Tracking Your Order",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "ID: #${orderId.takeLast(8).uppercase()}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = order?.orderStatus?.uppercase() ?: "FETCHING...",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = false),
                uiSettings = MapUiSettings(zoomControlsEnabled = false, tiltGesturesEnabled = true)
            ) {
                // Customer's Destination
                order?.customerLocation?.let { loc ->
                    Marker(
                        state = MarkerState(position = LatLng(loc.lat, loc.lng)),
                        title = "Delivery Point",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )
                }

                // LIVE Delivery Boy Marker
                deliveryLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Delivery Partner",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
                }
            }
        }

        // Live Info Bottom Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.ElectricBike,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = when(order?.orderStatus?.lowercase()) {
                                "pending" -> "Preparing your order"
                                "accepted" -> "Order accepted"
                                "assigned" -> "Delivery partner assigned"
                                "out_for_delivery" -> "Partner is on the way"
                                "delivered" -> "Order delivered"
                                else -> "Order status: ${order?.orderStatus ?: "Fetching..."}"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = order?.address ?: "Address not available",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                if (order?.orderStatus != "delivered") {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        }
    }
}
