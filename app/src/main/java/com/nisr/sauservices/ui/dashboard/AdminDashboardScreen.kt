package com.nisr.sauservices.ui.dashboard

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.model.FirebaseUser
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.ui.viewmodels.AdminViewModel

private val AdminPrimary = Color(0xFF4F46E5)
private val Background = Color(0xFFF8FAFC)
private val Surface = Color(0xFFFFFFFF)
private val Border = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: AdminViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val onLogout = {
        sessionManager.logout()
        navController.navigate("role_selection") { popUpTo(0) { inclusive = true } }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Admin Panel", color = Color.White) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Rounded.Logout, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AdminPrimary)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Surface) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Rounded.People, null) },
                    label = { Text("Users") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Rounded.ShoppingBag, null) },
                    label = { Text("Orders") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Rounded.EventAvailable, null) },
                    label = { Text("Bookings") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Rounded.Analytics, null) },
                    label = { Text("Analytics") }
                )
            }
        }
    ) { padding ->
        val users by viewModel.allUsers.collectAsState()
        val orders by viewModel.allOrders.collectAsState()
        val bookings by viewModel.allBookings.collectAsState()

        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> UserManagementList(users) { viewModel.deleteUser(it) }
                1 -> AdminOrderList(orders)
                2 -> AdminBookingList(bookings)
                3 -> AdminAnalyticsScreen(users, orders, bookings)
            }
        }
    }
}

@Composable
fun UserManagementList(users: List<FirebaseUser>, onDelete: (String) -> Unit) {
    if (users.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No users found", color = Color.Gray)
        }
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(users) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(user.displayName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(user.role.uppercase(), fontSize = 11.sp, color = AdminPrimary, fontWeight = FontWeight.Bold)
                            Text(user.displayEmail, fontSize = 12.sp, color = Color.Gray)
                            Text(user.displayPhone, fontSize = 12.sp, color = Color.Gray)
                        }
                        IconButton(onClick = { onDelete(user.userId) }) {
                            Icon(Icons.Rounded.Delete, null, tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrderList(orders: List<OrderModel>) {
    if (orders.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No orders found", color = Color.Gray)
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
                            Text("Order #${order.orderId.takeLast(6).uppercase()}", fontWeight = FontWeight.Bold)
                            Text(order.displayStatus.uppercase(), color = AdminPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Total: ₹${order.totalPrice}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text("Address: ${order.displayAddress}", fontSize = 12.sp, color = Color.Gray, maxLines = 2)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminBookingList(bookings: List<BookingModel>) {
    if (bookings.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No bookings found", color = Color.Gray)
        }
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(bookings) { booking ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(booking.displayService, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(booking.status.uppercase(), color = Color(0xFF22C55E), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Scheduled: ${booking.displayDate} at ${booking.displayTime}", fontSize = 14.sp)
                        Text("Address: ${booking.displayAddress}", fontSize = 12.sp, color = Color.Gray)
                        Text("Client ID: ${booking.customerId.takeLast(6).uppercase()}", fontSize = 11.sp, color = Color.LightGray)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAnalyticsScreen(users: List<FirebaseUser>, orders: List<OrderModel>, bookings: List<BookingModel>) {
    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("System Overview", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))
        
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AnalyticsCard("Total Users", users.size.toString(), Icons.Rounded.Group, Modifier.weight(1f))
            AnalyticsCard("Total Sales", "₹${orders.sumOf { it.totalPrice }.toInt()}", Icons.Rounded.Payments, Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AnalyticsCard("Orders", orders.size.toString(), Icons.Rounded.ShoppingCart, Modifier.weight(1f))
            AnalyticsCard("Bookings", bookings.size.toString(), Icons.Rounded.EventNote, Modifier.weight(1f))
        }
        
        Spacer(Modifier.height(32.dp))
        Text("Recent Activity", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(12.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Border)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Last 5 Bookings", fontWeight = FontWeight.Bold, color = AdminPrimary)
                bookings.takeLast(5).reversed().forEach { booking ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(booking.displayService, fontSize = 13.sp)
                        Text(booking.status, fontSize = 12.sp, color = Color.Gray)
                    }
                    HorizontalDivider(color = Border)
                }
            }
        }
    }
}

@Composable
fun AnalyticsCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = AdminPrimary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
