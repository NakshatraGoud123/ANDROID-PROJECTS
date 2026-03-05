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
import androidx.compose.runtime.livedata.observeAsState
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
import com.nisr.sauservices.data.model.Booking
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.viewmodel.ServiceWorkerViewModel

// Updated Clean Palette
private val PrimaryBlue = Color(0xFF1E3A8A) // Darker blue for header
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
                0 -> HomeScreen(viewModel, sessionManager, navController)
                1 -> TasksScreen(viewModel)
                2 -> EarningsScreen()
                3 -> ProfileScreen()
            }
            
            if (isChatOpen) {
                ChatWindow(onClose = { isChatOpen = false })
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: ServiceWorkerViewModel, sessionManager: SessionManager, navController: NavController) {
    val bookings by viewModel.bookings.observeAsState(initial = emptyList())
    var isOnline by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            DashboardHeader(
                title = "Service Worker",
                onLogout = {
                    sessionManager.logout()
                    navController.navigate(Screen.RoleSelection.route) { 
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
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
                    StatCard("Pending Jobs", "3", Icons.Rounded.Schedule, PendingOrange, Modifier.weight(1f))
                    StatCard("Accepted", "2", Icons.Rounded.WorkOutline, ActiveBlue, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Completed", "8", Icons.Rounded.CheckCircleOutline, SuccessGreen, Modifier.weight(1f))
                    StatCard("Today's Earnings", "₹2,400", Icons.Rounded.Payments, Color(0xFFEAB308), Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Today's Jobs", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        items(bookings.take(3)) { booking ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                JobCard(booking, viewModel)
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Customer Location", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(12.dp))
                MiniMapCard()
            }
        }
    }
}

@Composable
fun TasksScreen(viewModel: ServiceWorkerViewModel) {
    val bookings by viewModel.bookings.observeAsState(initial = emptyList())
    
    Column(modifier = Modifier.fillMaxSize()) {
        DashboardHeader(title = "Tasks")
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(bookings) { booking ->
                JobCard(booking, viewModel)
            }
        }
    }
}

@Composable
fun EarningsScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        DashboardHeader(title = "Earnings")
        Column(modifier = Modifier.padding(16.dp)) {
            // Weekly Trend Chart Placeholder
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Surface,
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Weekly Trend", fontSize = 14.sp, color = TextGrey)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val heights = listOf(0.4f, 0.7f, 0.5f, 0.8f, 0.6f, 0.9f, 0.5f)
                        heights.forEach { h ->
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .fillMaxHeight(h)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PrimaryBlue)
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
            Text("Job History", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(12.dp))

            // History List
            val history = listOf(
                Triple("AC Repair", "Amit Sharma • Today", "₹800"),
                Triple("Plumbing", "Priya Verma • Today", "₹600"),
                Triple("Cleaning", "Neha Gupta • Yesterday", "₹1,000"),
                Triple("Electrical", "Rajesh Kumar • Yesterday", "₹500")
            )

            history.forEach { (type, details, price) ->
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
                            Text(type, fontWeight = FontWeight.Bold, color = TextDark)
                            Text(details, fontSize = 12.sp, color = TextGrey)
                        }
                        Text(price, fontWeight = FontWeight.Bold, color = SuccessGreen, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        DashboardHeader(title = "Profile")
        Column(
            modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Surface,
                border = BorderStroke(1.dp, Border)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("RS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Ravi Shankar", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text("Service Worker • ID: SW-1024", fontSize = 13.sp, color = TextGrey)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Star, null, tint = Color(0xFFEAB308), modifier = Modifier.size(14.dp))
                            Text(" 4.8 (120 reviews)", fontSize = 12.sp, color = TextGrey)
                        }
                    }
                }
            }

            // Skills Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Surface,
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Skills", fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(12.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val skills = listOf("AC Repair", "Plumbing", "Electrical", "Cleaning", "Painting")
                        skills.forEach { skill ->
                            Surface(
                                color = Background,
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, Border)
                            ) {
                                Text(skill, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, color = TextDark)
                            }
                        }
                    }
                }
            }

            // Verification Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Surface,
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Verification Status", fontWeight = FontWeight.Bold, color = TextDark)
                        Text("Completed", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    VerificationItem("Aadhaar Verified", Icons.Rounded.CheckCircleOutline)
                    VerificationItem("ID Proof Uploaded", Icons.AutoMirrored.Rounded.Notes)
                    VerificationItem("Phone Verified: +91 98765xxxxx", Icons.Rounded.Phone)
                }
            }
        }
    }
}

@Composable
fun VerificationItem(text: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(icon, null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 14.sp, color = TextGrey)
    }
}

@Composable
fun DashboardHeader(
    title: String,
    onLogout: () -> Unit = {}
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
                    if (title != "Service Worker") {
                        IconButton(onClick = onLogout, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.AutoMirrored.Rounded.Logout, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }
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
fun StatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
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
fun JobCard(booking: Booking, viewModel: ServiceWorkerViewModel) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(booking.customerName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    Text(booking.serviceType, fontSize = 14.sp, color = TextGrey)
                }
                Surface(
                    color = when(booking.status.lowercase()) {
                        "pending" -> PendingOrange.copy(alpha = 0.1f)
                        "active" -> ActiveBlue.copy(alpha = 0.1f)
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
                            "active" -> ActiveBlue
                            else -> SuccessGreen
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.LocationOn, null, tint = PendingOrange, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(booking.address, fontSize = 13.sp, color = TextGrey)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Schedule, null, tint = TextGrey, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(booking.timeSlot, fontSize = 13.sp, color = TextGrey)
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            if (booking.status.lowercase() == "pending") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { viewModel.updateBookingStatus(booking.bookingId, "active") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Accept")
                    }
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Border)
                    ) {
                        Text("Decline", color = TextDark)
                    }
                }
            } else if (booking.status.lowercase() == "active") {
                Button(
                    onClick = { viewModel.updateBookingStatus(booking.bookingId, "completed") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(10.dp)
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
        modifier = Modifier.fillMaxWidth().height(180.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFE5E7EB),
        border = BorderStroke(1.dp, Border)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.LocationOn, null, tint = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Customer: Sector 12", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("You: Sector 10", fontSize = 11.sp, color = TextGrey)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
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
            icon = { Icon(Icons.AutoMirrored.Rounded.Notes, null) },
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
            icon = { Icon(Icons.Rounded.PersonOutline, null) },
            label = { Text("Profile") },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) }
        )
    }
}

@Composable
fun FloatingChatButton(onClick: () -> Unit) {
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
fun ChatWindow(onClose: () -> Unit) {
    // Simplified Chat Overlay
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
                    IconButton(onClick = onClose) {
                        Icon(Icons.Rounded.Close, null, tint = Color.White)
                    }
                }
                
                Box(modifier = Modifier.weight(1f).padding(16.dp)) {
                    Text("How can I help you today?", color = TextGrey)
                }
                
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type message...") },
                        shape = RoundedCornerShape(24.dp)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        content = { content() }
    )
}
