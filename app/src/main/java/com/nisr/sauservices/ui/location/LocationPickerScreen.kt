package com.nisr.sauservices.ui.location

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nisr.sauservices.ui.viewmodel.LocationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerScreen(
    navController: NavController,
    viewModel: LocationViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(uiState.centerLocation, 15f)
    }

    var searchQuery by remember { mutableStateOf("") }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
            if (isGranted) {
                viewModel.getCurrentLocation(context)
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            viewModel.getCurrentLocation(context)
        }
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            viewModel.updateCenterLocation(cameraPositionState.position.target, context)
        }
    }

    // Update camera when centerLocation changes in VM (e.g., from GPS or Search)
    LaunchedEffect(uiState.centerLocation) {
        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(uiState.centerLocation, 15f))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Delivery Location", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(myLocationButtonEnabled = false, zoomControlsEnabled = false)
            )

            // Center Pin
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_mylocation),
                        contentDescription = "Pin",
                        tint = Color.Red,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Search Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search for area, street name...") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { 
                                viewModel.searchLocation(searchQuery, context)
                            }) {
                                Icon(Icons.Default.MyLocation, null, tint = Color(0xFFE91E63))
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        viewModel.searchLocation(searchQuery, context)
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

            // My Location FAB
            FloatingActionButton(
                onClick = { 
                    if (hasLocationPermission) viewModel.getCurrentLocation(context) 
                    else launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 240.dp, end = 16.dp),
                containerColor = Color.White,
                contentColor = Color(0xFFE91E63)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location")
            }

            // Bottom Confirm Sheet
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                shadowElevation = 16.dp,
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "CONFIRM DELIVERY LOCATION", 
                        style = MaterialTheme.typography.labelMedium, 
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFEBF0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.LocationOn, 
                                contentDescription = null, 
                                tint = Color(0xFFE91E63), 
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = if (uiState.isFetchingAddress) "Locating..." else uiState.landmark.ifEmpty { "Pinned Location" }, 
                                fontWeight = FontWeight.ExtraBold, 
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                            Text(
                                text = if (uiState.isFetchingAddress) "Updating address..." else uiState.address, 
                                color = Color.Gray, 
                                fontSize = 14.sp, 
                                maxLines = 2,
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { 
                            viewModel.confirmLocation { 
                                navController.popBackStack() 
                            } 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                    ) {
                        Text(
                            "Confirm Location", 
                            fontSize = 16.sp, 
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
