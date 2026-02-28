package com.nisr.sauservices.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen

@Composable
fun AuthOptionsScreen(navController: NavController, role: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7F6))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome ${role.replace("_", " ").replaceFirstChar { it.uppercase() }}",
            fontSize = 24.sp,
            color = Color(0xFF2E7D6B),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(80.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OptionCircle("Sign In", Color(0xFF2E7D6B)) {
                navController.navigate(Screen.Login.createRoute(role))
            }
            OptionCircle("Sign Up", Color(0xFF2E7D6B)) {
                when (role) {
                    "shopkeeper" -> navController.navigate(Screen.ShopkeeperRegister.route)
                    "service_worker" -> navController.navigate(Screen.ServiceWorkerRegister.route)
                    "delivery" -> navController.navigate(Screen.DeliveryPartnerRegister.route)
                    "customer" -> navController.navigate(Screen.Register.route)
                }
            }
            OptionCircle("Forgot PW", Color(0xFFE57373)) {
                navController.navigate(Screen.ForgotPassword.route)
            }
        }
    }
}

@Composable
fun OptionCircle(label: String, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        Surface(
            modifier = Modifier
                .size(80.dp)
                .clickable { onClick() },
            shape = CircleShape,
            color = Color.White,
            tonalElevation = 6.dp,
            shadowElevation = 6.dp,
            border = BorderStroke(2.dp, color)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = label.replace(" ", "\n"),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
