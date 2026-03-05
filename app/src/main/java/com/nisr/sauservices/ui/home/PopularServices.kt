package com.nisr.sauservices.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.R
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary

data class PopularService(
    val id: String,
    val name: String,
    val imageRes: Int,
    val price: String,
    val rating: String,
    val categoryId: String = "ac_repair",
    val subcategoryId: String = "ac_service"
)

@Composable
fun PopularServicesSection(navController: NavController) {
    val list = listOf(
        PopularService("ac1", "AC Repair", R.drawable.ac_repair, "₹499", "4.6", "ac_repair", "ac_service"),
        PopularService("hc2", "Bathroom Cleaning", R.drawable.bathroom_cleaning, "₹399", "4.8", "home_cleaning", "clean_room")
    )

    Column(modifier = Modifier.padding(vertical = 20.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Popular Near You",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            
            // Pager indicator dots (dummy)
            Row {
                Box(Modifier.size(6.dp).clip(RoundedCornerShape(50)).background(PinkPrimary))
                Spacer(Modifier.width(4.dp))
                Box(Modifier.size(6.dp).clip(RoundedCornerShape(50)).background(Color.LightGray))
                Spacer(Modifier.width(4.dp))
                Box(Modifier.size(6.dp).clip(RoundedCornerShape(50)).background(Color.LightGray))
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(list) { item ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.width(260.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        Image(
                            painter = painterResource(item.imageRes),
                            contentDescription = item.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    item.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    item.price,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.Black
                                )
                            }
                            
                            Spacer(Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Star,
                                        null,
                                        tint = Color(0xFFFFB300),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        item.rating,
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }
                                
                                Button(
                                    onClick = {
                                        navController.navigate(Screen.ResidentialServiceList.createRoute(item.categoryId, item.subcategoryId))
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp).width(90.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Book Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
