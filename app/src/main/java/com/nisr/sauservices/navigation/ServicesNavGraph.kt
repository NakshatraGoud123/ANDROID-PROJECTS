package com.nisr.sauservices.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nisr.sauservices.ui.business.*
import com.nisr.sauservices.ui.education.*
import com.nisr.sauservices.ui.healthcare.*
import com.nisr.sauservices.ui.home.*
import com.nisr.sauservices.ui.lifestyle.*
import com.nisr.sauservices.ui.mens.*
import com.nisr.sauservices.ui.tech.*
import com.nisr.sauservices.ui.viewmodel.*
import com.nisr.sauservices.ui.womens.*

fun NavGraphBuilder.servicesNavGraph(
    navController: NavController,
    residentialViewModel: ResidentialViewModel,
    businessViewModel: BusinessViewModel,
    lifestyleViewModel: LifestyleViewModel,
    techViewModel: TechServicesViewModel,
    mensGroomingViewModel: MensGroomingViewModel,
    womensBeautyViewModel: WomensBeautyViewModel,
    healthViewModel: HealthcareViewModel
) {
    // --- RESIDENTIAL SERVICES ---
    composable(Routes.RESIDENTIAL_CATEGORIES) {
        ResidentialCategoryScreen(navController)
    }
    composable(
        route = Routes.RESIDENTIAL_SUBCATEGORIES,
        arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
    ) { backStackEntry ->
        val id = backStackEntry.arguments?.getString("categoryId") ?: ""
        ResidentialSubcategoryScreen(navController, id)
    }
    composable(
        route = Routes.RESIDENTIAL_SERVICES,
        arguments = listOf(
            navArgument("categoryId") { type = NavType.StringType },
            navArgument("subcategoryId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val cat = backStackEntry.arguments?.getString("categoryId") ?: ""
        val sub = backStackEntry.arguments?.getString("subcategoryId") ?: ""
        ResidentialServiceListScreen(navController, cat, sub, residentialViewModel)
    }

    // --- BUSINESS SERVICES ---
    composable(
        route = Routes.BUSINESS_SUBCATEGORY,
        arguments = listOf(navArgument("category") { type = NavType.StringType })
    ) { backStackEntry ->
        val cat = backStackEntry.arguments?.getString("category") ?: ""
        BusinessSubCategoryScreen(navController, cat)
    }
    composable(
        route = Routes.BUSINESS_SERVICES,
        arguments = listOf(navArgument("subcategory") { type = NavType.StringType })
    ) { backStackEntry ->
        val sub = backStackEntry.arguments?.getString("subcategory") ?: ""
        BusinessServicesScreen(navController, sub, businessViewModel)
    }

    // --- LIFESTYLE SERVICES ---
    composable(
        route = Routes.LIFESTYLE_SUBCATEGORY,
        arguments = listOf(navArgument("category") { type = NavType.StringType })
    ) { backStackEntry ->
        val cat = backStackEntry.arguments?.getString("category") ?: ""
        LifestyleSubCategoryScreen(navController, cat)
    }
    composable(
        route = Routes.LIFESTYLE_SERVICES,
        arguments = listOf(navArgument("subcategory") { type = NavType.StringType })
    ) { backStackEntry ->
        val sub = backStackEntry.arguments?.getString("subcategory") ?: ""
        LifestyleServicesScreen(navController, sub, lifestyleViewModel)
    }

    // --- TECH SERVICES ---
    composable(
        route = Routes.TECH_SUBCATEGORY,
        arguments = listOf(navArgument("category") { type = NavType.StringType })
    ) { backStackEntry ->
        val cat = backStackEntry.arguments?.getString("category") ?: ""
        TechSubCategoryScreen(navController, cat)
    }
    composable(
        route = Routes.TECH_SERVICES,
        arguments = listOf(navArgument("subcategory") { type = NavType.StringType })
    ) { backStackEntry ->
        val sub = backStackEntry.arguments?.getString("subcategory") ?: ""
        TechServiceListScreen(navController, sub, techViewModel)
    }

    // --- MENS GROOMING ---
    composable(
        route = Routes.MENS_SUBCATEGORIES,
        arguments = listOf(navArgument("category") { type = NavType.StringType })
    ) { backStackEntry ->
        val cat = backStackEntry.arguments?.getString("category") ?: ""
        MensSubcategoryScreen(navController, cat)
    }
    composable(
        route = Routes.MENS_SERVICES,
        arguments = listOf(navArgument("subcategory") { type = NavType.StringType })
    ) { backStackEntry ->
        val sub = backStackEntry.arguments?.getString("subcategory") ?: ""
        MensServiceListScreen(navController, sub, mensGroomingViewModel)
    }

    // --- WOMENS BEAUTY ---
    composable(
        route = Routes.WOMENS_SUBCATEGORIES,
        arguments = listOf(navArgument("category") { type = NavType.StringType })
    ) { backStackEntry ->
        val cat = backStackEntry.arguments?.getString("category") ?: ""
        WomensBeautySubcategoryScreen(navController, cat)
    }
    composable(
        route = Routes.WOMENS_SERVICES,
        arguments = listOf(navArgument("subcategory") { type = NavType.StringType })
    ) { backStackEntry ->
        val sub = backStackEntry.arguments?.getString("subcategory") ?: ""
        BeautyServiceListScreen(navController, sub, womensBeautyViewModel)
    }

    // --- HEALTHCARE ---
    composable(
        route = Routes.HEALTHCARE_SUBCATEGORIES,
        arguments = listOf(navArgument("category") { type = NavType.StringType })
    ) { backStackEntry ->
        val cat = backStackEntry.arguments?.getString("category") ?: ""
        HealthcareSubcategoryScreen(navController, cat)
    }
    composable(
        route = Routes.HEALTHCARE_SERVICES,
        arguments = listOf(navArgument("subcategory") { type = NavType.StringType })
    ) { backStackEntry ->
        val sub = backStackEntry.arguments?.getString("subcategory") ?: ""
        HealthcareServiceListScreen(navController, sub, healthViewModel)
    }

    // --- EDUCATION ---
    composable(
        route = Routes.EDUCATION_SUBCATEGORY,
        arguments = listOf(navArgument("category") { type = NavType.StringType })
    ) { backStackEntry ->
        val cat = backStackEntry.arguments?.getString("category") ?: ""
        EducationSubCategoryScreen(navController, cat)
    }
    composable(
        route = Routes.EDUCATION_COURSES,
        arguments = listOf(navArgument("subcategory") { type = NavType.StringType })
    ) { backStackEntry ->
        val sub = backStackEntry.arguments?.getString("subcategory") ?: ""
        EducationCoursesScreen(navController, sub, viewModel())
    }
    composable(Routes.EDUCATION_CART) { 
        EducationCartScreen(navController, viewModel()) 
    }
    composable(Routes.EDUCATION_BOOKING) { 
        TutorBookingScreen(navController, viewModel())
    }
    composable(Routes.EDUCATION_SUCCESS) { 
        EducationSuccessScreen(navController) 
    }
}
