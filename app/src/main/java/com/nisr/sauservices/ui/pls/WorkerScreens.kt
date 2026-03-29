package com.nisr.sauservices.ui.pls

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.nisr.sauservices.data.model.PLSBooking
import com.nisr.sauservices.ui.viewmodels.PropertyLifestyleViewModel

private val WorkerPrimary = Color(0xFF059669) // Emerald
private val Background = Color(0xFFF9FAFB)
private val Surface = Color(0xFFFFFFFF)
private val Border = Color(0xFFE5E7EB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerDashboardScreen(navController: NavController, viewModel: PropertyLifestyleViewModel) {
    var isOnline by remember { mutableStateOf(true) }
    val bookings by viewModel.userBookings.collectAsState()

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Worker Dashboard", fontWeight = FontWeight.Bold) },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 16.dp)) {
                        Text(if (isOnline) "Online" else "Offline", fontSize = 12.sp, color = if (isOnline) WorkerPrimary else Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(checked = isOnline, onCheckedChange = { isOnline = it })
                    }
                }
            )
        },
        containerColor = Background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                WorkerStatCard("New Requests", "3", Icons.Rounded.NotificationsActive, Color(0xFFF97316), Modifier.weight(1f))
                WorkerStatCard("Active Jobs", "1", Icons.Rounded.PlayCircle, WorkerPrimary, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            WorkerStatCard("Total Earnings", "₹12,450", Icons.Rounded.AccountBalanceWallet, WorkerPrimary, Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(32.dp))
            Text("Job Management", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            WorkerMenuItem("Job Requests", Icons.Rounded.WorkOutline, "3 New") { navController.navigate("WORKER_jobs") }
            WorkerMenuItem("My Active Jobs", Icons.Rounded.RunningWithErrors, "1 Active") { }
            WorkerMenuItem("Job History", Icons.Rounded.History, "") { }
            WorkerMenuItem("Profile & Skills", Icons.Rounded.Person, "") { }
        }
    }
}

@Composable
fun WorkerStatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.size(40.dp).background(color.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun WorkerMenuItem(title: String, icon: ImageVector, badge: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = WorkerPrimary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.weight(1f))
            if (badge.isNotEmpty()) {
                Surface(color = Color(0xFFF97316), shape = RoundedCornerShape(4.dp)) {
                    Text(badge, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(Icons.Rounded.ChevronRight, null, tint = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerJobsScreen(navController: NavController, viewModel: PropertyLifestyleViewModel) {
    val bookings by viewModel.userBookings.collectAsState()
    val pendingJobs = bookings.filter { it.status == "Pending" }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Available Job Requests") }) }
    ) { padding ->
        if (pendingJobs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No new job requests at the moment.", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                items(pendingJobs) { job ->
                    JobRequestCard(job)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun JobRequestCard(job: PLSBooking) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(job.serviceName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("₹${job.totalPrice}", fontWeight = FontWeight.ExtraBold, color = WorkerPrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(job.userAddress, fontSize = 13.sp, color = Color.Gray)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Event, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${job.date} at ${job.timeSlot}", fontSize = 13.sp, color = Color.Gray)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) { Text("Reject") }
                Button(onClick = { }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = WorkerPrimary), shape = RoundedCornerShape(8.dp)) { Text("Accept Job") }
            }
        }
    }
}
