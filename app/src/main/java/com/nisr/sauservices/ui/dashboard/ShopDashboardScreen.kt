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
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nisr.sauservices.data.model.Order
import com.nisr.sauservices.ui.viewmodel.ShopkeeperViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopDashboardScreen(viewModel: ShopkeeperViewModel) {
    val orders by viewModel.orders.observeAsState(initial = emptyList())
    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopkeeper Dashboard", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2E7D6B)),
                actions = {
                    IconButton(onClick = { /* Logout */ }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = White)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryCard("Total Products", "120", Modifier.weight(1f))
                    SummaryCard("Today Orders", "15", Modifier.weight(1f))
                    SummaryCard("Revenue", "$1.2k", Modifier.weight(1f))
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
                        cameraPositionState = cameraPositionState
                    ) {
                        Marker(
                            state = MarkerState(position = singapore),
                            title = "Shop Location"
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = { /* Update Location Logic */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D6B))
                ) {
                    Text("Update Location")
                }
            }

            item {
                Text(
                    text = "Orders",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(orders) { order ->
                OrderListItem(order) { newStatus ->
                    viewModel.updateOrderStatus(order.orderId, newStatus)
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
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 12.sp, color = Color.Gray)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val backgroundColor = when (status) {
        "Pending" -> Color(0xFFFFF3E0)
        "Accepted" -> Color(0xFFE8F5E9)
        "Completed" -> Color(0xFFE3F2FD)
        else -> Color.LightGray
    }
    val contentColor = when (status) {
        "Pending" -> Color(0xFFEF6C00)
        "Accepted" -> Color(0xFF2E7D32)
        "Completed" -> Color(0xFF1565C0)
        else -> Color.DarkGray
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

@Composable
fun OrderListItem(order: Order, onStatusChange: (String) -> Unit) {
    var status by remember { mutableStateOf(order.status) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Order ID: ${order.orderId}", fontSize = 12.sp, color = Color.Gray)
                StatusBadge(status)
            }
            Text(text = order.customerName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = "Total: ${order.amount}", fontSize = 14.sp, color = Color.DarkGray)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

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
