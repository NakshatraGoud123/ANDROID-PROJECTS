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
import com.nisr.sauservices.data.model.Delivery
import com.nisr.sauservices.data.model.Order
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.viewmodel.DeliveryViewModel
import com.nisr.sauservices.ui.viewmodel.ShopkeeperViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDashboardScreen(
    navController: NavController,
    sessionManager: SessionManager,
    deliveryViewModel: DeliveryViewModel,
    shopkeeperViewModel: ShopkeeperViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) } 
    val deliveries by deliveryViewModel.deliveries.observeAsState(initial = emptyList())
    val orders by shopkeeperViewModel.orders.observeAsState(initial = emptyList())
    
    val location = LatLng(12.9716, 77.5946)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 11f)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Delivery Partner Dashboard", color = Color.White, fontWeight = FontWeight.Bold) },
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
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF2E7D6B),
                    contentColor = Color.White,
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Deliveries") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Shop Orders") }
                    )
                }
            }
        },
        containerColor = Color(0xFFF5F7F6)
    ) { padding ->
        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryCard("Today Deliveries", "8", Modifier.weight(1f))
                        SummaryCard("Earnings", "$85.50", Modifier.weight(1f))
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
                                state = rememberMarkerState(position = LatLng(12.9720, 77.5950)),
                                title = "Pickup"
                            )
                            Marker(
                                state = rememberMarkerState(position = LatLng(12.9800, 77.6000)),
                                title = "Drop"
                            )
                        }
                    }
                }

                items(deliveries) { delivery ->
                    DeliveryListItem(delivery) { newStatus ->
                        deliveryViewModel.updateDeliveryStatus(delivery, newStatus)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(orders) { order ->
                    DashboardOrderListItem(order) { newStatus ->
                        shopkeeperViewModel.updateOrderStatus(order, newStatus)
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 10.sp, color = Color.Gray)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun DeliveryListItem(delivery: Delivery, onStatusChange: (String) -> Unit) {
    var status by remember { mutableStateOf(delivery.status) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = delivery.customerName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = "From: ${delivery.pickupAddress}", fontSize = 14.sp, color = Color.Gray)
            Text(text = "To: ${delivery.dropAddress}", fontSize = 14.sp, color = Color.Gray)
            Text(text = "Distance: ${delivery.distance}", fontSize = 14.sp, color = Color(0xFF2E7D6B))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Button(
                    onClick = {
                        val nextStatus = when (status) {
                            "Pending" -> "Picked"
                            "Picked" -> "On The Way"
                            "On The Way" -> "Delivered"
                            else -> "Delivered"
                        }
                        status = nextStatus
                        onStatusChange(nextStatus)
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D6B))
                ) {
                    Text(status)
                }
            }
        }
    }
}

@Composable
private fun DashboardOrderListItem(order: Order, onStatusChange: (String) -> Unit) {
    var status by remember { mutableStateOf(order.status) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Order ID: ${order.orderId}", fontSize = 12.sp, color = Color.Gray)
            Text(text = order.customerName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = "Total: ${order.amount}", fontSize = 14.sp, color = Color.DarkGray)

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Button(
                    onClick = {
                        val nextStatus = when (status) {
                            "Pending" -> "Accepted"
                            "Accepted" -> "Completed"
                            else -> "Completed"
                        }
                        status = nextStatus
                        onStatusChange(nextStatus)
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D6B))
                ) {
                    Text(status)
                }
            }
        }
    }
}
