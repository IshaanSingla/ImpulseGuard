package com.example.impulseguard.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.impulseguard.ImpulseGuardApplication
import com.example.impulseguard.model.MockAppSession
import com.example.impulseguard.model.OverlaySession
import com.example.impulseguard.model.PermissionItem
import com.example.impulseguard.model.PurchaseLogEntry
import com.example.impulseguard.model.PurchaseSource
import com.example.impulseguard.model.PurchaseTag
import com.example.impulseguard.model.Stage
import com.example.impulseguard.model.Tab
import com.example.impulseguard.model.WatchedApp
import com.example.impulseguard.model.trailingStreak
import com.example.impulseguard.permissions.PermissionsHelper
import com.example.impulseguard.service.AppMonitorService
import kotlinx.coroutines.launch

class ImpulseViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as ImpulseGuardApplication).repository

    var stage by mutableStateOf(Stage.LOADING)
    var onboardStep by mutableIntStateOf(0)
    var tab by mutableStateOf(Tab.HOME)

    var watchedApps by mutableStateOf<List<WatchedApp>>(emptyList())
    var opensToday by mutableStateOf<Map<String, Int>>(emptyMap())
    var purchaseLog by mutableStateOf<List<PurchaseLogEntry>>(emptyList())
    var last10 by mutableStateOf<List<Boolean?>>(List(10) { null })
    var totalOpensMonth by mutableIntStateOf(0)
    var totalSpentMonth by mutableIntStateOf(0)
    private var avgImpulseAmount by mutableIntStateOf(450)

    var escalationThreshold by mutableIntStateOf(3)
    var foodLimit by mutableIntStateOf(2000)
    var shopLimit by mutableIntStateOf(3000)
    var smarterTracking by mutableStateOf(false)
    var pauseAll by mutableStateOf(false)

    var permissions by mutableStateOf(buildPermissionItems(application, whyOpenById = emptyMap()))
    var reinforceMsg by mutableStateOf<String?>(null)

    var overlay by mutableStateOf<OverlaySession?>(null)
    var mockApp by mutableStateOf<MockAppSession?>(null)
    var logSheetOpen by mutableStateOf(false)
    var logAmount by mutableStateOf("0")
    var logTag by mutableStateOf(PurchaseTag.IMPULSE)

    val streakCurrentRun: Int get() = trailingStreak(last10)
    val estSaved: Int get() = last10.count { it == true } * avgImpulseAmount

    init {
        viewModelScope.launch { repo.seedDefaultAppsIfEmpty() }
        viewModelScope.launch { repo.watchedApps.collect { watchedApps = it } }
        viewModelScope.launch { repo.opensTodayByApp().collect { opensToday = it } }
        viewModelScope.launch {
            repo.recentPurchases.collect {
                purchaseLog = it
                avgImpulseAmount = repo.averageImpulseAmount()
            }
        }
        viewModelScope.launch { repo.streakOutcomes.collect { last10 = it } }
        viewModelScope.launch { repo.totalOpensThisMonth().collect { totalOpensMonth = it } }
        viewModelScope.launch { repo.totalSpentThisMonth().collect { totalSpentMonth = it } }
        viewModelScope.launch {
            repo.settings.collect { s ->
                escalationThreshold = s.escalationThreshold
                foodLimit = s.foodLimit
                shopLimit = s.shopLimit
                smarterTracking = s.smarterTracking
                pauseAll = s.pauseAll
                if (stage == Stage.LOADING) stage = if (s.onboardingComplete) Stage.APP else Stage.ONBOARDING
            }
        }
        refreshPermissions()
    }

    fun refreshPermissions() {
        val whyOpenById = permissions.associate { it.id to it.whyOpen }
        permissions = buildPermissionItems(getApplication(), whyOpenById)
        if (stage == Stage.APP && PermissionsHelper.hasUsageAccess(getApplication())) {
            AppMonitorService.start(getApplication())
        }
    }

    fun advanceOnboarding() {
        onboardStep += 1
    }

    fun completeOnboarding() {
        viewModelScope.launch { repo.setOnboardingComplete(true) }
        stage = Stage.APP
        refreshPermissions()
    }

    fun onboardSkip() {
        if (onboardStep < 2) onboardStep += 1 else completeOnboarding()
    }

    fun togglePermissionWhy(id: String) {
        permissions = permissions.map { if (it.id == id) it.copy(whyOpen = !it.whyOpen) else it }
    }

    /** Same code path the real background service uses — this is a genuine trigger,
     * not a fake counter, which is why it persists and interacts correctly with real data. */
    fun simulateOpen(appId: String) {
        viewModelScope.launch {
            if (pauseAll) {
                reinforceMsg = "Monitoring is paused — opened with no pause."
                return@launch
            }
            reinforceMsg = null
            val decision = repo.onForegroundAppChanged(appId) ?: return@launch
            overlay = OverlaySession(
                appId = decision.appId,
                sessionId = decision.sessionId,
                opensThisMonth = decision.opensThisMonth,
                spentThisMonth = decision.spentThisMonth,
                lastPurchase = decision.lastPurchase,
                countdown = 3,
            )
        }
    }

    fun tickOverlayCountdown() {
        val current = overlay ?: return
        if (current.countdown > 0) overlay = current.copy(countdown = current.countdown - 1)
    }

    fun overlayContinue(intendedTag: String) {
        val ov = overlay ?: return
        mockApp = MockAppSession(ov.appId, ov.sessionId, intendedTag)
        overlay = null
    }

    fun mockSimulatePayment() {
        val ma = mockApp ?: return
        logTag = if (ma.intendedTag == "planned") PurchaseTag.PLANNED else PurchaseTag.IMPULSE
        logAmount = "350"
        logSheetOpen = true
    }

    fun mockCloseNoBuy() {
        viewModelScope.launch {
            repo.closeAnyOpenSession()
            mockApp = null
            logSheetOpen = false
            tab = Tab.HOME
            reinforceMsg = "Nice — you left without buying."
        }
    }

    fun submitLog() {
        val ma = mockApp ?: return
        val amount = logAmount.toIntOrNull() ?: 0
        viewModelScope.launch {
            repo.logPurchase(ma.appId, ma.sessionId, amount, logTag, PurchaseSource.MANUAL)
            mockApp = null
            logSheetOpen = false
            tab = Tab.HOME
            reinforceMsg = if (logTag == PurchaseTag.PLANNED) {
                "Logged as planned — that still counts as a win."
            } else {
                "Logged as impulse."
            }
        }
    }

    fun skipLog() {
        mockApp = null
        logSheetOpen = false
        tab = Tab.HOME
    }

    fun toggleAppEnabled(appId: String) {
        viewModelScope.launch {
            val enabled = watchedApps.find { it.id == appId }?.enabled ?: return@launch
            repo.setAppEnabled(appId, !enabled)
        }
    }

    fun addWatchedApp(packageName: String, name: String) {
        viewModelScope.launch {
            repo.addWatchedApp(packageName, name, category = "Other", initial = name.take(1).uppercase())
        }
    }

    fun updateEscalationThreshold(value: Int) {
        escalationThreshold = value
        viewModelScope.launch { repo.setEscalationThreshold(value) }
    }

    fun updateFoodLimit(value: Int) {
        foodLimit = value
        viewModelScope.launch { repo.setFoodLimit(value) }
    }

    fun updateShopLimit(value: Int) {
        shopLimit = value
        viewModelScope.launch { repo.setShopLimit(value) }
    }

    fun toggleSmarterTracking() {
        val newValue = !smarterTracking
        smarterTracking = newValue
        viewModelScope.launch { repo.setSmarterTracking(newValue) }
    }

    fun togglePauseAll() {
        val newValue = !pauseAll
        pauseAll = newValue
        viewModelScope.launch { repo.setPauseAll(newValue) }
    }
}

private fun buildPermissionItems(context: Context, whyOpenById: Map<String, Boolean>): List<PermissionItem> = listOf(
    PermissionItem(
        id = "usage",
        name = "App usage access",
        granted = PermissionsHelper.hasUsageAccess(context),
        whyOpen = whyOpenById["usage"] ?: false,
        why = "Lets ImpulseGuard notice when you open a watched app — nothing else is read.",
    ),
    PermissionItem(
        id = "overlay",
        name = "Display over other apps",
        granted = PermissionsHelper.hasOverlayPermission(context),
        whyOpen = whyOpenById["overlay"] ?: false,
        why = "Needed to show the pause screen on top of the app you just opened. Always dismissible in one tap.",
    ),
    PermissionItem(
        id = "notif",
        name = "Notification access (optional)",
        granted = PermissionsHelper.hasNotificationListenerAccess(context),
        whyOpen = whyOpenById["notif"] ?: false,
        why = "Reads payment-confirmation notifications only, on-device, to auto-log purchases instead of asking you every time.",
    ),
)
