package com.nisr.sauservices.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class MechanicCategory(val id: String, val name: String, val icon: ImageVector)
data class MechanicSubcategory(val id: String, val categoryId: String, val name: String)
data class MechanicServiceItem(
    val id: String,
    val name: String,
    val price: Double,
    val categoryId: String,
    val subcategoryId: String,
    val estimatedMinutes: Int
)

object MechanicData {
    val categories = listOf(
        MechanicCategory("bike", "Bike Services", Icons.Default.TwoWheeler),
        MechanicCategory("car", "Car Services", Icons.Default.DirectionsCar),
        MechanicCategory("auto", "Auto Services", Icons.Default.Settings),
        MechanicCategory("ev", "EV Services", Icons.Default.ElectricCar),
        MechanicCategory("towing", "Towing Services", Icons.Default.LocalShipping)
    )

    val subcategories = listOf(
        // Bike
        MechanicSubcategory("bike_oil", "bike", "Oil Change"),
        MechanicSubcategory("bike_brake", "bike", "Brake Service"),
        MechanicSubcategory("bike_tyre", "bike", "Tyre Puncture"),
        MechanicSubcategory("bike_battery", "bike", "Battery Replacement"),
        MechanicSubcategory("bike_engine", "bike", "Engine Repair"),
        MechanicSubcategory("bike_gen", "bike", "General Service"),
        
        // Car
        MechanicSubcategory("car_ac", "car", "AC Repair"),
        MechanicSubcategory("car_engine", "car", "Engine Diagnostics"),
        MechanicSubcategory("car_wheel", "car", "Wheel Alignment"),
        MechanicSubcategory("car_susp", "car", "Suspension Repair"),
        MechanicSubcategory("car_brake", "car", "Brake Service"),
        MechanicSubcategory("car_oil", "car", "Oil Change"),
        MechanicSubcategory("car_gen", "car", "General Service"),

        // Auto
        MechanicSubcategory("auto_elec", "auto", "Electrical Repair"),
        MechanicSubcategory("auto_brake", "auto", "Brake Repair"),
        MechanicSubcategory("auto_batt", "auto", "Battery Service"),
        MechanicSubcategory("auto_eng", "auto", "Engine Repair"),
        MechanicSubcategory("auto_gen", "auto", "General Service"),

        // EV
        MechanicSubcategory("ev_batt", "ev", "Battery Health Check"),
        MechanicSubcategory("ev_charge", "ev", "Charging System Repair"),
        MechanicSubcategory("ev_motor", "ev", "Motor Inspection"),
        MechanicSubcategory("ev_ctrl", "ev", "Controller Diagnostics"),
        MechanicSubcategory("ev_soft", "ev", "Software Update"),

        // Towing
        MechanicSubcategory("tow_break", "towing", "Breakdown Assistance"),
        MechanicSubcategory("tow_acc", "towing", "Accident Recovery"),
        MechanicSubcategory("tow_fuel", "towing", "Fuel Delivery"),
        MechanicSubcategory("tow_emerg", "towing", "Emergency Towing")
    )

    val services = listOf(
        MechanicServiceItem("m1", "Standard Oil Change", 499.0, "bike", "bike_oil", 30),
        MechanicServiceItem("m2", "Premium Oil Change", 899.0, "bike", "bike_oil", 30),
        MechanicServiceItem("m3", "Front Brake Pad", 299.0, "bike", "bike_brake", 45),
        MechanicServiceItem("m4", "Rear Brake Shoe", 399.0, "bike", "bike_brake", 45),
        MechanicServiceItem("m5", "Tyre Puncture Fix", 150.0, "bike", "bike_tyre", 20),
        
        MechanicServiceItem("m6", "AC Gas Refill", 1500.0, "car", "car_ac", 60),
        MechanicServiceItem("m7", "Engine Tuneup", 2500.0, "car", "car_engine", 120),
        MechanicServiceItem("m8", "Alignment & Balancing", 800.0, "car", "car_wheel", 45),
        
        MechanicServiceItem("m9", "Auto Wiring Repair", 500.0, "auto", "auto_elec", 60),
        
        MechanicServiceItem("m10", "EV Battery Diagnostic", 1200.0, "ev", "ev_batt", 90),
        
        MechanicServiceItem("m11", "Flatbed Towing", 1500.0, "towing", "tow_emerg", 60)
    )
}
