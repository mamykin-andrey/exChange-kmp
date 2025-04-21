💱 KMP Currency Exchange App
A Kotlin Multiplatform (KMP) showcase application that fetches currency exchange rates from https://exchangeratesapi.io and calculates exchange rates based on a selected currency.

| Platform | UI |
|----------|------------|
| Android  | <img src="android_main.gif" alt="Android UI" width="300"/> |
| iOS      | <img src="ios_main.gif" alt="iOS UI" width="300"/> |

🧱 Tech Stack

- Kotlin Multiplatform Mobile (KMM) - Android & iOS
- Ktor
- Kotlin Coroutines + Flow
- Koin
- Clean Architecture + MVVM
- Swift UI (iOS) + Jetpack Compose (Android)

🚀 Getting Started

Prerequisites
- Android Studio Giraffe or higher with KMP plugin support
- Xcode 14+ for iOS builds
- Access token from https://exchangeratesapi.io

Setup
1. Clone the repo:
`git clone git@github.com:mamykin-andrey/exChange-kmp.git && cd exChange`
2. Add your API key to local.properties as `exchangeapikey=YOUR_KEY`
3. Run the app:
3.1 Android: Open the project in Android Studio and run on an emulator or device.
3.2 iOS: Open the iosApp folder with Xcode and run on a simulator or device.

🧪 Architecture Overview

```
|- androidApp
|--- Jetpack Compose code
|- iosApp
|--- Swift UI code
|- shared
|--- data             # Data sources, API, DTOs
|--- domain           # Use cases, domain models
|--- presentation     # ViewModels, UI states
|--- di               # Dependency injection
```
