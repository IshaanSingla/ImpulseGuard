package com.example.impulseguard.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "impulseguard_settings")

data class UserSettings(
    val escalationThreshold: Int = 3,
    val foodLimit: Int = 2000,
    val shopLimit: Int = 3000,
    val smarterTracking: Boolean = false,
    val pauseAll: Boolean = false,
    val onboardingComplete: Boolean = false,
)

class SettingsDataStore(private val context: Context) {
    private object Keys {
        val ESCALATION_THRESHOLD = intPreferencesKey("escalation_threshold")
        val FOOD_LIMIT = intPreferencesKey("food_limit")
        val SHOP_LIMIT = intPreferencesKey("shop_limit")
        val SMARTER_TRACKING = booleanPreferencesKey("smarter_tracking")
        val PAUSE_ALL = booleanPreferencesKey("pause_all")
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
    }

    val settings: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            escalationThreshold = prefs[Keys.ESCALATION_THRESHOLD] ?: 3,
            foodLimit = prefs[Keys.FOOD_LIMIT] ?: 2000,
            shopLimit = prefs[Keys.SHOP_LIMIT] ?: 3000,
            smarterTracking = prefs[Keys.SMARTER_TRACKING] ?: false,
            pauseAll = prefs[Keys.PAUSE_ALL] ?: false,
            onboardingComplete = prefs[Keys.ONBOARDING_COMPLETE] ?: false,
        )
    }

    suspend fun setEscalationThreshold(value: Int) {
        context.dataStore.edit { it[Keys.ESCALATION_THRESHOLD] = value }
    }

    suspend fun setFoodLimit(value: Int) {
        context.dataStore.edit { it[Keys.FOOD_LIMIT] = value }
    }

    suspend fun setShopLimit(value: Int) {
        context.dataStore.edit { it[Keys.SHOP_LIMIT] = value }
    }

    suspend fun setSmarterTracking(value: Boolean) {
        context.dataStore.edit { it[Keys.SMARTER_TRACKING] = value }
    }

    suspend fun setPauseAll(value: Boolean) {
        context.dataStore.edit { it[Keys.PAUSE_ALL] = value }
    }

    suspend fun setOnboardingComplete(value: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETE] = value }
    }
}
