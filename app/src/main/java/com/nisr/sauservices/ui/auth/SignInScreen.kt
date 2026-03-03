
package com.nisr.sauservices.ui.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.R
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.viewmodel.AuthState
import com.nisr.sauservices.ui.viewmodel.AuthViewModel

// Colors as per design rules
private val ElegantTeal = Color(0xFF0FA3A3)
private val ElegantTealDark = Color(0xFF087E7E)
private val SoftGreyBgEnd = Color(0xFFECEFF1)
private val TextGrey = Color(0xFF717171)
private val TextDark = Color(0xFF1A1C1E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController: NavController, role: String, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    
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
                authViewModel.signInWithGoogle(credential, role)
            }
        } catch (e: ApiException) {
            // Handle error
        }
    }

    val isShopkeeper = role == "shopkeeper"
    val isWorker = role == "service_worker"
    val isDelivery = role == "delivery"
    val isCustomer = role == "customer"
    
    val headerTitle = when {
        isShopkeeper -> "Shop Login"
        isWorker -> "Worker Login"
        isDelivery -> "Driver Login"
        else -> "Welcome Back"
    }
    
    val headerSubtitle = when {
        isShopkeeper -> "Access your shop dashboard"
        isWorker -> "Sign in to find work opportunities"
        isDelivery -> "Access your delivery dashboard"
        else -> "Sign in to browse and order services"
    }
    
    val headerIcon = when {
        isShopkeeper -> Icons.Rounded.Storefront
        isWorker -> Icons.Rounded.Engineering
        isDelivery -> Icons.Rounded.LocalShipping
        else -> Icons.Rounded.Login
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            sessionManager.saveLoginState(true)
            sessionManager.saveUserRole(role)
            
            val route = when {
                isShopkeeper -> Screen.ShopkeeperDashboard.route
                isWorker -> Screen.ServiceWorkerDashboard.route
                isDelivery -> Screen.DeliveryDashboard.route
                else -> Screen.Home.route
            }
            navController.navigate(route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
            authViewModel.resetState()
        }
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
        ) {
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

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (authState is AuthState.Error) {
                        Text(
                            text = (authState as AuthState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Rounded.AlternateEmail, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        enabled = authState !is AuthState.Loading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElegantTeal,
                            unfocusedBorderColor = Color(0xFFF0F0F0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

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
                        enabled = authState !is AuthState.Loading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElegantTeal,
                            unfocusedBorderColor = Color(0xFFF0F0F0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(checkedColor = ElegantTeal)
                            )
                            Text("Remember me", color = TextGrey, fontSize = 14.sp)
                        }
                        Text(
                            text = "Forgot password?",
                            color = Color(0xFFE91E63),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { 
                                navController.navigate(Screen.ForgotPassword.route)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { 
                            if (email.isNotBlank() && password.isNotBlank()) {
                                authViewModel.signIn(email, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = ElegantTeal
                            ),
                        enabled = authState !is AuthState.Loading,
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
                            if (authState is AuthState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
                    Text(
                        " OR CONTINUE WITH ",
                        color = TextGrey,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { GoogleSignInUtils.launchGoogleSignIn(context, googleLauncher) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_google),
                                contentDescription = "Google",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Google", color = TextDark, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    OutlinedButton(
                        onClick = { /* Phone Login */ },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Smartphone, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Phone OTP", color = TextDark, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))

            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = TextGrey)) {
                    append("Don't have an account? ")
                }
                withStyle(style = SpanStyle(color = Color(0xFFE91E63), fontWeight = FontWeight.Bold)) {
                    append("Sign Up")
                }
            }
            
            Text(
                text = annotatedString,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { 
                        authViewModel.resetState()
                        navController.navigate(Screen.SignUp.createRoute(role)) 
                    }
                    .padding(bottom = 24.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Security, null, modifier = Modifier.size(12.dp), tint = ElegantTeal.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Protected by 256-bit encryption", fontSize = 11.sp, color = TextGrey.copy(alpha = 0.7f))
            }
        }
    }
}
