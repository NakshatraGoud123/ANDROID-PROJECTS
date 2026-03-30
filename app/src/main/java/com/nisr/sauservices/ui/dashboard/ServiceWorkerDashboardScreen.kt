package com.nisr.sauservices.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.automirrored.rounded.Send
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
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.viewmodels.ServiceWorkerViewModel

// Updated Clean Palette
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
fun ServiceWorkerDashboardScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: ServiceWorkerViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var isChatOpen by remember { mutableStateOf(false) }

    val onLogout = {
        sessionManager.logout()
        navController.navigate("role_selection") {
            popUpTo(0) { inclusive = true }
        }
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            BottomNavigationBar(selectedTab) { selectedTab = it }
        },
        floatingActionButton = {
            FloatingChatButton { isChatOpen = true }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> HomeScreen(viewModel, onLogout)
                1 -> TasksScreen(viewModel, onLogout)
                2 -> EarningsScreen(onLogout)
                3 -> ProfileScreen(onLogout)
            }
            
            if (isChatOpen) {
                ChatWindow(onClose = { isChatOpen = false })
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: ServiceWorkerViewModel, onLogout: () -> Unit) {
    val pendingBookings by viewModel.pendingBookings.collectAsState()
    val acceptedBookings by viewModel.acceptedBookings.collectAsState()
    val completedBookings by viewModel.completedBookings.collectAsState()
    var isOnline by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            DashboardHeader(title = "Service Dashboard", onLogout = onLogout)
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
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
                            Text("Work Status", fontWeight = FontWeight.Bold, color = TextDark)
                            Text(
                                if (isOnline) "Online — Receiving requests" else "Offline — Not receiving",
                                fontSize = 12.sp,
                                color = if (isOnline) SuccessGreen else TextGrey
                            )
                        }
                        Switch(
                            checked = isOnline,
                            onCheckedChange = { isOnline = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SuccessGreen)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Requests", pendingBookings.size.toString(), Icons.Rounded.Schedule, PendingOrange, Modifier.weight(1f))
                    StatCard("Accepted", acceptedBookings.size.toString(), Icons.Rounded.WorkOutline, ActiveBlue, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Completed", completedBookings.size.toString(), Icons.Rounded.CheckCircleOutline, SuccessGreen, Modifier.weight(1f))
                    StatCard("Earnings", "₹---", Icons.Rounded.Payments, Color(0xFFEAB308), Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Ongoing & New Jobs", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        items(acceptedBookings) { booking ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                JobCard(booking, viewModel)
            }
        }

        items(pendingBookings) { booking ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                JobCard(booking, viewModel)
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Location Preview", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(12.dp))
                MiniMapCard()
            }
        }
    }
}

@Composable
fun TasksScreen(viewModel: ServiceWorkerViewModel, onLogout: () -> Unit) {
    val pending by viewModel.pendingBookings.collectAsState()
    val accepted by viewModel.acceptedBookings.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        DashboardHeader(title = "Task List", onLogout = onLogout)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(accepted) { booking -> JobCard(booking, viewModel) }
            items(pending) { booking -> JobCard(booking, viewModel) }
        }
    }
}

@Composable
fun EarningsScreen(onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        DashboardHeader(title = "Earnings", onLogout = onLogout)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Detailed earnings summary coming soon")
        }
    }
}

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        DashboardHeader(title = "Worker Profile", onLogout = onLogout)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Worker profile settings coming soon")
        }
    }
}

@Composable
fun DashboardHeader(title: String, onLogout: () -> Unit) {
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
fun StatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(label, fontSize = 12.sp, color = TextGrey)
        }
    }
}

@Composable
fun JobCard(booking: BookingModel, viewModel: ServiceWorkerViewModel) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(booking.serviceName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    Text("Date: ${booking.scheduledDate}", fontSize = 13.sp, color = TextGrey)
                }
                Surface(
                    color = when(booking.status.lowercase()) {
                        "pending" -> PendingOrange.copy(alpha = 0.1f)
                        "accepted" -> ActiveBlue.copy(alpha = 0.1f)
                        else -> SuccessGreen.copy(alpha = 0.1f)
                    },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        booking.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when(booking.status.lowercase()) {
                            "pending" -> PendingOrange
                            "accepted" -> ActiveBlue
                            else -> SuccessGreen
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(booking.address, fontSize = 13.sp, color = TextGrey)
            
            if (booking.status.lowercase() == "pending") {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.acceptBooking(booking.bookingId) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Accept Booking")
                }
            } else if (booking.status.lowercase() == "accepted") {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.completeBooking(booking.bookingId) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Complete Job")
                }
            }
        }
    }
}

@Composable
fun MiniMapCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().height(150.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF3F4F6),
        border = BorderStroke(1.dp, Border)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(Icons.Rounded.LocationOn, null, tint = PrimaryBlue, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(containerColor = Surface) {
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Home, null) },
            label = { Text("Home") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Assignment, null) },
            label = { Text("Tasks") },
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
            icon = { Icon(Icons.Rounded.Person, null) },
            label = { Text("Profile") },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) }
        )
    }
}

@Composable
fun FloatingChatButton(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick, containerColor = PendingOrange, contentColor = Color.White, shape = CircleShape) {
        Icon(Icons.AutoMirrored.Rounded.Chat, null)
    }
}

@Composable
fun ChatWindow(onClose: () -> Unit) {
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
                Row(modifier = Modifier.fillMaxWidth().background(PrimaryBlue).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Chat Support", color = Color.White, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onClose) { Icon(Icons.Rounded.Close, null, tint = Color.White) }
                }
                Box(modifier = Modifier.weight(1f).padding(16.dp)) { Text("Need assistance?", color = TextGrey) }
            }
        }
    }
}
