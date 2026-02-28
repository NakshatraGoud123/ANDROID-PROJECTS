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
fun ServiceWorkerRegisterScreen(navController: NavController, userRepository: UserRepository) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Plumbing") }
    var experience by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("Plumbing", "Electrician", "Cleaning", "AC Repair", "Painting")

    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var experienceError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7F6))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Service Worker Registration",
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
                        value = category,
                        onValueChange = {},
                        label = { Text("Service Category") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, "dropdown", Modifier.clickable { expanded = true })
                        }
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = { category = cat; expanded = false }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = experience,
                    onValueChange = { experience = it; experienceError = null },
                    label = { Text("Experience (Years)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = experienceError != null,
                    supportingText = { experienceError?.let { Text(it) } }
                )

                Button(
                    onClick = {
                        var isValid = true
                        if (name.isBlank()) { nameError = "Required"; isValid = false }
                        if (phone.length != 10) { phoneError = "Enter 10 digits"; isValid = false }
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { emailError = "Invalid Email"; isValid = false }
                        if (password.length < 6) { passwordError = "Min 6 characters"; isValid = false }
                        if (password != confirmPassword) { confirmPasswordError = "Passwords do not match"; isValid = false }
                        if (experience.isBlank()) { experienceError = "Required"; isValid = false }

                        if (isValid) {
                            val user = User(
                                id = System.currentTimeMillis().toString(),
                                role = "service_worker",
                                name = name,
                                phone = phone,
                                email = email,
                                extraFields = mapOf("category" to category, "experience" to experience)
                            )
                            userRepository.registerUser(user)
                            navController.navigate(Screen.ServiceWorkerDashboard.route)
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
