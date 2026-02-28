package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nisr.sauservices.ui.theme.PinkPrimary

@Composable
fun TopAppBarUI() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding() // Added status bars padding to bring it down
            .padding(top = 16.dp, bottom = 8.dp) // Increased top padding
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo and Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp) // Slightly larger
                        .clip(CircleShape)
                        .background(PinkPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("S", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }

                Spacer(Modifier.width(10.dp))

                Text(
                    "SAU",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.Black
                )
            }

            // Location Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFF0F0F0))
                    .padding(horizontal = 12.dp, vertical = 8.dp), // Increased vertical padding
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    null,
                    tint = PinkPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    "Hyderabad",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    null,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Action Icons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* TODO */ }, modifier = Modifier.size(40.dp)) {
                    Box {
                        Icon(Icons.Outlined.Notifications, null, tint = Color.DarkGray, modifier = Modifier.size(26.dp))
                        // Notification dot
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(PinkPrimary)
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                                .border(1.5.dp, Color.White, CircleShape)
                        )
                    }
                }
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = { /* TODO */ }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Outlined.Person, null, tint = Color.DarkGray, modifier = Modifier.size(26.dp))
                }
            }
        }
        
        Text(
            "Delivering to your location",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
        
        HorizontalDivider( // Updated to HorizontalDivider
            modifier = Modifier.padding(top = 16.dp),
            thickness = 0.5.dp,
            color = Color.LightGray.copy(alpha = 0.5f)
        )
    }
}
