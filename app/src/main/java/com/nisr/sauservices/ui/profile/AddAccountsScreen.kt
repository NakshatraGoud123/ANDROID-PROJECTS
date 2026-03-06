package com.nisr.sauservices.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.nisr.sauservices.ui.theme.PinkPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountsScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val context = LocalContext.current

    val providers = user?.providerData?.map { it.providerId } ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Accounts", fontWeight = FontWeight.Bold) },
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
                "Link your accounts for easier login and better security.",
                fontSize = 14.sp,
                color = Color.Gray
            )

            AccountLinkItem(
                icon = Icons.Default.Email,
                title = "Secondary Email",
                isLinked = providers.count { it == "password" } > 1, // Simplified check
                onClick = { 
                    Toast.makeText(context, "Linking secondary email feature coming soon", Toast.LENGTH_SHORT).show()
                }
            )

            AccountLinkItem(
                icon = Icons.Default.Phone,
                title = "Phone Number",
                isLinked = providers.contains("phone"),
                onClick = {
                    Toast.makeText(context, "Linking phone number feature coming soon", Toast.LENGTH_SHORT).show()
                }
            )

            AccountLinkItem(
                icon = Icons.Default.Link,
                title = "Google Account",
                isLinked = providers.contains("google.com"),
                onClick = {
                    Toast.makeText(context, "Linking Google account feature coming soon", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun AccountLinkItem(icon: ImageVector, title: String, isLinked: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9F9F9),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = PinkPrimary, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
            
            if (isLinked) {
                Text("Linked", color = Color(0xFF4CAF50), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            } else {
                Text("Link", color = PinkPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
