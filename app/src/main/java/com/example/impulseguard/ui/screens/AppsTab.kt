package com.example.impulseguard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.impulseguard.model.AppUiState
import com.example.impulseguard.model.ColorRole
import com.example.impulseguard.model.formatRupees
import com.example.impulseguard.ui.components.IconBadge
import com.example.impulseguard.ui.components.OrganicCard
import com.example.impulseguard.ui.components.OrganicSwitch
import com.example.impulseguard.ui.components.SecondaryButton
import com.example.impulseguard.ui.theme.Accent2_200
import com.example.impulseguard.ui.theme.Accent2_800
import com.example.impulseguard.ui.theme.Accent200
import com.example.impulseguard.ui.theme.Accent800
import com.example.impulseguard.ui.theme.ColorText
import com.example.impulseguard.ui.theme.FigtreeFamily
import com.example.impulseguard.ui.theme.HeadingSm

@Composable
fun AppsTab(state: AppUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        Text("Watched apps", style = HeadingSm.copy(fontSize = 26.sp), color = ColorText, modifier = Modifier.padding(bottom = 4.dp))
        Text("Only these apps trigger a pause. Add or remove anytime.", fontFamily = FigtreeFamily, fontSize = 13.5.sp, color = ColorText.copy(alpha = 0.7f), modifier = Modifier.padding(bottom = 18.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            state.watchedApps.forEach { app ->
                val (bg, fg) = when (app.colorRole) {
                    ColorRole.ACCENT -> Accent200 to Accent800
                    ColorRole.ACCENT2 -> Accent2_200 to Accent2_800
                }
                OrganicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        IconBadge(app.initial, bg, fg)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(app.name, fontFamily = FigtreeFamily, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 14.5.sp, color = ColorText)
                            Text("${app.category} · ₹${formatRupees(app.weeklyLimit)}/wk limit", fontFamily = FigtreeFamily, fontSize = 12.sp, color = ColorText.copy(alpha = 0.6f))
                        }
                        OrganicSwitch(checked = app.enabled, onToggle = { state.toggleAppEnabled(app.id) })
                    }
                }
            }
        }

        SecondaryButton(
            text = "+ Add another app",
            onClick = { /* out of scope for this design pass */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        )
    }
}
