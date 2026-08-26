package com.example.impulseguard.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.impulseguard.ImpulseGuardApplication
import com.example.impulseguard.model.PurchaseSource
import com.example.impulseguard.model.PurchaseTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Handles the "Planned" / "Impulse" tap from the payment-confirmation prompt —
 * a single tap writes straight to the database, no app-open required. */
class PurchaseActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val sessionId = intent.getLongExtra(EXTRA_SESSION_ID, -1L)
        val appId = intent.getStringExtra(EXTRA_APP_ID) ?: return
        val amount = intent.getIntExtra(EXTRA_AMOUNT, 0)
        val planned = intent.getBooleanExtra(EXTRA_PLANNED, false)

        val pendingResult = goAsync()
        val repository = (context.applicationContext as ImpulseGuardApplication).repository
        CoroutineScope(Dispatchers.IO).launch {
            try {
                repository.logPurchase(
                    appId = appId,
                    sessionId = sessionId.takeIf { it >= 0 },
                    amount = amount,
                    tag = if (planned) PurchaseTag.PLANNED else PurchaseTag.IMPULSE,
                    source = PurchaseSource.NOTIFICATION,
                )
            } finally {
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.cancel(sessionId.toInt())
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val EXTRA_SESSION_ID = "session_id"
        private const val EXTRA_APP_ID = "app_id"
        private const val EXTRA_AMOUNT = "amount"
        private const val EXTRA_PLANNED = "planned"

        fun actionIntent(context: Context, sessionId: Long, appId: String, amount: Int, planned: Boolean): Intent =
            Intent(context, PurchaseActionReceiver::class.java).apply {
                putExtra(EXTRA_SESSION_ID, sessionId)
                putExtra(EXTRA_APP_ID, appId)
                putExtra(EXTRA_AMOUNT, amount)
                putExtra(EXTRA_PLANNED, planned)
            }
    }
}
