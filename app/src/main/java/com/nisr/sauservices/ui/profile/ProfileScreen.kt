package com.nisr.sauservices.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.viewmodel.ProfileViewModel

val SoftPeach = Color(0xFFFFE5D9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel = viewModel()) {
    val userProfile by viewModel.userProfile.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold, color = Color.Black) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                ProfileHeaderCard(
                    name = userProfile?.name ?: "Guest User",
                    email = userProfile?.email ?: "guest@example.com",
                    phone = userProfile?.phone ?: ""
                )
            }

            item { Spacer(Modifier.height(8.dp)) }

            item {
                ProfileMenuItem(
                    icon = Icons.Outlined.Edit,
                    label = "Edit Profile",
                    onClick = { navController.navigate(Screen.EditProfile.route) }
                )
            }
            item {
                ProfileMenuItem(
                    icon = Icons.Outlined.Notifications,
                    label = "Notifications",
                    onClick = { navController.navigate(Screen.Notifications.route) }
                )
            }
            item {
                ProfileMenuItem(
                    icon = Icons.Outlined.LocationOn,
                    label = "Shipping Address",
                    onClick = { navController.navigate(Screen.ShippingAddress.route) }
                )
            }
            item {
                ProfileMenuItem(
                    icon = Icons.Outlined.Lock,
                    label = "Change Password",
                    onClick = { navController.navigate(Screen.ChangePassword.route) }
                )
            }
            item {
                ProfileMenuItem(
                    icon = Icons.Outlined.PersonAdd,
                    label = "Add Accounts",
                    onClick = { navController.navigate(Screen.AddAccounts.route) }
                )
            }
            item {
                ProfileMenuItem(
                    icon = Icons.Outlined.ChatBubbleOutline,
                    label = "Contact Us",
                    onClick = { navController.navigate(Screen.ContactUs.route) }
                )
            }
            item {
                ProfileMenuItem(
                    icon = Icons.Outlined.HelpOutline,
                    label = "FAQ",
                    onClick = { navController.navigate(Screen.FAQ.route) }
                )
            }
            item {
                ProfileMenuItem(
                    icon = Icons.Outlined.Logout,
                    label = "Logout",
                    labelColor = Color.Red,
                    iconColor = Color.Red,
                    showArrow = false,
                    onClick = { showLogoutDialog = true }
                )
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.logout()
                    showLogoutDialog = false
                    navController.navigate(Screen.RoleSelection.route) {
                        popUpTo(0)
                    }
                }) {
                    Text("Logout", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileHeaderCard(name: String, email: String, phone: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SoftPeach,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Person,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.Gray
                )
            }
            
            Spacer(Modifier.width(20.dp))
            
            Column {
                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
                Text(
                    text = email,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                if (phone.isNotEmpty()) {
                    Text(
                        text = phone,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    labelColor: Color = Color.Black,
    iconColor: Color = PinkPrimary,
    showArrow: Boolean = true,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9F9F9),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(Modifier.width(16.dp))
            
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = labelColor,
                modifier = Modifier.weight(1f)
            )
            
            if (showArrow) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
