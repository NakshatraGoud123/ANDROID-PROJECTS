package com.nisr.sauservices.ui.dashboard

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.viewmodels.DeliveryBoyViewModel

// Clean Palette
private val PrimaryBlue = Color(0xFF1E3A8A)
private val Background = Color(0xFFF9FAFB)
private val Surface = Color(0xFFFFFFFF)
private val Border = Color(0xFFF1F5F9)
private val TextDark = Color(0xFF111827)
private val TextGrey = Color(0xFF6B7280)
private val SuccessGreen = Color(0xFF22C55E)
private val PendingOrange = Color(0xFFF97316)
private val ActiveBlue = Color(0xFF3B82F6)

@Composable
fun DeliveryDashboardScreen(
    navController: NavController,
    sessionManager: SessionManager,
    deliveryBoyViewModel: DeliveryBoyViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var isChatOpen by remember { mutableStateOf(false) }

    val onLogout = {
        sessionManager.logout()
        navController.navigate(Screen.RoleSelection.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            DeliveryBottomNavigationBar(selectedTab) { selectedTab = it }
        },
        floatingActionButton = {
            DeliveryFloatingChatButton { isChatOpen = true }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> DeliveryHomeScreen(deliveryBoyViewModel, onLogout)
                1 -> DeliveriesListScreen(deliveryBoyViewModel, onLogout)
                2 -> DeliveryEarningsScreen(onLogout)
                3 -> DeliveryProfileScreen(onLogout)
            }
            
            if (isChatOpen) {
                DeliveryChatWindow(onClose = { isChatOpen = false })
            }
        }
    }
}

@Composable
fun DeliveryHomeScreen(viewModel: DeliveryBoyViewModel, onLogout: () -> Unit) {
    val assignedOrders by viewModel.assignedOrders.collectAsState()
    val availableOrders by viewModel.availableOrders.collectAsState()
    val context = LocalContext.current
    var isOnline by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            DeliveryDashboardHeader(
                title = "Delivery Partner",
                onLogout = onLogout
            )
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                // Availability Card
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
                            Text("Availability", fontWeight = FontWeight.Bold, color = TextDark)
                            Text(
                                if (isOnline) "Online — Accepting jobs" else "Offline — Not accepting jobs",
                                fontSize = 12.sp,
                                color = if (isOnline) SuccessGreen else TextGrey
                            )
                        }
                        Switch(
                            checked = isOnline,
                            onCheckedChange = { isOnline = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PrimaryBlue
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Stats Grid
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DeliveryStatCard("Assigned", assignedOrders.size.toString(), Icons.Rounded.Inventory2, PendingOrange, Modifier.weight(1f))
                    DeliveryStatCard("Available", availableOrders.size.toString(), Icons.Rounded.ElectricBike, ActiveBlue, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Assigned Tasks", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (assignedOrders.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No assigned orders", color = TextGrey)
                }
            }
        } else {
            items(assignedOrders) { order ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    DeliveryTaskCard(order, onAction = { viewModel.startDelivery(context, order.orderId) })
                }
            }
        }

        item {
            Text("Available Orders", modifier = Modifier.padding(16.dp), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
        }

        if (availableOrders.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No available orders", color = TextGrey)
                }
            }
        } else {
            items(availableOrders) { order ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    DeliveryTaskCard(order, onAction = { })
                }
            }
        }
    }
}

@Composable
fun DeliveriesListScreen(viewModel: DeliveryBoyViewModel, onLogout: () -> Unit) {
    val assignedOrders by viewModel.assignedOrders.collectAsState()
    val context = LocalContext.current
    
    Column(modifier = Modifier.fillMaxSize()) {
        DeliveryDashboardHeader(title = "Deliveries", onLogout = onLogout, showToggle = false)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(assignedOrders) { order ->
                DeliveryTaskCard(order, onAction = { viewModel.startDelivery(context, order.orderId) })
            }
        }
    }
}

@Composable
fun DeliveryEarningsScreen(onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        DeliveryDashboardHeader(title = "Earnings", onLogout = onLogout, showToggle = false)
        Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DeliveryStatCard("Today", "₹1,800", Icons.Rounded.AttachMoney, ActiveBlue, Modifier.weight(1f))
                DeliveryStatCard("This Week", "₹9,200", Icons.Rounded.CalendarMonth, PrimaryBlue, Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Surface,
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Weekly Earnings", fontSize = 14.sp, color = TextGrey)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val heights = listOf(0.4f, 0.6f, 0.5f, 0.85f, 0.7f, 0.9f, 0.8f)
                        heights.forEach { h ->
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .fillMaxHeight(h)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ActiveBlue)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("M", "T", "W", "T", "F", "S", "S").forEach { 
                            Text(it, fontSize = 12.sp, color = TextGrey)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Delivery History", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(12.dp))

            val history = listOf(
                Triple("Kumar Store — Block A, Sec 5", "Today", "₹60"),
                Triple("Tech Hub — MG Road", "Today", "₹80"),
                Triple("Daily Mart — Dwarka Sec 7", "Yesterday", "₹55")
            )

            history.forEach { (route, time, amount) ->
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
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
                            Text(route, fontWeight = FontWeight.Bold, color = TextDark, fontSize = 14.sp)
                            Text(time, fontSize = 12.sp, color = TextGrey)
                        }
                        Text(amount, fontWeight = FontWeight.Bold, color = SuccessGreen, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryProfileScreen(onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        DeliveryDashboardHeader(title = "Profile", onLogout = onLogout, showToggle = false)
        Column(
            modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Surface,
                border = BorderStroke(1.dp, Border)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(ActiveBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.ElectricBike, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Suresh Yadav", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text("Delivery Partner • ID: DP-3078", fontSize = 13.sp, color = TextGrey)
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Surface,
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Vehicle Details", fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.ElectricBike, null, tint = Color.Red, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Honda Activa • DL-05-AB-1234", fontSize = 14.sp, color = TextGrey)
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Surface,
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Verification", fontWeight = FontWeight.Bold, color = TextDark)
                        Text("Completed", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    VerificationDetail(Icons.Rounded.CheckCircleOutline, "Aadhaar Verified")
                    VerificationDetail(Icons.Rounded.Badge, "Driving License Uploaded")
                    VerificationDetail(Icons.Rounded.Phone, "+91 87654xxxxx")
                }
            }
        }
    }
}

@Composable
fun VerificationDetail(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(icon, null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 14.sp, color = TextGrey)
    }
}

@Composable
fun DeliveryDashboardHeader(
    title: String,
    onLogout: () -> Unit = {},
    showToggle: Boolean = true
) {
    Surface(
        color = PrimaryBlue,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.statusBarsPadding().padding(horizontal = 16.dp, vertical = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onLogout, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.AutoMirrored.Rounded.Logout, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Rounded.Notifications, null, tint = Color.White)
                    }
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFEAB308)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Person, null, tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryStatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(label, fontSize = 12.sp, color = TextGrey)
        }
    }
}

@Composable
fun DeliveryTaskCard(order: OrderModel, onAction: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Storefront, null, tint = PendingOrange, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Order #${order.orderId.takeLast(6)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                        Text(order.orderId, fontSize = 13.sp, color = TextGrey)
                    }
                }
                Surface(
                    color = when(order.orderStatus.lowercase()) {
                        "accepted" -> PendingOrange.copy(alpha = 0.1f)
                        "assigned" -> ActiveBlue.copy(alpha = 0.1f)
                        "out_for_delivery" -> SuccessGreen.copy(alpha = 0.1f)
                        else -> Color.Gray.copy(alpha = 0.1f)
                    },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        order.orderStatus.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when(order.orderStatus.lowercase()) {
                            "accepted" -> PendingOrange
                            "assigned" -> ActiveBlue
                            "out_for_delivery" -> SuccessGreen
                            else -> Color.Gray
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.LocationOn, null, tint = PendingOrange, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(order.address, fontSize = 13.sp, color = TextGrey)
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            if (order.orderStatus == "assigned") {
                Button(
                    onClick = onAction,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Start Delivery")
                }
            }
        }
    }
}

@Composable
fun DeliveryBottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Surface,
        contentColor = PrimaryBlue,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Home, null) },
            label = { Text("Home") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Rounded.Assignment, null) },
            label = { Text("Deliveries") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.BarChart, null) },
            label = { Text("Earnings") },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.PersonOutline, null) },
            label = { Text("Profile") },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) }
        )
    }
}

@Composable
fun DeliveryFloatingChatButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = PendingOrange,
        contentColor = Color.White,
        shape = CircleShape
    ) {
        Icon(Icons.AutoMirrored.Rounded.Chat, null)
    }
}

@Composable
fun DeliveryChatWindow(onClose: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable { onClose() },
        contentAlignment = Alignment.BottomEnd
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.85f).fillMaxHeight(0.6f).padding(16.dp).clickable(enabled = false) { },
            shape = RoundedCornerShape(24.dp),
            color = Surface
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().background(PrimaryBlue).padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SAU Chatbot", color = Color.White, fontWeight = FontWeight.Bold)
                    IconButton(onClose) { Icon(Icons.Rounded.Close, null, tint = Color.White) }
                }
                Box(modifier = Modifier.weight(1f).padding(16.dp)) {
                    Text("Need help with your route or delivery?", color = TextGrey)
                }
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = "", onValueChange = {}, modifier = Modifier.weight(1f),
                        placeholder = { Text("Type message...") }, shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {}, modifier = Modifier.background(PrimaryBlue, CircleShape)) {
                        Icon(Icons.AutoMirrored.Rounded.Send, null, tint = Color.White)
                    }
                }
            }
        }
    }
}
