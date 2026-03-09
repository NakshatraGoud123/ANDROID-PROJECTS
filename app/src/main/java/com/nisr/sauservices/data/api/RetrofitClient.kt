package com.nisr.sauservices.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    /** 
     * HOW TO SET YOUR IP:
     * 1. If using EMULATOR: Use "10.0.2.2"
     * 2. If using REAL PHONE: 
     *    - Connect phone and laptop to same Wi-Fi.
     *    - Find your Laptop IP (Run 'ipconfig' in CMD). It looks like "192.168.x.x".
     *    - Replace the IP below with your Laptop IP.
     */
    private const val SERVER_IP = "10.0.2.2" // <-- CHANGE THIS FOR REAL PHONE (e.g., "192.168.1.5")
    private const val PORT = "3000"
    private const val BASE_URL = "http://$SERVER_IP:$PORT/api/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    val instance: SauApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(SauApiService::class.java)
    }
}
