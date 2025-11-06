package com.example.disasterprevention

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // ==========================================================
    //  新架構：Coroutine 版本（HomeActivity、WeatherDetailActivity 用）
    // ==========================================================

    // 取得地震資料（Coroutine）
    @GET("api/v1/earthquake/")
    suspend fun getEarthquakes(
        @Query("limit") limit: Int = 5
    ): EarthquakeResponse

    // 取得天氣摘要（Coroutine）
    @GET("api/v1/weather/summary")
    suspend fun getWeatherSummary(
        @Query("location") location: String = "大里區",
        @Query("days") days: Int = 5
    ): WeatherSummaryResponse

    // 取得指定地址的停電資訊（Coroutine）
    @GET("api/v1/enhanced-power/address-check")
    suspend fun getPowerOutageInfo(
        @Query("address") address: String
    ): PowerOutageResponse

    // 取得大里地區的淹水感測器資料（Coroutine）
    @GET("api/v1/flood/dali")
    suspend fun getFloodInfo(): FloodResponse

    // ==========================================================
    // 舊架構：Callback 版本（MainFragment、MainBrowseFragment 用）
    // ==========================================================

    // 取得地震資料
    @GET("api/v1/earthquake/")
    fun getEarthquakesLegacy(
        @Query("limit") limit: Int = 5
    ): Call<EarthquakeResponse>


    // 取得豪雨警報
    @GET("api/v1/heavy_rain/")
    fun getHeavyRainAlerts(): Call<Heavy_Rain_Response>


    // 取得停水資訊
    @GET("api/v1/")
    fun getWaterOutages(
        @Query("county") county: String?
    ): Call<WaterOutagesResponse>
}