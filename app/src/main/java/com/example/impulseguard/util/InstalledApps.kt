package com.example.impulseguard.util

import android.content.Context
import android.content.pm.PackageManager

data class InstalledApp(val name: String, val packageName: String)

fun getInstalledLaunchableApps(context: Context): List<InstalledApp> {
    val pm = context.packageManager
    return pm.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0))
        .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
        .map { InstalledApp(name = pm.getApplicationLabel(it).toString(), packageName = it.packageName) }
        .sortedBy { it.name.lowercase() }
}
