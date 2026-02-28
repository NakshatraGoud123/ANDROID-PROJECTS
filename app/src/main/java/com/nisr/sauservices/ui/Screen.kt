package com.nisr.sauservices.ui

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Categories : Screen("categories")
    object Bookings : Screen("bookings")
    object Cart : Screen("cart")
    object Profile : Screen("profile")
    
    // Home Essentials Hierarchy
    object ProductTypes : Screen("product_types/{categoryId}") {
        fun createRoute(categoryId: String) = "product_types/$categoryId"
    }
    object ProductList : Screen("product_list/{typeId}") {
        fun createRoute(typeId: String) = "product_list/$typeId"
    }

    object Login : Screen("login/{role}") {
        fun createRoute(role: String) = "login/$role"
    }
    object Register : Screen("register")
    object ProductDetail : Screen("product_detail")
    object ServiceList : Screen("service_list")
    object ServiceDetail : Screen("service_detail")
    object BookingSummary : Screen("booking_summary")
    object BookingSuccess : Screen("booking_success")
    object Payment : Screen("payment")
    
    // Residential Services Hierarchy
    object ResidentialCategories : Screen("res_categories")
    object ResidentialSubcategories : Screen("res_subcategories/{categoryId}") {
        fun createRoute(categoryId: String) = "res_subcategories/$categoryId"
    }
    object ResidentialServiceList : Screen("res_services/{categoryId}/{subcategoryId}") {
        fun createRoute(categoryId: String, subcategoryId: String) = "res_services/$categoryId/$subcategoryId"
    }
    object ResidentialCart : Screen("res_cart")
    object ResidentialBookingDetails : Screen("res_booking_details")
    object ResidentialPayment : Screen("res_payment_selection")
    object ResidentialOrderSummary : Screen("res_order_summary")
    
    // Role Selection & Auth
    object RoleSelection : Screen("role_selection")
    object AuthOptions : Screen("auth_options/{role}") {
        fun createRoute(role: String) = "auth_options/$role"
    }
    object ShopkeeperRegister : Screen("shopkeeper_register")
    object ServiceWorkerRegister : Screen("service_worker_register")
    object DeliveryPartnerRegister : Screen("delivery_partner_register")
    object ShopkeeperDashboard : Screen("shopkeeper_dashboard")
    object ServiceWorkerDashboard : Screen("service_worker_dashboard")
    object DeliveryDashboard : Screen("delivery_dashboard")
    object ForgotPassword : Screen("forgot_password")
}
