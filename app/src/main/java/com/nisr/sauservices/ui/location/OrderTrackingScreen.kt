package com.nisr.sauservices.ui.location

import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    navController: NavController,
    orderId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val geocoder = remember { Geocoder(context, Locale.getDefault()) }

    // Simulation States
    var searchQuery by remember { mutableStateOf("") }
    var statusTitle by remember { mutableStateOf("Processing...") }
    var statusSubtitle by remember { mutableStateOf("Fetching order details...") }
    var progress by remember { mutableFloatStateOf(0.2f) }
    
    // Initial Bike Position (Simulated)
    var bikeLatLng by remember { mutableStateOf(LatLng(20.5937, 78.9629)) }
    var destinationLatLng by remember { mutableStateOf<LatLng?>(null) }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bikeLatLng, 15f)
    }

    // Marker States
    val bikeMarkerState = rememberMarkerState(position = bikeLatLng)
    val destMarkerState = rememberMarkerState()

    // Sync marker state with bikeLatLng
    LaunchedEffect(bikeLatLng) {
        bikeMarkerState.position = bikeLatLng
    }

    // Real-time Map Update Function
    fun searchAndMove(query: String) {
        scope.launch {
            try {
                val addresses = withContext(Dispatchers.IO) {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocationName(query, 1)
                }
                if (!addresses.isNullOrEmpty()) {
                    val location = LatLng(addresses[0].latitude, addresses[0].longitude)
                    destinationLatLng = location
                    destMarkerState.position = location
                    statusTitle = "Out for Delivery"
                    statusSubtitle = "Partner is moving towards ${addresses[0].getAddressLine(0)}"
                    progress = 0.6f
                    
                    // Move Camera to new location
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(location, 15f))
                    
                    // Move bike to be near the destination for the demo
                    bikeLatLng = LatLng(location.latitude - 0.005, location.longitude - 0.005)
                }
            } catch (e: Exception) {
                statusSubtitle = "Location not found. Try again."
            }
        }
    }

    // Continuous Animation: Subtle movement
    LaunchedEffect(Unit) {
        while(true) {
            delay(2000)
            bikeLatLng = LatLng(
                bikeLatLng.latitude + (Math.random() - 0.5) * 0.0005,
                bikeLatLng.longitude + (Math.random() - 0.5) * 0.0005
            )
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
                    IconButton(onClick = { 
                        scope.launch {
                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(bikeLatLng, 15f))
                        }
                    }) {
                        Icon(Icons.Rounded.MyLocation, null, tint = Color(0xFF00A8A8))
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
                properties = MapProperties(mapType = MapType.NORMAL),
                uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
            ) {
                // Moving Bike Marker
                Marker(
                    state = bikeMarkerState,
                    title = "Delivery Partner",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )

                // Real Destination Marker
                destinationLatLng?.let {
                    Marker(
                        state = destMarkerState,
                        title = "Delivery Point",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )
                }
            }

            // Real Interactive Search Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Enter destination for tracking...") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF00A8A8)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        if (searchQuery.isNotEmpty()) {
                            searchAndMove(searchQuery)
                        }
                    }),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            // Bottom Tracking Status (Matches Screenshot Layout)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White,
                shadowElevation = 24.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp)) {
                    val isCompleted = statusTitle == "Service Completed" || statusTitle == "Delivered"
                    
                    Text(
                        text = statusTitle,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        color = if (isCompleted) Color(0xFF2E7D32) else Color.Black
                    )
                    Text(
                        text = statusSubtitle,
                        fontSize = 15.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))
                    
                    // Custom Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color(0xFFE0F2F2))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color(0xFF00A8A8))
                        )
                        // Progress Indicator Dot
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .align(Alignment.CenterStart)
                                .offset(x = (280 * progress).dp) 
                                .clip(RoundedCornerShape(7.dp))
                                .background(Color(0xFF007A7A))
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isCompleted) {
                            Icon(
                                imageVector = Icons.Rounded.ElectricBike,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFF00A8A8)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Live Tracking Active",
                                fontSize = 14.sp,
                                color = Color(0xFF00A8A8),
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFF2E7D32)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Job Successfully Completed",
                                fontSize = 14.sp,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
