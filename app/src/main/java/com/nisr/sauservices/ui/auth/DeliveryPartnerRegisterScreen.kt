package com.nisr.sauservices.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.data.model.User
import com.nisr.sauservices.data.repository.UserRepository
import com.nisr.sauservices.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryPartnerRegisterScreen(navController: NavController, userRepository: UserRepository) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("Bike") }
    var vehicleNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    val vehicles = listOf("Bike", "Scooter", "Auto", "Car")

    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var vehicleNumberError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7F6))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Delivery Partner Registration",
            fontSize = 24.sp,
            color = Color(0xFF2E7D6B),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 32.dp)
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = null },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError != null,
                    supportingText = { nameError?.let { Text(it) } }
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; phoneError = null },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = phoneError != null,
                    supportingText = { phoneError?.let { Text(it) } }
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; emailError = null },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailError != null,
                    supportingText = { emailError?.let { Text(it) } }
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; passwordError = null },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = passwordError != null,
                    supportingText = { passwordError?.let { Text(it) } }
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; confirmPasswordError = null },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = confirmPasswordError != null,
                    supportingText = { confirmPasswordError?.let { Text(it) } }
                )

                Box {
                    OutlinedTextField(
                        value = vehicleType,
                        onValueChange = {},
                        label = { Text("Vehicle Type") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, "dropdown", Modifier.clickable { expanded = true })
                        }
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        vehicles.forEach { v ->
                            DropdownMenuItem(
                                text = { Text(v) },
                                onClick = { vehicleType = v; expanded = false }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = vehicleNumber,
                    onValueChange = { vehicleNumber = it; vehicleNumberError = null },
                    label = { Text("Vehicle Number") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = vehicleNumberError != null,
                    supportingText = { vehicleNumberError?.let { Text(it) } }
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it; addressError = null },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = addressError != null,
                    supportingText = { addressError?.let { Text(it) } }
                )

                Button(
                    onClick = {
                        var isValid = true
                        if (name.isBlank()) { nameError = "Required"; isValid = false }
                        if (phone.length != 10) { phoneError = "Enter 10 digits"; isValid = false }
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { emailError = "Invalid Email"; isValid = false }
                        if (password.length < 6) { passwordError = "Min 6 characters"; isValid = false }
                        if (password != confirmPassword) { confirmPasswordError = "Passwords do not match"; isValid = false }
                        if (vehicleNumber.isBlank()) { vehicleNumberError = "Required"; isValid = false }
                        if (address.isBlank()) { addressError = "Required"; isValid = false }

                        if (isValid) {
                            val user = User(
                                id = System.currentTimeMillis().toString(),
                                role = "delivery",
                                name = name,
                                phone = phone,
                                email = email,
                                extraFields = mapOf(
                                    "vehicleType" to vehicleType,
                                    "vehicleNumber" to vehicleNumber,
                                    "address" to address
                                )
                            )
                            userRepository.registerUser(user)
                            navController.navigate(Screen.DeliveryDashboard.route)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D6B))
                ) {
                    Text("Register", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}
