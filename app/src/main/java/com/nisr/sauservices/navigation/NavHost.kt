package com.nisr.sauservices.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.data.repository.UserRepository
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.auth.*
import com.nisr.sauservices.ui.dashboard.*
import com.nisr.sauservices.ui.essentials.*
import com.nisr.sauservices.ui.home.*
import com.nisr.sauservices.ui.viewmodel.*

@Composable
fun AppNavHost(navController: NavHostController) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val userRepository = UserRepository()
    val cartViewModel: CartViewModel = viewModel()
    val residentialViewModel: ResidentialViewModel = viewModel()

    NavHost(navController, startDestination = Screen.RoleSelection.route) {
        // Core Auth & Dashboards
        composable(Screen.RoleSelection.route) { RoleSelectionScreen(navController) }
        composable(Screen.AuthOptions.route) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "customer"
            AuthOptionsScreen(navController, role)
        }
        composable(Screen.Login.route) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "customer"
            LoginScreen(navController, sessionManager, role)
        }
        composable(Screen.Register.route) { CustomerSignUpScreen(navController) }
        composable(Screen.ShopkeeperRegister.route) { ShopkeeperRegisterScreen(navController, userRepository) }
        composable(Screen.ServiceWorkerRegister.route) { ServiceWorkerRegisterScreen(navController, userRepository) }
        composable(Screen.DeliveryPartnerRegister.route) { DeliveryPartnerRegisterScreen(navController, userRepository) }
        composable(Screen.ShopkeeperDashboard.route) { ShopkeeperDashboardScreen(navController, sessionManager, viewModel()) }
        composable(Screen.ServiceWorkerDashboard.route) { ServiceWorkerDashboardScreen(navController, sessionManager, viewModel()) }
        composable(Screen.DeliveryDashboard.route) { DeliveryDashboardScreen(navController, sessionManager, viewModel(), viewModel()) }
        composable(Screen.ForgotPassword.route) { ForgotPasswordScreen(navController) }

        // Main Home & Categories
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Categories.route) { CategoriesScreen(navController) }
        
        // Home Essentials Flow
        composable(Screen.Cart.route) { CartScreen(navController, cartViewModel) }
        composable(Screen.Payment.route) { PaymentScreen(navController, cartViewModel) }
        composable(Screen.BookingSuccess.route) { BookingSuccessScreen(navController) }
        composable(Screen.ProductTypes.route) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            ProductTypeScreen(navController, categoryId)
        }
        composable(Screen.ProductList.route) { backStackEntry ->
            val typeId = backStackEntry.arguments?.getString("typeId") ?: ""
            ProductListScreen(navController, typeId, cartViewModel)
        }

        // Residential Services Flow
        composable(Screen.ResidentialCategories.route) { ResidentialCategoryScreen(navController) }
        composable(Screen.ResidentialSubcategories.route, arguments = listOf(navArgument("categoryId") { type = NavType.StringType })) { backStackEntry ->
            ResidentialSubcategoryScreen(navController, backStackEntry.arguments?.getString("categoryId") ?: "")
        }
        composable(Screen.ResidentialServiceList.route, arguments = listOf(
            navArgument("categoryId") { type = NavType.StringType },
            navArgument("subcategoryId") { type = NavType.StringType }
        )) { backStackEntry ->
            ResidentialServiceListScreen(
                navController = navController,
                categoryId = backStackEntry.arguments?.getString("categoryId") ?: "",
                subcategoryId = backStackEntry.arguments?.getString("subcategoryId") ?: "",
                viewModel = residentialViewModel
            )
        }
        composable(Screen.ResidentialCart.route) { ResidentialCartScreen(navController, residentialViewModel) }
        composable(Screen.ResidentialBookingDetails.route) { ResidentialBookingDetailsScreen(navController, residentialViewModel) }
        composable(Screen.ResidentialPayment.route) { ResidentialPaymentScreen(navController, residentialViewModel) }
        composable(Screen.ResidentialOrderSummary.route) { ResidentialOrderSummaryScreen(navController, residentialViewModel) }
    }
}
