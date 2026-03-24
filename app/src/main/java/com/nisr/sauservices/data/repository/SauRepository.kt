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
    suspend fun getProducts(category: String? = null): Response<List<ApiProduct>> = api.getProducts(category)
    suspend fun getServices(category: String? = null): Response<List<ApiServiceItem>> = api.getServices(category)
    suspend fun createOrder(orderData: Map<String, Any>): Response<ApiOrder> = api.createOrder(orderData)
    suspend fun getOrderStatus(orderId: Int): Response<ApiOrder> = api.getOrderStatus(orderId)

    // Dashboards
    suspend fun getShopOrders(): Response<List<ApiOrder>> = api.getShopOrders()
    suspend fun getWorkerJobs(): Response<List<ApiOrder>> = api.getWorkerJobs()
    suspend fun updateOrderStatus(orderId: Int, status: String): Response<ApiOrder> =
        api.updateOrderStatus(orderId, mapOf("status" to status))
}
