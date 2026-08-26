package com.example.impulseguard.model

import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

private val EN_IN: Locale = Locale.Builder().setLanguage("en").setRegion("IN").build()

fun formatRupees(n: Int): String {
    val nf = NumberFormat.getNumberInstance(EN_IN)
    return nf.format(n)
}

fun timeAgoLabel(timestampMillis: Long, nowMillis: Long = System.currentTimeMillis()): String {
    val diff = (nowMillis - timestampMillis).coerceAtLeast(0)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes min ago"
        hours < 24 -> if (hours == 1L) "1 hour ago" else "$hours hours ago"
        days < 1 -> "Today"
        days == 1L -> "1 day ago"
        else -> "$days days ago"
    }
}

enum class ColorRole { ACCENT, ACCENT2 }
enum class PurchaseTag { IMPULSE, PLANNED, REGRET }
enum class PurchaseSource { MANUAL, NOTIFICATION }
enum class Stage { LOADING, ONBOARDING, APP }
enum class Tab { HOME, STREAK, APPS, SETTINGS }

data class LastPurchase(val amount: Int, val daysAgo: Int, val tag: PurchaseTag)

data class WatchedApp(
    val id: String,
    val name: String,
    val category: String,
    val initial: String,
    val colorRole: ColorRole,
    val weeklyLimit: Int,
    val enabled: Boolean,
)

/** Real, well-known package names — matched against the actual foreground app on-device. */
object DefaultWatchedApps {
    val CATALOG = listOf(
        WatchedApp("com.application.zomato", "Zomato", "Food delivery", "Z", ColorRole.ACCENT, 2000, true),
        WatchedApp("in.swiggy.android", "Swiggy", "Food delivery", "S", ColorRole.ACCENT, 1500, true),
        WatchedApp("in.amazon.mShop.android.shopping", "Amazon", "Shopping", "A", ColorRole.ACCENT2, 3000, true),
        WatchedApp("com.myntra.android", "Myntra", "Shopping", "M", ColorRole.ACCENT2, 2000, false),
    )
}

data class PurchaseLogEntry(
    val id: Long,
    val appId: String,
    val appName: String,
    val initial: String,
    val amount: Int,
    val tag: PurchaseTag,
    val timestamp: Long,
) {
    val timeAgo: String get() = timeAgoLabel(timestamp)
}

data class PermissionItem(
    val id: String,
    val name: String,
    val granted: Boolean,
    val whyOpen: Boolean,
    val why: String,
)

data class OverlaySession(
    val appId: String,
    val sessionId: Long,
    val opensThisMonth: Int,
    val spentThisMonth: Int,
    val lastPurchase: LastPurchase?,
    val countdown: Int,
)

data class MockAppSession(val appId: String, val sessionId: Long, val intendedTag: String) // "planned" | "browsing"

fun tagLabel(tag: PurchaseTag): String = when (tag) {
    PurchaseTag.IMPULSE -> "Impulse"
    PurchaseTag.PLANNED -> "Planned"
    PurchaseTag.REGRET -> "Regret"
}

/** true = no purchase followed that open, false = a purchase followed, null = no real data yet. */
fun trailingStreak(outcomes: List<Boolean?>): Int {
    var n = 0
    for (i in outcomes.indices.reversed()) {
        if (outcomes[i] == true) n++ else break
    }
    return n
}
