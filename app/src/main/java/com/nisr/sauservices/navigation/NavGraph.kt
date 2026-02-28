package com.nisr.sauservices.navigation

sealed class Screen(val route:String){
    object Home:Screen("home")
    object Categories:Screen("categories")
    object Bookings:Screen("bookings")
    object Cart:Screen("cart")
    object Profile:Screen("profile")
}