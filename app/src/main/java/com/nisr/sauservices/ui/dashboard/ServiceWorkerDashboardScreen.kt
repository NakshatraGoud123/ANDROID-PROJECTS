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
import com.nisr.sauservices.ui.viewmodels.ServiceWorkerViewModel

private val PrimaryBlue = Color(0xFF1E3A8A)
private val Background = Color(0xFFF9FAFB)
private val Surface = Color(0xFFFFFFFF)
private val Border = Color(0xFFF1F5F9)
private val TextGrey = Color(0xFF6B7280)
private val SuccessGreen = Color(0xFF22C55E)
private val PendingOrange = Color(0xFFF97316)
private val ActiveBlue = Color(0xFF3B82F6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceWorkerDashboardScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: ServiceWorkerViewModel
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
                title = { Text("Service Worker", color = Color.White) },
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
                    icon = { Icon(Icons.Rounded.Schedule, null) },
                    label = { Text("Pending") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Rounded.WorkOutline, null) },
                    label = { Text("Accepted") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Rounded.CheckCircle, null) },
                    label = { Text("Completed") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Rounded.Person, null) },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->
        val pending by viewModel.pendingBookings.collectAsState()
        val accepted by viewModel.acceptedBookings.collectAsState()
        val completed by viewModel.completedBookings.collectAsState()

        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> BookingList(pending, "No pending requests") { viewModel.acceptBooking(it.bookingId) }
                1 -> BookingList(accepted, "No active jobs", "Complete Job") { viewModel.completeBooking(it.bookingId) }
                2 -> BookingList(completed, "No completed jobs")
                3 -> WorkerProfileSubScreen(onLogout)
            }
        }
    }
}

@Composable
fun BookingList(
    bookings: List<BookingModel>,
    emptyMsg: String,
    btnText: String? = "Accept Booking",
    onAction: (BookingModel) -> Unit = {}
) {
    if (bookings.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(emptyMsg, color = TextGrey)
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
                            Text(booking.status.uppercase(), color = ActiveBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(booking.displayAddress, fontSize = 13.sp, color = TextGrey)
                        Text("${booking.displayDate} at ${booking.displayTime}", fontSize = 12.sp, color = TextGrey)
                        
                        if (btnText != null && booking.status != "completed") {
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { onAction(booking) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = if(booking.status == "pending") PrimaryBlue else SuccessGreen)
                            ) {
                                Text(btnText)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkerProfileSubScreen(onLogout: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Rounded.AccountCircle, null, modifier = Modifier.size(80.dp), tint = PrimaryBlue)
        Spacer(Modifier.height(16.dp))
        Text("Service Professional", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(32.dp))
        
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Surface)) {
            Column(Modifier.padding(16.dp)) {
                Text("Account Details", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Rating", color = TextGrey)
                    Text("4.8 ★", color = PendingOrange, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Jobs Completed", color = TextGrey)
                    Text("124", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
