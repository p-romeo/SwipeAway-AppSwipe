package com.example.utils

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.data.SwipeRecord
import java.io.File

object AppLoader {
    fun getInstalledLauncherApps(context: Context): List<SwipeRecord> {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = try {
            packageManager.queryIntentActivities(intent, 0)
        } catch (e: Exception) {
            emptyList()
        }
        
        val apps = mutableListOf<SwipeRecord>()
        val seenPackages = mutableSetOf<String>()
        val systemPackageName = context.packageName // skip our own app
        
        // Pre-query usage statistics in batch for efficiency if permission is granted
        val usageStatsMap = try {
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            val endTime = System.currentTimeMillis()
            val startTime = endTime - (365L * 24 * 60 * 60 * 1000) // last 1 year
            usageStatsManager?.queryAndAggregateUsageStats(startTime, endTime)
        } catch (e: Exception) {
            null
        }
        
        for (info in resolveInfos) {
            val actInfo = info.activityInfo ?: continue
            val packageName = actInfo.packageName ?: continue
            if (packageName == systemPackageName) continue
            if (seenPackages.contains(packageName)) continue
            seenPackages.add(packageName)
            
            try {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                val appLabel = (packageManager.getApplicationLabel(appInfo) ?: packageName).toString()
                val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                if (isSystem) {
                    continue // Skip system apps
                }
                
                // Get ACTUAL physical APK storage size from file system!
                val sizeBytes = try {
                    File(appInfo.sourceDir).length()
                } catch (e: Exception) {
                    0L
                }
                val sizeMb = (sizeBytes / (1024L * 1024L)).toInt().coerceAtLeast(1)
                
                // Get ACTUAL app info timestamps
                val packageInfo = try {
                    packageManager.getPackageInfo(packageName, 0)
                } catch (e: Exception) {
                    null
                }
                val lastUpdateTime = packageInfo?.lastUpdateTime ?: System.currentTimeMillis()
                
                // Extract last used days from UsageStatsManager or fall back to actual installation/update time
                var lastUsed = -1
                if (usageStatsMap != null) {
                    val stats = usageStatsMap[packageName]
                    if (stats != null && stats.lastTimeUsed > 0L) {
                        val diffMs = System.currentTimeMillis() - stats.lastTimeUsed
                        lastUsed = (diffMs / (24 * 60 * 60 * 1000)).toInt().coerceAtLeast(0)
                    }
                }
                
                if (lastUsed == -1) {
                    // Real system fallback usage duration estimation based on installation/update
                    val diffMs = System.currentTimeMillis() - lastUpdateTime
                    lastUsed = (diffMs / (24 * 60 * 60 * 1000)).toInt().coerceAtLeast(0)
                }
                
                // Determine category based on package and label
                val category = determineCategory(packageName, appLabel)
                
                apps.add(
                    SwipeRecord(
                        packageName = packageName,
                        appName = appLabel,
                        category = category,
                        storageSizeMb = sizeMb,
                        lastUsedDaysAgo = lastUsed,
                        swipeStatus = "PENDING",
                        isSystemApp = isSystem,
                        swipeTimestamp = 0L
                    )
                )
            } catch (e: Exception) {
                // Ignore individual errors
            }
        }
        
        return apps
    }
    
    fun determineCategory(packageName: String, appLabel: String): String {
        val lowerPkg = packageName.lowercase()
        val lowerLabel = appLabel.lowercase()
        return when {
            lowerPkg.contains("game") || lowerPkg.contains("supercell") || lowerPkg.contains("king.candy") || lowerLabel.contains("game") || lowerLabel.contains("clash") || lowerLabel.contains("pubg") || lowerLabel.contains("crush") || lowerLabel.contains("play") -> "Games"
            lowerPkg.contains("social") || lowerPkg.contains("instagram") || lowerPkg.contains("facebook") || lowerPkg.contains("whatsapp") || lowerPkg.contains("twitter") || lowerPkg.contains("tiktok") || lowerLabel.contains("chat") || lowerLabel.contains("messenger") || lowerLabel.contains("duo") -> "Social"
            lowerPkg.contains("music") || lowerPkg.contains("spotify") || lowerPkg.contains("video") || lowerPkg.contains("netflix") || lowerPkg.contains("youtube") || lowerPkg.contains("tv") || lowerLabel.contains("music") || lowerLabel.contains("player") || lowerLabel.contains("video") -> "Entertainment"
            lowerPkg.contains("office") || lowerPkg.contains("doc") || lowerPkg.contains("pdf") || lowerPkg.contains("adobe") || lowerPkg.contains("notes") || lowerPkg.contains("mail") || lowerLabel.contains("word") || lowerLabel.contains("sheet") || lowerLabel.contains("drive") || lowerLabel.contains("calendar") || lowerLabel.contains("note") -> "Productivity"
            lowerPkg.contains("tools") || lowerPkg.contains("cleaner") || lowerPkg.contains("utility") || lowerPkg.contains("browser") || lowerPkg.contains("firefox") || lowerPkg.contains("chrome") || lowerPkg.contains("setting") || lowerLabel.contains("clean") || lowerLabel.contains("file") || lowerLabel.contains("security") || lowerLabel.contains("network") -> "Utilities"
            else -> "Other"
        }
    }
}
