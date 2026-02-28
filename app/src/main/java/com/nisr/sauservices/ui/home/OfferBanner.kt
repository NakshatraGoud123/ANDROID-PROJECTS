package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nisr.sauservices.ui.theme.LightGreen

@Composable
fun OfferBanner(){

    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(LightGreen)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ){

        Icon(Icons.Outlined.CardGiftcard,null, tint = Color(0xFF4CAF50), modifier = Modifier.size(28.dp))

        Spacer(Modifier.width(16.dp))

        Column{
            Text("Flat 20% OFF on First Booking", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(2.dp))
            Text("Use Code: SAU20", fontSize = 12.sp, color = Color.DarkGray)
        }
    }
}