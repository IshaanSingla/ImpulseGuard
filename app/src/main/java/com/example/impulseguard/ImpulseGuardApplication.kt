package com.example.impulseguard

import android.app.Application
import com.example.impulseguard.data.ImpulseRepository
import com.example.impulseguard.data.local.AppDatabase
import com.example.impulseguard.data.settings.SettingsDataStore

class ImpulseGuardApplication : Application() {
    val repository: ImpulseRepository by lazy {
        ImpulseRepository(
            db = AppDatabase.get(this),
            settingsStore = SettingsDataStore(this),
        )
    }
}
