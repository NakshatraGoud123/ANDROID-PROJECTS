package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nisr.sauservices.ui.theme.PinkPrimary

data class HowItWorksItem(val name: String, val icon: ImageVector)

@Composable
fun HowItWorks() {

    val list = listOf(
        HowItWorksItem("Choose Service", Icons.Outlined.EditCalendar),
        HowItWorksItem("Select Date & Time", Icons.Outlined.CalendarMonth),
        HowItWorksItem("Get Professional at Home", Icons.Outlined.CheckCircle)
    )

    Column(modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)) {
        Text(
            "How It Works",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.padding(start = 4.dp, bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Added spacing between items
        ) {
            list.forEach { item ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier
                            .size(75.dp) // Slightly larger
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFFFF0F5)), // Softer pink background
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            item.icon, 
                            null, 
                            tint = PinkPrimary, 
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    
                    Text(
                        item.name, 
                        fontSize = 11.sp, 
                        fontWeight = FontWeight.Bold, // Bolder for better visibility
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}
