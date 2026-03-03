package com.nisr.sauservices.ui.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.nisr.sauservices.R
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PeachAccent
import com.nisr.sauservices.ui.viewmodel.AuthState
import com.nisr.sauservices.ui.viewmodel.AuthViewModel

@Composable
fun CustomerSignUpScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val authState by authViewModel.authState

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { idToken ->
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                authViewModel.signInWithGoogle(credential, "customer")
            }
        } catch (e: ApiException) {
            // Handle error
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            sessionManager.saveLoginState(true)
            sessionManager.saveUserRole("customer")
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Register.route) { inclusive = true }
            }
            authViewModel.resetState()
        }
    }

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
                    text = "Create Account",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Join as a Customer",
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
            if (authState is AuthState.Error) {
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            SignUpTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Full Name",
                placeholder = "John Doe",
                icon = Icons.Default.Person,
                enabled = authState !is AuthState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            SignUpTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = "Phone Number",
                placeholder = "+1 234 567 8900",
                icon = Icons.Default.Phone,
                keyboardType = KeyboardType.Phone,
                enabled = authState !is AuthState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            SignUpTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email Address",
                placeholder = "example@gmail.com",
                icon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                enabled = authState !is AuthState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            SignUpTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "••••••••••••",
                icon = Icons.Default.Lock,
                isPassword = true,
                enabled = authState !is AuthState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            SignUpTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                placeholder = "••••••••",
                icon = Icons.Default.Lock,
                isPassword = true,
                enabled = authState !is AuthState.Loading
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { 
                    if (email.isNotBlank() && password.isNotBlank() && password == confirmPassword) {
                        val userData = mapOf(
                            "fullName" to fullName,
                            "phoneNumber" to phoneNumber,
                            "email" to email,
                            "role" to "customer"
                        )
                        authViewModel.signUp(email, password, userData)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = authState !is AuthState.Loading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PeachAccent)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Social Login Section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
                    Text(
                        " OR CONTINUE WITH ",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { GoogleSignInUtils.launchGoogleSignIn(context, googleLauncher) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_google),
                                contentDescription = "Google",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Google", color = Color.Black, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    OutlinedButton(
                        onClick = { /* Phone Login */ },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Smartphone, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Phone", color = Color.Black, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Already have an account? ", color = Color.Gray)
                Text(
                    text = "Sign In",
                    color = PeachAccent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { 
                        authViewModel.resetState()
                        navController.navigate(Screen.Login.createRoute("customer")) 
                    }
                )
            }
        }
    }
}

@Composable
fun SignUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            leadingIcon = { Icon(icon, contentDescription = null, tint = Color.LightGray) },
            trailingIcon = if (isPassword) {
                { Icon(Icons.Default.VisibilityOff, contentDescription = null, tint = Color.LightGray) }
            } else null,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PeachAccent,
                unfocusedBorderColor = Color(0xFFEEEEEE),
                focusedContainerColor = Color(0xFFF9F9F9),
                unfocusedContainerColor = Color(0xFFF9F9F9)
            )
        )
    }
}
