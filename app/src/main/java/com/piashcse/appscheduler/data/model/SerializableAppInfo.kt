package com.piashcse.appscheduler.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SerializableAppInfo(
    val packageName: String,
    val appName: String
)

// Extension function to convert between types
fun AppInfo.toSerializable(): SerializableAppInfo = SerializableAppInfo(
    packageName = packageName,
    appName = appName
)

fun SerializableAppInfo.toAppInfo(): AppInfo = AppInfo(
    packageName = packageName,
    appName = appName,
    icon = null // Will need to be loaded separately
)
