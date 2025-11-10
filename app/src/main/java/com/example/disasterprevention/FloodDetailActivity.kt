package com.example.disasterprevention

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class FloodDetailActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flood_detail)

        val response = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("flood_data", FloodResponse::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("flood_data")
        }

        viewPager = findViewById(R.id.view_pager_flood)

        if (response != null && response.data.isNotEmpty()) {
            viewPager.adapter = FloodStationAdapter(response.data)
            viewPager.offscreenPageLimit = 1
        } else {
            finish()
        }
    }

    /**  遙控器左右鍵切換  **/
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                val prev = viewPager.currentItem - 1
                if (prev >= 0) viewPager.currentItem = prev
                true
            }

            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                val next = viewPager.currentItem + 1
                if (next < (viewPager.adapter?.itemCount ?: 0))
                    viewPager.currentItem = next
                true
            }

            else -> super.onKeyDown(keyCode, event)
        }
    }
}

class FloodStationAdapter(private val items: List<FloodStation>) :
    RecyclerView.Adapter<FloodStationAdapter.ViewHolder>() {

    // 【關鍵修正】ViewHolder 只需持有 item_flood_station.xml 內部元件的引用
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stationName: TextView = view.findViewById(R.id.item_tv_station_name)
        val alertStatus: TextView = view.findViewById(R.id.item_tv_alert_status)
        val statusCard: CardView = view.findViewById(R.id.item_tv_alert_status_card) // 添加 statusCard 的引用
        val currentLevel: TextView = view.findViewById(R.id.item_tv_current_level)
        val yellowLevel: TextView = view.findViewById(R.id.item_tv_yellow_level)
        val redLevel: TextView = view.findViewById(R.id.item_tv_red_level)
        val stationImage: ImageView = view.findViewById(R.id.item_iv_station_image)
        // 【已刪除】val viewPager: ViewPager2 = view.findViewById(R.id.view_pager_flood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flood_station, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        Log.d(
            "FloodStationAdapter",
            "正在載入測站: ${item.stationName}, 圖片網址: ${item.imageUrl}"
        )
        holder.stationName.text = item.stationName
        holder.alertStatus.text = "狀態：${item.alertStatus}"
        holder.currentLevel.text = "${item.currentLevel} m" // 確保只設定數值
        holder.yellowLevel.text = "二級警戒：${item.yellowLevel} m"
        holder.redLevel.text = "一級警戒：${item.redLevel} m"

        // 根據狀態改變文字和卡片背景顏色
        when (item.alertStatus) {
            "正常" -> {
                holder.alertStatus.setTextColor(Color.parseColor("#4CAF50"))
                holder.statusCard.setCardBackgroundColor(Color.parseColor("#33FFFFFF"))
            }
            "注意" -> {
                holder.alertStatus.setTextColor(Color.parseColor("#FFC107"))
                holder.statusCard.setCardBackgroundColor(Color.parseColor("#33FFFFFF"))
            }
            "警戒" -> {
                holder.alertStatus.setTextColor(Color.parseColor("#F44336"))
                holder.statusCard.setCardBackgroundColor(Color.parseColor("#33FFFFFF"))
            }
            else -> {
                holder.alertStatus.setTextColor(Color.WHITE)
                holder.statusCard.setCardBackgroundColor(Color.parseColor("#424242"))
            }
        }

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.bg_card_normal) // 建議可以換成一個深色的 placeholder
            .error(R.drawable.cloud) // 建議可以換成一個深色背景的錯誤圖示
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e("GlideError", "圖片載入失敗，測站: ${item.stationName}", e)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .into(holder.stationImage)
    }
    override fun getItemCount() = items.size
}

