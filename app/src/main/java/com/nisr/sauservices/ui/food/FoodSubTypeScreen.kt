package com.nisr.sauservices.ui.food

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.RestaurantMenu
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

private val AccentGreen = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSubTypeScreen(navController: NavController, typeName: String) {
    val subTypes = when (typeName) {
        // Home Delivery -> Veg Meals
        "Mini Meals" -> listOf("Budget Mini Meals", "Executive Mini Meals")
        "Full Meals" -> listOf("Standard Full Meals", "Premium Full Thali")
        
        // Home Delivery -> Non-Veg Meals
        "Chicken Meals" -> listOf("Chicken Curry Meals", "Chicken Fry Meals")
        "Mutton Meals" -> listOf("Mutton Curry Meals", "Mutton Keema Meals")
        "Biryani Specials" -> listOf("Chicken Biryani", "Mutton Biryani", "Fish Biryani", "Egg Biryani")
        
        // Bakery -> Cakes
        "Birthday Cakes" -> listOf("Chocolate Birthday Cakes", "Vanilla Birthday Cakes", "Fruit Birthday Cakes")
        "Wedding Cakes" -> listOf("Multi-Tier Cakes", "Floral Designer Cakes")
        
        // Restaurant -> Biryani
        "Chicken Biryani" -> listOf("Hyderabadi Chicken Biryani", "Lucknowi Chicken Biryani", "Ambur Chicken Biryani")
        "Mutton Biryani" -> listOf("Dum Mutton Biryani", "Mutton Fry Piece Biryani")
        
        // Restaurant -> South Indian
        "Dosa Specials" -> listOf("Masala Dosa", "Paneer Dosa", "Rava Dosa")
        
        // Beverages -> Tea
        "Regular Tea" -> listOf("Ginger Tea", "Masala Tea", "Cardamom Tea")
        
        // Tiffin -> Daily Veg Tiffin
        "Lunch Tiffin" -> listOf("Standard Lunch", "Premium Lunch")
        
        // Catering -> Wedding
        "Wedding" -> listOf("North Indian Wedding Menu", "South Indian Wedding Menu", "Continental Wedding Menu")

        else -> listOf("Classic $typeName", "Special $typeName", "Premium $typeName")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(typeName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                text = "Select Sub-Type",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(subTypes) { subType ->
                    FoodSubTypeCard(subType) {
                        navController.navigate(Screen.FoodItems.createRoute(subType))
                    }
                }
            }
        }
    }
}

@Composable
fun FoodSubTypeCard(name: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(AccentGreen.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.RestaurantMenu, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = name,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}
