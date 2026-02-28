package com.nisr.sauservices.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.home.*
import com.nisr.sauservices.ui.theme.PinkPrimary

@Composable
fun HomeScreen(navController: NavController){

    Scaffold(
        topBar = { TopAppBarUI() },
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color.White
    ){ pad ->

        Column(
            Modifier
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ){

            HeroBanner()
            
            SearchBarUI()

            QuickServicesRow()

            Spacer(Modifier.height(24.dp))
            
            // Service Categories Grid
            CategoriesGrid(navController)

            Spacer(Modifier.height(24.dp))
            
            // Value Propositions (Verified Professionals, etc.)
            ValuePropositionsRow()

            PopularServicesSection()

            HowItWorks()

            OfferBanner()
            
            Spacer(Modifier.height(20.dp))
        }
    }
}
