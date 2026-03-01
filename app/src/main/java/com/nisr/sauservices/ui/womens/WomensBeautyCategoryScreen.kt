package com.nisr.sauservices.ui.womens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

data class BeautyCategory(val name: String, val icon: String, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WomensBeautyCategoryScreen(navController: NavController) {
    val categories = listOf(
        BeautyCategory("Hair Services", "💇‍♀️", Color(0xFFFFE4E1)),
        BeautyCategory("Facial & Cleanup", "💆‍♀️", Color(0xFFFFF0F5)),
        BeautyCategory("Waxing", "🍯", Color(0xFFFFF5EE)),
        BeautyCategory("Threading & Face", "🧵", Color(0xFFF0FFF0)),
        BeautyCategory("Manicure & Pedicure", "💅", Color(0xFFF5F5DC)),
        BeautyCategory("Makeup & Styling", "💄", Color(0xFFE6E6FA)),
        BeautyCategory("Spa & Massage", "🧖‍♀️", Color(0xFFF0FFFF)),
        BeautyCategory("Bridal & Premium", "👰", Color(0xFFFFFACD))
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Women's Beauty", fontWeight = FontWeight.Bold, color = Color(0xFF880E4F)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF880E4F))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFFFFBFD)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text(
                "Select a Category",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    BeautyCategoryCard(category) {
                        navController.navigate(Screen.WomensBeautySubcategories.createRoute(category.name))
                    }
                }
            }
        }
    }
}

@Composable
fun BeautyCategoryCard(category: BeautyCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
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
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(category.icon, fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                category.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFF4A148C)
            )
        }
    }
}
