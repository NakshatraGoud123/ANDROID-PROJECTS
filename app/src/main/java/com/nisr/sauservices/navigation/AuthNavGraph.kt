package com.nisr.sauservices.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nisr.sauservices.data.repository.UserRepository
import com.nisr.sauservices.ui.auth.*
import com.nisr.sauservices.ui.onboarding.OnboardingScreen

fun NavGraphBuilder.authNavGraph(navController: NavController, userRepository: UserRepository) {
    composable(Routes.ONBOARDING) {
        OnboardingScreen(navController)
    }
    
    composable(Routes.ROLE_SELECTION) {
        RoleSelectionScreen(navController)
    }
    
    composable(
        route = Routes.AUTH_OPTIONS,
        arguments = listOf(navArgument("role") { type = NavType.StringType })
    ) { backStackEntry ->
        val role = backStackEntry.arguments?.getString("role") ?: "customer"
        AuthOptionsScreen(navController, role)
    }
    
    composable(
        route = Routes.LOGIN,
        arguments = listOf(navArgument("role") { type = NavType.StringType })
    ) { backStackEntry ->
        val role = backStackEntry.arguments?.getString("role") ?: "customer"
        SignInScreen(navController, role) 
    }
    
    composable(
        route = Routes.SIGNUP,
        arguments = listOf(navArgument("role") { type = NavType.StringType })
    ) { backStackEntry ->
        val role = backStackEntry.arguments?.getString("role") ?: "customer"
        SignUpScreen(navController, role)
    }
    
    composable(Routes.REGISTER_CUSTOMER) {
        CustomerSignUpScreen(navController)
    }
    
    composable(Routes.FORGOT_PASSWORD) {
        ForgotPasswordScreen(navController)
    }

    // Dashboard Registration
    composable(Routes.SHOPKEEPER_REGISTER) {
        ShopkeeperRegisterScreen(navController, userRepository)
    }
    composable(Routes.SERVICE_WORKER_REGISTER) {
        ServiceWorkerRegisterScreen(navController, userRepository)
    }
    composable(Routes.DELIVERY_PARTNER_REGISTER) {
        DeliveryPartnerRegisterScreen(navController, userRepository)
    }
}
