package com.nisr.sauservices.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.home.*
import com.nisr.sauservices.ui.education.EducationBottomSheet
import com.nisr.sauservices.ui.business.BusinessBottomSheet
import com.nisr.sauservices.ui.lifestyle.LifestyleBottomSheet
import com.nisr.sauservices.ui.tech.TechBottomSheet
import com.nisr.sauservices.ui.viewmodel.BookingsViewModel
import com.nisr.sauservices.ui.viewmodel.ResidentialViewModel

@Composable
fun CustomerHomeScreen(
    navController: NavController, 
    sessionManager: SessionManager,
    bookingsViewModel: BookingsViewModel,
    residentialViewModel: ResidentialViewModel
) {
    var showEduSheet by remember { mutableStateOf(false) }
    var showBizSheet by remember { mutableStateOf(false) }
    var showLifeSheet by remember { mutableStateOf(false) }
    var showTechSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBarUI(navController, sessionManager) },
        bottomBar = { BottomNavBar(navController, residentialViewModel = residentialViewModel) },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            HeroBanner(navController)
            
            SearchBarUI(navController)

            QuickServicesRow()

            Spacer(Modifier.height(24.dp))
            
            CategoriesGrid(
                navController = navController,
                onEducationClick = { showEduSheet = true },
                onBusinessClick = { showBizSheet = true },
                onLifestyleClick = { showLifeSheet = true },
                onTechClick = { showTechSheet = true }
            )

            Spacer(Modifier.height(24.dp))
            
            ValuePropositionsRow()

            PopularServicesSection(navController)

            HowItWorks()

            OfferBanner()
            
            Spacer(Modifier.height(20.dp))
        }
    }

    if (showEduSheet) {
        EducationBottomSheet(
            navController = navController,
            onDismiss = { showEduSheet = false }
        )
    }

    if (showBizSheet) {
        BusinessBottomSheet(
            navController = navController,
            onDismiss = { showBizSheet = false }
        )
    }

    if (showLifeSheet) {
        LifestyleBottomSheet(
            navController = navController,
            onDismiss = { showLifeSheet = false }
        )
    }

    if (showTechSheet) {
        TechBottomSheet(
            navController = navController,
            onDismiss = { showTechSheet = false }
        )
    }
}
