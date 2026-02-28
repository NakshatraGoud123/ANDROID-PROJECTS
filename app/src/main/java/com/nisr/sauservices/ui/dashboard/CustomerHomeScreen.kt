package com.nisr.sauservices.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.home.*

@Composable
fun CustomerHomeScreen(navController: NavController, sessionManager: SessionManager) {
    Scaffold(
        topBar = { TopAppBarUI() },
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            HeroBanner()
            
            SearchBarUI()

            QuickServicesRow()

            Spacer(Modifier.height(24.dp))
            
            // Pass navController to handle "More Services" click
            CategoriesGrid(navController)

            Spacer(Modifier.height(24.dp))
            
            ValuePropositionsRow()

            PopularServicesSection()

            HowItWorks()

            OfferBanner()
            
            Spacer(Modifier.height(20.dp))
        }
    }
}
