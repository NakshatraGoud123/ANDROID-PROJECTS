package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.RetrofitClient
import com.nisr.sauservices.data.model.*
import retrofit2.Response

class SauRepository {
    private val api = RetrofitClient.instance

    // Auth
    suspend fun login(credentials: Map<String, String>): Response<LoginResponse> = api.login(credentials)
    suspend fun register(userData: Map<String, String>): Response<LoginResponse> = api.register(userData)

    // Customer
    suspend fun getProducts(category: String? = null): Response<List<Product>> = api.getProducts(category)
    suspend fun getServices(category: String? = null): Response<List<ServiceItem>> = api.getServices(category)
    suspend fun createOrder(orderData: Map<String, Any>): Response<Order> = api.createOrder(orderData)
    suspend fun getOrderStatus(orderId: Int): Response<Order> = api.getOrderStatus(orderId)

    // Dashboards
    suspend fun getShopOrders(): Response<List<Order>> = api.getShopOrders()
    suspend fun getWorkerJobs(): Response<List<Order>> = api.getWorkerJobs()
    suspend fun updateOrderStatus(orderId: Int, status: String): Response<Order> = 
        api.updateOrderStatus(orderId, mapOf("status" to status))
}
