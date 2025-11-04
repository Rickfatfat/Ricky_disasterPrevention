package com.example.disasterprevention

import com.google.gson.annotations.SerializedName

data class Heavy_Rain_Response(

    @SerializedName("status_code")    val status_code: Int,

    @SerializedName("message")
    val message: String,

    // --- ▼▼▼ 修改點就在這裡 ▼▼▼ ---
    // 這裡的類型必須和你定義的 data class 名稱完全一致
    @SerializedName("data")
    val data: List<Heavy_Rain_Alert> // <-- 從 WeatherAlert 修改為 Heavy_Rain_Alert
)