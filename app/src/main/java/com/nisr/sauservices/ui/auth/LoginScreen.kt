package com.nisr.sauservices.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*

@Composable
fun LoginScreen(navController: NavController, sessionManager: SessionManager, role: String) {
    var loginMethod by remember { mutableStateOf("Email") } // Email, Phone
    var emailOrPhone by remember { mutableStateOf("") }
    var passwordOrOtp by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7F6))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        Text(
            text = "SAU Services",
            color = Color(0xFF2E7D6B),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Logging in as ${role.replace("_", " ").replaceFirstChar { it.uppercase() }}",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Login Method Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Email",
                color = if (loginMethod == "Email") Color(0xFF2E7D6B) else Color.Gray,
                modifier = Modifier.clickable { loginMethod = "Email" }
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = "Phone",
                color = if (loginMethod == "Phone") Color(0xFF2E7D6B) else Color.Gray,
                modifier = Modifier.clickable { loginMethod = "Phone" }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Input Fields
        OutlinedTextField(
            value = emailOrPhone,
            onValueChange = { emailOrPhone = it },
            label = { Text(if (loginMethod == "Email") "Email Address" else "Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2E7D6B),
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color(0xFF2E7D6B)
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (loginMethod == "Email") KeyboardType.Email else KeyboardType.Phone
            ),
            leadingIcon = {
                Icon(
                    imageVector = if (loginMethod == "Email") Icons.Default.Email else Icons.Default.Phone,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = passwordOrOtp,
            onValueChange = { passwordOrOtp = it },
            label = { Text(if (loginMethod == "Email") "Password" else "OTP") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2E7D6B),
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color(0xFF2E7D6B)
            ),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (loginMethod == "Email") PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (loginMethod == "Email") KeyboardType.Password else KeyboardType.Number
            ),
            leadingIcon = {
                Icon(
                    imageVector = if (loginMethod == "Email") Icons.Default.Lock else Icons.Default.Message,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (emailOrPhone.isNotEmpty() && passwordOrOtp.isNotEmpty()) {
                    sessionManager.saveLoginState(true)
                    sessionManager.saveUserRole(role)
                    
                    val destination = when (role) {
                        "customer" -> Screen.Home.route
                        "shopkeeper" -> Screen.ShopkeeperDashboard.route
                        "service_worker" -> Screen.ServiceWorkerDashboard.route
                        "delivery" -> Screen.DeliveryDashboard.route
                        else -> Screen.Home.route
                    }
                    
                    navController.navigate(destination) {
                        popUpTo(Screen.RoleSelection.route) { inclusive = true }
                    }
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D6B))
        ) {
            Text("LOGIN", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Don't have an account? Sign Up",
            color = Color(0xFF2E7D6B),
            modifier = Modifier.clickable { 
                val regRoute = when (role) {
                    "customer" -> Screen.Register.route
                    "shopkeeper" -> Screen.ShopkeeperRegister.route
                    "service_worker" -> Screen.ServiceWorkerRegister.route
                    "delivery" -> Screen.DeliveryPartnerRegister.route
                    else -> Screen.Register.route
                }
                navController.navigate(regRoute)
            }
        )
    }
}
