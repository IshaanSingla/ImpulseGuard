package com.example.impulseguard.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.impulseguard.permissions.PermissionsHelper

/** Restarts monitoring after a reboot, provided the user already granted the
 * permissions it needs — never requests anything itself. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        if (!PermissionsHelper.hasUsageAccess(context)) return
        AppMonitorService.start(context.applicationContext)
    }
}
