package com.nisr.sauservices.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.dashboard.*
import com.nisr.sauservices.ui.home.*
import com.nisr.sauservices.ui.viewmodel.*

fun NavGraphBuilder.homeNavGraph(
    navController: NavController,
    sessionManager: SessionManager,
    bookingsViewModel: BookingsViewModel,
    residentialViewModel: ResidentialViewModel
) {
    composable(Routes.HOME) {
        val role = sessionManager.getUserRole()
        if (role == "customer" && sessionManager.isLoggedIn()) {
            CustomerHomeScreen(navController, sessionManager, bookingsViewModel, residentialViewModel)
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
    
    // Partner Dashboards
    composable(Routes.SHOPKEEPER_DASHBOARD) { ShopkeeperDashboardScreen(navController, sessionManager, viewModel<ShopkeeperViewModel>()) }
    composable(Routes.SERVICE_WORKER_DASHBOARD) { ServiceWorkerDashboardScreen(navController, sessionManager, viewModel<ServiceWorkerViewModel>()) }
    composable(Routes.DELIVERY_DASHBOARD) { DeliveryDashboardScreen(navController, sessionManager, viewModel<DeliveryViewModel>()) }
}
