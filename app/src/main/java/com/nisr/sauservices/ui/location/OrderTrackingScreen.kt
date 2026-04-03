package com.nisr.sauservices.ui.location

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.ElectricBike
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.nisr.sauservices.ui.viewmodel.CustomerTrackingViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    navController: NavController,
    orderId: String,
    viewModel: CustomerTrackingViewModel = viewModel()
) {
    val order by viewModel.trackedOrder.collectAsState()
    val deliveryLocation by viewModel.deliveryLocation.collectAsState()
    val context = LocalContext.current
    
    // Dummy Simulation States
    var dummySearchQuery by remember { mutableStateOf("") }
    var simulatedBikePos by remember { mutableStateOf(LatLng(20.5937, 78.9629)) }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(simulatedBikePos, 12f)
    }

    // Bike Movement Animation (Dummy)
    LaunchedEffect(Unit) {
        while(true) {
            delay(2000)
            simulatedBikePos = LatLng(
                simulatedBikePos.latitude + (Math.random() - 0.5) * 0.01,
                simulatedBikePos.longitude + (Math.random() - 0.5) * 0.01
            )
        }
    }

    LaunchedEffect(orderId) {
        viewModel.trackOrder(orderId)
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
                    IconButton(onClick = { }) {
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
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            ) {
                // Animated Bike Marker (Dummy)
                Marker(
                    state = MarkerState(position = simulatedBikePos),
                    title = "Delivery Partner",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )

                // Actual Destination if available
                order?.customerLocation?.let { loc ->
                    if (loc.lat != 0.0) {
                        Marker(
                            state = MarkerState(position = LatLng(loc.lat, loc.lng)),
                            title = "Your Location",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                        )
                    }
                }
            }

            // Dummy Search Bar for Demo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                TextField(
                    value = dummySearchQuery,
                    onValueChange = { dummySearchQuery = it },
                    placeholder = { Text("Enter address to simulate...") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    maxLines = 1
                )
            }

            // Bottom Tracking Card (Matches your Screenshot)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = Color.White,
                shadowElevation = 20.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)) {
                    Text(
                        text = "Processing...",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Fetching details...",
                        fontSize = 15.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Custom Progress Bar like in screenshot
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color(0xFFE0F7F7))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.4f) // Progress level
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color(0xFF00A8A8))
                        )
                        // Progress Dot
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .align(Alignment.CenterStart)
                                .offset(x = 130.dp) // Adjust dot position
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF007A7A))
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ElectricBike,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF00A8A8)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Live Tracking Active",
                            fontSize = 13.sp,
                            color = Color(0xFF00A8A8),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
