package com.nisr.sauservices.ui.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nisr.sauservices.R

data class OnboardingItem(
    val title: String,
    val description: String,
    @DrawableRes val image: Int
) {
    companion object {
        val items = listOf(
            OnboardingItem(
                title = "Welcome to SAU Services",
                description = "All your daily services in one place. Fast, easy, reliable.",
                image = R.drawable.drawable_illustration1
            ),
            OnboardingItem(
                title = "Everything You Need",
                description = "Book trusted professionals for home, personal, and local services instantly.",
                image = R.drawable.drawable_illustration2
            ),
            OnboardingItem(
                title = "Quick & Secure Booking",
                description = "Schedule services in seconds with safe payments and real-time tracking.",
                image = R.drawable.drawable_illustration3
            )
        )
    }
}

@Composable
fun OnboardingItemView(item: OnboardingItem) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = item.image),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = item.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = item.description,
            style = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = 24.sp,
                fontSize = 16.sp
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}
