package com.nisr.sauservices.ui.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nisr.sauservices.ui.theme.PinkPrimary

@Composable
fun OnboardingIndicator(
    count: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(count) { index ->
            val isSelected = currentPage == index
            val width = animateDpAsState(
                targetValue = if (isSelected) 32.dp else 10.dp,
                animationSpec = tween(durationMillis = 300),
                label = "width"
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(10.dp)
                    .width(width.value)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) PinkPrimary else Color.LightGray.copy(alpha = 0.5f)
                    )
            )
        }
    }
}
