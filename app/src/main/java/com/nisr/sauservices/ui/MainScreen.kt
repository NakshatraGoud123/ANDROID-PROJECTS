package com.nisr.sauservices.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.home.*

@Composable
fun HomeScreen(navController: NavController){
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    Scaffold(
        topBar = { TopAppBarUI(navController, sessionManager) },
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color.White
    ){ pad ->

        Column(
            Modifier
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ){

            HeroBanner(navController)
            
            SearchBarUI(navController)

            QuickServicesRow()

            Spacer(Modifier.height(24.dp))
            
            // Service Categories Grid
            CategoriesGrid(navController = navController)

            Spacer(Modifier.height(24.dp))
            
            // Value Propositions (Verified Professionals, etc.)
            ValuePropositionsRow()

            PopularServicesSection(navController)

            HowItWorks()

            OfferBanner()
            
            Spacer(Modifier.height(20.dp))
        }
    }
}
