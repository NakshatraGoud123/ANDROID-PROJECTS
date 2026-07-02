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
private val ErrorRed = Color(0xFFEF4444)

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
                    label = { Text("Stats") }
                )
            }
        }
    ) { padding ->
        val users by viewModel.allUsers.collectAsState()
        val orders by viewModel.allOrders.collectAsState()
        val bookings by viewModel.allBookings.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()

        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AdminPrimary)
            } else {
                when (selectedTab) {
                    0 -> UserManagementList(users, onDelete = { viewModel.deleteUser(it) })
                    1 -> AdminOrderList(orders, 
                        onDelete = { viewModel.deleteOrder(it) },
                        onUpdateStatus = { id, status -> viewModel.updateOrderStatus(id, status) }
                    )
                    2 -> AdminBookingList(bookings,
                        onDelete = { viewModel.deleteBooking(it) },
                        onUpdateStatus = { id, status -> viewModel.updateBookingStatus(id, status) }
                    )
                    3 -> AdminAnalyticsScreen(users, orders, bookings)
                }
            }
        }
    }
}

@Composable
fun UserManagementList(users: List<FirebaseUser>, onDelete: (String) -> Unit) {
    if (users.isEmpty()) {
        EmptyState("No users registered yet")
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(users) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AdminPrimary.copy(alpha = 0.1f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Rounded.Person, null, tint = AdminPrimary, modifier = Modifier.padding(12.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(user.displayName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Badge(containerColor = AdminPrimary.copy(alpha = 0.1f), contentColor = AdminPrimary) {
                                    Text(user.role.uppercase(), fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp))
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(user.displayPhone, fontSize = 12.sp, color = Color.Gray)
                            }
                            Text(user.displayEmail, fontSize = 12.sp, color = Color.Gray)
                        }
                        IconButton(onClick = { onDelete(user.userId) }) {
                            Icon(Icons.Rounded.DeleteOutline, null, tint = ErrorRed)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrderList(
    orders: List<OrderModel>, 
    onDelete: (String) -> Unit,
    onUpdateStatus: (String, String) -> Unit
) {
    if (orders.isEmpty()) {
        EmptyState("No orders available")
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(orders) { order ->
                var showMenu by remember { mutableStateOf(false) }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Order ID: ${order.orderId.takeLast(6).uppercase()}", fontWeight = FontWeight.Bold)
                                Text(order.serviceName.ifEmpty { "Product Order" }, fontSize = 13.sp, color = Color.Gray)
                            }
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Rounded.MoreVert, null)
                            }
                            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                DropdownMenuItem(
                                    text = { Text("Mark Delivered") },
                                    onClick = { onUpdateStatus(order.orderId, "delivered"); showMenu = false }
                                )
                                DropdownMenuItem(
                                    text = { Text("Mark Cancelled") },
                                    onClick = { onUpdateStatus(order.orderId, "cancelled"); showMenu = false }
                                )
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Delete Order", color = Color.Red) },
                                    onClick = { onDelete(order.orderId); showMenu = false }
                                )
                            }
                        }
                        
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Payment, null, size = 16.dp, tint = Color.Gray)
                            Spacer(Modifier.width(4.dp))
                            Text("Total: ₹${order.totalPrice}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.width(12.dp))
                            StatusChip(order.displayStatus)
                        }
                        
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Rounded.LocationOn, null, size = 16.dp, tint = Color.Gray)
                            Spacer(Modifier.width(4.dp))
                            Text(order.displayAddress, fontSize = 12.sp, color = Color.Gray, maxLines = 2)
                        }

                        if (order.items.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text("Items: ${order.items.joinToString { it.itemName }}", fontSize = 11.sp, color = AdminPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminBookingList(
    bookings: List<BookingModel>,
    onDelete: (String) -> Unit,
    onUpdateStatus: (String, String) -> Unit
) {
    if (bookings.isEmpty()) {
        EmptyState("No service bookings found")
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(bookings) { booking ->
                var showMenu by remember { mutableStateOf(false) }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(booking.displayService, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Customer: ${booking.customerId.takeLast(6).uppercase()}", fontSize = 12.sp, color = Color.Gray)
                            }
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Rounded.MoreVert, null)
                            }
                            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                DropdownMenuItem(
                                    text = { Text("Complete Booking") },
                                    onClick = { onUpdateStatus(booking.bookingId, "completed"); showMenu = false }
                                )
                                DropdownMenuItem(
                                    text = { Text("Cancel Booking") },
                                    onClick = { onUpdateStatus(booking.bookingId, "cancelled"); showMenu = false }
                                )
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Delete Booking", color = Color.Red) },
                                    onClick = { onDelete(booking.bookingId); showMenu = false }
                                )
                            }
                        }
                        
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Schedule, null, size = 16.dp, tint = AdminPrimary)
                            Spacer(Modifier.width(4.dp))
                            Text("${booking.displayDate} • ${booking.displayTime}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.width(12.dp))
                            StatusChip(booking.status)
                        }
                        
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Rounded.Home, null, size = 16.dp, tint = Color.Gray)
                            Spacer(Modifier.width(4.dp))
                            Text(booking.displayAddress, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAnalyticsScreen(users: List<FirebaseUser>, orders: List<OrderModel>, bookings: List<BookingModel>) {
    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("System Dashboard", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.Black)
        Spacer(Modifier.height(20.dp))
        
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AnalyticsCard("Total Users", users.size.toString(), Icons.Rounded.Group, Modifier.weight(1f))
            AnalyticsCard("Revenue", "₹${orders.filter { it.displayStatus == "delivered" || it.displayStatus == "completed" }.sumOf { it.totalPrice }.toInt()}", Icons.Rounded.Payments, Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AnalyticsCard("Orders", orders.size.toString(), Icons.Rounded.ShoppingCart, Modifier.weight(1f))
            AnalyticsCard("Bookings", bookings.size.toString(), Icons.Rounded.Engineering, Modifier.weight(1f))
        }
        
        Spacer(Modifier.height(24.dp))
        Text("Recent Bookings", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(12.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Border)
        ) {
            Column(Modifier.padding(16.dp)) {
                if (bookings.isEmpty()) {
                    Text("No recent activity", color = Color.Gray, fontSize = 13.sp)
                } else {
                    for (booking in bookings.takeLast(5).reversed()) {
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(booking.displayService, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text(booking.displayDate, fontSize = 11.sp, color = Color.Gray)
                            }
                            StatusChip(booking.status)
                        }
                        if (booking != bookings.takeLast(5).first()) HorizontalDivider(color = Border)
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
fun AnalyticsCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier.size(40.dp).background(AdminPrimary.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = AdminPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val color = when (status.lowercase()) {
        "pending", "placed" -> Color(0xFFF59E0B)
        "accepted", "assigned", "out_for_delivery" -> Color(0xFF3B82F6)
        "completed", "delivered" -> Color(0xFF10B981)
        else -> Color(0xFFEF4444)
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = status.uppercase(),
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun EmptyState(msg: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Rounded.Inbox, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
            Spacer(Modifier.height(8.dp))
            Text(msg, color = Color.Gray)
        }
    }
}

@Composable
fun Icon(imageVector: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String?, size: androidx.compose.ui.unit.Dp, tint: Color) {
    Icon(imageVector, contentDescription, modifier = Modifier.size(size), tint = tint)
}
