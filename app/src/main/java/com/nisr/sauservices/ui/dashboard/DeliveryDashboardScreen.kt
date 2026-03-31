package com.nisr.sauservices.ui.dashboard

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.ui.viewmodels.DeliveryBoyViewModel

private val PrimaryBlue = Color(0xFF1E3A8A)
private val Background = Color(0xFFF9FAFB)
private val Surface = Color(0xFFFFFFFF)
private val Border = Color(0xFFF1F5F9)
private val TextGrey = Color(0xFF6B7280)
private val SuccessGreen = Color(0xFF22C55E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDashboardScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: DeliveryBoyViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    val onLogout = {
        sessionManager.logout()
        navController.navigate("role_selection") { popUpTo(0) { inclusive = true } }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Delivery Dashboard", color = Color.White) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Rounded.Logout, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Surface) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Rounded.List, null) },
                    label = { Text("Available") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Rounded.AssignmentTurnedIn, null) },
                    label = { Text("Assigned") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Rounded.Map, null) },
                    label = { Text("Live Map") }
                )
            }
        }
    ) { padding ->
        val assigned by viewModel.assignedOrders.collectAsState()
        val available by viewModel.availableOrders.collectAsState()
        val currentLocation by viewModel.currentLocation.collectAsState()

        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> DeliveryOrderList(available, "No available deliveries") { order ->
                    // Logic to accept available delivery
                }
                1 -> DeliveryOrderList(assigned, "No assigned deliveries", "Start Delivery") { order ->
                    viewModel.startDelivery(context, order.orderId)
                }
                2 -> {
                    val activeOrder = assigned.find { it.orderStatus == "out_for_delivery" }
                    if (activeOrder != null) {
                        DeliveryBoyMapScreen(activeOrder, currentLocation)
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No active delivery to track", color = TextGrey)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryOrderList(
    orders: List<OrderModel>,
    emptyMsg: String,
    btnText: String? = null,
    onAction: (OrderModel) -> Unit = {}
) {
    if (orders.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(emptyMsg, color = TextGrey)
        }
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(orders) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Order #${order.orderId.takeLast(6)}", fontWeight = FontWeight.Bold)
                            DeliveryStatusBadge(order.orderStatus)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(order.address, fontSize = 13.sp, color = TextGrey)
                        
                        if (btnText != null && order.orderStatus == "assigned") {
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { onAction(order) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(btnText)
                            }
                        } else if (order.orderStatus == "out_for_delivery") {
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { /* Mark as Delivered logic in VM */ },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                            ) {
                                Text("Mark Delivered")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryStatusBadge(status: String) {
    val color = when(status.lowercase()) {
        "pending" -> Color(0xFFF97316)
        "accepted" -> Color(0xFF3B82F6)
        "assigned" -> Color(0xFF6366F1)
        "out_for_delivery" -> Color(0xFF22C55E)
        "delivered" -> Color(0xFF10B981)
        else -> Color.Gray
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            status.uppercase(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
