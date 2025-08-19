package com.piashcse.appscheduler.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import com.piashcse.appscheduler.data.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AppUtils {

    private const val TAG = "AppUtils"

    /**
     * Get list of installed user apps (excluding system apps)
     */
    suspend fun getInstalledApps(context: Context): List<AppInfo> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val installedApps = mutableListOf<AppInfo>()

        try {
            val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            for (packageInfo in packages) {
                // Filter out system apps
                if (packageInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                    val appName = packageManager.getApplicationLabel(packageInfo).toString()
                    val packageName = packageInfo.packageName
                    val icon = try {
                        packageManager.getApplicationIcon(packageInfo)
                    } catch (e: Exception) {
                        null
                    }

                    installedApps.add(
                        AppInfo(
                            packageName = packageName,
                            appName = appName,
                            icon = icon
                        )
                    )
                }
            }

            // Sort by app name
            installedApps.sortBy { it.appName.lowercase() }

        } catch (e: Exception) {
            Log.e(TAG, "Error getting installed apps", e)
        }

        installedApps
    }

    /**
     * Launch an app by package name
     */
    fun launchApp(context: Context, packageName: String): Boolean {
        return try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                Log.d(TAG, "Successfully launched app: $packageName")
                true
            } else {
                Log.e(TAG, "No launch intent found for package: $packageName")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch app: $packageName", e)
            false
        }
    }

    /**
     * Check if an app is installed
     */
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * Get app name from package name
     */
    fun getAppName(context: Context, packageName: String): String? {
        return try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: Exception) {
            null
        }
    }
}