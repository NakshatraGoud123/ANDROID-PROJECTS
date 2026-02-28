package com.nisr.sauservices.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun ShopkeeperRegisterScreen(navController: NavController, userRepository: UserRepository) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var shopAddress by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var shopNameError by remember { mutableStateOf<String?>(null) }
    var shopAddressError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7F6))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Shopkeeper Registration",
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

                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it; shopNameError = null },
                    label = { Text("Shop Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = shopNameError != null,
                    supportingText = { shopNameError?.let { Text(it) } }
                )

                OutlinedTextField(
                    value = shopAddress,
                    onValueChange = { shopAddress = it; shopAddressError = null },
                    label = { Text("Shop Address") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = shopAddressError != null,
                    supportingText = { shopAddressError?.let { Text(it) } }
                )

                Button(
                    onClick = {
                        var isValid = true
                        if (name.isBlank()) { nameError = "Required"; isValid = false }
                        if (phone.length != 10) { phoneError = "Enter 10 digits"; isValid = false }
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { emailError = "Invalid Email"; isValid = false }
                        if (password.length < 6) { passwordError = "Min 6 characters"; isValid = false }
                        if (password != confirmPassword) { confirmPasswordError = "Passwords do not match"; isValid = false }
                        if (shopName.isBlank()) { shopNameError = "Required"; isValid = false }
                        if (shopAddress.isBlank()) { shopAddressError = "Required"; isValid = false }

                        if (isValid) {
                            val user = User(
                                id = System.currentTimeMillis().toString(),
                                role = "shopkeeper",
                                name = name,
                                phone = phone,
                                email = email,
                                extraFields = mapOf("shopName" to shopName, "shopAddress" to shopAddress)
                            )
                            userRepository.registerUser(user)
                            navController.navigate(Screen.ShopkeeperDashboard.route)
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
