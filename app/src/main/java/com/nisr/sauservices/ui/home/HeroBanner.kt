package com.nisr.sauservices.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.R
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary

@Composable
fun HeroBanner(navController: NavController) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE3F2FD))
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(Modifier.weight(1.2f)) {

            Text(
                "ALL SERVICES,\nONE APP",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                lineHeight = 26.sp,
                color = Color.Black
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Trusted professionals\nat your doorstep",
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate(Screen.ResidentialCategories.route) },
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Book a Service", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Image(
            painterResource(R.drawable.hero_technicians),
            null,
            modifier = Modifier
                .weight(1f)
                .size(120.dp)
        )
    }
}
