package com.nisr.sauservices.ui.food

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.viewmodel.FoodCartViewModel
import com.nisr.sauservices.ui.theme.PinkPrimary
import java.net.URLEncoder

data class FoodItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val rating: String = "4.5"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodItemsScreen(navController: NavController, type: String, cartViewModel: FoodCartViewModel) {
    val decodedType = type
    
    // Catering logic
    val cateringKeywords = listOf("Wedding", "Birthday", "Corporate", "Outdoor", "Small Party", "Catering")
    val isCatering = cateringKeywords.any { decodedType.contains(it, ignoreCase = true) }

    val itemsList = when (decodedType) {
        // Veg Meals
        "South Indian Meals" -> listOf(FoodItem("v1", "South Meals", "Traditional South Indian Meal", 120))
        "North Indian Thali" -> listOf(FoodItem("v2", "Deluxe Meals", "Rich North Indian Thali", 180))
        "Premium Meals" -> listOf(FoodItem("v3", "Special Thali", "Premium Quality Veg Thali", 220))
        "Mini Meals" -> listOf(FoodItem("v4", "Mini Meals", "Quick Veg Mini Meal", 90))
        "Festival Meals" -> listOf(FoodItem("v5", "Festival Combo", "Special Festive Meal", 250))

        // Non Veg Meals
        "Chicken Meals" -> listOf(FoodItem("nv1", "Chicken Meal", "Spicy Chicken Curry & Rice", 180))
        "Mutton Meals" -> listOf(FoodItem("nv2", "Mutton Meal", "Traditional Mutton Curry Meal", 240))
        "Fish Meals" -> listOf(FoodItem("nv3", "Fish Meal", "Tasty Fish Curry & Fried Fish", 200))
        "Egg Meals" -> listOf(FoodItem("nv4", "Egg Curry Meal", "Classic Egg Curry Meal", 140))
        "Mixed Meals" -> listOf(FoodItem("nv5", "Mixed Meal", "Combination Non-Veg Meal", 260))

        // Fast Food
        "Burgers" -> listOf(
            FoodItem("ff1", "Veg Burger", "Classic Crispy Veg Burger", 80),
            FoodItem("ff2", "Chicken Burger", "Spicy Grilled Chicken Burger", 120)
        )
        "Pizzas" -> listOf(FoodItem("ff3", "Pizza", "Cheesy Loaded Pizza", 200))
        "Rolls" -> listOf(FoodItem("ff4", "Paneer Roll", "Delicious Paneer Tikka Roll", 90))
        "Snacks" -> listOf(FoodItem("ff5", "French Fries", "Golden Salted Fries", 70))

        // Cakes
        "Chocolate" -> listOf(FoodItem("ck1", "Chocolate Cake", "Rich Dark Chocolate Cake", 500))
        "Vanilla" -> listOf(FoodItem("ck2", "Vanilla Cake", "Classic Creamy Vanilla Cake", 400))
        "Fruit" -> listOf(FoodItem("ck3", "Fruit Cake", "Fresh Loaded Fruit Cake", 550))
        "Red Velvet" -> listOf(FoodItem("ck4", "Red Velvet", "Elegant Red Velvet Cake", 650))
        "Designer Cakes" -> listOf(FoodItem("ck5", "Custom Cake", "Themed Custom Designed Cake", 1200))

        // Fresh Juices
        "Orange" -> listOf(FoodItem("bj1", "Orange Juice", "Freshly Squeezed Orange", 60))
        "Apple" -> listOf(FoodItem("bj2", "Apple Juice", "Fresh Red Apple Juice", 80))
        "Pineapple" -> listOf(FoodItem("bj3", "Pineapple Juice", "Fresh Pineapple Juice", 70))
        "Watermelon" -> listOf(FoodItem("bj4", "Watermelon Juice", "Refreshing Watermelon Juice", 50))
        "Mixed Fruit" -> listOf(FoodItem("bj5", "Mixed Juice", "Healthy Mixed Fruit Juice", 90))

        // Fine Dining
        "Starters" -> listOf(
            FoodItem("rs1", "Paneer Starter", "Tandoori Paneer Tikka", 220),
            FoodItem("rs2", "Chicken Starter", "Crispy Chicken Wings", 260)
        )
        "Main Course" -> listOf(
            FoodItem("rm1", "Veg Main Course", "Special Veg Curry & Roti", 300),
            FoodItem("rm2", "Non Veg Main", "Chicken Masala & Rice", 420)
        )
        "Desserts" -> listOf(FoodItem("rd1", "Dessert", "Sweet Gulab Jamun with Ice Cream", 150))

        // Catering
        "Wedding Catering" -> listOf(FoodItem("cat1", "Wedding Catering", "Complete Wedding Meal - ₹500/plate", 500))
        "Birthday Catering" -> listOf(FoodItem("cat2", "Birthday Catering", "Party Meal - ₹350/plate", 350))
        "Corporate Catering" -> listOf(FoodItem("cat3", "Corporate Catering", "Official Meal - ₹400/plate", 400))
        "Outdoor Catering" -> listOf(FoodItem("cat4", "Outdoor Catering", "Buffet System - ₹450/plate", 450))
        "Small Party Catering" -> listOf(FoodItem("cat5", "Small Party Catering", "Home Party Meal - ₹300/plate", 300))

        else -> listOf(FoodItem("def", "$decodedType Item", "Delicious food item", 100))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(decodedType, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isCatering) {
                        IconButton(onClick = { navController.navigate("FOODS_cart") }) {
                            BadgedBox(badge = {
                                if (cartViewModel.cartItems.isNotEmpty()) {
                                    Badge(containerColor = PinkPrimary) {
                                        val totalQty = cartViewModel.cartItems.sumOf { it.quantity }
                                        Text(totalQty.toString(), color = Color.White)
                                    }
                                }
                            }) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA),
        bottomBar = {
            if (!isCatering && cartViewModel.cartItems.isNotEmpty()) {
                BottomCartBar(cartViewModel) {
                    navController.navigate("FOODS_cart")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(itemsList) { item ->
                if (isCatering) {
                    CateringItemCard(item) {
                        val encoded = URLEncoder.encode(item.name, "UTF-8")
                        navController.navigate("FOODS_booking/$encoded")
                    }
                } else {
                    FoodItemCard(item, cartViewModel)
                }
            }
        }
    }
}

@Composable
fun BottomCartBar(viewModel: FoodCartViewModel, onViewCart: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = PinkPrimary,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                val totalQty = viewModel.cartItems.sumOf { it.quantity }
                Text(
                    text = "$totalQty Items",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Total: ₹${viewModel.getTotal()}",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )
            }
            TextButton(
                onClick = onViewCart,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("VIEW CART", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun CateringItemCard(item: FoodItem, onBook: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(PinkPrimary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Restaurant, contentDescription = null, tint = PinkPrimary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = item.description, fontSize = 13.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onBook,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
            ) {
                Text("BOOK SERVICE", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FoodItemCard(item: FoodItem, viewModel: FoodCartViewModel) {
    val quantity = viewModel.getItemQuantity(item.id)

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
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PinkPrimary.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Fastfood, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(36.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = item.description, fontSize = 12.sp, color = Color.Gray, maxLines = 2)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "₹${item.price}", fontWeight = FontWeight.ExtraBold, color = PinkPrimary, fontSize = 17.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                    Text(text = " ${item.rating}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (quantity == 0) {
                    Button(
                        onClick = { viewModel.addItem(item.id, item.name, item.price, item.description) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp)
                    ) {
                        Text("ADD", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(PinkPrimary, RoundedCornerShape(10.dp))
                            .height(36.dp)
                    ) {
                        IconButton(onClick = { viewModel.decreaseQty(item.id) }, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = quantity.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        IconButton(onClick = { viewModel.increaseQty(item.id) }, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
