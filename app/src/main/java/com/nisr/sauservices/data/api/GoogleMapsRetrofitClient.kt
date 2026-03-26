package com.nisr.sauservices.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GoogleMapsRetrofitClient {
    private const val BASE_URL = "https://maps.googleapis.com/"

    val directionsApi: DirectionsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DirectionsApiService::class.java)
    }
}
