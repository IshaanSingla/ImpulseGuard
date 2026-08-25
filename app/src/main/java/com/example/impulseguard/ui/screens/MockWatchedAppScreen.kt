package com.example.impulseguard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.impulseguard.model.AppUiState
import com.example.impulseguard.model.ColorRole
import com.example.impulseguard.ui.theme.Accent2_700
import com.example.impulseguard.ui.theme.Accent600
import com.example.impulseguard.ui.theme.CaprasimoFamily
import com.example.impulseguard.ui.theme.ColorText
import com.example.impulseguard.ui.theme.FigtreeFamily
import com.example.impulseguard.ui.theme.Radius

@Composable
fun MockWatchedAppScreen(state: AppUiState) {
    val mock = state.mockApp ?: return
    val app = state.watchedApps.find { it.id == mock.appId } ?: return
    val bg = when (app.colorRole) {
        ColorRole.ACCENT -> Accent600
        ColorRole.ACCENT2 -> Accent2_700
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 22.dp, vertical = 28.dp),
    ) {
        Text(
            "← Close without ordering",
            fontFamily = FigtreeFamily, fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f),
            modifier = Modifier
                .clickable { state.mockCloseNoBuy() }
                .padding(bottom = 24.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("${app.name} (simulated)", fontFamily = CaprasimoFamily, fontSize = 24.sp, color = Color.White)
            Text(
                "This stands in for the real app. Use it to simulate placing an order.",
                fontFamily = FigtreeFamily, fontSize = 13.5.sp, color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(Radius.pill))
                    .background(Color.White)
                    .clickable { state.mockSimulatePayment() }
                    .padding(horizontal = 22.dp, vertical = 14.dp),
            ) {
                Text("Simulate completing an order", fontFamily = CaprasimoFamily, fontSize = 15.sp, color = ColorText)
            }
        }
    }
}
