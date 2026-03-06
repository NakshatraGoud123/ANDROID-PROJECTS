package com.nisr.sauservices.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController, viewModel: ProfileViewModel = viewModel()) {
    val prefs by viewModel.notificationPrefs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Manage your notification preferences",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            NotificationToggleItem(
                title = "Order Updates",
                description = "Get notified about your booking status",
                checked = prefs.orderUpdates,
                onCheckedChange = { viewModel.updateNotificationPref("orderUpdates", it) }
            )

            NotificationToggleItem(
                title = "Promotions & Offers",
                description = "Receive updates on latest deals and discounts",
                checked = prefs.promotions,
                onCheckedChange = { viewModel.updateNotificationPref("promotions", it) }
            )

            NotificationToggleItem(
                title = "Service Alerts",
                description = "Important updates regarding services in your area",
                checked = prefs.serviceAlerts,
                onCheckedChange = { viewModel.updateNotificationPref("serviceAlerts", it) }
            )

            NotificationToggleItem(
                title = "App Updates",
                description = "Stay informed about new features and improvements",
                checked = prefs.appUpdates,
                onCheckedChange = { viewModel.updateNotificationPref("appUpdates", it) }
            )
        }
    }
}

@Composable
fun NotificationToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9F9F9),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(description, fontSize = 12.sp, color = Color.Gray)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PinkPrimary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
    }
}
