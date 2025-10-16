/*package com.example.disasterprevention

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            // .baseUrl("http://192.168.2.189:3000/") //實體機
            .baseUrl("http://10.0.2.2:3000/")    //模擬器
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
*/