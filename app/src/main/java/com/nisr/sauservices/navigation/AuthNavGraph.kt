package com.nisr.sauservices.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nisr.sauservices.data.repository.UserRepository
import com.nisr.sauservices.ui.auth.*
import com.nisr.sauservices.ui.onboarding.OnboardingScreen
import com.nisr.sauservices.ui.Screen

fun NavGraphBuilder.authNavGraph(navController: NavController, userRepository: UserRepository) {
    composable(Routes.ONBOARDING) {
        OnboardingScreen(navController)
    }
    
    composable(Routes.ROLE_SELECTION) {
        // Redirect to Login as customer
        LaunchedEffect(Unit) {
            navController.navigate(Screen.Login.createRoute("customer")) {
                popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
            }
        }
    }
    
    composable(
        route = Routes.AUTH_OPTIONS,
        arguments = listOf(navArgument("role") { type = NavType.StringType })
    ) { backStackEntry ->
        // Ignore role and use customer
        AuthOptionsScreen(navController, "customer")
    }
    
    composable(
        route = Routes.LOGIN,
        arguments = listOf(navArgument("role") { type = NavType.StringType })
    ) { backStackEntry ->
        // Ignore role and use customer
        SignInScreen(navController, "customer") 
    }
    
    composable(
        route = Routes.SIGNUP,
        arguments = listOf(navArgument("role") { type = NavType.StringType })
    ) { backStackEntry ->
        // Ignore role and use customer
        SignUpScreen(navController, "customer")
    }
    
    composable(Routes.REGISTER_CUSTOMER) {
        CustomerSignUpScreen(navController)
    }
    
    composable(Routes.FORGOT_PASSWORD) {
        ForgotPasswordScreen(navController)
    }

    // Dashboard Registration removed as the app is now customer-only
}
