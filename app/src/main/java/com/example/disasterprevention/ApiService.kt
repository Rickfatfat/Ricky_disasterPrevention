package com.example.disasterprevention

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/v1/earthquake/")
    fun getEarthquakes(@Query("limit") limit: Int = 5): Call<EarthquakeResponse>
}