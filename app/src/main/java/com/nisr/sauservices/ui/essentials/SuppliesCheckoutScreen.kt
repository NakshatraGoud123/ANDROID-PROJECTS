package com.nisr.sauservices.ui.essentials

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.home.OrderSuccessDialog
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuppliesCheckoutScreen(navController: NavController, viewModel: CartViewModel) {
    val cartItems by viewModel.dbCartItems.collectAsState()
    var address by remember { mutableStateOf("") }
    var selectedPayment by remember { mutableStateOf("UPI") }
    
    val itemTotal = cartItems.sumOf { it.totalPrice }
    val deliveryCharge = 30.0
    val tax = itemTotal * 0.05
    val grandTotal = itemTotal + deliveryCharge + tax
    
    val orderStatus by viewModel.orderStatus.collectAsState()
    var showSuccess by remember { mutableStateOf(false) }
    var orderId by remember { mutableStateOf("") }

    LaunchedEffect(orderStatus) {
        orderStatus?.let {
            if (it.isSuccess) {
                orderId = it.getOrNull() ?: "ORD${System.currentTimeMillis() % 1000000}"
                showSuccess = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout Supplies", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    if (address.isNotBlank()) {
                        viewModel.placeOrder(address, selectedPayment)
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                enabled = address.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
            ) {
                Text("Pay & Confirm Order", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFB))
            ) {
                Column(Modifier.padding(16.dp)) {
                    cartItems.forEach { item ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${item.itemName} x ${item.quantity}")
                            Text("₹${item.totalPrice}")
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    PriceRow("Subtotal", itemTotal)
                    PriceRow("Delivery Charge", deliveryCharge)
                    PriceRow("Tax (5%)", tax)
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Grand Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("₹$grandTotal", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PinkPrimary)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Delivery Address", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                placeholder = { Text("Enter full address") },
                leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = PinkPrimary) },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))
            Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            
            PaymentOptionRow("UPI", Icons.Default.QrCode, selectedPayment == "UPI") { selectedPayment = "UPI" }
            PaymentOptionRow("Cash on Delivery", Icons.Default.Payments, selectedPayment == "COD") { selectedPayment = "COD" }
        }
    }

    if (showSuccess) {
        OrderSuccessDialog(
            orderId = orderId,
            onViewOrder = {
                showSuccess = false
                viewModel.resetOrderStatus()
                navController.navigate(Screen.MyOrders.route) {
                    popUpTo(Screen.Home.route) { inclusive = false }
                }
            },
            onGoHome = {
                showSuccess = false
                viewModel.resetOrderStatus()
                navController.navigate(Screen.Home.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
}

@Composable
fun PriceRow(label: String, amount: Double) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray)
        Text("₹$amount")
    }
}

@Composable
fun PaymentOptionRow(name: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onSelect: () -> Unit) {
    OutlinedCard(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isSelected) PinkPrimary.copy(alpha = 0.05f) else Color.Transparent
        ),
        border = CardDefaults.outlinedCardBorder(enabled = true).copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) PinkPrimary else Color.LightGray)
        )
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = if (isSelected) PinkPrimary else Color.Gray)
            Spacer(Modifier.width(16.dp))
            Text(name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
            Spacer(Modifier.weight(1f))
            RadioButton(selected = isSelected, onClick = null, colors = RadioButtonDefaults.colors(selectedColor = PinkPrimary))
        }
    }
}
