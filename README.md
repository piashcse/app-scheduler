# App Scheduler 📱⏰

A modern Android app scheduler built with **Jetpack Compose**, **MVVM Architecture** and the latest Android development practices.  
Easily schedule any installed app to launch automatically at your desired time.

## ✨ Features
- 🎯 **Schedule Any App** – Launch any installed app at a specific date and time  
- 📊 **Schedule Management** – View, edit, and delete scheduled tasks  
- 🔄 **Status Tracking** – Monitor pending, executed, cancelled, and failed schedules  
- 🔔 **Exact Alarms** – Precise scheduling using Android's AlarmManager
- 🗄 Local Storage - Room database

## 📱 Screenshots
<p align="center">
  <img width="35%" src="https://github.com/piashcse/app-scheduler/blob/main/screenshots/screenshot_1755706113.png" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <img width="35%" src="https://github.com/piashcse/app-scheduler/blob/main/screenshots/screenshot_1755706109.png" />
</p>
<br>
<p align="center">
  <img width="35%" src="https://github.com/piashcse/app-scheduler/blob/main/screenshots/screenshot_1755706121.png" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <img width="35%" src="https://github.com/piashcse/app-scheduler/blob/main/screenshots/screenshot_1755706116.png" />
</p>
<br>
<p align="center">
  <img width="80%" src="https://github.com/piashcse/app-scheduler/blob/main/screenshots/screenshot 2025-08-20-at-10.09.57-pm.png" /> </br>
  <img width="80%" src="https://github.com/piashcse/app-scheduler/blob/main/screenshots/screenshot 2025-08-20-at-10.10.12-pm.png" />
  <img width="80%" src="https://github.com/piashcse/app-scheduler/blob/main/screenshots/screenshot 2025-08-20-at-10.10.32-pm.png" />
  <img width="80%" src="https://github.com/piashcse/app-scheduler/blob/main/screenshots/screenshot-2025-08-20-at-10.09.39-pm.png" />
</p>

## Project Structure 

```
AppScheduler/
├── app/
│   ├── manifests/
│   │   └── AndroidManifest.xml
│   ├── kotlin+java/
│   │   └── com.piashcse.appscheduler/
│   │       ├── data/
│   │       │   ├── local/
│   │       │   ├── model/
│   │       │   └── repository/
│   │       ├── di/
│   │       │   ├── DatabaseModule.kt
│   │       │   └── RepositoryModule.kt
│   │       ├── receiver/
│   │       │   ├── AppLaunchReceiver.kt
│   │       │   └── BootReceiver.kt
│   │       ├── ui/
│   │       │   ├── component/
│   │       │   ├── screen/
│   │       │   └── theme/
│   │       ├── utils/
│   │       │   ├── AlarmUtils.kt
│   │       │   ├── AppUtils.kt
│   │       │   ├── Converters.kt
│   │       │   └── TimeUtils.kt
│   │       ├── Application.kt
│   │       └── MainActivity.kt
│   │   ├── com.piashcse.appscheduler (androidTest)/
│   │   └── com.piashcse.appscheduler (test)/
│   ├── java (generated)/
│   ├── res/
│   └── res (generated)/
├── Gradle Scripts/
│   ├── build.gradle.kts (Project: App_Scheduler)
│   ├── build.gradle.kts (Module: app)
│   ├── proguard-rules.pro (ProGuard Rules for ":app")
│   ├── gradle.properties (Project Properties)
│   └── libs.versions.toml (Version Catalog)
└── README.md
```
## 🖥 Main Interface  
- **Schedules Tab** – View all your scheduled apps with status badges  
- **Apps Tab** – Browse and select from installed apps  
- **Statistics** – Monitor your scheduling activity

## ⚙️ Schedule Management  
- **Create Schedule** – Pick date/time for any installed app  
-  **Status Tracking** – Flow: **PENDING → EXECUTED / CANCELLED / FAILED**  
-  **Quick Actions** – Edit, cancel, or retry schedules

# 🛠 Tech Stack

## Core Technologies
- **Language** - Kotlin 2.2.10
- **UI Framework** - Jetpack Compose (Material 3)
- **Architecture** - MVVM
- **Build System** - Gradle with Version Catalogs (TOML)

## 🔧 Libraries & Dependencies
-  **Build Tools** -  KSP (Kotlin Symbol Processing)
-  **DI** - Hilt for dependency injection
-  **Async** - Kotlin Coroutines & Flow
-  **Scheduling** - Android AlarmManager
-  **State** - StateFlow & Compose State

# 🧪 Testing
## Unit Tests Coverage
- **ScheduleDao Tests** - Database operations (7 tests)
- **MainViewModel Tests**-  Business logic (11 tests)
- **Repository Tests** - Data layer (8 tests)
- **Model Tests** - Data validation (3 tests)

# 📋 Usage
## Creating a Schedule
- Navigate to Apps tab
- Tap the notification icon next to desired app
- Select date and time in the dialog
- Tap Schedule to confirm


## Managing Schedules
- **View** - Check Schedules tab for all scheduled tasks
- **Filter** - Use status chips (PENDING, EXECUTED, etc.)
- **Edit** - Tap edit icon to modify schedule time
- **Cancel** - Tap delete icon to cancel pending schedules
- **Retry** - Retry failed schedules (up to 3 attempts)


## 👨 Developed By

<a href="https://twitter.com/piashcse" target="_blank">
  <img src="https://avatars.githubusercontent.com/piashcse" width="80" align="left">
</a>

**Mehedi Hassan Piash**

[![Twitter](https://img.shields.io/badge/-Twitter-1DA1F2?logo=x&logoColor=white&style=for-the-badge)](https://twitter.com/piashcse)
[![Medium](https://img.shields.io/badge/-Medium-00AB6C?logo=medium&logoColor=white&style=for-the-badge)](https://medium.com/@piashcse)
[![Linkedin](https://img.shields.io/badge/-LinkedIn-0077B5?logo=linkedin&logoColor=white&style=for-the-badge)](https://www.linkedin.com/in/piashcse/)
[![Web](https://img.shields.io/badge/-Web-0073E6?logo=appveyor&logoColor=white&style=for-the-badge)](https://piashcse.github.io/)
[![Blog](https://img.shields.io/badge/-Blog-0077B5?logo=readme&logoColor=white&style=for-the-badge)](https://piashcse.blogspot.com)
