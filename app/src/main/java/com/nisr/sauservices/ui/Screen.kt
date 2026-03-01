package com.nisr.sauservices.ui

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Categories : Screen("categories")
    object Bookings : Screen("bookings")
    object Cart : Screen("cart")
    object Profile : Screen("profile")
    
    // Home Essentials Hierarchy (COMPREHENSIVE)
    object HomeEssentialsMain : Screen("home_main")
    object HomeEssentialsCategory : Screen("home_category/{categoryId}") {
        fun createRoute(categoryId: String) = "home_category/$categoryId"
    }
    object HomeEssentialsSubcategory : Screen("home_subcategory/{subcategoryId}") {
        fun createRoute(subcategoryId: String) = "home_subcategory/$subcategoryId"
    }
    object HomeEssentialsItems : Screen("home_items/{subcategoryId}") {
        fun createRoute(subcategoryId: String) = "home_items/$subcategoryId"
    }
    object HomeEssentialsCart : Screen("home_cart")
    object HomeEssentialsCheckout : Screen("home_checkout")
    object HomeEssentialsSuccess : Screen("home_success")

    // Legacy Home Essentials (Keeping for compatibility if needed, but using new flow)
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
    
    // Food & Beverages Hierarchy (STRICT ROUTES)
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

    // Education Services Hierarchy
    object EducationBottomSheet : Screen("education_bottom_sheet")
    object EducationSubCategory : Screen("edu_subcategories/{category}") {
        fun createRoute(category: String) = "edu_subcategories/$category"
    }
    object EducationCourses : Screen("edu_courses/{subcategory}") {
        fun createRoute(subcategory: String) = "edu_courses/$subcategory"
    }
    object EducationCart : Screen("edu_cart")
    object EducationBooking : Screen("edu_booking")
    object EducationSuccess : Screen("edu_success")

    // Business & Professional Services Hierarchy
    object BusinessBottomSheet : Screen("biz_bottom_sheet")
    object BusinessSubCategory : Screen("biz_subcategories/{category}") {
        fun createRoute(category: String) = "biz_subcategories/$category"
    }
    object BusinessServices : Screen("biz_services/{subcategory}") {
        fun createRoute(subcategory: String) = "biz_services/$subcategory"
    }
    object BusinessCart : Screen("biz_cart")
    object BusinessBooking : Screen("biz_booking")
    object BusinessCheckout : Screen("biz_checkout")
    object BusinessPayment : Screen("biz_payment")
    object BusinessSuccess : Screen("biz_success")

    // Home & Lifestyle Hierarchy
    object LifestyleBottomSheet : Screen("life_bottom_sheet")
    object LifestyleSubCategory : Screen("life_subcategories/{category}") {
        fun createRoute(category: String) = "life_subcategories/$category"
    }
    object LifestyleServices : Screen("life_services/{subcategory}") {
        fun createRoute(subcategory: String) = "life_services/$subcategory"
    }
    object LifestyleCart : Screen("life_cart")
    object LifestyleBooking : Screen("life_booking")
    object LifestyleCheckout : Screen("life_checkout")
    object LifestylePayment : Screen("life_payment")
    object LifestyleSuccess : Screen("life_success")

    // Tech Services Hierarchy
    object TechBottomSheet : Screen("tech_bottom_sheet")
    object TechSubCategory : Screen("tech_subcategories/{category}") {
        fun createRoute(category: String) = "tech_subcategories/$category"
    }
    object TechServices : Screen("tech_services/{subcategory}") {
        fun createRoute(subcategory: String) = "tech_services/$subcategory"
    }
    object TechCart : Screen("tech_cart")
    object TechBooking : Screen("tech_booking")
    object TechCheckout : Screen("tech_checkout")
    object TechPayment : Screen("tech_payment")
    object TechSuccess : Screen("tech_success")

    // Men's Grooming Hierarchy
    object MensCategories : Screen("mens_categories")
    object MensSubcategories : Screen("mens_subcategories/{category}") {
        fun createRoute(category: String) = "mens_subcategories/$category"
    }
    object MensServices : Screen("mens_services/{subcategory}") {
        fun createRoute(subcategory: String) = "mens_services/$subcategory"
    }
    object MensCart : Screen("mens_cart")
    object MensBooking : Screen("mens_booking")
    object MensCheckout : Screen("mens_checkout")
    object MensPayment : Screen("mens_payment")
    object MensSuccess : Screen("mens_success")

    // Women's Beauty Hierarchy
    object WomensBeautyCategories : Screen("womens_beauty_categories")
    object WomensBeautySubcategories : Screen("womens_beauty_subcategories/{category}") {
        fun createRoute(category: String) = "womens_beauty_subcategories/$category"
    }
    object WomensBeautyServices : Screen("womens_beauty_services/{subcategory}") {
        fun createRoute(subcategory: String) = "womens_beauty_services/$subcategory"
    }
    object WomensBeautyCart : Screen("womens_beauty_cart")
    object WomensBeautyBooking : Screen("womens_beauty_booking")
    object WomensBeautyPayment : Screen("womens_beauty_payment")
    object WomensBeautyOrderSummary : Screen("womens_beauty_order_summary")
    object WomensBeautySuccess : Screen("womens_beauty_success")

    // Healthcare & Pharmacy Hierarchy
    object HealthcareCategories : Screen("health_categories")
    object HealthcareSubcategories : Screen("health_subcategories/{category}") {
        fun createRoute(category: String) = "health_subcategories/$category"
    }
    object HealthcareServices : Screen("health_services/{subcategory}") {
        fun createRoute(subcategory: String) = "health_services/$subcategory"
    }
    object HealthcareCart : Screen("health_cart")
    object HealthcarePrescription : Screen("health_prescription")
    object HealthcareBooking : Screen("health_booking")
    object HealthcarePayment : Screen("health_payment")
    object HealthcareOrderSummary : Screen("health_order_summary")
    object HealthcareOrderTracking : Screen("health_order_tracking")
    object HealthcareSuccess : Screen("health_success")

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
