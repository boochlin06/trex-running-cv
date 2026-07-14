# T-Rex Running (AI 視覺體感跑酷遊戲) - 電腦視覺與 Edge AI 架構深度解析

## 第一章：專案總覽與 Edge AI 技術革命 (Executive Summary)

T-Rex Running 不僅僅是一款致敬 Google Chrome 斷網小恐龍的遊戲，它是一個高度複雜的 **Edge AI (邊緣運算人工智慧) 與電腦視覺 (Computer Vision)** 互動展示專案。本專案整合了 Emotibot 的 RobotVision SDK，徹底顛覆了傳統依賴觸控螢幕的互動模式，讓玩家能夠透過「真實的肢體動作」 (例如：原地跳躍、蹲下) 來即時控制遊戲中的恐龍。

在技術層面上，本專案必須在硬體資源受限的 Android 行動裝置上，同時解決多項世界級難題：包含高幀率的相機影像擷取 (Camera Preview)、毫秒級的 AI 姿態估計 (Pose Estimation) 推論延遲、遊戲引擎的渲染迴圈 (Game Loop)，以及背景數據的非同步上傳。本白皮書將為您深度剖析這款 AI 體感遊戲背後的軟體架構與效能優化策略。

## 第二章：系統架構與資料流解析 (Architecture & Data Flow)

### 2.1 多執行緒非同步運算架構 (Multi-Threaded Asynchronous Pipeline)
要讓視覺辨識與遊戲渲染同時流暢運行，絕對不能在主執行緒 (Main UI Thread) 中執行 AI 推論。本專案的資料流架構如下：
1. **影像擷取層 (Camera Capture Thread)**: 透過 `Camera` 或 `Camera2` API，設定預覽解析度 (例如 640x480 以節省算力)，並透過 `SurfaceTexture` 或 `ImageReader` 持續捕獲 YUV 格式的影像幀。
2. **AI 推論層 (Inference Thread)**: 從擷取層取得最新的影像幀後，送入 RobotVision SDK (通常底層是基於 NCNN, MNN 或 TFLite 等輕量化推論引擎)。模型會進行特徵提取，並輸出人體關鍵點 (Keypoints) 的座標，進而判定當下使用者的姿勢狀態 (如：跳躍狀態 `isJumping`，下蹲狀態 `isDucking`)。
3. **遊戲渲染層 (Render Thread)**: 遊戲引擎本身擁有獨立的 Game Loop (通常是透過 `SurfaceView` 與 `Choreographer`)。它會以 60FPS 的頻率不斷讀取 AI 推論層更新的「狀態變數」，並據此更新恐龍的 Y 軸座標、碰撞判定與畫面繪製。

### 2.2 狀態機驅動的遊戲生命週期 (State Machine Lifecycle)
遊戲場景切換由嚴格的 Activity 狀態機控管，以確保硬體資源在不同階段得到最佳配置：
- **`StartActivity`**: 輕量級啟動畫面，進行權限請求 (相機、儲存空間) 與資源預載。
- **`PlayerCheckActivity`**: 這是體感遊戲中最關鍵的一環 (校準階段)。相機開啟，畫面上出現人體輪廓框，要求玩家退後並站立於特定位置，確保攝影機能完整捕捉全身。此階段的演算法重點在於「追蹤穩定性 (Tracking Stability)」。
- **`GameActivity`**: 進入核心邏輯，啟動背景捲動與障礙物隨機生成演算法，並嚴密監聽從 RobotVision 回傳的姿勢狀態。
- **`FinishActivity`**: 遊戲結束，釋放昂貴的相機與 AI 模型資源。

## 第三章：效能優化與邊緣計算挑戰 (Performance Optimization)

### 3.1 延遲抑制 (Latency Mitigation)
體感遊戲的致命傷在於「輸入延遲 (Input Lag)」。如果玩家跳起來，遊戲中的恐龍過了 0.5 秒才跳，體驗將極差。
- **解決方案**: 放棄處理每一幀影像 (Frame Dropping)。假設相機以 30FPS 輸出，但 AI 推論一張圖需要 50ms (即最高只能處理 20FPS)，架構上必須實作「丟幀機制」。永遠只取最新的那一幀影像送入神經網路，丟棄隊列中積壓的舊影像，確保 AI 輸出的判定結果是最即時的。

### 3.2 記憶體與熱能控管 (Memory & Thermal Management)
長時間執行神經網路推論會導致手機 CPU/GPU 滿載並引發嚴重發熱，進而導致系統降頻 (Thermal Throttling)。
- **解決方案**: 
  1. 採用量化 (Quantization, INT8) 過的輕量級模型。
  2. 動態降低相機預覽解析度，因為對於姿態估計來說，過高的解析度反而增加計算負擔且對精確度提升有限。
  3. C++ JNI 層的記憶體池：避免在 Java 層頻繁建立 `byte[]` 或 `Bitmap` 傳遞影像，改為直接將相機的 YUV `ByteBuffer` 指標傳遞給 C++ 底層處理。

## 第四章：背景資料遙測與雲端同步 (Data Telemetry)

### 4.1 `UploadService.java` 與 `JobScheduler`
為了分析玩家行為、統計高分排行榜或收集模型難以辨識的 Edge Cases (邊緣案例) 以便未來重新訓練模型，專案設計了背景資料上傳機制。
- **技術實作**: 在 `AndroidManifest.xml` 中宣告了 `android.permission.BIND_JOB_SERVICE`。這代表 `UploadService` 並不是一個傳統的無限期背景服務，而是利用了 Android 官方強烈推薦的 `JobScheduler` API。
- **優勢**: 可以精確設定條件（例如：「裝置連上無限網路且正在充電時」）才啟動批量上傳，極大程度地優化了電池續航力與使用者的網路流量，符合現代 Android 開發的最佳實踐標準。

## 第五章：總結與展望 (Conclusion)

`TrexRunning` 專案完美示範了如何將前沿的 AI 視覺技術落地於充滿趣味性的消費級應用中。它不僅是一套遊戲原始碼，更是一套成熟的 **「相機 -> 推論引擎 -> 渲染引擎」架構模板**。未來只需替換 RobotVision 裡面的模型（例如換成手勢辨識模型），這套架構就能立刻轉型為切水果 (Fruit Ninja) 體感版，具有無限的商業擴展潛力。
