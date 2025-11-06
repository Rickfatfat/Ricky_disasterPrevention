package com.example.disasterprevention

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

// ---------------------------------------------------------------------------------
// 【修正】將所有來自 API 的欄位都改為可空類型 (例如 String -> String?)
// 這樣即使 API 回應的 JSON 中有任何欄位缺失或為 null，App 也不會崩潰。
// ---------------------------------------------------------------------------------

@Parcelize
data class PowerOutageResponse(
    @SerializedName("status")
    val status: String?, // 可空
    @SerializedName("message")
    val message: String?, // 可空
    @SerializedName("user_address")
    val userAddress: String?, // 可空
    @SerializedName("user_region_info")
    val userRegionInfo: UserRegionInfo?, // 可空
    @SerializedName("affected_count")
    val affectedCount: Int?, // 可空
    @SerializedName("total_count")
    val totalCount: Int?, // 可空
    @SerializedName("impact_summary")
    val impactSummary: String?, // 可空
    @SerializedName("data")
    val data: List<AffectedData>? // 可空
) : Parcelable

@Parcelize
data class AffectedData(
    @SerializedName("outage_info")
    val outageInfo: OutageInfo?, // 可空
    @SerializedName("is_affected")
    val isAffected: Boolean?, // 可空
    @SerializedName("similarity")
    val similarity: Double?, // 可空
    @SerializedName("impact_description")
    val impactDescription: String? // 可空
) : Parcelable

@Parcelize
data class OutageInfo(
    @SerializedName("reason")
    val reason: String?, // 可空
    @SerializedName("start_time")
    val startTime: String?, // 可空
    @SerializedName("date")
    val date: String?, // 可空
    @SerializedName("project_id")
    val projectId: String?, // 可空
    @SerializedName("area")
    val area: String? // 可空
) : Parcelable

@Parcelize
data class UserRegionInfo(
    @SerializedName("full_normalized")
    val fullNormalized: String?, // 可空
    @SerializedName("street")
    val street: String?, // <-- 主要修正點
    @SerializedName("number")
    val number: String? // 可空
) : Parcelable