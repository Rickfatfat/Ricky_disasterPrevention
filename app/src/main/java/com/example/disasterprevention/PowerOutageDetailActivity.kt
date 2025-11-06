package com.example.disasterprevention

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class PowerOutageDetailActivity : AppCompatActivity() {

    // @SuppressLint("MissingInflatedId") // <-- 建議刪除，讓IDE幫你檢查ID
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_power_outage_detail)

        // 接收從 HomeActivity 傳來的資料
        val response = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("power_outage_data", PowerOutageResponse::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("power_outage_data")
        }

        // 綁定所有會用到的 View
        val layoutHasData: LinearLayout = findViewById(R.id.layout_has_data)
        val layoutNoData: LinearLayout = findViewById(R.id.layout_no_data)
        val btnBackToHome: Button = findViewById(R.id.btn_back_to_home)

        // --- 按鈕點擊事件 ---
        btnBackToHome.setOnClickListener {
            finish() // 呼叫 finish() 即可關閉當前頁面，返回主畫面
        }

        if (response != null) {
            val affectedCount = response.affectedCount ?: 0
            val affectedData = response.data ?: emptyList()

            // ▼▼▼ 【核心邏輯】根據 affectedCount 決定顯示哪個畫面 ▼▼▼
            if (affectedCount > 0 && affectedData.isNotEmpty()) {
                //【情況一：有停電】顯示停電列表
                layoutHasData.visibility = View.VISIBLE
                layoutNoData.visibility = View.GONE

                // 綁定"有資料"區塊內的 View
                val tvSummary: TextView = findViewById(R.id.tv_summary_content)
                val rvDetails: RecyclerView = findViewById(R.id.rv_outage_details)

                tvSummary.text = response.impactSummary ?: "無相關摘要資訊。"

                // 設定 RecyclerView
                val adapter = PowerOutageDetailAdapter(affectedData)
                rvDetails.adapter = adapter

            } else {
                //【情況二：無停電】顯示"無資料"畫面
                layoutHasData.visibility = View.GONE
                layoutNoData.visibility = View.VISIBLE
            }

        } else {
            //【情況三：API根本沒回傳資料或出錯】也顯示"無資料"畫面
            layoutHasData.visibility = View.GONE
            layoutNoData.visibility = View.VISIBLE

            // 可以考慮修改提示文字，讓使用者知道是載入失敗
            val tvNoDataMessage: TextView = findViewById(R.id.tv_no_data_message)
            tvNoDataMessage.text = "無法載入停電資料"
        }
    }
}

// --- RecyclerView Adapter (這部分不用變) ---
class PowerOutageDetailAdapter(private val items: List<AffectedData>) :
    RecyclerView.Adapter<PowerOutageDetailAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvReason: TextView = view.findViewById(R.id.item_tv_reason)
        val tvTime: TextView = view.findViewById(R.id.item_tv_time)
        val tvArea: TextView = view.findViewById(R.id.item_tv_area)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_power_outage_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val outageInfo = items[position].outageInfo

        if (outageInfo != null) {
            holder.tvReason.text = "原因：${outageInfo.reason ?: "未提供"}"
            val date = outageInfo.date ?: ""
            val time = outageInfo.startTime ?: ""
            holder.tvTime.text = "時間：$date $time"
            holder.tvArea.text = "影響範圍：${outageInfo.area ?: "未提供"}"
        } else {
            holder.tvReason.text = "原因：資料不完整"
            holder.tvTime.text = "時間：資料不完整"
            holder.tvArea.text = "影響範圍：資料不完整"
        }
    }

    override fun getItemCount() = items.size
}