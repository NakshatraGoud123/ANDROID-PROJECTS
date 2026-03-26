package com.nisr.sauservices.data.model

data class UserLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val role: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)
