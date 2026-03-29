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
import com.nisr.sauservices.ui.pls.*
import com.nisr.sauservices.ui.viewmodel.BookingsViewModel
import com.nisr.sauservices.ui.viewmodel.ResidentialViewModel
import com.nisr.sauservices.ui.viewmodel.ServiceWorkerViewModel
import com.nisr.sauservices.ui.viewmodel.DeliveryViewModel
import com.nisr.sauservices.ui.viewmodels.ShopkeeperViewModel
import com.nisr.sauservices.ui.viewmodels.PropertyLifestyleViewModel

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

    composable(
        route = Routes.PLS_BOOKING,
        arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
    ) { backStackEntry ->
        val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
        PLSBookingScreen(navController, serviceId, viewModel(), sessionManager)
    }

    composable(Routes.PLS_CHECKOUT) { PLSCheckoutScreen(navController, viewModel()) }
    composable(Routes.PLS_SUCCESS) { PLSSuccessScreen(navController, viewModel()) }

    // Admin Panel
    composable(Routes.ADMIN_DASHBOARD) { AdminDashboardScreen(navController, viewModel()) }
    composable(Routes.ADMIN_BOOKINGS) { AdminBookingsScreen(navController, viewModel()) }
    composable(Routes.ADMIN_SERVICES) { AdminServicesScreen(navController, viewModel()) }

    // Worker Side
    composable(Routes.WORKER_DASHBOARD) { WorkerDashboardScreen(navController, viewModel()) }
    composable(Routes.WORKER_JOBS) { WorkerJobsScreen(navController, viewModel()) }
}
