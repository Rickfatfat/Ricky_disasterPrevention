package com.example.disasterprevention

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@GlideModule
class MyAppGlideModule : AppGlideModule() {

    // 關閉 manifest 解析，避免與其他函式庫的 GlideModule 衝突
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        // 建立一個信任所有憑證的 OkHttpClient
        val unsafeOkHttpClient = getUnsafeOkHttpClient()

        // 用我們自訂的 OkHttpClient 取代 Glide 預設的網路連線庫
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(unsafeOkHttpClient)
        )
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        try {
            // 建立一個信任所有憑證的 TrustManager
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            // 安裝這個 TrustManager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // 建立一個使用該 SSLSocketFactory 的 OkHttpClient.Builder
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true } // 關閉主機名稱驗證

            return builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}