package com.example.impulseguard.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.text.NumberFormat
import java.util.Locale

fun formatRupees(n: Int): String {
    val nf = NumberFormat.getNumberInstance(Locale("en", "IN"))
    return nf.format(n)
}

enum class ColorRole { ACCENT, ACCENT2 }
enum class PurchaseTag { IMPULSE, PLANNED, REGRET }
enum class Stage { ONBOARDING, APP }
enum class Tab { HOME, STREAK, APPS, SETTINGS }

data class LastPurchase(val amount: Int, val daysAgo: Int, val tag: PurchaseTag)

data class WatchedApp(
    val id: String,
    val name: String,
    val category: String,
    val initial: String,
    val colorRole: ColorRole,
    val opensThisMonth: Int,
    val spentThisMonth: Int,
    val weeklyLimit: Int,
    val enabled: Boolean,
    val lastPurchase: LastPurchase?,
)

data class PurchaseLogEntry(
    val id: Long,
    val appId: String,
    val appName: String,
    val initial: String,
    val amount: Int,
    val tag: PurchaseTag,
    val timeAgo: String,
)

data class PermissionItem(
    val id: String,
    val name: String,
    val granted: Boolean,
    val whyOpen: Boolean,
    val why: String,
)

data class OverlaySession(val appId: String, val countdown: Int)
data class MockAppSession(val appId: String, val intendedTag: String) // "planned" | "browsing"

fun tagLabel(tag: PurchaseTag): String = when (tag) {
    PurchaseTag.IMPULSE -> "Impulse"
    PurchaseTag.PLANNED -> "Planned"
    PurchaseTag.REGRET -> "Regret"
}

class AppUiState {
    var stage by mutableStateOf(Stage.ONBOARDING)
    var onboardStep by mutableIntStateOf(0)
    var tab by mutableStateOf(Tab.HOME)

    var watchedApps by mutableStateOf(
        listOf(
            WatchedApp("zomato", "Zomato", "Food delivery", "Z", ColorRole.ACCENT, 14, 6200, 2000, true, LastPurchase(450, 2, PurchaseTag.REGRET)),
            WatchedApp("swiggy", "Swiggy", "Food delivery", "S", ColorRole.ACCENT, 6, 1800, 1500, true, LastPurchase(300, 5, PurchaseTag.IMPULSE)),
            WatchedApp("amazon", "Amazon", "Shopping", "A", ColorRole.ACCENT2, 9, 4300, 3000, true, LastPurchase(1200, 1, PurchaseTag.IMPULSE)),
            WatchedApp("myntra", "Myntra", "Shopping", "M", ColorRole.ACCENT2, 3, 0, 2000, false, null),
        ),
    )

    var opensToday by mutableStateOf(mapOf("zomato" to 2, "swiggy" to 0, "amazon" to 1, "myntra" to 0))
    var escalationThreshold by mutableIntStateOf(3)
    var foodLimit by mutableIntStateOf(2000)
    var shopLimit by mutableIntStateOf(3000)
    var smarterTracking by mutableStateOf(false)
    var last10 by mutableStateOf(listOf(true, true, false, true, true, true, false, true, true, true))

    var purchaseLog by mutableStateOf(
        listOf(
            PurchaseLogEntry(1, "zomato", "Zomato", "Z", 450, PurchaseTag.REGRET, "2 days ago"),
            PurchaseLogEntry(2, "amazon", "Amazon", "A", 1200, PurchaseTag.IMPULSE, "1 day ago"),
            PurchaseLogEntry(3, "swiggy", "Swiggy", "S", 300, PurchaseTag.IMPULSE, "5 days ago"),
        ),
    )

    var permissions by mutableStateOf(
        listOf(
            PermissionItem("usage", "App usage access", granted = true, whyOpen = false, why = "Lets ImpulseGuard notice when you open a watched app — nothing else is read."),
            PermissionItem("overlay", "Display over other apps", granted = true, whyOpen = false, why = "Needed to show the pause screen on top of the app you just opened. Always dismissible in one tap."),
            PermissionItem("notif", "Notification access (optional)", granted = false, whyOpen = false, why = "Reads payment-confirmation notifications only, on-device, to auto-log purchases instead of asking you every time."),
        ),
    )

    var pauseAll by mutableStateOf(false)
    var reinforceMsg by mutableStateOf<String?>(null)

    var overlay by mutableStateOf<OverlaySession?>(null)
    var mockApp by mutableStateOf<MockAppSession?>(null)
    var logSheetOpen by mutableStateOf(false)
    var logAmount by mutableStateOf("0")
    var logTag by mutableStateOf(PurchaseTag.IMPULSE)

    val streakCurrentRun: Int
        get() {
            var n = 0
            for (i in last10.indices.reversed()) {
                if (last10[i]) n++ else break
            }
            return n
        }

    val totalOpensMonth: Int get() = watchedApps.sumOf { it.opensThisMonth }
    val totalSpentMonth: Int get() = watchedApps.sumOf { it.spentThisMonth }
    val estSaved: Int get() = last10.count { it } * 450

    fun onboardPrimary() {
        when (onboardStep) {
            0 -> onboardStep = 1
            1 -> {
                onboardStep = 2
                permissions = permissions.map { if (it.id == "usage") it.copy(granted = true) else it }
            }
            else -> {
                stage = Stage.APP
                permissions = permissions.map { if (it.id == "overlay") it.copy(granted = true) else it }
            }
        }
    }

    fun onboardSkip() {
        if (onboardStep < 2) onboardStep += 1 else stage = Stage.APP
    }

    fun simulateOpen(appId: String) {
        if (pauseAll) {
            reinforceMsg = "Monitoring is paused — opened with no pause."
            return
        }
        val today = (opensToday[appId] ?: 0) + 1
        opensToday = opensToday + (appId to today)
        reinforceMsg = null
        if (today >= escalationThreshold) {
            overlay = OverlaySession(appId, countdown = 3)
        }
    }

    fun tickOverlayCountdown() {
        val current = overlay ?: return
        if (current.countdown > 0) overlay = current.copy(countdown = current.countdown - 1)
    }

    fun overlayContinue(intendedTag: String) {
        val appId = overlay?.appId ?: return
        mockApp = MockAppSession(appId, intendedTag)
        overlay = null
    }

    fun mockSimulatePayment() {
        val intended = mockApp?.intendedTag
        logTag = if (intended == "planned") PurchaseTag.PLANNED else PurchaseTag.IMPULSE
        logAmount = "350"
        logSheetOpen = true
    }

    fun mockCloseNoBuy() {
        last10 = last10.drop(1) + true
        mockApp = null
        logSheetOpen = false
        tab = Tab.HOME
        reinforceMsg = "Nice — you left without buying. That's ${last10.count { it }} of your last 10."
    }

    fun submitLog() {
        val appId = mockApp?.appId ?: return
        val app = watchedApps.find { it.id == appId } ?: return
        val amount = logAmount.toIntOrNull() ?: 0
        val won = logTag == PurchaseTag.PLANNED
        last10 = last10.drop(1) + won
        val entry = PurchaseLogEntry(System.currentTimeMillis(), appId, app.name, app.initial, amount, logTag, "Just now")
        watchedApps = watchedApps.map {
            if (it.id == appId) it.copy(spentThisMonth = it.spentThisMonth + amount, lastPurchase = LastPurchase(amount, 0, logTag)) else it
        }
        purchaseLog = listOf(entry) + purchaseLog
        mockApp = null
        logSheetOpen = false
        tab = Tab.HOME
        reinforceMsg = if (won) "Logged as planned — that still counts as a win." else "Logged. That's ${last10.count { it }} of your last 10 without an impulse buy."
    }

    fun skipLog() {
        mockApp = null
        logSheetOpen = false
        tab = Tab.HOME
    }

    fun togglePermissionWhy(id: String) {
        permissions = permissions.map { if (it.id == id) it.copy(whyOpen = !it.whyOpen) else it }
    }

    fun toggleAppEnabled(id: String) {
        watchedApps = watchedApps.map { if (it.id == id) it.copy(enabled = !it.enabled) else it }
    }

    fun toggleSmarterTracking() {
        smarterTracking = !smarterTracking
        permissions = permissions.map { if (it.id == "notif") it.copy(granted = smarterTracking) else it }
    }

    fun togglePauseAll() {
        pauseAll = !pauseAll
    }
}
