package com.nisr.sauservices.ui.healthcare

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen

data class HealthCategory(val name: String, val icon: String, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthcareCategoryScreen(navController: NavController) {
    val categories = listOf(
        HealthCategory("Lab Tests & Diagnostics", "🔬", Color(0xFFE3F2FD)),
        HealthCategory("Doctor Consultation", "👨‍⚕️", Color(0xFFE8F5E9)),
        HealthCategory("Pharmacy & Medicines", "💊", Color(0xFFFFF3E0)),
        HealthCategory("Home Healthcare Services", "🏠", Color(0xFFF3E5F5))
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Healthcare & Pharmacy", fontWeight = FontWeight.Bold, color = Color(0xFF1976D2)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF1976D2))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FBFF)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2))
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Healthcare", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("Medicines, lab tests & doctor care at your doorstep", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                    Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                }
            }

            Text("What are you looking for?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.padding(bottom = 16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    HealthCategoryCard(category) {
                        navController.navigate(Screen.HealthcareSubcategories.createRoute(category.name))
                    }
                }
            }
        }
    }
}

@Composable
fun HealthCategoryCard(category: HealthCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(140.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = category.color),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Text(category.icon, fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(category.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = Color(0xFF0D47A1))
        }
    }
}
