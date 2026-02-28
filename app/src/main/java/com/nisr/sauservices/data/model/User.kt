package com.nisr.sauservices.data.model

data class User(
    val id: String,
    val role: String, // shopkeeper, service_worker, delivery
    val name: String,
    val phone: String,
    val email: String,
    val extraFields: Map<String, String> = emptyMap(),
    val createdAt: Long = System.currentTimeMillis()
)
