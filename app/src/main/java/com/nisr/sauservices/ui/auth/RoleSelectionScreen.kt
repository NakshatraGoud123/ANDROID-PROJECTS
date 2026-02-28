package com.nisr.sauservices.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen

@Composable
fun RoleSelectionScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7F6))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SAU Services",
            fontSize = 32.sp,
            color = Color(0xFF2E7D6B),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Choose Your Role",
            fontSize = 18.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(60.dp))

        // First Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RoleCircle("Customer", Icons.Default.Person) {
                navController.navigate(Screen.AuthOptions.createRoute("customer"))
            }
            RoleCircle("Shopkeeper", Icons.Default.Store) {
                navController.navigate(Screen.AuthOptions.createRoute("shopkeeper"))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Second Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RoleCircle("Worker", Icons.Default.Build) {
                navController.navigate(Screen.AuthOptions.createRoute("service_worker"))
            }
            RoleCircle("Delivery", Icons.Default.DeliveryDining) {
                navController.navigate(Screen.AuthOptions.createRoute("delivery"))
            }
        }
    }
}

@Composable
fun RoleCircle(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(120.dp)
    ) {
        Surface(
            modifier = Modifier
                .size(100.dp)
                .clickable { onClick() },
            shape = CircleShape,
            color = Color.White,
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            border = BorderStroke(2.dp, Color(0xFF2E7D6B))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(44.dp),
                    tint = Color(0xFF2E7D6B)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}
