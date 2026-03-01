package com.nisr.sauservices.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.essentials.HomeEssentialsSheetContent
import com.nisr.sauservices.ui.education.EducationBottomSheet
import com.nisr.sauservices.ui.business.BusinessBottomSheet
import com.nisr.sauservices.ui.lifestyle.LifestyleBottomSheet
import com.nisr.sauservices.ui.tech.TechBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(navController: NavController) {
    var showHomeEssentialsSheet by remember { mutableStateOf(false) }
    var showEduSheet by remember { mutableStateOf(false) }
    var showBizSheet by remember { mutableStateOf(false) }
    var showLifeSheet by remember { mutableStateOf(false) }
    var showTechSheet by remember { mutableStateOf(false) }

    if (showHomeEssentialsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showHomeEssentialsSheet = false }
        ) {
            HomeEssentialsSheetContent(navController) {
                showHomeEssentialsSheet = false
            }
        }
    }

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
            Text(
                "All Categories",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Show all categories in the grid
            CategoriesGrid(
                navController = navController, 
                showAll = true,
                onHomeEssentialsClick = {
                    showHomeEssentialsSheet = true
                },
                onEducationClick = {
                    showEduSheet = true
                },
                onBusinessClick = {
                    showBizSheet = true
                },
                onLifestyleClick = {
                    showLifeSheet = true
                },
                onTechClick = {
                    showTechSheet = true
                }
            )
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
