package com.nisr.sauservices.ui.dashboard

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.model.FirebaseUser
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.viewmodels.ShopkeeperViewModel

private val PrimaryBlue = Color(0xFF1E3A8A)
private val Background = Color(0xFFF9FAFB)
private val Surface = Color(0xFFFFFFFF)
private val Border = Color(0xFFF1F5F9)
private val TextDark = Color(0xFF111827)
private val TextGrey = Color(0xFF6B7280)
private val SuccessGreen = Color(0xFF22C55E)
private val PendingOrange = Color(0xFFF97316)
private val InfoBlue = Color(0xFF3B82F6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopkeeperDashboardScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: ShopkeeperViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAssignSheet by remember { mutableStateOf(false) }
    var selectedOrderForAssign by remember { mutableStateOf<OrderModel?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val deliveryBoys by viewModel.deliveryBoys.collectAsState()
    val context = LocalContext.current

    val onLogout = {
        sessionManager.logout()
        navController.navigate("role_selection") { popUpTo(0) { inclusive = true } }
    }

    if (showAssignSheet && selectedOrderForAssign != null) {
        ModalBottomSheet(
            onDismissRequest = { showAssignSheet = false },
            sheetState = sheetState,
            containerColor = Surface
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth().padding(bottom = 32.dp)) {
                Text("Assign Delivery Partner", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Order #${selectedOrderForAssign!!.orderId.takeLast(6)}", color = TextGrey)
                Spacer(modifier = Modifier.height(16.dp))
                
                if (deliveryBoys.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text("No delivery partners available")
                    }
                } else {
                    LazyColumn {
                        items(deliveryBoys) { boy ->
                            Surface(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    viewModel.assignDeliveryBoy(selectedOrderForAssign!!.orderId, boy.userId)
                                    showAssignSheet = false
                                    Toast.makeText(context, "Assigned to ${boy.name}", Toast.LENGTH_SHORT).show()
                                },
                                color = Surface
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Person, null, tint = InfoBlue)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(boy.name, fontWeight = FontWeight.Bold)
                                        Text(boy.phone, fontSize = 12.sp, color = TextGrey)
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(Icons.Rounded.ChevronRight, null, tint = Border)
                                }
                            }
                            HorizontalDivider(color = Border)
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            ShopBottomNavigationBar(selectedTab) { selectedTab = it }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> ShopHomeScreen(viewModel, onLogout) { order ->
                    selectedOrderForAssign = order
                    showAssignSheet = true
                }
                1 -> InventoryScreen(onLogout)
                2 -> ShopAnalyticsScreen(onLogout)
                3 -> ShopProfileScreen(onLogout)
            }
        }
    }
}

@Composable
fun ShopHomeScreen(
    viewModel: ShopkeeperViewModel, 
    onLogout: () -> Unit,
    onAssignClick: (OrderModel) -> Unit
) {
    val pendingOrders by viewModel.pendingOrders.collectAsState()
    val acceptedOrders by viewModel.acceptedOrders.collectAsState()
    val assignedOrders by viewModel.assignedOrders.collectAsState()
    var isOnline by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            ShopDashboardHeader(title = "Shop Dashboard", onLogout = onLogout)
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                AvailabilityCard(isOnline) { isOnline = it }
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ShopStatCard("Pending", pendingOrders.size.toString(), Icons.Rounded.Pending, PendingOrange, Modifier.weight(1f))
                    ShopStatCard("Active", (acceptedOrders.size + assignedOrders.size).toString(), Icons.Rounded.CheckCircle, SuccessGreen, Modifier.weight(1f))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Text("New Orders", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (pendingOrders.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No new orders", color = TextGrey)
                }
            }
        } else {
            items(pendingOrders) { order ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    OrderCard(order, statusText = "PENDING", statusColor = PendingOrange) { 
                        viewModel.acceptOrder(order.orderId) 
                    }
                }
            }
        }

        item {
            Text(
                "Ready to Assign",
                modifier = Modifier.padding(16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        if (acceptedOrders.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No orders to assign", color = TextGrey, fontSize = 14.sp)
                }
            }
        } else {
            items(acceptedOrders) { order ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    OrderCard(order, statusText = "ACCEPTED", statusColor = InfoBlue, buttonText = "Assign Delivery Boy") {
                        onAssignClick(order)
                    }
                }
            }
        }

        item {
            Text(
                "Ongoing Deliveries",
                modifier = Modifier.padding(16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        items(assignedOrders) { order ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                OrderCard(order, statusText = order.orderStatus.uppercase(), statusColor = SuccessGreen, isAssigned = true) {
                    // Navigate to track on map if needed
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: OrderModel, 
    statusText: String, 
    statusColor: Color, 
    buttonText: String? = null,
    isAssigned: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Order #${order.orderId.takeLast(6)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    Text("Amount: ₹${order.totalPrice}", fontSize = 14.sp, color = TextGrey)
                }
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        statusText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(order.address, fontSize = 13.sp, color = TextGrey, maxLines = 1)
            
            if (statusText == "PENDING" || statusText == "ACCEPTED") {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = if(statusText == "PENDING") PrimaryBlue else InfoBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(buttonText ?: "Accept Order")
                }
            }
        }
    }
}

@Composable
fun AvailabilityCard(isOnline: Boolean, onToggle: (Boolean) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Store Status", fontWeight = FontWeight.Bold, color = TextDark)
                Text(
                    if (isOnline) "Accepting new orders" else "Not accepting orders",
                    fontSize = 12.sp,
                    color = if (isOnline) SuccessGreen else TextGrey
                )
            }
            Switch(
                checked = isOnline,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SuccessGreen)
            )
        }
    }
}

@Composable
fun InventoryScreen(onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        ShopDashboardHeader(title = "Inventory", onLogout = onLogout)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Inventory Management Coming Soon")
        }
    }
}

@Composable
fun ShopAnalyticsScreen(onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        ShopDashboardHeader(title = "Analytics", onLogout = onLogout)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Sales Reports Coming Soon")
        }
    }
}

@Composable
fun ShopProfileScreen(onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        ShopDashboardHeader(title = "Profile", onLogout = onLogout)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Profile Settings Coming Soon")
        }
    }
}

@Composable
fun ShopDashboardHeader(title: String, onLogout: () -> Unit) {
    Surface(color = PrimaryBlue, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.statusBarsPadding().padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            IconButton(onClick = onLogout) {
                Icon(Icons.AutoMirrored.Rounded.Logout, null, tint = Color.White)
            }
        }
    }
}

@Composable
fun ShopStatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
            Text(label, fontSize = 12.sp, color = TextGrey)
        }
    }
}

@Composable
fun ShopBottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(containerColor = Surface, tonalElevation = 8.dp) {
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Dashboard, null) },
            label = { Text("Home") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Inventory, null) },
            label = { Text("Stock") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Assessment, null) },
            label = { Text("Stats") },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Settings, null) },
            label = { Text("Profile") },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) }
        )
    }
}
