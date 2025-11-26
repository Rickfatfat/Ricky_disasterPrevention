# 📱 專案名稱

DisasterPrevention — 災害預警與防災資訊 App

---

## 📝 專案介紹

DisasterPrevention 是一款提供即時防災資訊的 Android App，整合多項政府開放資料 API，讓使用者能在第一時間掌握天氣狀況、水位資訊、淹水警戒、、監視影像等資訊。此 App 旨在協助民眾快速了解周邊環境風險，提高防災意識。

---

## 📦 安裝方法

1. 下載本專案或使用 Git Clone

   ```bash
   git clone https://github.com/Ricky_disasterPrevention/DisasterPrevention.git
   ```
2. 使用 Android Studio 開啟專案資料夾。
3. 等待 Gradle Sync 完成。
4. 接上 Android 裝置（或啟動模擬器）。
5. 點擊 Android Studio 上方的 ▶️ Run 按鈕即可安裝到裝置。

---

## 🏗️ Build 方法

專案使用 **Gradle + Kotlin DSL**，建置方式如下：

### 透過命令列建置：

```bash
./gradlew assembleDebug
```

產生 APK 位於：

```
app/build/outputs/apk/debug/app-debug.apk
```

### Android Studio 建置：

* 點選 `Build > Make Project`
* 或直接按 `Shift + F10` 執行

---


``
### 🧰 使用技術

* **Kotlin**
* **MVVM 架構**
* **Retrofit + OkHttp**（API 串接）
* **Coroutines / Flow**（非同步處理）
* **Glide**（圖片載入）
* **LiveData / ViewModel**（狀態管理）
* **ViewBinding**（畫面綁定)

## 🔌 API 說明

本 App 整合以下政府開放資料 API：

| 功能    | API來源      | 備註          |
| ----- | ---------- | ----------- |
| 淹水資訊  | 水利署開放資料    | 即時水位顯示      |
| 降雨測站  | 中央氣象署      | 顯示即時雨量與雨勢趨勢 |
| 影像監視  | 交通部 or 水利署 | 監視器影像串流     |
| 淹水警戒  | 水利署        | 區域淹水預警      |
| 淹水警戒  | 水利署        | 區域淹水預警      |
| 淹水警戒  | 水利署        | 區域淹水預警      |

每個 API 會透過 Retrofit 統一管理，並由 Repository 層封裝後提供給 ViewModel 使用。

---

## 🏷️ 版本資訊 (Version Info)

### v1.0.0 — 首次發布

* 整合多個政府開放資料 API
* 提供水位/雨量/影像等資訊查詢
* 支援即時刷新功能
* 使用 MVVM 架構及 Retrofit

---

如需我協助你補滿「截圖區」、「API 詳細格式」、「資料流程圖」或「架構圖」，告訴我即可！
