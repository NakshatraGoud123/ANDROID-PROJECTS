package com.nisr.sauservices.data.api

import com.nisr.sauservices.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface SauApiService {

    // Auth
    @POST("auth/login")
    suspend fun login(@Body credentials: Map<String, String>): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body userData: Map<String, String>): Response<LoginResponse>

    // Customer
    @GET("customer/products")
    suspend fun getProducts(@Query("category") category: String? = null): Response<List<ApiProduct>>

    @GET("customer/services")
    suspend fun getServices(@Query("category") category: String? = null): Response<List<ApiServiceItem>>

    @POST("customer/orders")
    suspend fun createOrder(@Body orderData: Map<String, Any>): Response<ApiOrder>

    @GET("customer/orders/{id}")
    suspend fun getOrderStatus(@Path("id") orderId: Int): Response<ApiOrder>

    // Shopkeeper
    @GET("shop/orders")
    suspend fun getShopOrders(): Response<List<ApiOrder>>

    @PATCH("shop/orders/{id}")
    suspend fun updateOrderStatus(
        @Path("id") orderId: Int,
        @Body status: Map<String, String>
    ): Response<ApiOrder>

    // Worker
    @GET("worker/jobs")
    suspend fun getWorkerJobs(): Response<List<ApiOrder>>
}
