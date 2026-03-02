
package com.nisr.sauservices.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen

// Colors as per design rules
private val ElegantTeal = Color(0xFF0FA3A3)
private val ElegantTealDark = Color(0xFF087E7E)
private val SoftGreyBgEnd = Color(0xFFECEFF1)
private val TextGrey = Color(0xFF717171)
private val TextDark = Color(0xFF1A1C1E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController, role: String) {
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    // Role specific fields
    var shopName by remember { mutableStateOf("") }
    var shopAddress by remember { mutableStateOf("") }
    var shopCategory by remember { mutableStateOf("") }
    
    var skillType by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }

    var vehicleType by remember { mutableStateOf("") }

    val isShopkeeper = role == "shopkeeper"
    val isWorker = role == "service_worker"
    val isDelivery = role == "delivery"
    
    val headerTitle = when {
        isShopkeeper -> "Register Shop"
        isWorker -> "Join as Worker"
        isDelivery -> "Become a Driver"
        else -> "Create Account"
    }
    val headerSubtitle = when {
        isShopkeeper -> "Set up your shop and start selling"
        isWorker -> "Create your profile and find work"
        isDelivery -> "Register and start delivering"
        else -> "Start ordering services in minutes"
    }
    val headerIcon = when {
        isShopkeeper -> Icons.Rounded.Storefront
        isWorker -> Icons.Rounded.Engineering
        isDelivery -> Icons.Rounded.LocalShipping
        else -> Icons.Rounded.PersonAdd
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, SoftGreyBgEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Updated Back Button alignment
            Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(ElegantTeal.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        headerIcon,
                        contentDescription = null,
                        tint = ElegantTeal,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = headerTitle,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = headerSubtitle,
                    fontSize = 15.sp,
                    color = TextGrey,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Progress Indicators (Dots)
            val dotCount = if (isShopkeeper || isWorker || isDelivery) 6 else 4
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(dotCount) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (index == 0) ElegantTeal else TextGrey.copy(alpha = 0.2f),
                                CircleShape
                            )
                    )
                    if (index < dotCount - 1) Spacer(modifier = Modifier.width(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Card Container for Inputs
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (isShopkeeper) {
                        SignUpField(value = shopName, onValueChange = { shopName = it }, placeholder = "Shop Name", icon = Icons.Rounded.Store)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    SignUpField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = when {
                            isShopkeeper -> "Owner Name"
                            else -> "Full Name"
                        },
                        icon = Icons.Rounded.Person
                    )

                    if (isWorker) {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = skillType,
                            onValueChange = { skillType = it },
                            placeholder = { Text("Skill Type") },
                            leadingIcon = { Icon(Icons.Rounded.Work, null, modifier = Modifier.size(20.dp)) },
                            trailingIcon = { Icon(Icons.Rounded.KeyboardArrowDown, null) },
                            readOnly = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElegantTeal,
                                unfocusedBorderColor = Color(0xFFF0F0F0),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        SignUpField(value = experience, onValueChange = { experience = it }, placeholder = "Experience (Years)", icon = Icons.Rounded.History, keyboardType = KeyboardType.Number)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SignUpField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        placeholder = "Phone Number",
                        icon = Icons.Rounded.Smartphone,
                        keyboardType = KeyboardType.Phone
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SignUpField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Email Address",
                        icon = Icons.Rounded.AlternateEmail,
                        keyboardType = KeyboardType.Email
                    )

                    if (isShopkeeper) {
                        Spacer(modifier = Modifier.height(16.dp))
                        SignUpField(value = shopAddress, onValueChange = { shopAddress = it }, placeholder = "Shop Address", icon = Icons.Rounded.LocationOn)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = shopCategory,
                            onValueChange = { shopCategory = it },
                            placeholder = { Text("Shop Category") },
                            leadingIcon = { Icon(Icons.Rounded.Category, null, modifier = Modifier.size(20.dp)) },
                            trailingIcon = { Icon(Icons.Rounded.KeyboardArrowDown, null) },
                            readOnly = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElegantTeal,
                                unfocusedBorderColor = Color(0xFFF0F0F0),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }

                    if (isDelivery) {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = vehicleType,
                            onValueChange = { vehicleType = it },
                            placeholder = { Text("Vehicle Type") },
                            leadingIcon = { Icon(Icons.Rounded.DirectionsCar, null, modifier = Modifier.size(20.dp)) },
                            trailingIcon = { Icon(Icons.Rounded.KeyboardArrowDown, null) },
                            readOnly = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElegantTeal,
                                unfocusedBorderColor = Color(0xFFF0F0F0),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }

                    if (isWorker || isDelivery) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(Color(0xFFF9F9F9), RoundedCornerShape(16.dp))
                                .border(1.dp, Color(0xFFF0F0F0).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .clickable { /* Upload */ }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(ElegantTeal.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Rounded.CloudUpload, null, tint = ElegantTeal, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(if (isDelivery) "Driving License" else "ID Document", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
                                    Text("Tap to upload • PDF, JPG, PNG", fontSize = 11.sp, color = TextGrey)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Password") },
                        leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(image, "Toggle visibility", modifier = Modifier.size(20.dp))
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElegantTeal,
                            unfocusedBorderColor = Color(0xFFF0F0F0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { /* Handle Sign Up */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = ElegantTeal
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(listOf(ElegantTeal, ElegantTealDark))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bottom Navigation
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = TextGrey)) {
                    append("Already have an account? ")
                }
                withStyle(style = SpanStyle(color = Color(0xFFE91E63), fontWeight = FontWeight.Bold)) {
                    append("Sign In")
                }
            }
            
            Text(
                text = annotatedString,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { navController.popBackStack() }
                    .padding(bottom = 32.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Security, null, modifier = Modifier.size(12.dp), tint = ElegantTeal.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Your data is encrypted and secure", fontSize = 11.sp, color = TextGrey.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun SignUpField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp)) },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ElegantTeal,
            unfocusedBorderColor = Color(0xFFF0F0F0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}
