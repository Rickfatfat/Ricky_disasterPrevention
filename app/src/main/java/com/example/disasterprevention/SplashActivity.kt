package com.example.disasterprevention

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 讓畫面先透明，做淡入動畫
        val rootView = window.decorView.findViewById<View>(android.R.id.content)
        rootView.alpha = 0f
        rootView.animate()
            .alpha(1f)
            .setDuration(800L) // 淡入時間 0.8 秒
            .withEndAction {
                // 停留一點時間再進入主畫面
                rootView.postDelayed({
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }, 700L) // 額外延遲 0.7 秒
            }
            .start()
    }
}
