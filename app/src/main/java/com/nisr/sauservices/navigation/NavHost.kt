
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
import com.nisr.sauservices.ui.food.*
import com.nisr.sauservices.ui.education.*
import com.nisr.sauservices.ui.business.*
import com.nisr.sauservices.ui.lifestyle.*
import com.nisr.sauservices.ui.tech.*
import com.nisr.sauservices.ui.mens.*
import com.nisr.sauservices.ui.womens.*
import com.nisr.sauservices.ui.healthcare.*
import com.nisr.sauservices.ui.onboarding.OnboardingScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val userRepository = UserRepository()
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

    NavHost(navController, startDestination = Screen.Onboarding.route) {
        // Onboarding
        composable(Screen.Onboarding.route) { OnboardingScreen(navController) }
        
        // Core Auth & Dashboards
        composable(Screen.RoleSelection.route) { RoleSelectionScreen(navController) }
        composable(Screen.AuthOptions.route) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "customer"
            AuthOptionsScreen(navController, role)
        }
        composable(Screen.Login.route) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "customer"
            SignInScreen(navController, role) // Using the new SignInScreen
        }
        composable(Screen.SignUp.route) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "customer"
            SignUpScreen(navController, role)
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
        composable(Screen.Home.route) { CustomerHomeScreen(navController, sessionManager) }
        composable(Screen.Categories.route) { CategoriesScreen(navController) }
        
        // --- NEW Home Essentials Flow ---
        composable(Screen.HomeEssentialsMain.route) { 
            HomeEssentialsMainScreen(navController, cartViewModel) 
        }
        composable(Screen.HomeEssentialsCategory.route) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            HomeEssentialsCategoryScreen(navController, categoryId)
        }
        composable(Screen.HomeEssentialsItems.route) { backStackEntry ->
            val subcategoryId = backStackEntry.arguments?.getString("subcategoryId") ?: ""
            HomeEssentialsItemsScreen(navController, subcategoryId, cartViewModel)
        }
        composable(Screen.HomeEssentialsCart.route) { 
            HomeEssentialsCartScreen(navController, cartViewModel) 
        }
        composable(Screen.HomeEssentialsCheckout.route) { 
            HomeEssentialsCheckoutScreen(navController, cartViewModel) 
        }
        composable(Screen.HomeEssentialsSuccess.route) { 
            HomeEssentialsSuccessScreen(navController, cartViewModel)
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

        // Food & Beverages Flow
        composable("FOODS_categories") { FoodMainScreen(navController) }
        composable("FOODS_subcategories/{category}", arguments = listOf(navArgument("category") { type = NavType.StringType })) { backStackEntry ->
            FoodSubCategoryScreen(navController, backStackEntry.arguments?.getString("category") ?: "")
        }
        composable("FOODS_types/{subcategory}", arguments = listOf(navArgument("subcategory") { type = NavType.StringType })) { backStackEntry ->
            FoodTypeScreen(navController, backStackEntry.arguments?.getString("subcategory") ?: "")
        }
        composable("FOODS_items/{type}", arguments = listOf(navArgument("type") { type = NavType.StringType })) { backStackEntry ->
            FoodItemsScreen(navController, backStackEntry.arguments?.getString("type") ?: "", foodCartViewModel)
        }
        composable("FOODS_cart") { FoodCartScreen(navController, foodCartViewModel) }
        composable("FOODS_checkout") { FoodCheckoutScreen(navController, foodCartViewModel) }
        composable("FOODS_booking/{service}", arguments = listOf(navArgument("service") { type = NavType.StringType })) { backStackEntry ->
            BookingScreen(navController, backStackEntry.arguments?.getString("service") ?: "")
        }
        composable("FOODS_order_success") { FoodSuccessScreen(navController) }

        // Education Services Flow
        composable(Screen.EducationSubCategory.route) { backStackEntry ->
            EducationSubCategoryScreen(navController, backStackEntry.arguments?.getString("category") ?: "")
        }
        composable(Screen.EducationCourses.route) { backStackEntry ->
            EducationCoursesScreen(navController, backStackEntry.arguments?.getString("subcategory") ?: "", educationCartViewModel)
        }
        composable(Screen.EducationCart.route) { EducationCartScreen(navController, educationCartViewModel) }
        composable(Screen.EducationBooking.route) { TutorBookingScreen(navController, educationCartViewModel) }
        composable(Screen.EducationSuccess.route) { EducationSuccessScreen(navController) }

        // Business & Professional Services Flow
        composable(Screen.BusinessSubCategory.route) { backStackEntry ->
            BusinessSubCategoryScreen(navController, backStackEntry.arguments?.getString("category") ?: "")
        }
        composable(Screen.BusinessServices.route) { backStackEntry ->
            BusinessServicesScreen(navController, backStackEntry.arguments?.getString("subcategory") ?: "", businessViewModel)
        }
        composable(Screen.BusinessCart.route) { BusinessCartScreen(navController, businessViewModel) }
        composable(Screen.BusinessBooking.route) { BusinessBookingScreen(navController, businessViewModel) }
        composable(Screen.BusinessCheckout.route) { BusinessCheckoutScreen(navController, businessViewModel) }
        composable(Screen.BusinessPayment.route) { BusinessPaymentScreen(navController, businessViewModel) }
        composable(Screen.BusinessSuccess.route) { BusinessSuccessScreen(navController, businessViewModel) }

        // Home & Lifestyle Flow
        composable(Screen.LifestyleSubCategory.route) { backStackEntry ->
            LifestyleSubCategoryScreen(navController, backStackEntry.arguments?.getString("category") ?: "")
        }
        composable(Screen.LifestyleServices.route) { backStackEntry ->
            LifestyleServicesScreen(navController, backStackEntry.arguments?.getString("subcategory") ?: "", lifestyleViewModel)
        }
        composable(Screen.LifestyleCart.route) { LifestyleCartScreen(navController, lifestyleViewModel) }
        composable(Screen.LifestyleBooking.route) { LifestyleBookingScreen(navController, lifestyleViewModel) }
        composable(Screen.LifestyleCheckout.route) { LifestyleCheckoutScreen(navController, lifestyleViewModel) }
        composable(Screen.LifestylePayment.route) { LifestylePaymentScreen(navController, lifestyleViewModel) }
        composable(Screen.LifestyleSuccess.route) { LifestyleSuccessScreen(navController, lifestyleViewModel) }

        // Tech Services Flow
        composable(Screen.TechSubCategory.route) { backStackEntry ->
            TechSubCategoryScreen(navController, backStackEntry.arguments?.getString("category") ?: "")
        }
        composable(Screen.TechServices.route) { backStackEntry ->
            TechServiceListScreen(navController, backStackEntry.arguments?.getString("subcategory") ?: "", techViewModel)
        }
        composable(Screen.TechCart.route) { TechCartScreen(navController, techViewModel) }
        composable(Screen.TechBooking.route) { TechBookingScreen(navController, techViewModel) }
        composable(Screen.TechCheckout.route) { TechCheckoutScreen(navController, techViewModel) }
        composable(Screen.TechPayment.route) { TechPaymentScreen(navController, techViewModel) }
        composable(Screen.TechSuccess.route) { TechBookingSuccessScreen(navController, techViewModel) }

        // Men's Grooming Flow
        composable(Screen.MensCategories.route) { MensGroomingCategoryScreen(navController) }
        composable(Screen.MensSubcategories.route) { backStackEntry ->
            MensSubcategoryScreen(navController, backStackEntry.arguments?.getString("category") ?: "")
        }
        composable(Screen.MensServices.route) { backStackEntry ->
            MensServiceListScreen(navController, backStackEntry.arguments?.getString("subcategory") ?: "", mensGroomingViewModel)
        }
        composable(Screen.MensCart.route) { MensCartScreen(navController, mensGroomingViewModel) }
        composable(Screen.MensBooking.route) { MensBookingScreen(navController, mensGroomingViewModel) }
        composable(Screen.MensCheckout.route) { MensCheckoutScreen(navController, mensGroomingViewModel) }
        composable(Screen.MensSuccess.route) { MensSuccessScreen(navController, mensGroomingViewModel) }

        // Women's Beauty Flow
        composable(Screen.WomensBeautyCategories.route) { WomensBeautyCategoryScreen(navController) }
        composable(Screen.WomensBeautySubcategories.route) { backStackEntry ->
            WomensBeautySubcategoryScreen(navController, backStackEntry.arguments?.getString("category") ?: "")
        }
        composable(Screen.WomensBeautyServices.route) { backStackEntry ->
            BeautyServiceListScreen(navController, backStackEntry.arguments?.getString("subcategory") ?: "", womensBeautyViewModel)
        }
        composable(Screen.WomensBeautyCart.route) { BeautyCartScreen(navController, womensBeautyViewModel) }
        composable(Screen.WomensBeautyBooking.route) { BeautyBookingScreen(navController, womensBeautyViewModel) }
        composable(Screen.WomensBeautyPayment.route) { BeautyPaymentScreen(navController, womensBeautyViewModel) }
        composable(Screen.WomensBeautyOrderSummary.route) { BeautyOrderSummaryScreen(navController, womensBeautyViewModel) }
        composable(Screen.WomensBeautySuccess.route) { BeautyBookingSuccessScreen(navController, womensBeautyViewModel) }

        // Healthcare & Pharmacy Flow
        composable(Screen.HealthcareCategories.route) { HealthcareCategoryScreen(navController) }
        composable(Screen.HealthcareSubcategories.route) { backStackEntry ->
            HealthcareSubcategoryScreen(navController, backStackEntry.arguments?.getString("category") ?: "")
        }
        composable(Screen.HealthcareServices.route) { backStackEntry ->
            HealthcareServiceListScreen(navController, backStackEntry.arguments?.getString("subcategory") ?: "", healthViewModel)
        }
        composable(Screen.HealthcareCart.route) { HealthcareCartScreen(navController, healthViewModel) }
        composable(Screen.HealthcarePrescription.route) { PrescriptionUploadScreen(navController, healthViewModel) }
        composable(Screen.HealthcareBooking.route) { HealthcareBookingScreen(navController, healthViewModel) }
        composable(Screen.HealthcarePayment.route) { HealthcarePaymentScreen(navController, healthViewModel) }
        composable(Screen.HealthcareOrderSummary.route) { HealthcareOrderSummaryScreen(navController, healthViewModel) }
        composable(Screen.HealthcareOrderTracking.route) { HealthcareOrderTrackingScreen(navController, healthViewModel) }
        composable(Screen.HealthcareSuccess.route) { HealthcareSuccessScreen(navController, healthViewModel) }
    }
}
