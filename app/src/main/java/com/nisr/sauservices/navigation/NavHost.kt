
package com.nisr.sauservices.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.nisr.sauservices.ui.food.*
import com.nisr.sauservices.ui.education.*
import com.nisr.sauservices.ui.business.*
import com.nisr.sauservices.ui.lifestyle.*
import com.nisr.sauservices.ui.tech.*
import com.nisr.sauservices.ui.mens.*
import com.nisr.sauservices.ui.womens.*
import com.nisr.sauservices.ui.healthcare.*
import com.nisr.sauservices.ui.onboarding.OnboardingScreen
import com.nisr.sauservices.ui.profile.*

@Composable
fun AppNavHost(navController: NavHostController) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val userRepository = UserRepository()
    
    // Global ViewModels
    val cartViewModel: CartViewModel = viewModel()
    val residentialViewModel: ResidentialViewModel = viewModel()
    val foodCartViewModel: FoodCartViewModel = viewModel()
    val educationCartViewModel: EducationCartViewModel = viewModel()
    val businessViewModel: BusinessViewModel = viewModel()
    val lifestyleViewModel: LifestyleViewModel = viewModel()
    val techViewModel: TechServicesViewModel = viewModel()
    val mensGroomingViewModel: MensGroomingViewModel = viewModel()
    val womensBeautyViewModel: WomensBeautyViewModel = viewModel()
    val healthViewModel: HealthcareViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val bookingsViewModel: BookingsViewModel = viewModel()

    NavHost(navController, startDestination = Screen.Onboarding.route) {
        
        // --- ONBOARDING & AUTH ---
        composable(Screen.Onboarding.route) { OnboardingScreen(navController) }
        composable(Screen.RoleSelection.route) { RoleSelectionScreen(navController) }
        
        composable(
            route = Screen.AuthOptions.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "customer"
            AuthOptionsScreen(navController, role)
        }
        
        composable(
            route = Screen.Login.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "customer"
            SignInScreen(navController, role) 
        }
        
        composable(
            route = Screen.SignUp.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "customer"
            SignUpScreen(navController, role)
        }
        
        composable(Screen.Register.route) { CustomerSignUpScreen(navController) }
        composable(Screen.ForgotPassword.route) { ForgotPasswordScreen(navController) }
        
        // --- DASHBOARDS ---
        composable(Screen.ShopkeeperRegister.route) { ShopkeeperRegisterScreen(navController, userRepository) }
        composable(Screen.ServiceWorkerRegister.route) { ServiceWorkerRegisterScreen(navController, userRepository) }
        composable(Screen.DeliveryPartnerRegister.route) { DeliveryPartnerRegisterScreen(navController, userRepository) }
        
        composable(Screen.ShopkeeperDashboard.route) { ShopkeeperDashboardScreen(navController, sessionManager, viewModel()) }
        composable(Screen.ServiceWorkerDashboard.route) { ServiceWorkerDashboardScreen(navController, sessionManager, viewModel()) }
        composable(Screen.DeliveryDashboard.route) { DeliveryDashboardScreen(navController, sessionManager, viewModel()) }

        // --- CUSTOMER MAIN ---
        composable(Screen.Home.route) { 
            val role = sessionManager.getUserRole()
            if (role == "customer" && sessionManager.isLoggedIn()) {
                CustomerHomeScreen(navController, sessionManager, bookingsViewModel, residentialViewModel)
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.createRoute("customer")) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            }
        }

        composable(Screen.Categories.route) { CategoriesScreen(navController) }
        composable(Screen.Bookings.route) { BookingsScreen(navController, bookingsViewModel) }
        
        // UNIFIED CART
        composable(Screen.Cart.route) { 
            UnifiedCartScreen(
                navController = navController,
                residentialViewModel = residentialViewModel,
                businessViewModel = businessViewModel,
                lifestyleViewModel = lifestyleViewModel,
                techViewModel = techViewModel,
                mensGroomingViewModel = mensGroomingViewModel,
                womensBeautyViewModel = womensBeautyViewModel,
                healthcareViewModel = healthViewModel,
                foodCartViewModel = foodCartViewModel,
                homeCartViewModel = cartViewModel
            ) 
        }
        
        // --- PROFILE SYSTEM ---
        composable(Screen.Profile.route) { ProfileScreen(navController, profileViewModel) }
        composable(Screen.EditProfile.route) { EditProfileScreen(navController, profileViewModel) }
        composable(Screen.Notifications.route) { NotificationsScreen(navController, profileViewModel) }
        composable(Screen.ShippingAddress.route) { ShippingAddressScreen(navController, profileViewModel) }
        composable(Screen.ChangePassword.route) { ChangePasswordScreen(navController) }
        composable(Screen.AddAccounts.route) { AddAccountsScreen(navController) }
        composable(Screen.ContactUs.route) { ContactUsScreen(navController, profileViewModel) }
        composable(Screen.FAQ.route) { FAQScreen(navController) }

        // --- HOME ESSENTIALS ---
        composable(Screen.HomeEssentialsMain.route) { HomeEssentialsMainScreen(navController, cartViewModel) }
        composable(
            route = Screen.HomeEssentialsCategory.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("categoryId") ?: ""
            HomeEssentialsCategoryScreen(navController, id)
        }
        composable(
            route = Screen.HomeEssentialsItems.route,
            arguments = listOf(navArgument("subcategoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("subcategoryId") ?: ""
            HomeEssentialsItemsScreen(navController, id, cartViewModel)
        }
        composable(Screen.HomeEssentialsCart.route) { HomeEssentialsCartScreen(navController, cartViewModel) }
        composable(Screen.HomeEssentialsCheckout.route) { HomeEssentialsCheckoutScreen(navController, cartViewModel) }
        composable(Screen.HomeEssentialsSuccess.route) { HomeEssentialsSuccessScreen(navController, cartViewModel, bookingsViewModel) }

        // --- FOOD & BEVERAGES ---
        composable(Screen.FoodCategories.route) { FoodMainScreen(navController) }
        composable(
            route = Screen.FoodSubcategories.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val cat = backStackEntry.arguments?.getString("category") ?: ""
            FoodSubCategoryScreen(navController, cat)
        }
        composable(
            route = Screen.FoodTypes.route,
            arguments = listOf(navArgument("subcategory") { type = NavType.StringType })
        ) { backStackEntry ->
            val sub = backStackEntry.arguments?.getString("subcategory") ?: ""
            FoodTypeScreen(navController, sub)
        }
        composable(
            route = Screen.FoodItems.route,
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: ""
            FoodItemsScreen(navController, type, foodCartViewModel)
        }
        composable(Screen.FoodCart.route) { FoodCartScreen(navController, foodCartViewModel) }
        composable(Screen.FoodCheckout.route) { FoodCheckoutScreen(navController, foodCartViewModel) }
        composable(
            route = Screen.FoodBooking.route,
            arguments = listOf(navArgument("service") { type = NavType.StringType })
        ) { backStackEntry ->
            val srv = backStackEntry.arguments?.getString("service") ?: ""
            BookingScreen(navController, srv)
        }
        composable(Screen.FoodOrderSuccess.route) { FoodSuccessScreen(navController, bookingsViewModel) }

        // --- UNIFIED SERVICES FLOW ---
        
        // Residential
        composable(Screen.ResidentialCategories.route) { ResidentialCategoryScreen(navController) }
        composable(
            route = Screen.ResidentialSubcategories.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("categoryId") ?: ""
            ResidentialSubcategoryScreen(navController, id)
        }
        composable(
            route = Screen.ResidentialServiceList.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType },
                navArgument("subcategoryId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cat = backStackEntry.arguments?.getString("categoryId") ?: ""
            val sub = backStackEntry.arguments?.getString("subcategoryId") ?: ""
            ResidentialServiceListScreen(navController, cat, sub, residentialViewModel)
        }
        
        // Business
        composable(
            route = Screen.BusinessSubCategory.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val cat = backStackEntry.arguments?.getString("category") ?: ""
            BusinessSubCategoryScreen(navController, cat)
        }
        composable(
            route = Screen.BusinessServices.route,
            arguments = listOf(navArgument("subcategory") { type = NavType.StringType })
        ) { backStackEntry ->
            val sub = backStackEntry.arguments?.getString("subcategory") ?: ""
            BusinessServicesScreen(navController, sub, businessViewModel)
        }
        
        // Lifestyle
        composable(
            route = Screen.LifestyleSubCategory.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val cat = backStackEntry.arguments?.getString("category") ?: ""
            LifestyleSubCategoryScreen(navController, cat)
        }
        composable(
            route = Screen.LifestyleServices.route,
            arguments = listOf(navArgument("subcategory") { type = NavType.StringType })
        ) { backStackEntry ->
            val sub = backStackEntry.arguments?.getString("subcategory") ?: ""
            LifestyleServicesScreen(navController, sub, lifestyleViewModel)
        }
        
        // Tech
        composable(
            route = Screen.TechSubCategory.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val cat = backStackEntry.arguments?.getString("category") ?: ""
            TechSubCategoryScreen(navController, cat)
        }
        composable(
            route = Screen.TechServices.route,
            arguments = listOf(navArgument("subcategory") { type = NavType.StringType })
        ) { backStackEntry ->
            val sub = backStackEntry.arguments?.getString("subcategory") ?: ""
            TechServiceListScreen(navController, sub, techViewModel)
        }
        
        // Mens
        composable(Screen.MensCategories.route) { MensGroomingCategoryScreen(navController) }
        composable(
            route = Screen.MensSubcategories.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val cat = backStackEntry.arguments?.getString("category") ?: ""
            MensSubcategoryScreen(navController, cat)
        }
        composable(
            route = Screen.MensServices.route,
            arguments = listOf(navArgument("subcategory") { type = NavType.StringType })
        ) { backStackEntry ->
            val sub = backStackEntry.arguments?.getString("subcategory") ?: ""
            MensServiceListScreen(navController, sub, mensGroomingViewModel)
        }
        
        // Womens Beauty
        composable(Screen.WomensBeautyCategories.route) { WomensBeautyCategoryScreen(navController) }
        composable(
            route = Screen.WomensBeautySubcategories.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val cat = backStackEntry.arguments?.getString("category") ?: ""
            WomensBeautySubcategoryScreen(navController, cat)
        }
        composable(
            route = Screen.WomensBeautyServices.route,
            arguments = listOf(navArgument("subcategory") { type = NavType.StringType })
        ) { backStackEntry ->
            val sub = backStackEntry.arguments?.getString("subcategory") ?: ""
            BeautyServiceListScreen(navController, sub, womensBeautyViewModel)
        }
        composable(Screen.WomensBeautyBooking.route) { BeautyBookingScreen(navController, womensBeautyViewModel) }
        
        // Healthcare
        composable(Screen.HealthcareCategories.route) { HealthcareCategoryScreen(navController) }
        composable(
            route = Screen.HealthcareSubcategories.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val cat = backStackEntry.arguments?.getString("category") ?: ""
            HealthcareSubcategoryScreen(navController, cat)
        }
        composable(
            route = Screen.HealthcareServices.route,
            arguments = listOf(navArgument("subcategory") { type = NavType.StringType })
        ) { backStackEntry ->
            val sub = backStackEntry.arguments?.getString("subcategory") ?: ""
            HealthcareServiceListScreen(navController, sub, healthViewModel)
        }
        composable(Screen.HealthcareBooking.route) { HealthcareBookingScreen(navController, healthViewModel) }

        // Shared Checkout System for Unified Services
        composable(Screen.ResidentialBookingDetails.route) { ResidentialBookingDetailsScreen(navController, residentialViewModel) }
        composable(Screen.ResidentialPayment.route) { ResidentialPaymentScreen(navController, residentialViewModel) }
        composable(Screen.ResidentialOrderSummary.route) { 
            ResidentialOrderSummaryScreen(
                navController = navController, 
                viewModel = residentialViewModel, 
                bookingsViewModel = bookingsViewModel, 
                businessViewModel = businessViewModel, 
                lifestyleViewModel = lifestyleViewModel, 
                techViewModel = techViewModel, 
                mensGroomingViewModel = mensGroomingViewModel, 
                womensBeautyViewModel = womensBeautyViewModel, 
                healthcareViewModel = healthViewModel
            ) 
        }
        composable(Screen.BookingSuccess.route) { 
            com.nisr.sauservices.ui.home.BookingSuccessScreen(navController) 
        }

        // --- EDUCATION ---
        composable(
            route = Screen.EducationSubCategory.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val cat = backStackEntry.arguments?.getString("category") ?: ""
            EducationSubCategoryScreen(navController, cat)
        }
        composable(
            route = Screen.EducationCourses.route,
            arguments = listOf(navArgument("subcategory") { type = NavType.StringType })
        ) { backStackEntry ->
            val sub = backStackEntry.arguments?.getString("subcategory") ?: ""
            EducationCoursesScreen(navController, sub, educationCartViewModel)
        }
        composable(Screen.EducationCart.route) { EducationCartScreen(navController, educationCartViewModel) }
        composable(Screen.EducationBooking.route) { TutorBookingScreen(navController, educationCartViewModel) }
        composable(Screen.EducationSuccess.route) { EducationSuccessScreen(navController) }
        
        // --- REDIRECTS TO UNIFIED CART ---
        composable(Screen.ResidentialCart.route) { navController.navigate(Screen.Cart.route) { popUpTo(Screen.Cart.route) { inclusive = true } } }
        composable(Screen.BusinessCart.route) { navController.navigate(Screen.Cart.route) { popUpTo(Screen.Cart.route) { inclusive = true } } }
        composable(Screen.LifestyleCart.route) { navController.navigate(Screen.Cart.route) { popUpTo(Screen.Cart.route) { inclusive = true } } }
        composable(Screen.TechCart.route) { navController.navigate(Screen.Cart.route) { popUpTo(Screen.Cart.route) { inclusive = true } } }
        composable(Screen.MensCart.route) { navController.navigate(Screen.Cart.route) { popUpTo(Screen.Cart.route) { inclusive = true } } }
        composable(Screen.WomensBeautyCart.route) { navController.navigate(Screen.Cart.route) { popUpTo(Screen.Cart.route) { inclusive = true } } }
        composable(Screen.HealthcareCart.route) { navController.navigate(Screen.Cart.route) { popUpTo(Screen.Cart.route) { inclusive = true } } }

        // --- SEARCH ---
        composable(
            route = "search_results/{query}",
            arguments = listOf(navArgument("query") { type = NavType.StringType })
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            SearchResultsScreen(navController, query, residentialViewModel)
        }
        
        // --- SUCCESS SCREENS ---
        composable(Screen.BusinessSuccess.route) { BusinessSuccessScreen(navController, businessViewModel) }
        composable(Screen.LifestyleSuccess.route) { LifestyleSuccessScreen(navController, lifestyleViewModel) }
        composable(Screen.TechSuccess.route) { TechBookingSuccessScreen(navController, techViewModel) }
        composable(Screen.MensSuccess.route) { MensSuccessScreen(navController, mensGroomingViewModel) }
        composable(Screen.WomensBeautySuccess.route) { BeautyBookingSuccessScreen(navController, womensBeautyViewModel) }
        composable(Screen.HealthcareSuccess.route) { HealthcareSuccessScreen(navController, healthViewModel) }
    }
}
