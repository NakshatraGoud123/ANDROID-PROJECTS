package com.nisr.sauservices.ui.lifestyle

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.theme.LightPink
import com.nisr.sauservices.ui.viewmodel.LifestyleViewModel
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifestyleServicesScreen(navController: NavController, subcategory: String, viewModel: LifestyleViewModel) {
    val decodedSub = URLDecoder.decode(subcategory, "UTF-8")
    
    val services = getServicesForSubcategory(decodedSub)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(decodedSub, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.LifestyleCart.route) }) {
                        BadgedBox(badge = {
                            if (viewModel.cartItems.isNotEmpty()) {
                                Badge(containerColor = PinkPrimary) {
                                    Text(viewModel.cartItems.sumOf { it.quantity }.toString(), color = Color.White)
                                }
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF7F7F7)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(services) { service ->
                LifestyleServiceCard(service, viewModel)
            }
        }
    }
}

@Composable
fun LifestyleServiceCard(service: LifestyleServiceItem, viewModel: LifestyleViewModel) {
    val cartItem = viewModel.cartItems.find { it.id == service.id }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = service.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "₹${service.price.toInt()}", fontWeight = FontWeight.Bold, color = PinkPrimary, fontSize = 14.sp)
            }

            if (cartItem == null) {
                Button(
                    onClick = { viewModel.addToCart(service) },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("ADD", fontWeight = FontWeight.Bold)
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(LightPink)
                ) {
                    IconButton(onClick = { viewModel.decreaseQty(service.id) }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Remove, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = cartItem.quantity.toString(),
                        fontWeight = FontWeight.Bold,
                        color = PinkPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = { viewModel.increaseQty(service.id) }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

fun getServicesForSubcategory(sub: String): List<LifestyleServiceItem> {
    return when (sub) {
        // EVENT PLANNER
        "Birthday Events" -> listOf(
            LifestyleServiceItem("ep_1", "Kids Birthday Setup", 5000.0, "Event Planner", "Birthday Events"),
            LifestyleServiceItem("ep_2", "Adult Birthday Setup", 7000.0, "Event Planner", "Birthday Events"),
            LifestyleServiceItem("ep_3", "Theme Decoration", 4000.0, "Event Planner", "Birthday Events")
        )
        "Wedding Events" -> listOf(
            LifestyleServiceItem("ep_4", "Wedding Planning", 50000.0, "Event Planner", "Wedding Events"),
            LifestyleServiceItem("ep_5", "Reception Decoration", 25000.0, "Event Planner", "Wedding Events"),
            LifestyleServiceItem("ep_6", "Stage Decoration", 15000.0, "Event Planner", "Wedding Events")
        )
        "Corporate Events" -> listOf(
            LifestyleServiceItem("ep_7", "Office Party Setup", 10000.0, "Event Planner", "Corporate Events"),
            LifestyleServiceItem("ep_8", "Product Launch Event", 20000.0, "Event Planner", "Corporate Events"),
            LifestyleServiceItem("ep_9", "Conference Setup", 15000.0, "Event Planner", "Corporate Events")
        )
        "Private Events" -> listOf(
            LifestyleServiceItem("ep_10", "House Party Setup", 6000.0, "Event Planner", "Private Events"),
            LifestyleServiceItem("ep_11", "Anniversary Setup", 8000.0, "Event Planner", "Private Events")
        )
        // PHOTOGRAPHER
        "Photography" -> listOf(
            LifestyleServiceItem("ph_1", "Birthday Photography", 3000.0, "Photographer", "Photography"),
            LifestyleServiceItem("ph_2", "Wedding Photography", 25000.0, "Photographer", "Photography"),
            LifestyleServiceItem("ph_3", "Event Photography", 8000.0, "Photographer", "Photography")
        )
        "Videography" -> listOf(
            LifestyleServiceItem("ph_4", "Event Videography", 10000.0, "Photographer", "Videography"),
            LifestyleServiceItem("ph_5", "Wedding Videography", 30000.0, "Photographer", "Videography")
        )
        "Editing Services" -> listOf(
            LifestyleServiceItem("ph_6", "Photo Editing", 1000.0, "Photographer", "Editing Services"),
            LifestyleServiceItem("ph_7", "Video Editing", 3000.0, "Photographer", "Editing Services")
        )
        "Drone" -> listOf(
            LifestyleServiceItem("ph_8", "Drone Shoot", 5000.0, "Photographer", "Drone"),
            LifestyleServiceItem("ph_9", "Aerial Video", 7000.0, "Photographer", "Drone")
        )
        // PERSONAL TRAINER
        "Fitness Training" -> listOf(
            LifestyleServiceItem("pt_1", "Weight Loss Program", 3000.0, "Personal Trainer", "Fitness Training"),
            LifestyleServiceItem("pt_2", "Muscle Gain Program", 3500.0, "Personal Trainer", "Fitness Training"),
            LifestyleServiceItem("pt_3", "Strength Training", 4000.0, "Personal Trainer", "Fitness Training")
        )
        "Yoga" -> listOf(
            LifestyleServiceItem("pt_4", "Home Yoga", 2500.0, "Personal Trainer", "Yoga"),
            LifestyleServiceItem("pt_5", "Online Yoga", 1500.0, "Personal Trainer", "Yoga")
        )
        "Diet Plans" -> listOf(
            LifestyleServiceItem("pt_6", "Weight Loss Diet", 1000.0, "Personal Trainer", "Diet Plans"),
            LifestyleServiceItem("pt_7", "Muscle Gain Diet", 1200.0, "Personal Trainer", "Diet Plans")
        )
        "Home Personal Trainer" -> listOf(
            LifestyleServiceItem("pt_8", "Monthly Personal Trainer", 6000.0, "Personal Trainer", "Home Personal Trainer")
        )
        // TRAVEL AGENT
        "Domestic Tours" -> listOf(
            LifestyleServiceItem("tr_1", "Goa Package", 8000.0, "Travel Agent", "Domestic Tours"),
            LifestyleServiceItem("tr_2", "Kerala Package", 12000.0, "Travel Agent", "Domestic Tours"),
            LifestyleServiceItem("tr_3", "Himachal Package", 15000.0, "Travel Agent", "Domestic Tours")
        )
        "International Tours" -> listOf(
            LifestyleServiceItem("tr_4", "Dubai Package", 45000.0, "Travel Agent", "International Tours"),
            LifestyleServiceItem("tr_5", "Singapore Package", 50000.0, "Travel Agent", "International Tours")
        )
        "Ticket Booking" -> listOf(
            LifestyleServiceItem("tr_6", "Flight Booking", 500.0, "Travel Agent", "Ticket Booking"),
            LifestyleServiceItem("tr_7", "Train Booking", 200.0, "Travel Agent", "Ticket Booking"),
            LifestyleServiceItem("tr_8", "Bus Booking", 100.0, "Travel Agent", "Ticket Booking")
        )
        "Hotel Booking" -> listOf(
            LifestyleServiceItem("tr_9", "Budget Hotel", 2000.0, "Travel Agent", "Hotel Booking"),
            LifestyleServiceItem("tr_10", "Luxury Hotel", 6000.0, "Travel Agent", "Hotel Booking")
        )
        // PET CARE
        "Pet Grooming" -> listOf(
            LifestyleServiceItem("pc_1", "Dog Grooming", 1500.0, "Pet Care", "Pet Grooming"),
            LifestyleServiceItem("pc_2", "Cat Grooming", 1200.0, "Pet Care", "Pet Grooming")
        )
        "Pet Walking" -> listOf(
            LifestyleServiceItem("pc_3", "Daily Walking", 200.0, "Pet Care", "Pet Walking"),
            LifestyleServiceItem("pc_4", "Monthly Plan", 4000.0, "Pet Care", "Pet Walking")
        )
        "Pet Sitting" -> listOf(
            LifestyleServiceItem("pc_5", "Home Pet Sitting", 500.0, "Pet Care", "Pet Sitting"),
            LifestyleServiceItem("pc_6", "Overnight Sitting", 800.0, "Pet Care", "Pet Sitting")
        )
        "Vet Services" -> listOf(
            LifestyleServiceItem("pc_7", "Basic Checkup", 500.0, "Pet Care", "Vet Services"),
            LifestyleServiceItem("pc_8", "Vaccination", 1000.0, "Pet Care", "Vet Services")
        )
        // GARDENING
        "Garden Setup" -> listOf(
            LifestyleServiceItem("gd_1", "Small Garden Setup", 3000.0, "Gardening", "Garden Setup"),
            LifestyleServiceItem("gd_2", "Terrace Garden", 7000.0, "Gardening", "Garden Setup")
        )
        "Maintenance" -> listOf(
            LifestyleServiceItem("gd_3", "Monthly Maintenance", 2000.0, "Gardening", "Maintenance"),
            LifestyleServiceItem("gd_4", "Weekly Maintenance", 800.0, "Gardening", "Maintenance")
        )
        "Plant Supply" -> listOf(
            LifestyleServiceItem("gd_5", "Indoor Plants", 300.0, "Gardening", "Plant Supply"),
            LifestyleServiceItem("gd_6", "Outdoor Plants", 200.0, "Gardening", "Plant Supply")
        )
        "Lawn Services" -> listOf(
            LifestyleServiceItem("gd_7", "Lawn Cutting", 500.0, "Gardening", "Lawn Services"),
            LifestyleServiceItem("gd_8", "Lawn Design", 4000.0, "Gardening", "Lawn Services")
        )
        else -> emptyList()
    }
}
