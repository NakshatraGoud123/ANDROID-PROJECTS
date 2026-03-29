package com.nisr.sauservices.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.model.InventoryItem
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
private val ActiveBlue = Color(0xFF3B82F6)

@Composable
fun ShopkeeperDashboardScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: ShopkeeperViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val onLogout = {
        sessionManager.logout()
        navController.navigate(Screen.RoleSelection.route) { popUpTo(0) { inclusive = true } }
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            ShopBottomNavigationBar(selectedTab) { selectedTab = it }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> ShopHomeScreen(viewModel, onLogout)
                1 -> InventoryScreen(viewModel, onLogout)
                2 -> ShopAnalyticsScreen(onLogout)
                3 -> ShopProfileScreen(onLogout)
            }
        }
    }
}

@Composable
fun ShopHomeScreen(viewModel: ShopkeeperViewModel, onLogout: () -> Unit) {
    val pendingOrders by viewModel.pendingOrders.collectAsState()
    val acceptedOrders by viewModel.acceptedOrders.collectAsState()
    var isOnline by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            ShopDashboardHeader(title = "Vendor Dashboard", onLogout = onLogout)
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                AvailabilityCard(isOnline) { isOnline = it }
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ShopStatCard("Pending", pendingOrders.size.toString(), Icons.Rounded.Pending, PendingOrange, Modifier.weight(1f))
                    ShopStatCard("Accepted", acceptedOrders.size.toString(), Icons.Rounded.CheckCircle, SuccessGreen, Modifier.weight(1f))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Text("Pending Orders", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (pendingOrders.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No pending orders", color = TextGrey)
                }
            }
        } else {
            items(pendingOrders) { order ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    OrderCard(order) { viewModel.accept(order.orderId) }
                }
            }
        }

        item {
            Text(
                "Accepted Orders",
                modifier = Modifier.padding(16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        items(acceptedOrders) { order ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                OrderCard(order, isAccepted = true) {}
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderModel, isAccepted: Boolean = false, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { if (!isAccepted) onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Order #${order.orderId.takeLast(6)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    Text("${order.items.size} Items • ₹${order.totalPrice}", fontSize = 14.sp, color = TextGrey)
                }
                Surface(
                    color = (if (isAccepted) SuccessGreen else PendingOrange).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        if (isAccepted) "ACCEPTED" else "PENDING",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAccepted) SuccessGreen else PendingOrange
                    )
                }
            }
            if (!isAccepted) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Accept Order")
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
                Text("Availability", fontWeight = FontWeight.Bold, color = TextDark)
                Text(
                    if (isOnline) "Online — Accepting orders" else "Offline — Closed",
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
fun InventoryScreen(viewModel: ShopkeeperViewModel, onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        ShopDashboardHeader(title = "Inventory", onLogout = onLogout)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Inventory Content")
        }
    }
}

@Composable
fun ShopAnalyticsScreen(onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        ShopDashboardHeader(title = "Analytics", onLogout = onLogout)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Analytics Content")
        }
    }
}

@Composable
fun ShopProfileScreen(onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        ShopDashboardHeader(title = "Profile", onLogout = onLogout)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Profile Content")
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLogout) {
                    Icon(Icons.AutoMirrored.Rounded.Logout, null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(label, fontSize = 12.sp, color = TextGrey)
        }
    }
}

@Composable
fun ShopBottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(containerColor = Surface) {
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Home, null) },
            label = { Text("Home") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Inventory2, null) },
            label = { Text("Inventory") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.BarChart, null) },
            label = { Text("Analytics") },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Person, null) },
            label = { Text("Profile") },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) }
        )
    }
}
