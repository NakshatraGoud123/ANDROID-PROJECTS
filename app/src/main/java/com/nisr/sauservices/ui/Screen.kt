package com.nisr.sauservices.ui

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Categories : Screen("categories")
    object Bookings : Screen("bookings")
    object Cart : Screen("cart")
    object Profile : Screen("profile")
    object MyOrders : Screen("my_orders")
    
    // New Modules
    object EssentialSupplies : Screen("essential_supplies")
    object BookingsModule : Screen("bookings_module")

    // Map & Location Routes
    object MapPicker : Screen("map_picker")
    object OrderTracking : Screen("order_tracking/{orderId}") {
        fun createRoute(orderId: String) = "order_tracking/$orderId"
    }

    // Profile System Routes
    object EditProfile : Screen("profile/edit")
    object Notifications : Screen("profile/notifications")
    object ShippingAddress : Screen("profile/address")
    object ChangePassword : Screen("profile/change-password")
    object AddAccounts : Screen("profile/add-accounts")
    object ContactUs : Screen("profile/contact")
    object FAQ : Screen("profile/faq")
    
    // Home Essentials Hierarchy
    object HomeEssentialsMain : Screen("home_main")
    object HomeEssentialsCategory : Screen("home_category/{categoryId}") {
        fun createRoute(categoryId: String) = "home_category/$categoryId"
    }
    object HomeEssentialsItems : Screen("home_items/{subcategoryId}") {
        fun createRoute(subcategoryId: String) = "home_items/$subcategoryId"
    }
    object HomeEssentialsCart : Screen("home_cart")
    object HomeEssentialsCheckout : Screen("home_checkout")
    object HomeEssentialsSuccess : Screen("home_success")

    object Login : Screen("login/{role}") {
        fun createRoute(role: String) = "login/$role"
    }
    object Register : Screen("register")
    object BookingSummary : Screen("booking_summary")
    object BookingSuccess : Screen("service_booking_success")
    
    // Dashboards
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
    object AdminDashboard : Screen("admin_dashboard")
    object ForgotPassword : Screen("forgot_password")
    object SignUp : Screen("signup/{role}") {
        fun createRoute(role: String) = "signup/$role"
    }

    // Food & Beverages
    object FoodCategories : Screen("FOODS_categories")
    object FoodSubcategories : Screen("FOODS_subcategories/{category}") {
        fun createRoute(category: String) = "FOODS_subcategories/$category"
    }
    object FoodTypes : Screen("FOODS_types/{subcategory}") {
        fun createRoute(subcategory: String) = "FOODS_types/$subcategory"
    }
    object FoodItems : Screen("FOODS_items/{type}") {
        fun createRoute(type: String) = "FOODS_items/$type"
    }
    object FoodCart : Screen("FOODS_cart")
    object FoodCheckout : Screen("FOODS_checkout")
    object FoodBooking : Screen("FOODS_booking/{service}") {
        fun createRoute(service: String) = "FOODS_booking/$service"
    }
    object FoodOrderSuccess : Screen("FOODS_order_success")

    // Residential
    object ResidentialCategories : Screen("res_categories")
    object ResidentialSubcategories : Screen("res_subcategories/{categoryId}") {
        fun createRoute(categoryId: String) = "res_subcategories/$categoryId"
    }
    object ResidentialServiceList : Screen("res_services/{categoryId}/{subcategoryId}") {
        fun createRoute(categoryId: String, subcategoryId: String) = "res_services/$categoryId/$subcategoryId"
    }
    object ResidentialBookingDetails : Screen("service_booking_details")
    object ResidentialPayment : Screen("service_payment")
    object ResidentialOrderSummary : Screen("service_order_summary")
    object ResidentialSuccess : Screen("service_booking_success")

    // Unified Services
    object EducationSubCategory : Screen("edu_subcategories/{category}") {
        fun createRoute(category: String) = "edu_subcategories/$category"
    }
    object EducationCourses : Screen("edu_courses/{subcategory}") {
        fun createRoute(subcategory: String) = "edu_courses/$subcategory"
    }
    object EducationCart : Screen("edu_cart")
    object EducationBooking : Screen("edu_booking")
    object EducationSuccess : Screen("edu_success")

    object BusinessSubCategory : Screen("biz_subcategories/{category}") {
        fun createRoute(category: String) = "biz_subcategories/$category"
    }
    object BusinessServices : Screen("biz_services/{subcategory}") {
        fun createRoute(subcategory: String) = "biz_services/$subcategory"
    }
    object BusinessBooking : Screen("biz_booking")
    object BusinessCheckout : Screen("biz_checkout")
    object BusinessPayment : Screen("biz_payment")
    object BusinessSuccess : Screen("biz_success")

    object LifestyleSubCategory : Screen("life_subcategories/{category}") {
        fun createRoute(category: String) = "life_subcategories/$category"
    }
    object LifestyleServices : Screen("life_services/{subcategory}") {
        fun createRoute(subcategory: String) = "life_services/$subcategory"
    }
    object LifestyleBooking : Screen("life_booking")
    object LifestyleCheckout : Screen("life_checkout")
    object LifestylePayment : Screen("life_payment")
    object LifestyleSuccess : Screen("life_success")

    object TechSubCategory : Screen("tech_subcategories/{category}") {
        fun createRoute(category: String) = "tech_subcategories/$category"
    }
    object TechServices : Screen("tech_services/{subcategory}") {
        fun createRoute(subcategory: String) = "tech_services/$subcategory"
    }
    object TechBooking : Screen("tech_booking")
    object TechCheckout : Screen("tech_checkout")
    object TechPayment : Screen("tech_payment")
    object TechSuccess : Screen("tech_success")

    object MensCategories : Screen("mens_categories")
    object MensSubcategories : Screen("mens_subcategories/{category}") {
        fun createRoute(category: String) = "mens_subcategories/$category"
    }
    object MensServices : Screen("mens_services/{subcategory}") {
        fun createRoute(subcategory: String) = "mens_services/$subcategory"
    }
    object MensBooking : Screen("mens_booking")
    object MensCheckout : Screen("mens_checkout")
    object MensPayment : Screen("mens_payment")
    object MensSuccess : Screen("mens_success")

    object WomensBeautyCategories : Screen("womens_beauty_categories")
    object WomensBeautySubcategories : Screen("womens_beauty_subcategories/{category}") {
        fun createRoute(category: String) = "womens_beauty_subcategories/$category"
    }
    object WomensBeautyServices : Screen("womens_beauty_services/{subcategory}") {
        fun createRoute(subcategory: String) = "womens_beauty_services/$subcategory"
    }
    object WomensBeautyBooking : Screen("womens_beauty_booking")
    object WomensBeautyPayment : Screen("womens_beauty_payment")
    object WomensBeautyOrderSummary : Screen("womens_beauty_order_summary")
    object WomensBeautySuccess : Screen("womens_beauty_success")

    object HealthcareCategories : Screen("health_categories")
    object HealthcareSubcategories : Screen("health_subcategories/{category}") {
        fun createRoute(category: String) = "health_subcategories/$category"
    }
    object HealthcareServices : Screen("health_services/{subcategory}") {
        fun createRoute(subcategory: String) = "health_services/$subcategory"
    }
    object HealthcareBooking : Screen("health_booking")
    object HealthcarePayment : Screen("health_payment")
    object HealthcareOrderSummary : Screen("health_order_summary")
    object HealthcareOrderTracking : Screen("health_order_tracking")
    object HealthcareSuccess : Screen("health_success")

    // Mechanic Services
    object MechanicMain : Screen("mechanic_main")
    object MechanicSubcategories : Screen("mechanic_subcategories/{category}") {
        fun createRoute(category: String) = "mechanic_subcategories/$category"
    }
    object MechanicBooking : Screen("mechanic_booking")
    object MechanicSuccess : Screen("mechanic_success")

    // Mobility Services
    object MobilityMain : Screen("mobility_main")
    object MobilityServiceTypes : Screen("mobility_types")
    object MobilityBooking : Screen("mobility_booking")
    object MobilityRideTracking : Screen("mobility_tracking")
    object MobilitySuccess : Screen("mobility_success")
}
