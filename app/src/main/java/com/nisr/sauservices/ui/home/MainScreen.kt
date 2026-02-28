package com.nisr.sauservices.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.essentials.HomeEssentialsSheetContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController){
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            HomeEssentialsSheetContent(navController) {
                showSheet = false
            }
        }
    }

    Scaffold(
        topBar = { TopAppBarUI() },
        bottomBar = { BottomNavBar(navController) }
    ){ pad ->

        Column(
            Modifier
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ){

            HeroBanner()
            SearchBarUI()

            Spacer(Modifier.height(16.dp))
            QuickServicesRow()

            Spacer(Modifier.height(20.dp))
            // Pass a callback to trigger the bottom sheet for "Home Essentials"
            CategoriesGrid(navController, onHomeEssentialsClick = {
                showSheet = true
            })

            Spacer(Modifier.height(20.dp))
            HowItWorks()

            Spacer(Modifier.height(20.dp))
            OfferBanner()
        }
    }
}
