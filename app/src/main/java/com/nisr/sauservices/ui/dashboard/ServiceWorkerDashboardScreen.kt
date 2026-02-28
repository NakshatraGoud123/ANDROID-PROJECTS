package com.nisr.sauservices.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.data.model.Booking
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.viewmodel.ServiceWorkerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceWorkerDashboardScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: ServiceWorkerViewModel
) {
    val bookings by viewModel.bookings.observeAsState(initial = emptyList())
    val bangalore = LatLng(12.9716, 77.5946)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bangalore, 12f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Service Worker Dashboard", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2E7D6B)),
                actions = {
                    IconButton(onClick = {
                        sessionManager.logout()
                        navController.navigate(Screen.RoleSelection.route) {
                            popUpTo(0)
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        },
        containerColor = Color(0xFFF5F7F6)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Today's Bookings", fontSize = 14.sp, color = Color.Gray)
                        Text(text = "5", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D6B))
                    }
                }
            }

            item {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = false)
                    ) {
                        Marker(
                            state = rememberMarkerState(position = LatLng(12.9750, 77.5980)),
                            title = "Customer Location"
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Bookings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(bookings) { booking ->
                BookingListItem(booking) { newStatus ->
                    viewModel.updateBookingStatus(booking, newStatus)
                }
            }
        }
    }
}

@Composable
fun BookingListItem(booking: Booking, onStatusChange: (String) -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = booking.customerName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = booking.serviceType, color = Color(0xFF2E7D6B), fontSize = 14.sp)
                }
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = booking.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = Color(0xFF2E7D6B),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Address: ${booking.address}", fontSize = 13.sp, color = Color.Gray)
            Text(text = "Slot: ${booking.timeSlot}", fontSize = 13.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            val nextStatus = when (booking.status) {
                "Assigned" -> "On The Way"
                "On The Way" -> "Started"
                "Started" -> "Completed"
                "Pending" -> "Accepted"
                "Accepted" -> "On The Way"
                else -> null
            }

            if (nextStatus != null) {
                Button(
                    onClick = { onStatusChange(nextStatus) },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D6B)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Mark as $nextStatus")
                }
            }
        }
    }
}
