package com.example.disasterprevention

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Heavy_Rain_Alert(
    // 標題，例如："解除大雨特報"
    @SerializedName("headline")
    val headline: String,

    // 詳細描述
    @SerializedName("description")
    val description: String,

    // 特報生效時間
    @SerializedName("effective")
    val effectiveTime: String,

    // 特報預計過期時間
    @SerializedName("expires")
    val expiresTime: String,

    // --- ▼▼▼ 修改點就在這裡 ▼▼▼ ---
    // 嚴重性等級，API 提供的欄位是 "severity"，而不是 "severity_level"
    @SerializedName("severity") // <-- 從 "severity_level" 修改為 "severity"
    val severity: String,      // <-- 建議也將變數名改為 severity，更直觀

    // 影響區域描述
    @SerializedName("area_desc")
    val areaDesc: String,

    // --- ▼▼▼ 新增的欄位 (可選，但建議加入) ▼▼▼ ---
    // 緊急程度，API 有提供 "urgency"
    @SerializedName("urgency")
    val urgency: String

) : Parcelable