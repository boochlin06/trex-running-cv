# T-Rex Running (AI 視覺體感跑酷遊戲)

這是一款極具技術創意的 AI 體感互動遊戲。它將經典的 Google Chrome 恐龍跑酷遊戲 (T-Rex Run) 搬到 Android 平台上，並透過 Emotibot 的機器人視覺 SDK (RobotVision)，讓玩家透過**真實的肢體動作 (如跳躍或蹲下)** 來控制遊戲中的恐龍！

---

## 程式功能分析 (Program Feature Analysis)

1. **視覺體感控制 (Computer Vision Control)**
   * 宣告了強制的相機硬體權限 (`android.hardware.camera.front`)，在遊戲中即時開啟前置鏡頭捕捉玩家畫面。
   * 利用底層視覺引擎進行即時人體姿態估計 (Pose Estimation) 或動作觸發，將肢體動作轉換為遊戲控制訊號。
2. **遊戲生命週期與場景切換**
   * **`StartActivity`**: 遊戲開始選單。
   * **`PlayerCheckActivity`**: 遊戲前置檢查，負責確保玩家站在相機的可視範圍內，並完成視覺定位校準。
   * **`GameActivity`**: 主遊戲邏輯渲染，並處理即時的視覺辨識輸入。
   * **`FinishActivity`**: 遊戲結算與成績顯示。
3. **背景數據上傳 (Data Telemetry)**
   * 實作了 `UploadService`，這是一個負責將玩家的遊戲紀錄或肢體分析數據上傳至雲端的系統元件。

---

## 檔案與目錄結構 (Directory Structure)

```text
TrexRunning/
├── app/src/main/
│   ├── AndroidManifest.xml          # 宣告相機權限、全橫向螢幕 (landscape) 與 Service
│   └── java/com/emotibot/robotvision/game/trexrun/
│       ├── activity/                # 控制各個遊戲場景狀態的 Activity 集合
│       └── service/
│           └── UploadService.java   # 負責資料同步的背景服務
├── device-2019-08-12-114331.png     # 遊戲截圖或測試圖檔
└── build.gradle                     # 專案建置設定
```

---

## 使用到的 Design Pattern 與架構決策

1. **State Machine (狀態機設計模式)**
   * **決策**：將遊戲不同的階段嚴格拆分到不同的 Activity 中（Start -> Check -> Game -> Finish）。
   * **優勢**：有助於管理硬體相機資源的開啟與關閉，並確保視覺模型只有在需要時（Check 與 Game 階段）才被載入，節省記憶體。
2. **JobScheduler 模式 (非同步任務)**
   * `UploadService` 在 Manifest 中宣告了 `android.permission.BIND_JOB_SERVICE`。
   * **決策**：利用 Android 系統的 JobScheduler 機制來排程數據上傳，確保在有網路且裝置效能允許的情況下才進行背景同步，避免影響遊戲運行時的 CPU 資源。

---

## 專案亮點介紹 (Highlights)

* 🏃‍♂️ **Edge AI 與遊戲體驗的完美結合**
  * 突破了傳統觸控螢幕的限制，將複雜的邊緣計算 (Edge Computing) 視覺模型應用在即時且低延遲要求的跑酷遊戲中，展現了極高的技術含金量與互動樂趣。
* 📸 **嚴謹的硬體需求配置**
  * 在 Manifest 中利用 `<uses-feature>` 標籤強制要求裝置必須具備**支援自動對焦的前置鏡頭** (`android.hardware.camera.front.autofocus`)，確保下載此 App 的裝置皆能順利運行 AI 視覺偵測。
