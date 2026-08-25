package com.example.impulseguard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.impulseguard.model.AppUiState
import com.example.impulseguard.model.Stage
import com.example.impulseguard.model.Tab
import com.example.impulseguard.ui.screens.AppsTab
import com.example.impulseguard.ui.screens.BottomNavBar
import com.example.impulseguard.ui.screens.HomeTab
import com.example.impulseguard.ui.screens.InterceptionOverlay
import com.example.impulseguard.ui.screens.MockWatchedAppScreen
import com.example.impulseguard.ui.screens.OnboardingScreen
import com.example.impulseguard.ui.screens.PurchaseLogSheet
import com.example.impulseguard.ui.screens.SettingsTab
import com.example.impulseguard.ui.screens.StreakTab
import com.example.impulseguard.ui.theme.ColorBg
import com.example.impulseguard.ui.theme.ImpulseGuardTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpulseGuardApp() {
    val state = remember { AppUiState() }

    ImpulseGuardTheme {
        Box(modifier = Modifier.fillMaxSize().background(ColorBg)) {
            when (state.stage) {
                Stage.ONBOARDING -> OnboardingScreen(state)
                Stage.APP -> {
                    Scaffold(
                        containerColor = ColorBg,
                        bottomBar = {
                            if (state.mockApp == null) {
                                BottomNavBar(currentTab = state.tab, onTabSelected = { state.tab = it })
                            }
                        },
                    ) { padding ->
                        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                            when (state.tab) {
                                Tab.HOME -> HomeTab(state)
                                Tab.STREAK -> StreakTab(state)
                                Tab.APPS -> AppsTab(state)
                                Tab.SETTINGS -> SettingsTab(state)
                            }
                        }
                    }

                    if (state.mockApp != null) {
                        MockWatchedAppScreen(state)
                    }

                    if (state.logSheetOpen) {
                        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                        PurchaseLogSheet(state, sheetState)
                    }

                    if (state.overlay != null) {
                        InterceptionOverlay(state)
                    }
                }
            }
        }
    }
}
