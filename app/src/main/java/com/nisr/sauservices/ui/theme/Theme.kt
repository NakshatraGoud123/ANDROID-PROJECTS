package com.nisr.sauservices.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppTheme(content:@Composable ()->Unit){

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFFE91E63),
            secondary = Color(0xFFFF9800),
            background = Color(0xFFF7F7F7)
        ),
        typography = Typography(),
        content = content
    )
}