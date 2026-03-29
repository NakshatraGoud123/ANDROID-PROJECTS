package com.nisr.sauservices.ui.pls

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.data.model.PLSBooking
import com.nisr.sauservices.ui.viewmodels.PropertyLifestyleViewModel

private val AdminPrimary = Color(0xFF4F46E5) // Indigo
private val Background = Color(0xFFF8FAFC)
private val Surface = Color(0xFFFFFFFF)
private val Border = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController, viewModel: PropertyLifestyleViewModel) {
    LaunchedEffect(Unit) { viewModel.loadUserBookings("") } // Admin loads all
    val bookings by viewModel.userBookings.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Admin Dashboard") }) },
        containerColor = Background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AdminStatCard("Total Bookings", bookings.size.toString(), Icons.Rounded.ListAlt, Modifier.weight(1f))
                AdminStatCard("Revenue", "₹${bookings.sumOf { it.totalPrice }.toInt()}", Icons.Rounded.Payments, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AdminStatCard("Pending", bookings.count { it.status == "Pending" }.toString(), Icons.Rounded.Pending, Modifier.weight(1f))
                AdminStatCard("Completed", bookings.count { it.status == "Completed" }.toString(), Icons.Rounded.CheckCircle, Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            Text("Management", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            ManagementItem("Manage Bookings", Icons.Rounded.Assignment) { navController.navigate("ADMIN_bookings") }
            ManagementItem("Manage Services", Icons.Rounded.Settings) { navController.navigate("ADMIN_services") }
        }
    }
}

@Composable
fun AdminStatCard(label: String, value: String, icon: ImageVector, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = AdminPrimary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ManagementItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = AdminPrimary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Rounded.ChevronRight, null, tint = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingsScreen(navController: NavController, viewModel: PropertyLifestyleViewModel) {
    val bookings by viewModel.userBookings.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Bookings Management") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ScrollableRow(modifier = Modifier.padding(16.dp)) {
                listOf("All", "Pending", "Confirmed", "Completed", "Cancelled").forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            
            val filteredList = if (selectedFilter == "All") bookings else bookings.filter { it.status == selectedFilter }
            
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                items(filteredList) { booking ->
                    AdminBookingCard(booking)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun AdminBookingCard(booking: PLSBooking) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(booking.serviceName, fontWeight = FontWeight.Bold)
                StatusBadge(booking.status)
            }
            Text("Client: ${booking.userName}", fontSize = 14.sp)
            Text("Phone: ${booking.userPhone}", fontSize = 14.sp, color = Color.Gray)
            Text("Date: ${booking.date} | ${booking.timeSlot}", fontSize = 14.sp, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { }, modifier = Modifier.weight(1f)) { Text("Reject") }
                Button(onClick = { }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = AdminPrimary)) { Text("Confirm") }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = when(status) {
        "Pending" -> Color(0xFFF97316)
        "Confirmed" -> Color(0xFF3B82F6)
        "Completed" -> Color(0xFF22C55E)
        else -> Color.Gray
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(status, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
    }
}

@Composable
fun ScrollableRow(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Row(modifier = modifier.horizontalScroll(rememberScrollState())) { content() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminServicesScreen(navController: NavController, viewModel: PropertyLifestyleViewModel) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Service Management") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { }, containerColor = AdminPrimary) {
                Icon(Icons.Rounded.Add, null, tint = Color.White)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(Icons.Rounded.SettingsSuggest, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
            Text("Service List Loading...", color = Color.Gray)
        }
    }
}
