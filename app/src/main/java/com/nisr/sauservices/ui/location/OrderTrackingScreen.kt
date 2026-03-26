package com.nisr.sauservices.ui.location

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.*
import com.nisr.sauservices.data.api.GoogleMapsRetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    navController: NavController,
    orderId: String
) {
    val database = FirebaseDatabase.getInstance().reference
    var deliveryBoyLocation by remember { mutableStateOf<LatLng?>(null) }
    var customerLocation by remember { mutableStateOf<LatLng?>(null) }
    var polylinePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var eta by remember { mutableStateOf("Calculating...") }
    var distance by remember { mutableStateOf("") }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.5937, 78.9629), 15f)
    }

    // 1. Listen for Delivery Boy's live location
    LaunchedEffect(orderId) {
        val ref = database.child("orders").child(orderId).child("deliveryBoyLocation")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lat = snapshot.child("latitude").getValue(Double::class.java)
                val lng = snapshot.child("longitude").getValue(Double::class.java)
                if (lat != null && lng != null) {
                    deliveryBoyLocation = LatLng(lat, lng)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Also get customer location (fixed for the order)
        database.child("orders").child(orderId).child("customerLocation").get().addOnSuccessListener { snapshot ->
            val lat = snapshot.child("latitude").getValue(Double::class.java)
            val lng = snapshot.child("longitude").getValue(Double::class.java)
            if (lat != null && lng != null) {
                customerLocation = LatLng(lat, lng)
            }
        }
    }

    // 2. Fetch Directions & Polyline
    LaunchedEffect(deliveryBoyLocation, customerLocation) {
        if (deliveryBoyLocation != null && customerLocation != null) {
            try {
                // NOTE: In a production app, replace with a valid API Key
                val response = GoogleMapsRetrofitClient.directionsApi.getDirections(
                    origin = "${deliveryBoyLocation!!.latitude},${deliveryBoyLocation!!.longitude}",
                    destination = "${customerLocation!!.latitude},${customerLocation!!.longitude}",
                    apiKey = "YOUR_GOOGLE_MAPS_API_KEY_HERE"
                )
                if (response.routes.isNotEmpty()) {
                    val points = response.routes[0].overview_polyline.points
                    polylinePoints = PolyUtil.decode(points)
                    eta = response.routes[0].legs[0].duration.text
                    distance = response.routes[0].legs[0].distance.text
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 3. Smooth Camera Follow
    LaunchedEffect(deliveryBoyLocation) {
        deliveryBoyLocation?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLng(it))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Order #$orderId") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                // Delivery Boy Marker (Animated logic can be added here)
                deliveryBoyLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Delivery Partner",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
                }

                // Customer Marker
                customerLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Your Location",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )
                }

                // Route Polyline
                if (polylinePoints.isNotEmpty()) {
                    Polyline(
                        points = polylinePoints,
                        color = Color(0xFF2196F3),
                        width = 12f
                    )
                }
            }

            // ETA Bottom Card
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Arriving in", color = Color.Gray, fontSize = 14.sp)
                            Text(eta, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color(0xFF4CAF50))
                        }
                        Text(distance, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Color(0xFF4CAF50),
                        trackColor = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Your delivery partner is on the way!", fontSize = 14.sp)
                }
            }
        }
    }
}
