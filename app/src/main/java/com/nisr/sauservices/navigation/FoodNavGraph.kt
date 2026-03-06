package com.nisr.sauservices.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nisr.sauservices.ui.food.*
import com.nisr.sauservices.ui.viewmodel.BookingsViewModel
import com.nisr.sauservices.ui.viewmodel.FoodCartViewModel

fun NavGraphBuilder.foodNavGraph(
    navController: NavController,
    foodCartViewModel: FoodCartViewModel,
    bookingsViewModel: BookingsViewModel
) {
    composable(Routes.FOOD_CATEGORIES) {
        FoodMainScreen(navController)
    }

    composable(
        route = Routes.FOOD_SUBCATEGORIES,
        arguments = listOf(navArgument("category") { type = NavType.StringType })
    ) { backStackEntry ->
        val category = backStackEntry.arguments?.getString("category") ?: ""
        FoodSubCategoryScreen(navController, category)
    }

    composable(
        route = Routes.FOOD_TYPES,
        arguments = listOf(navArgument("subcategory") { type = NavType.StringType })
    ) { backStackEntry ->
        val subCat = backStackEntry.arguments?.getString("subcategory") ?: ""
        FoodTypeScreen(navController, subCat)
    }

    composable(
        route = Routes.FOOD_ITEMS,
        arguments = listOf(navArgument("type") { type = NavType.StringType })
    ) { backStackEntry ->
        val type = backStackEntry.arguments?.getString("type") ?: ""
        FoodItemsScreen(navController, type, foodCartViewModel)
    }

    composable(Routes.FOOD_CART) {
        FoodCartScreen(navController, foodCartViewModel)
    }

    composable(Routes.FOOD_CHECKOUT) {
        FoodCheckoutScreen(navController, foodCartViewModel)
    }

    composable(
        route = Routes.FOOD_BOOKING,
        arguments = listOf(navArgument("service") { type = NavType.StringType })
    ) { backStackEntry ->
        val service = backStackEntry.arguments?.getString("service") ?: ""
        BookingScreen(navController, service)
    }

    composable(Routes.FOOD_SUCCESS) {
        FoodSuccessScreen(navController, bookingsViewModel)
    }
}
