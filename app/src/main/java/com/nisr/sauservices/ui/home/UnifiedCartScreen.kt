package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedCartScreen(
    navController: NavController,
    residentialViewModel: ResidentialViewModel,
    businessViewModel: BusinessViewModel,
    lifestyleViewModel: LifestyleViewModel,
    techViewModel: TechServicesViewModel,
    mensGroomingViewModel: MensGroomingViewModel,
    womensBeautyViewModel: WomensBeautyViewModel,
    healthcareViewModel: HealthcareViewModel,
    foodCartViewModel: FoodCartViewModel,
    homeCartViewModel: CartViewModel
) {
    val resItems = residentialViewModel.cartItems
    val businessItems = businessViewModel.cartItems
    val lifestyleItems = lifestyleViewModel.cartItems
    val techItems = techViewModel.cartItems
    val mensItems = mensGroomingViewModel.cartItems
    val womensItems = womensBeautyViewModel.cartItems
    val healthItems = healthcareViewModel.cartItems
    val foodItems = foodCartViewModel.cartItems
    val homeItems = homeCartViewModel.homeCartItems

    val isEmpty = resItems.isEmpty() && businessItems.isEmpty() && 
                  lifestyleItems.isEmpty() && techItems.isEmpty() &&
                  mensItems.isEmpty() && womensItems.isEmpty() && healthItems.isEmpty() &&
                  foodItems.isEmpty() && homeItems.isEmpty()
    
    val subtotal = residentialViewModel.calculateTotal().toDouble() + 
                   businessViewModel.getTotalPrice() +
                   lifestyleViewModel.getTotalPrice() +
                   techViewModel.getTotalPrice() +
                   mensGroomingViewModel.getTotalPrice() +
                   womensBeautyViewModel.calculateTotal() +
                   healthcareViewModel.calculateTotal() +
                   foodCartViewModel.getTotal().toDouble() +
                   homeCartViewModel.itemTotal.toDouble()
    
    val deliveryFee = if (isEmpty) 0.0 else 30.0
    val grandTotal = subtotal + deliveryFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unified Cart", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (!isEmpty) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Column(modifier = Modifier.padding(16.dp).navigationBarsPadding()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Amount", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("₹$grandTotal", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PinkPrimary)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { 
                                navController.navigate(Screen.ResidentialBookingDetails.route)
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                        ) {
                            Text("Proceed to Checkout", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        },
        containerColor = Color.White
    ) { padding ->
        if (isEmpty) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Text("Your cart is empty", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (resItems.isNotEmpty()) {
                    item { Text("Residential Services", fontWeight = FontWeight.Bold, color = PinkPrimary) }
                    items(resItems) { item ->
                        CartItemRow(item.service.name, item.service.price.toInt(), item.quantity, 
                            { residentialViewModel.updateQty(item.service.id, true) }, 
                            { residentialViewModel.updateQty(item.service.id, false) })
                    }
                }

                if (foodItems.isNotEmpty()) {
                    item { Text("Food & Beverages", fontWeight = FontWeight.Bold, color = PinkPrimary) }
                    items(foodItems) { item ->
                        CartItemRow(item.name, item.price, item.quantity, 
                            { foodCartViewModel.increaseQty(item.id) }, 
                            { foodCartViewModel.decreaseQty(item.id) })
                    }
                }

                if (homeItems.isNotEmpty()) {
                    item { Text("Home Essentials", fontWeight = FontWeight.Bold, color = PinkPrimary) }
                    items(homeItems) { item ->
                        CartItemRow(item.product.name, item.product.price, item.quantity, 
                            { homeCartViewModel.addHomeProduct(item.product) }, 
                            { homeCartViewModel.removeHomeProduct(item.product.id) })
                    }
                }

                if (businessItems.isNotEmpty()) {
                    item { Text("Business Services", fontWeight = FontWeight.Bold, color = PinkPrimary) }
                    items(businessItems) { item ->
                        CartItemRow(item.name, item.price.toInt(), item.quantity, 
                            { businessViewModel.increaseQty(item.id) }, 
                            { businessViewModel.decreaseQty(item.id) })
                    }
                }

                if (lifestyleItems.isNotEmpty()) {
                    item { Text("Lifestyle Services", fontWeight = FontWeight.Bold, color = PinkPrimary) }
                    items(lifestyleItems) { item ->
                        CartItemRow(item.name, item.price.toInt(), item.quantity, 
                            { lifestyleViewModel.increaseQty(item.id) }, 
                            { lifestyleViewModel.decreaseQty(item.id) })
                    }
                }

                if (techItems.isNotEmpty()) {
                    item { Text("Tech Services", fontWeight = FontWeight.Bold, color = PinkPrimary) }
                    items(techItems) { item ->
                        CartItemRow(item.name, item.price.toInt(), item.quantity, 
                            { techViewModel.increaseQty(item.id) }, 
                            { techViewModel.decreaseQty(item.id) })
                    }
                }

                if (mensItems.isNotEmpty()) {
                    item { Text("Mens Grooming", fontWeight = FontWeight.Bold, color = PinkPrimary) }
                    items(mensItems) { item ->
                        CartItemRow(item.name, item.price.toInt(), item.quantity, 
                            { mensGroomingViewModel.increaseQty(item.id) }, 
                            { mensGroomingViewModel.decreaseQty(item.id) })
                    }
                }

                if (womensItems.isNotEmpty()) {
                    item { Text("Womens Beauty", fontWeight = FontWeight.Bold, color = PinkPrimary) }
                    items(womensItems) { item ->
                        CartItemRow(item.name, item.price.toInt(), item.quantity, 
                            { womensBeautyViewModel.updateQty(item.id, true) }, 
                            { womensBeautyViewModel.updateQty(item.id, false) })
                    }
                }

                if (healthItems.isNotEmpty()) {
                    item { Text("Healthcare & Pharmacy", fontWeight = FontWeight.Bold, color = PinkPrimary) }
                    items(healthItems) { item ->
                        CartItemRow(item.name, item.price.toInt(), item.quantity, 
                            { healthcareViewModel.updateQty(item.id, true) }, 
                            { healthcareViewModel.updateQty(item.id, false) })
                    }
                }

                item {
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", fontWeight = FontWeight.Medium)
                        Text("₹$subtotal")
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Service Fee", color = Color.Gray)
                        Text("₹$deliveryFee", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(name: String, price: Int, quantity: Int, onIncrease: () -> Unit, onDecrease: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFB)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("₹$price", color = PinkPrimary, fontWeight = FontWeight.SemiBold)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(PinkPrimary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))) {
                IconButton(onClick = onDecrease) { Icon(Icons.Default.Remove, null, tint = PinkPrimary) }
                Text(quantity.toString(), fontWeight = FontWeight.Bold, color = PinkPrimary)
                IconButton(onClick = onIncrease) { Icon(Icons.Default.Add, null, tint = PinkPrimary) }
            }
        }
    }
}
