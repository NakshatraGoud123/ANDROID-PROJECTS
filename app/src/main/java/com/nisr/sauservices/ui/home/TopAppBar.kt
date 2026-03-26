package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.viewmodel.CartViewModel

@Composable
fun TopAppBarUI(navController: NavController, sessionManager: SessionManager) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var userAddress by remember { mutableStateOf("Set Location") }
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val cartViewModel: CartViewModel = viewModel()
    val cartItems by cartViewModel.dbCartItems.collectAsState()
    val cartCount = cartItems.sumOf { it.quantity }

    // Real-time listener for address
    LaunchedEffect(userId) {
        userId?.let { uid ->
            val ref = FirebaseDatabase.getInstance().reference.child("users").child(uid).child("selectedLocation").child("address")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userAddress = snapshot.getValue(String::class.java) ?: "Set Location"
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    sessionManager.logout()
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(0) { inclusive = true }
                    }
                    showLogoutDialog = false
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo and Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { navController.navigate(Screen.Home.route) }
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PinkPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("S", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }
                Spacer(Modifier.width(10.dp))
                Text("SAU", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.Black)
            }

            // Location Pill (Blinkit Style)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFF5F5F5))
                    .clickable { navController.navigate(Screen.MapPicker.route) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.LocationOn, null, tint = PinkPrimary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    text = userAddress,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.KeyboardArrowDown, null, modifier = Modifier.size(16.dp))
            }

            // Action Icons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.navigate(Screen.MyOrders.route) }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Outlined.ShoppingBag, null, tint = Color.DarkGray, modifier = Modifier.size(26.dp))
                }
                
                IconButton(onClick = { navController.navigate(Screen.Cart.route) }, modifier = Modifier.size(40.dp)) {
                    BadgedBox(badge = {
                        if (cartCount > 0) {
                            Badge(containerColor = PinkPrimary) {
                                Text(cartCount.toString(), color = Color.White)
                            }
                        }
                    }) {
                        Icon(Icons.Default.ShoppingCart, null, tint = Color.DarkGray, modifier = Modifier.size(26.dp))
                    }
                }

                IconButton(onClick = { showLogoutDialog = true }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Outlined.Logout, contentDescription = "Logout", tint = Color.DarkGray, modifier = Modifier.size(26.dp))
                }
            }
        }
    }
}
