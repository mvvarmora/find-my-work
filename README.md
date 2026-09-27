# Find My Work 🛠️

> **Find My Work** is an on-demand service marketplace and worker management Android application built with **Jetpack Compose**, **Material 3**, and **Firebase Firestore**.

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202026.02.01-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-Firestore%20%7C%20Auth-FFCA28.svg)](https://firebase.google.com)

---

## 🌟 Key Features

### 👤 Customer Marketplace
- **Home & Category Discovery**: Browse services like AC Repair, Plumbing, Electrical, Cleaning, and Carpentry.
- **Worker Matching**: Filter local verified service providers by rating, distance, price, and skills in Rajkot & Gujarat.
- **Booking Flow**: Multi-step booking with address selection, scheduled time-slots, price calculation, platform fees, and live order status.
- **Active Bookings & Tracking**: Real-time status updates (`PENDING` → `ACCEPTED` → `ON_THE_WAY` → `IN_PROGRESS` → `COMPLETED`).
- **Review & Ratings**: Post job completion feedback and worker ratings.

### 👷 Worker App Experience
- **Live Job Board**: Instant notifications of customer requests with distance, price, and quick Accept/Decline actions.
- **Active Job Management**: Lifecycle management with customer contact, step-by-step timeline, OTP verification, and completion triggers.
- **Earnings & Analytics**: Daily/monthly payout tracking, settlement history, and metrics.
- **Profile Management**: Bio, document verification badges, skill tagging, and service radius adjustments.
- **Online/Offline Switch**: Instant toggle for availability with active job safeguards.

### 🎨 Design & Experience
- Curated high-contrast color system and typography with seamless Light/Dark theme switching.
- Asymmetric stats cards, live bottom sheets, and haptic feedback.
- Interactive HTML/JS Web prototype in `preview/` for rapid design demonstration.

---

## 🏗️ Architecture & Tech Stack

- **UI**: 100% Jetpack Compose with Material 3 tokens and custom components
- **Architecture**: MVVM with unidirectional data flow and Kotlin Coroutines / `StateFlow`
- **Backend / Database**: Cloud Firestore with real-time snapshot listeners & fallback store
- **Authentication**: Google Sign-In via Android Credential Manager API & Firebase Auth
- **Testing**: Robolectric, Roborazzi screenshot testing, and Android Instrumented tests

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug | 2024.2+ or Android CLI
- JDK 17 / JDK 21+
- Android SDK 34 (Android 14) / Min SDK 26

### Building the Android App
```bash
# Clone the repository
git clone https://github.com/mvvarmora/find-my-work.git

# Navigate to project directory
cd find-my-work

# Build debug APK
./gradlew assembleDebug
```

### Running the Live Interactive Web Preview
The project includes a full-featured web simulation of both the customer marketplace and worker app:
```bash
node preview/server.js
# Open http://localhost:3000 in your browser
```

---

## 👥 Contributors & Collaborators

- **Meet Varmora** ([@mvvarmora](https://github.com/mvvarmora)) - Project Lead & Core Architecture
- **Rudra Shah** ([@genius-398](https://github.com/genius-398)) - Customer Marketplace, Booking Flow & UI/UX Redesign
