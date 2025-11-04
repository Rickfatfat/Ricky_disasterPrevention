package com.example.disasterprevention

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * 已被 HomeActivity (RecyclerView 架構) 取代。
 * 原本用於 Leanback TV UI，現在僅保留作為參考或備用版本。
 */
@Deprecated("此類已由 HomeActivity 取代，不再使用。")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, MainBrowseFragment())
                .commit()
        }
    }
}
