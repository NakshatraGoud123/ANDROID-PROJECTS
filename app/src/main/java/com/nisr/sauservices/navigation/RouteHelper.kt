package com.nisr.sauservices.navigation

import java.net.URLEncoder

object RouteHelper {
    fun authOptions(role: String) = "auth_options/$role"
    fun login(role: String) = "login/$role"
    fun signup(role: String) = "signup/$role"
    fun searchResults(query: String) = "search_results/${URLEncoder.encode(query, "UTF-8")}"

    // Home Essentials
    fun essentialsCategory(categoryId: String) = "home_category/$categoryId"
    fun essentialsItems(subcategoryId: String) = "home_items/$subcategoryId"

    // Food
    fun foodSubcategories(category: String) = "FOODS_subcategories/${URLEncoder.encode(category, "UTF-8")}"
    fun foodTypes(subcategory: String) = "FOODS_types/${URLEncoder.encode(subcategory, "UTF-8")}"
    fun foodItems(type: String) = "FOODS_items/${URLEncoder.encode(type, "UTF-8")}"
    fun foodBooking(service: String) = "FOODS_booking/${URLEncoder.encode(service, "UTF-8")}"

    // Residential
    fun residentialSubcategories(categoryId: String) = "res_subcategories/$categoryId"
    fun residentialServices(categoryId: String, subcategoryId: String) = "res_services/$categoryId/$subcategoryId"

    // Services
    fun businessSubcategory(category: String) = "biz_subcategories/${URLEncoder.encode(category, "UTF-8")}"
    fun businessServices(subcategory: String) = "biz_services/${URLEncoder.encode(subcategory, "UTF-8")}"

    fun lifestyleSubcategory(category: String) = "life_subcategories/${URLEncoder.encode(category, "UTF-8")}"
    fun lifestyleServices(subcategory: String) = "life_services/${URLEncoder.encode(subcategory, "UTF-8")}"

    fun techSubcategory(category: String) = "tech_subcategories/${URLEncoder.encode(category, "UTF-8")}"
    fun techServices(subcategory: String) = "tech_services/${URLEncoder.encode(subcategory, "UTF-8")}"

    fun mensSubcategories(category: String) = "mens_subcategories/${URLEncoder.encode(category, "UTF-8")}"
    fun mensServices(subcategory: String) = "mens_services/${URLEncoder.encode(subcategory, "UTF-8")}"

    fun womensSubcategories(category: String) = "womens_beauty_subcategories/${URLEncoder.encode(category, "UTF-8")}"
    fun womensServices(subcategory: String) = "womens_beauty_services/${URLEncoder.encode(subcategory, "UTF-8")}"

    fun healthcareSubcategories(category: String) = "health_subcategories/${URLEncoder.encode(category, "UTF-8")}"
    fun healthcareServices(subcategory: String) = "health_services/${URLEncoder.encode(subcategory, "UTF-8")}"

    fun educationSubcategory(category: String) = "edu_subcategories/${URLEncoder.encode(category, "UTF-8")}"
    fun educationCourses(subcategory: String) = "edu_courses/${URLEncoder.encode(subcategory, "UTF-8")}"
}
