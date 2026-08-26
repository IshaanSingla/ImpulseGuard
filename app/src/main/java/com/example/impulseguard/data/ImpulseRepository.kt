package com.example.impulseguard.data

import com.example.impulseguard.data.local.AppDatabase
import com.example.impulseguard.data.local.PurchaseLogEntity
import com.example.impulseguard.data.local.SessionEntity
import com.example.impulseguard.data.local.WatchedAppEntity
import com.example.impulseguard.data.settings.SettingsDataStore
import com.example.impulseguard.data.settings.UserSettings
import com.example.impulseguard.model.ColorRole
import com.example.impulseguard.model.DefaultWatchedApps
import com.example.impulseguard.model.LastPurchase
import com.example.impulseguard.model.PurchaseLogEntry
import com.example.impulseguard.model.PurchaseSource
import com.example.impulseguard.model.PurchaseTag
import com.example.impulseguard.model.WatchedApp
import java.util.Calendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

data class OverlayDecision(
    val appId: String,
    val appName: String,
    val initial: String,
    val colorRole: ColorRole,
    val sessionId: Long,
    val opensThisMonth: Int,
    val spentThisMonth: Int,
    val lastPurchase: LastPurchase?,
)

/** Everything that reads or mutates app state — the single source of truth for
 * both the UI layer and the background monitoring service. */
class ImpulseRepository(
    private val db: AppDatabase,
    private val settingsStore: SettingsDataStore,
) {
    val settings: Flow<UserSettings> = settingsStore.settings

    val watchedApps: Flow<List<WatchedApp>> =
        db.watchedAppDao().observeAll().map { list -> list.map { it.toDomain() } }

    val recentPurchases: Flow<List<PurchaseLogEntry>> =
        combine(db.purchaseLogDao().observeRecent(20), watchedApps) { log, apps ->
            val byId = apps.associateBy { it.id }
            log.map { it.toDomain(byId[it.appId]?.name ?: it.appId, byId[it.appId]?.initial ?: "?") }
        }

    /** true/false = a real outcome, null = padding (no data yet). Oldest first, most recent last. */
    val streakOutcomes: Flow<List<Boolean?>> =
        db.sessionDao().observeRecentOutcomes(10).map { outcomes ->
            val real = outcomes.sortedBy { it.startTime }.map { it.won }
            val padding = List((10 - real.size).coerceAtLeast(0)) { null }
            padding + real
        }

    fun totalOpensThisMonth(): Flow<Int> = db.sessionDao().observeCountSince(monthStartMillis())
    fun totalSpentThisMonth(): Flow<Int> = db.purchaseLogDao().observeSpentSince(monthStartMillis())

    fun opensTodayByApp(): Flow<Map<String, Int>> =
        db.sessionDao().observeOpensTodayByApp(dayStartMillis()).map { rows ->
            rows.associate { it.appId to it.cnt }
        }

    suspend fun averageImpulseAmount(): Int =
        db.purchaseLogDao().averageImpulseAmount()?.toInt() ?: DEFAULT_AVG_IMPULSE

    suspend fun seedDefaultAppsIfEmpty() {
        if (db.watchedAppDao().count() == 0) {
            DefaultWatchedApps.CATALOG.forEach { db.watchedAppDao().upsert(it.toEntity()) }
        }
    }

    suspend fun addWatchedApp(packageName: String, name: String, category: String, initial: String) {
        db.watchedAppDao().upsert(
            WatchedAppEntity(
                packageName = packageName,
                name = name,
                category = category,
                initial = initial,
                colorRole = ColorRole.ACCENT2.name,
                weeklyLimit = 2000,
                enabled = true,
            ),
        )
    }

    suspend fun setAppEnabled(appId: String, enabled: Boolean) = db.watchedAppDao().setEnabled(appId, enabled)
    suspend fun setWeeklyLimit(appId: String, value: Int) = db.watchedAppDao().setWeeklyLimit(appId, value)

    suspend fun setEscalationThreshold(value: Int) = settingsStore.setEscalationThreshold(value)
    suspend fun setFoodLimit(value: Int) = settingsStore.setFoodLimit(value)
    suspend fun setShopLimit(value: Int) = settingsStore.setShopLimit(value)
    suspend fun setSmarterTracking(value: Boolean) = settingsStore.setSmarterTracking(value)
    suspend fun setPauseAll(value: Boolean) = settingsStore.setPauseAll(value)
    suspend fun setOnboardingComplete(value: Boolean) = settingsStore.setOnboardingComplete(value)

    /**
     * Called every time the detected foreground app changes (by the real monitoring
     * service, or by the in-app "Simulate" hook — same code path either way).
     * Closes whatever session was open, opens a new one if [packageName] is a
     * watched + enabled app, and returns an [OverlayDecision] when the resulting
     * open-count for that app crosses the user's escalation threshold.
     */
    suspend fun onForegroundAppChanged(packageName: String?): OverlayDecision? {
        val now = System.currentTimeMillis()
        val settings = settingsStore.settings.first()

        val openSession = db.sessionDao().getAnyOpenSession()
        if (openSession != null && openSession.appId != packageName) {
            db.sessionDao().closeSession(openSession.id, now)
        }
        if (settings.pauseAll || packageName == null) return null
        if (openSession != null && openSession.appId == packageName) return null // already tracking this app

        val app = db.watchedAppDao().getByPackage(packageName) ?: return null
        if (!app.enabled) return null

        val sessionId = db.sessionDao().insert(SessionEntity(appId = packageName, startTime = now))
        val opensToday = db.sessionDao().countSince(packageName, dayStartMillis())
        if (opensToday < settings.escalationThreshold) return null

        val opensThisMonth = db.sessionDao().countSince(packageName, monthStartMillis())
        val spentThisMonth = db.purchaseLogDao().sumSinceForApp(packageName, monthStartMillis())
        val lastPurchase = db.purchaseLogDao().getLastForApp(packageName)?.let {
            LastPurchase(
                amount = it.amount,
                daysAgo = ((now - it.timestamp) / (24 * 60 * 60 * 1000L)).toInt(),
                tag = PurchaseTag.valueOf(it.tag),
            )
        }
        db.sessionDao().markOverlayShown(sessionId)
        return OverlayDecision(
            appId = packageName,
            appName = app.name,
            initial = app.initial,
            colorRole = ColorRole.valueOf(app.colorRole),
            sessionId = sessionId,
            opensThisMonth = opensThisMonth,
            spentThisMonth = spentThisMonth,
            lastPurchase = lastPurchase,
        )
    }

    /** Marks the current open session (if any) as ended right now — used when a
     * watched app is explicitly backgrounded without a new foreground app observed. */
    suspend fun closeAnyOpenSession() {
        db.sessionDao().getAnyOpenSession()?.let { db.sessionDao().closeSession(it.id, System.currentTimeMillis()) }
    }

    suspend fun logPurchase(
        appId: String,
        sessionId: Long?,
        amount: Int,
        tag: PurchaseTag,
        source: PurchaseSource,
    ) {
        db.purchaseLogDao().insert(
            PurchaseLogEntity(
                appId = appId,
                sessionId = sessionId,
                amount = amount,
                tag = tag.name,
                timestamp = System.currentTimeMillis(),
                source = source.name,
            ),
        )
    }

    /** For the notification-listener payment-confirmation flow: attribute a detected
     * payment to whichever watched-app session most recently ended, if within [windowMillis]. */
    suspend fun findSessionForPaymentAttribution(windowMillis: Long = 60_000L): SessionEntity? {
        val session = db.sessionDao().getMostRecentlyClosedSession() ?: return null
        val end = session.endTime ?: return null
        return if (System.currentTimeMillis() - end <= windowMillis) session else null
    }

    suspend fun getWatchedApp(appId: String): WatchedApp? = db.watchedAppDao().getByPackage(appId)?.toDomain()

    companion object {
        private const val DEFAULT_AVG_IMPULSE = 450

        fun dayStartMillis(): Long = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        fun monthStartMillis(): Long = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}

private fun WatchedAppEntity.toDomain() = WatchedApp(
    id = packageName,
    name = name,
    category = category,
    initial = initial,
    colorRole = ColorRole.valueOf(colorRole),
    weeklyLimit = weeklyLimit,
    enabled = enabled,
)

private fun WatchedApp.toEntity() = WatchedAppEntity(
    packageName = id,
    name = name,
    category = category,
    initial = initial,
    colorRole = colorRole.name,
    weeklyLimit = weeklyLimit,
    enabled = enabled,
)

private fun PurchaseLogEntity.toDomain(appName: String, initial: String) = PurchaseLogEntry(
    id = id,
    appId = appId,
    appName = appName,
    initial = initial,
    amount = amount,
    tag = PurchaseTag.valueOf(tag),
    timestamp = timestamp,
)
