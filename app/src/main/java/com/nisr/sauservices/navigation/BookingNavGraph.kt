package com.nisr.sauservices.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nisr.sauservices.ui.home.*
import com.nisr.sauservices.ui.viewmodel.*

fun NavGraphBuilder.bookingNavGraph(
    navController: NavController,
    residentialViewModel: ResidentialViewModel,
    businessViewModel: BusinessViewModel,
    lifestyleViewModel: LifestyleViewModel,
    techViewModel: TechServicesViewModel,
    mensGroomingViewModel: MensGroomingViewModel,
    womensBeautyViewModel: WomensBeautyViewModel,
    healthcareViewModel: HealthcareViewModel,
    bookingsViewModel: BookingsViewModel,
    foodCartViewModel: FoodCartViewModel,
    homeCartViewModel: CartViewModel
) {
    // Unified Bookings List
    composable(Routes.BOOKINGS) {
        BookingsScreen(navController, bookingsViewModel)
    }

    // Unified Cart for all Services
    composable(Routes.CART) {
        UnifiedCartScreen(
            navController = navController,
            residentialViewModel = residentialViewModel,
            businessViewModel = businessViewModel,
            lifestyleViewModel = lifestyleViewModel,
            techViewModel = techViewModel,
            mensGroomingViewModel = mensGroomingViewModel,
            womensBeautyViewModel = womensBeautyViewModel,
            healthcareViewModel = healthcareViewModel,
            foodCartViewModel = foodCartViewModel,
            homeCartViewModel = homeCartViewModel
        )
    }

    // Shared Checkout Flow for Services
    composable(Routes.SERVICE_BOOKING_DETAILS) {
        ResidentialBookingDetailsScreen(navController, residentialViewModel)
    }

    composable(Routes.SERVICE_PAYMENT) {
        ResidentialPaymentScreen(navController, residentialViewModel)
    }

    composable(Routes.SERVICE_ORDER_SUMMARY) {
        ResidentialOrderSummaryScreen(
            navController = navController,
            viewModel = residentialViewModel,
            bookingsViewModel = bookingsViewModel,
            businessViewModel = businessViewModel,
            lifestyleViewModel = lifestyleViewModel,
            techViewModel = techViewModel,
            mensGroomingViewModel = mensGroomingViewModel,
            womensBeautyViewModel = womensBeautyViewModel,
            healthcareViewModel = healthcareViewModel
        )
    }

    composable(Routes.SERVICE_BOOKING_SUCCESS) {
        com.nisr.sauservices.ui.home.BookingSuccessScreen(navController)
    }
}
