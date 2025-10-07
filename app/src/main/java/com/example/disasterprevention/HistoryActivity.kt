package com.example.disasterprevention

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        rvHistory = findViewById(R.id.rv_history)
        rvHistory.layoutManager = LinearLayoutManager(this)

        val historyList = intent.getParcelableArrayListExtra<Earthquake>("historyList") ?: arrayListOf()

        val sortedList = historyList.sortedByDescending { parseTime(it.time) }

        rvHistory.adapter = HistoryAdapter(sortedList)
    }

    private fun parseTime(timeString: String): Date {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            sdf.parse(timeString) ?: Date(0)
        } catch (e: Exception) {
            Date(0)
        }
    }
}
