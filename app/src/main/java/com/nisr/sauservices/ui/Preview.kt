package com.nisr.sauservices.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.nisr.sauservices.ui.home.HomeScreen
import com.nisr.sauservices.ui.theme.AppTheme

@Preview(showBackground = true)
@Composable
fun PreviewHome(){
    val navController = rememberNavController()
    AppTheme {
        HomeScreen(navController)
    }
}