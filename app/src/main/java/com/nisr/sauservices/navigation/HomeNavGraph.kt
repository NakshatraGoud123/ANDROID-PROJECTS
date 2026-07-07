package com.nisr.sauservices.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.home.*
import com.nisr.sauservices.ui.pls.*
import com.nisr.sauservices.ui.dashboard.CustomerHomeScreen
import com.nisr.sauservices.ui.viewmodel.BookingsViewModel
import com.nisr.sauservices.ui.viewmodel.ResidentialViewModel
import com.nisr.sauservices.ui.viewmodels.PropertyLifestyleViewModel

fun NavGraphBuilder.homeNavGraph(
    navController: NavController,
    sessionManager: SessionManager,
    bookingsViewModel: BookingsViewModel,
    residentialViewModel: ResidentialViewModel
) {
    composable(Routes.HOME) {
        // App is now customer only, but we check if logged in
        if (sessionManager.isLoggedIn()) {
            CustomerHomeScreen(navController, sessionManager)
        } else {
            LaunchedEffect(Unit) {
                navController.navigate(RouteHelper.login("customer")) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
            }
        }
    }

    composable(Routes.CATEGORIES) {
        CategoriesScreen(navController)
    }

    composable(
        route = Routes.SEARCH_RESULTS,
        arguments = listOf(navArgument("query") { type = NavType.StringType })
    ) { backStackEntry ->
        val query = backStackEntry.arguments?.getString("query") ?: ""
        SearchResultsScreen(navController, query, residentialViewModel)
    }
    
    // Partner Dashboards removed as the app is now customer-only

    // Property & Lifestyle Services (PLS)
    composable(Routes.PLS_MAIN) { PLSMainScreen(navController) }
    
    composable(
        route = Routes.PLS_SUBCATEGORIES,
        arguments = listOf(navArgument("category") { type = NavType.StringType })
    ) { backStackEntry ->
        val category = backStackEntry.arguments?.getString("category") ?: ""
        PLSSubcategoriesScreen(navController, category)
    }

    composable(
        route = Routes.PLS_SERVICES,
        arguments = listOf(navArgument("subcategory") { type = NavType.StringType })
    ) { backStackEntry ->
        val subcategory = backStackEntry.arguments?.getString("subcategory") ?: ""
        PLSServicesScreen(navController, subcategory, viewModel())
    }

    // Shared ViewModel for PLS booking flow
    composable(
        route = Routes.PLS_BOOKING,
        arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
    ) { backStackEntry ->
        val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(Routes.PLS_MAIN)
        }
        val plsViewModel: PropertyLifestyleViewModel = viewModel(parentEntry)
        PLSBookingScreen(navController, serviceId, plsViewModel, sessionManager)
    }

    composable(Routes.PLS_CHECKOUT) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(Routes.PLS_MAIN)
        }
        val plsViewModel: PropertyLifestyleViewModel = viewModel(parentEntry)
        PLSCheckoutScreen(navController, plsViewModel)
    }

    composable(Routes.PLS_SUCCESS) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(Routes.PLS_MAIN)
        }
        val plsViewModel: PropertyLifestyleViewModel = viewModel(parentEntry)
        PLSSuccessScreen(navController, plsViewModel)
    }

    // Admin Panel and Worker Side removed as the app is now customer-only
}
