<h1 align="center">T-Rex Run: AI Vision Edition</h1>

<p align="center">
  <a href="https://android-arsenal.com/api?level=24"><img alt="API" src="https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://img.shields.io/badge/Edge%20AI-Computer%20Vision-purple.svg"><img alt="Edge AI" src="https://img.shields.io/badge/Edge%20AI-Computer%20Vision-purple.svg"/></a>
</p>

A next-generation interactive Android game that brings the classic Chrome T-Rex game to life using **Edge AI and Computer Vision**. Powered by Emotibot's RobotVision SDK, players control the dinosaur by physically jumping and ducking in front of the device's camera in real-time.

## 🚀 Features

- **Motion-Controlled Gameplay**: Completely touch-free interaction. Uses the front-facing camera for real-time human pose estimation.
- **State Machine Lifecycle**: Strictly managed game states (`PlayerCheckActivity` -> `GameActivity`) to optimize camera and AI resource allocation.
- **Asynchronous Telemetry**: Built-in `UploadService` to synchronize player telemetry and edge-case analytics to the cloud.

## 🛠 Tech Stack & Architecture

- **AI Inference Pipeline**:
  - **Capture Layer**: High-framerate YUV image buffer extraction using Camera APIs.
  - **Inference Layer**: On-device neural network execution (Pose Estimation) generating keypoint states (`isJumping`, `isDucking`).
  - **Render Layer**: 60FPS game loop reacting to AI state variables.
- **Performance Optimizations**:
  - **Frame Dropping Mechanism**: Discards queued camera frames to guarantee the AI engine processes only the absolute latest image, eliminating input lag.
  - **Memory Pooling**: Direct C++ memory access for YUV byte buffers to prevent Java Garbage Collection stutters during gameplay.
- **Background Processing**: Uses Android `JobScheduler` for battery-efficient, network-aware telemetry uploads.

## 📦 Getting Started

### Prerequisites
- Android device with a front-facing camera supporting autofocus.
- Minimum API Level 24.

### Build & Run
```bash
git clone https://github.com/boochlin06/trex-running-cv.git
```
Open in Android Studio, sync Gradle, and run on a physical device (Emulators are not supported due to the requirement for physical camera hardware and native ARM libraries).

## 📄 License
Licensed under the Apache License 2.0.
