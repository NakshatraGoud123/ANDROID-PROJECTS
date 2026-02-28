package com.nisr.sauservices.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.data.model.*
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.viewmodel.ResidentialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentialCategoryScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Residential Services", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(padding)
        ) {
            items(ResidentialData.categories) { category ->
                ResidentialCategoryItem(category) {
                    navController.navigate(Screen.ResidentialSubcategories.createRoute(category.id))
                }
            }
        }
    }
}

@Composable
fun ResidentialCategoryItem(category: ResidentialCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F5)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(category.icon, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(category.name, fontSize = 12.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentialSubcategoryScreen(navController: NavController, categoryId: String) {
    val category = ResidentialData.categories.find { it.id == categoryId }
    val subcategories = ResidentialData.subcategories.filter { it.categoryId == categoryId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category?.name ?: "Subcategories", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(subcategories) { sub ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        navController.navigate(Screen.ResidentialServiceList.createRoute(categoryId, sub.id))
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(sub.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = PinkPrimary)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentialServiceListScreen(navController: NavController, categoryId: String, subcategoryId: String, viewModel: ResidentialViewModel) {
    val sub = ResidentialData.subcategories.find { it.id == subcategoryId }
    val services = ResidentialData.services.filter { it.subcategory == subcategoryId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(sub?.name ?: "Services", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White,
        bottomBar = {
            if (viewModel.cartItems.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Button(
                        onClick = { navController.navigate(Screen.ResidentialCart.route) },
                        modifier = Modifier.padding(16.dp).fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${viewModel.cartItems.sumOf { it.quantity }} Items | ₹${viewModel.calculateTotal()}", fontWeight = FontWeight.Bold)
                            Text("View Cart", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(services) { service ->
                ResidentialServiceCard(
                    service = service,
                    quantity = viewModel.getItemQuantity(service.id),
                    onAdd = { viewModel.addToCart(service) },
                    onIncrease = { viewModel.updateQty(service.id, true) },
                    onDecrease = { viewModel.updateQty(service.id, false) }
                )
            }
        }
    }
}

@Composable
fun ResidentialServiceCard(service: ResidentialServiceItem, quantity: Int, onAdd: () -> Unit, onIncrease: () -> Unit, onDecrease: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(service.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Text("₹${service.price}", fontWeight = FontWeight.ExtraBold, color = PinkPrimary, fontSize = 14.sp)
                Text("${service.durationMinutes} mins", fontSize = 12.sp, color = Color.Gray)
            }
            if (quantity == 0) {
                OutlinedButton(
                    onClick = onAdd, 
                    shape = RoundedCornerShape(8.dp), 
                    border = BorderStroke(1.dp, PinkPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PinkPrimary)
                ) {
                    Text("ADD", fontWeight = FontWeight.Bold)
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically, 
                    modifier = Modifier.background(PinkPrimary, RoundedCornerShape(8.dp))
                ) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(36.dp)) { Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White) }
                    Text(quantity.toString(), color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                    IconButton(onClick = onIncrease, modifier = Modifier.size(36.dp)) { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentialCartScreen(navController: NavController, viewModel: ResidentialViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Cart", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        if (viewModel.cartItems.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Text("Your cart is empty", color = Color.Gray)
                }
            }
        } else {
            Column(Modifier.padding(padding).fillMaxSize()) {
                LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(viewModel.cartItems) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFB)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(item.service.name, fontWeight = FontWeight.Bold, color = Color.Black)
                                    Text("₹${item.service.price}", color = PinkPrimary, fontWeight = FontWeight.SemiBold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(PinkPrimary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))) {
                                    IconButton(onClick = { viewModel.updateQty(item.service.id, false) }) { Icon(Icons.Default.Remove, contentDescription = null, tint = PinkPrimary) }
                                    Text(item.quantity.toString(), fontWeight = FontWeight.Bold, color = PinkPrimary)
                                    IconButton(onClick = { viewModel.updateQty(item.service.id, true) }) { Icon(Icons.Default.Add, contentDescription = null, tint = PinkPrimary) }
                                }
                            }
                        }
                    }
                }
                Surface(Modifier.fillMaxWidth(), shadowElevation = 16.dp, color = Color.White) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Amount", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("₹${viewModel.calculateTotal()}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PinkPrimary)
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { navController.navigate(Screen.ResidentialBookingDetails.route) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                        ) {
                            Text("Proceed to Booking", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentialBookingDetailsScreen(navController: NavController, viewModel: ResidentialViewModel) {
    var address by remember { mutableStateOf(viewModel.bookingDetails.value.address) }
    var phone by remember { mutableStateOf(viewModel.bookingDetails.value.phone) }
    var date by remember { mutableStateOf(viewModel.bookingDetails.value.date) }
    var selectedSlot by remember { mutableStateOf(viewModel.bookingDetails.value.timeSlot) }

    val slots = listOf("Morning: 9AM–12PM", "Afternoon: 12PM–3PM", "Evening: 3PM–6PM", "Night: 6PM–9PM")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Details", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            OutlinedTextField(
                value = address, 
                onValueChange = { address = it; viewModel.setAddress(it) }, 
                label = { Text("Address") }, 
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkPrimary, focusedLabelColor = PinkPrimary),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = phone, 
                onValueChange = { phone = it; viewModel.setPhone(it) }, 
                label = { Text("Phone") }, 
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkPrimary, focusedLabelColor = PinkPrimary),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = date, 
                onValueChange = { date = it; viewModel.setDate(it) }, 
                label = { Text("Date (DD/MM/YYYY)") }, 
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PinkPrimary, focusedLabelColor = PinkPrimary),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(24.dp))
            Text("Select Time Slot", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Column(Modifier.selectableGroup()) {
                slots.forEach { slot ->
                    Row(
                        Modifier.fillMaxWidth()
                            .selectable(selected = selectedSlot == slot, onClick = { selectedSlot = slot; viewModel.setTimeSlot(slot) }, role = Role.RadioButton)
                            .padding(vertical = 8.dp), 
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedSlot == slot, 
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = PinkPrimary)
                        )
                        Text(slot, modifier = Modifier.padding(start = 12.dp), fontSize = 15.sp)
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { if(address.isNotBlank() && phone.isNotBlank() && selectedSlot.isNotBlank()) navController.navigate(Screen.ResidentialPayment.route) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
            ) { Text("Proceed", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentialPaymentScreen(navController: NavController, viewModel: ResidentialViewModel) {
    var selectedOption by remember { mutableStateOf(viewModel.bookingDetails.value.paymentMethod.ifBlank { "UPI" }) }
    val options = listOf(
        PaymentOptionData("Cash on Delivery", Icons.Default.Payments),
        PaymentOptionData("UPI", Icons.Default.AccountBalanceWallet),
        PaymentOptionData("Debit/Credit Card", Icons.Default.CreditCard),
        PaymentOptionData("Wallet", Icons.Default.AccountBalance)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment Methods", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Select Payment Option", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(16.dp))
            Column(Modifier.selectableGroup()) {
                options.forEach { option ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .selectable(
                                selected = selectedOption == option.name, 
                                onClick = { selectedOption = option.name; viewModel.setPaymentMethod(option.name) }, 
                                role = Role.RadioButton
                            ),
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedOption == option.name) PinkPrimary.copy(alpha = 0.05f) else Color(0xFFF8F8F8),
                        border = if (selectedOption == option.name) BorderStroke(1.dp, PinkPrimary) else null
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(option.icon, contentDescription = null, tint = if (selectedOption == option.name) PinkPrimary else Color.Gray)
                            Spacer(Modifier.width(16.dp))
                            Text(text = option.name, modifier = Modifier.weight(1f), fontWeight = if (selectedOption == option.name) FontWeight.Bold else FontWeight.Medium)
                            RadioButton(
                                selected = selectedOption == option.name, 
                                onClick = null,
                                colors = RadioButtonDefaults.colors(selectedColor = PinkPrimary)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { navController.navigate(Screen.ResidentialOrderSummary.route) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
            ) {
                Text("Proceed to Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

data class PaymentOptionData(val name: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentialOrderSummaryScreen(navController: NavController, viewModel: ResidentialViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Summary", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Card(
                Modifier.fillMaxWidth(), 
                colors = CardDefaults.cardColors(containerColor = PinkPrimary.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = PinkPrimary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Service Address", fontWeight = FontWeight.Bold)
                    }
                    Text(viewModel.bookingDetails.value.address, modifier = Modifier.padding(start = 28.dp), color = Color.DarkGray)
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Event, null, tint = PinkPrimary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Date & Time", fontWeight = FontWeight.Bold)
                    }
                    Text("${viewModel.bookingDetails.value.date} | ${viewModel.bookingDetails.value.timeSlot}", modifier = Modifier.padding(start = 28.dp), color = Color.DarkGray)
                    
                    Spacer(Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payment, null, tint = PinkPrimary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Payment Method", fontWeight = FontWeight.Bold)
                    }
                    Text(viewModel.bookingDetails.value.paymentMethod, modifier = Modifier.padding(start = 28.dp), color = Color.DarkGray)
                }
            }
            
            Spacer(Modifier.height(24.dp))
            Text("Selected Services", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(viewModel.cartItems) { item ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${item.service.name} x ${item.quantity}", color = Color.DarkGray)
                        Text("₹${item.service.price * item.quantity}", fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            HorizontalDivider(Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.5f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Amount", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Text("₹${viewModel.calculateTotal()}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = PinkPrimary)
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.clearCart()
                    navController.navigate(Screen.BookingSuccess.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
            ) { Text("Confirm Booking", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
        }
    }
}

@Composable
fun BookingSuccessScreen(navController: NavController) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                Box(Modifier.size(160.dp).scale(scale).clip(CircleShape).background(PinkPrimary.copy(alpha = 0.1f)))
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(100.dp))
            }
            Spacer(Modifier.height(32.dp))
            Text("Booking Successful!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(Modifier.height(8.dp))
            Text("Our expert will reach you soon.", color = Color.Gray, fontSize = 16.sp)
            Spacer(Modifier.height(48.dp))
            Button(
                onClick = { navController.navigate(Screen.Home.route) { popUpTo(0) } },
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp).padding(horizontal = 32.dp)
            ) {
                Text("Go to Home", fontWeight = FontWeight.Bold)
            }
        }
    }
}
