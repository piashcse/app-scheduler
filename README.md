# App Scheduler 📱⏰

A modern Android app scheduler built with **Jetpack Compose**, **MVVM Clean Architecture**, and the latest Android development practices.  
Easily schedule any installed app to launch automatically at your desired time.

## ✨ Features
- 🎯 **Schedule Any App** – Launch any installed app at a specific date and time  
- 📊 **Schedule Management** – View, edit, and delete scheduled tasks  
- 🔄 **Status Tracking** – Monitor pending, executed, cancelled, and failed schedules  
- 🔔 **Exact Alarms** – Precise scheduling using Android's AlarmManager

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
