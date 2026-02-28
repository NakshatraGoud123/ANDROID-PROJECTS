package com.nisr.sauservices.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.theme.PeachAccent

@Composable
fun DeliverySignUpScreen(navController: NavController) {
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("Bike") }
    var vehicleNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    PeachAccent,
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Join as Delivery Partner",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Fill in your details to get started",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            SignUpTextField(fullName, { fullName = it }, "Full Name *", "Enter your full name", Icons.Default.Person)
            Spacer(modifier = Modifier.height(16.dp))
            SignUpTextField(phoneNumber, { phoneNumber = it }, "Phone Number *", "+91 XXXXXXXXXX", Icons.Default.Phone)
            Spacer(modifier = Modifier.height(16.dp))
            SignUpTextField(email, { email = it }, "Email *", "email@example.com", Icons.Default.Email)
            Spacer(modifier = Modifier.height(16.dp))
            SignUpTextField(password, { password = it }, "Password *", "••••••••", Icons.Default.Lock, isPassword = true)
            Spacer(modifier = Modifier.height(16.dp))
            SignUpTextField(confirmPassword, { confirmPassword = it }, "Confirm Password *", "Re-enter your password", Icons.Default.Lock, isPassword = true)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Vehicle Type *", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VehicleOption("Bike", Icons.Default.DirectionsBike, vehicleType == "Bike") { vehicleType = "Bike" }
                VehicleOption("Scooter", Icons.Default.ElectricScooter, vehicleType == "Scooter") { vehicleType = "Scooter" }
                VehicleOption("Auto", Icons.Default.ElectricRickshaw, vehicleType == "Auto") { vehicleType = "Auto" }
                VehicleOption("Car", Icons.Default.DirectionsCar, vehicleType == "Car") { vehicleType = "Car" }
            }

            Spacer(modifier = Modifier.height(24.dp))
            SignUpTextField(vehicleNumber, { vehicleNumber = it }, "Vehicle Number *", "E.G. MH 01 AB 1234", Icons.Default.Numbers)
            Spacer(modifier = Modifier.height(16.dp))
            SignUpTextField(address, { address = it }, "Address *", "Enter your full address", Icons.Default.LocationOn)
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Upload ID Proof", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    .clickable { /* Upload logic */ },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tap to upload (Image or PDF)", color = Color.Gray, fontSize = 14.sp)
                }
            }
            Text("Aadhar, PAN, Driving License etc.", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* Register */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PeachAccent)
            ) {
                Text("Register as Delivery Partner", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Already registered? ", color = Color.Gray)
                Text("Sign In", color = PeachAccent, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { navController.navigate("login") })
            }
        }
    }
}

@Composable
fun VehicleOption(name: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(75.dp)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) PeachAccent else Color(0xFFEEEEEE),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = if (isSelected) PeachAccent else Color.Gray, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(name, fontSize = 10.sp, color = if (isSelected) PeachAccent else Color.Black)
        }
    }
}
