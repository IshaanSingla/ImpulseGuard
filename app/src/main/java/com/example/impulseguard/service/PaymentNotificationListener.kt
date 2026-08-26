package com.example.impulseguard.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import com.example.impulseguard.ImpulseGuardApplication
import com.example.impulseguard.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

/**
 * Reads only the notification text of whatever posts a notification — on-device,
 * never leaving the phone — looking for a payment-confirmation pattern. If one
 * lands within 60s of a watched-app session ending, prompts a single tap:
 * "Planned or impulse?" This entire path is gated behind the user's own
 * "Smarter tracking" toggle in Settings.
 */
class PaymentNotificationListener : NotificationListenerService() {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName == packageName) return // ignore our own notifications
        val extras = sbn.notification.extras
        val title = extras.getCharSequence("android.title")?.toString().orEmpty()
        val text = extras.getCharSequence("android.text")?.toString().orEmpty()
        val combined = "$title $text"
        val amount = extractPaymentAmount(combined) ?: return

        scope.launch {
            val repository = (application as ImpulseGuardApplication).repository
            val settings = repository.settings.first()
            if (!settings.smarterTracking) return@launch

            val session = repository.findSessionForPaymentAttribution() ?: return@launch
            val app = repository.getWatchedApp(session.appId) ?: return@launch
            promptPlannedOrImpulse(session.id, session.appId, app.name, amount)
        }
    }

    private fun promptPlannedOrImpulse(sessionId: Long, appId: String, appName: String, amount: Int) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(PROMPT_CHANNEL_ID, "Purchase check-ins", NotificationManager.IMPORTANCE_HIGH),
            )
        }

        val plannedIntent = PurchaseActionReceiver.actionIntent(this, sessionId, appId, amount, planned = true)
        val impulseIntent = PurchaseActionReceiver.actionIntent(this, sessionId, appId, amount, planned = false)
        val notificationId = sessionId.toInt()

        val notification = NotificationCompat.Builder(this, PROMPT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("₹$amount at $appName — planned or impulse?")
            .setContentText("Payment confirmation caught right after your session.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(0, "Planned", plannedPendingIntent(notificationId, plannedIntent))
            .addAction(0, "Impulse", plannedPendingIntent(notificationId + 1, impulseIntent))
            .build()

        manager.notify(notificationId, notification)
    }

    private fun plannedPendingIntent(requestCode: Int, intent: Intent): PendingIntent =
        PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    companion object {
        const val PROMPT_CHANNEL_ID = "purchase_checkin"

        private val AMOUNT_REGEX = Regex(
            """(?:₹|rs\.?|inr)\s?([0-9][0-9,]*(?:\.\d{1,2})?)""",
            RegexOption.IGNORE_CASE,
        )
        private val PAYMENT_KEYWORDS = listOf("paid", "payment successful", "debited", "sent", "transaction successful")

        internal fun extractPaymentAmount(text: String): Int? {
            val lower = text.lowercase()
            if (PAYMENT_KEYWORDS.none { lower.contains(it) }) return null
            val match = AMOUNT_REGEX.find(text) ?: return null
            return match.groupValues[1].replace(",", "").toDoubleOrNull()?.toInt()
        }
    }
}
