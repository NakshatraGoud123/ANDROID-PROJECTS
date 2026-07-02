package com.nisr.sauservices.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class MobilityServiceType(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val baseFare: Double,
    val perKmFare: Double,
    val description: String = ""
)

data class MobilityParcelCategory(
    val id: String,
    val name: String,
    val icon: ImageVector
)

object MobilityData {
    val serviceTypes = listOf(
        MobilityServiceType("bike_ride", "Bike Ride", Icons.Default.TwoWheeler, 30.0, 10.0, "Quick and economical"),
        MobilityServiceType("auto_ride", "Auto Ride", Icons.Default.ElectricRickshaw, 50.0, 15.0, "Convenient for city traffic"),
        MobilityServiceType("cab_ride", "Cab Ride", Icons.Default.DirectionsCar, 100.0, 20.0, "Comfortable and safe"),
        MobilityServiceType("parcel_delivery", "Parcel Delivery", Icons.Default.Inventory2, 40.0, 12.0, "Reliable delivery for your items"),
        MobilityServiceType("airport_ride", "Airport Pickup & Drop", Icons.Default.FlightTakeoff, 500.0, 25.0, "On-time airport transfers"),
        MobilityServiceType("outstation_ride", "Outstation Ride", Icons.Default.DepartureBoard, 1500.0, 18.0, "Inter-city travel with ease"),
        MobilityServiceType("shared_ride", "Shared Ride", Icons.Default.Group, 20.0, 8.0, "Split the fare, save more"),
        MobilityServiceType("rental_ride", "Rental Ride", Icons.Default.Key, 300.0, 0.0, "Hire a vehicle by the hour")
    )

    val cabCategories = listOf(
        MobilityServiceType("cab_mini", "Mini", Icons.Default.DirectionsCar, 100.0, 18.0, "Compact cars"),
        MobilityServiceType("cab_sedan", "Sedan", Icons.Default.DirectionsCar, 120.0, 22.0, "Spacious sedans"),
        MobilityServiceType("cab_suv", "SUV", Icons.Default.DirectionsCar, 180.0, 28.0, "Big cars for big groups"),
        MobilityServiceType("cab_premium", "Premium", Icons.Default.Star, 250.0, 35.0, "Luxury experience"),
        MobilityServiceType("cab_xl", "XL", Icons.Default.DirectionsCar, 220.0, 30.0, "Extra large for 6+ passengers")
    )

    val parcelCategories = listOf(
        MobilityParcelCategory("parcel_docs", "Documents", Icons.Default.Description),
        MobilityParcelCategory("parcel_food", "Food", Icons.Default.Fastfood),
        MobilityParcelCategory("parcel_groceries", "Groceries", Icons.Default.ShoppingCart),
        MobilityParcelCategory("parcel_electronics", "Electronics", Icons.Default.Devices),
        MobilityParcelCategory("parcel_fragile", "Fragile Items", Icons.Default.ReportProblem)
    )
}
