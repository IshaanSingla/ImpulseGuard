package com.example.impulseguard.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.impulseguard.ImpulseGuardApplication
import com.example.impulseguard.MainActivity
import com.example.impulseguard.R
import com.example.impulseguard.model.trailingStreak
import com.example.impulseguard.permissions.PermissionsHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Foreground service that polls [UsageStatsManager] every [POLL_INTERVAL_MS] — only
 * while the screen is on — to notice when a watched app comes to the front, and
 * hands the decision off to [ImpulseRepository]/[OverlayController].
 */
class AppMonitorService : LifecycleService() {

    private val repository by lazy { (application as ImpulseGuardApplication).repository }
    private var pollingJob: Job? = null
    private var lastEventTime: Long = System.currentTimeMillis()
    private var currentForegroundPackage: String? = null
    private lateinit var screenStateReceiver: ScreenStateReceiver

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, buildNotification())

        screenStateReceiver = ScreenStateReceiver(
            onScreenOn = { startPolling() },
            onScreenOff = { stopPolling() },
        )
        ContextCompat.registerReceiver(
            this,
            screenStateReceiver,
            IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
            },
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        startPolling()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    private fun startPolling() {
        if (pollingJob?.isActive == true) return
        pollingJob = lifecycleScope.launch {
            while (isActive) {
                if (PermissionsHelper.hasUsageAccess(this@AppMonitorService)) {
                    pollOnce()
                }
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    private fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private suspend fun pollOnce() {
        val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val now = System.currentTimeMillis()
        val events = usm.queryEvents(lastEventTime, now)
        lastEventTime = now

        var latestForegroundPackage: String? = null
        val event = UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            @Suppress("DEPRECATION")
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                latestForegroundPackage = event.packageName
            }
        }

        val newForeground = latestForegroundPackage ?: return
        if (newForeground == currentForegroundPackage) return
        currentForegroundPackage = newForeground

        val decision = repository.onForegroundAppChanged(newForeground) ?: return
        if (PermissionsHelper.hasOverlayPermission(this)) {
            val streak = trailingStreak(repository.streakOutcomes.first())
            OverlayController.show(applicationContext, decision, streak)
        }
    }

    private fun buildNotification(): Notification {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Monitoring",
                NotificationManager.IMPORTANCE_MIN,
            ).apply { description = "ImpulseGuard is watching for your watched apps" }
            manager.createNotificationChannel(channel)
        }
        val openIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ImpulseGuard is watching")
            .setContentText("Tap to open · nothing you type or view is ever read")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(openIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPolling()
        runCatching { unregisterReceiver(screenStateReceiver) }
    }

    companion object {
        private const val CHANNEL_ID = "monitoring"
        private const val NOTIFICATION_ID = 1001
        private const val POLL_INTERVAL_MS = 2500L

        fun start(context: Context) {
            val intent = Intent(context, AppMonitorService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, AppMonitorService::class.java))
        }
    }
}
