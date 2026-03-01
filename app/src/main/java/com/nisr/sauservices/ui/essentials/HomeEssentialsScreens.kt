package com.nisr.sauservices.ui.essentials

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.data.model.HomeCategory
import com.nisr.sauservices.data.model.HomeEssentialsData
import com.nisr.sauservices.data.model.HomeProduct
import com.nisr.sauservices.data.model.HomeSubcategory
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.theme.LightPink
import com.nisr.sauservices.ui.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEssentialsSheetContent(navController: NavController, onDismiss: () -> Unit) {
    val categories = HomeEssentialsData.categories

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Home Essentials",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PinkPrimary
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDismiss()
                            navController.navigate(Screen.HomeEssentialsCategory.createRoute(category.id))
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LightPink),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(category.icon, fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = category.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PinkPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// --- LEVEL 1: MAIN SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEssentialsMainScreen(navController: NavController, cartViewModel: CartViewModel) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Home Essentials", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = PinkPrimary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                            Text(" Select Location", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    IconButton(onClick = { navController.navigate(Screen.HomeEssentialsCart.route) }) {
                        BadgedBox(badge = {
                            if (cartViewModel.homeCartItems.isNotEmpty()) {
                                Badge(containerColor = PinkPrimary) {
                                    Text(cartViewModel.homeCartItems.sumOf { it.quantity }.toString(), color = Color.White)
                                }
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.Black)
                        }
                    }
                }
                
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search groceries, meat, dairy...", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF5F5F5), focusedContainerColor = Color(0xFFF5F5F5), unfocusedBorderColor = Color.Transparent)
                )
            }
        },
        containerColor = Color.White
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(padding)
        ) {
            items(HomeEssentialsData.categories) { category ->
                HomeCategoryCard(category) {
                    navController.navigate(Screen.HomeEssentialsCategory.createRoute(category.id))
                }
            }
        }
    }
}

@Composable
fun HomeCategoryCard(category: HomeCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(160.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFB)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(category.icon, fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = category.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        }
    }
}

// --- LEVEL 2: SUBCATEGORY SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEssentialsCategoryScreen(navController: NavController, categoryId: String) {
    val category = HomeEssentialsData.categories.find { it.id == categoryId }
    val subcategories = HomeEssentialsData.subcategories.filter { it.categoryId == categoryId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category?.name ?: "Categories", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(subcategories) { sub ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        navController.navigate(Screen.HomeEssentialsItems.createRoute(sub.id))
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(sub.name, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
                    }
                }
            }
        }
    }
}

// --- LEVEL 3: ITEMS SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEssentialsItemsScreen(navController: NavController, subcategoryId: String, cartViewModel: CartViewModel) {
    val subcategory = HomeEssentialsData.subcategories.find { it.id == subcategoryId }
    val products = HomeEssentialsData.products.filter { it.subcategoryId == subcategoryId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(subcategory?.name ?: "Items", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.HomeEssentialsCart.route) }) {
                        BadgedBox(badge = {
                            if (cartViewModel.homeCartItems.isNotEmpty()) {
                                Badge(containerColor = PinkPrimary) {
                                    Text(cartViewModel.homeCartItems.sumOf { it.quantity }.toString(), color = Color.White)
                                }
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    }
                }
            )
        },
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(products) { product ->
                HomeProductCard(
                    product = product,
                    quantity = cartViewModel.getHomeItemQuantity(product.id),
                    onIncrease = { cartViewModel.addHomeProduct(product) },
                    onDecrease = { cartViewModel.removeHomeProduct(product.id) }
                )
            }
        }
    }
}

@Composable
fun HomeProductCard(
    product: HomeProduct,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingBasket, contentDescription = null, tint = PinkPrimary.copy(alpha = 0.3f), modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("₹${product.price} / ${product.unit}", color = PinkPrimary, fontWeight = FontWeight.ExtraBold)
            }
            
            if (quantity == 0) {
                Button(
                    onClick = onIncrease,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                ) {
                    Text("ADD")
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.background(PinkPrimary, RoundedCornerShape(8.dp)).padding(horizontal = 4.dp)
                ) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White)
                    }
                    Text(quantity.toString(), color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                    IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
    }
}

// --- CART SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEssentialsCartScreen(navController: NavController, cartViewModel: CartViewModel) {
    val cartItems = cartViewModel.homeCartItems
    val deliveryFee = 30
    val subtotal = cartItems.sumOf { it.product.price * it.quantity }
    val grandTotal = if (cartItems.isEmpty()) 0 else subtotal + deliveryFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Cart", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Grand Total", fontSize = 16.sp, color = Color.Gray)
                            Text("₹$grandTotal", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = PinkPrimary)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { navController.navigate(Screen.HomeEssentialsCheckout.route) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                        ) {
                            Text("Proceed to Checkout", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFFBFBFB)
    ) { padding ->
        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Your cart is empty", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.product.name, fontWeight = FontWeight.Bold)
                                Text("₹${item.product.price} x ${item.quantity}", color = Color.Gray, fontSize = 12.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { cartViewModel.removeHomeProduct(item.product.id) }) { Icon(Icons.Default.Remove, null) }
                                Text(item.quantity.toString(), fontWeight = FontWeight.Bold)
                                IconButton(onClick = { 
                                    val prod = HomeEssentialsData.products.find { it.id == item.product.id }
                                    prod?.let { cartViewModel.addHomeProduct(it) }
                                }) { Icon(Icons.Default.Add, null) }
                            }
                        }
                    }
                }
                item {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Item Total")
                            Text("₹$subtotal")
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Delivery Fee")
                            Text("₹$deliveryFee")
                        }
                    }
                }
            }
        }
    }
}

// --- CHECKOUT SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEssentialsCheckoutScreen(navController: NavController, cartViewModel: CartViewModel) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedSlot by remember { mutableStateOf("Morning") }
    var selectedPayment by remember { mutableStateOf("Cash on Delivery") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Checkout", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("Delivery Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Full Address") }, modifier = Modifier.fillMaxWidth())
            
            Spacer(Modifier.height(24.dp))
            Text("Delivery Slot", fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Morning", "Afternoon", "Evening").forEach { slot ->
                    FilterChip(selected = selectedSlot == slot, onClick = { selectedSlot = slot }, label = { Text(slot) })
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("Payment Method", fontWeight = FontWeight.Bold)
            Column {
                listOf("Cash on Delivery", "UPI").forEach { method ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { selectedPayment = method }) {
                        RadioButton(selected = selectedPayment == method, onClick = { selectedPayment = method })
                        Text(method)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { navController.navigate(Screen.HomeEssentialsSuccess.route) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                enabled = name.isNotEmpty() && phone.isNotEmpty() && address.isNotEmpty()
            ) {
                Text("Place Order", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- SUCCESS SCREEN ---
@Composable
fun HomeEssentialsSuccessScreen(navController: NavController, cartViewModel: CartViewModel) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(100.dp), tint = Color(0xFF43A047))
            Spacer(Modifier.height(24.dp))
            Text("Order Placed Successfully!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Order ID: #SAU${(1000..9999).random()}", color = Color.Gray)
            Spacer(Modifier.height(16.dp))
            Text("• Groceries packed\n• Delivery in 60–90 minutes", textAlign = TextAlign.Center, color = Color.DarkGray)
            Spacer(Modifier.height(48.dp))
            Button(
                onClick = { 
                    cartViewModel.clearHomeCart()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
            ) {
                Text("Back to Home", fontWeight = FontWeight.Bold)
            }
        }
    }
}
