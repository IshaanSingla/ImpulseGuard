package com.example.impulseguard.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.impulseguard.viewmodel.ImpulseViewModel
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
import com.example.impulseguard.util.InstalledApp
import com.example.impulseguard.util.getInstalledLaunchableApps

@Composable
fun AppsTab(state: ImpulseViewModel) {
    val context = LocalContext.current
    var showPicker by remember { mutableStateOf(false) }
    var installedApps by remember { mutableStateOf<List<InstalledApp>>(emptyList()) }

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
            onClick = {
                installedApps = getInstalledLaunchableApps(context)
                showPicker = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        )
    }

    if (showPicker) {
        val watchedIds = state.watchedApps.map { it.id }.toSet()
        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = { Text("Select an app to watch") },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 420.dp)) {
                    items(installedApps.filter { it.packageName !in watchedIds }) { app ->
                        Text(
                            text = app.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    state.addWatchedApp(app.packageName, app.name)
                                    showPicker = false
                                }
                                .padding(vertical = 12.dp),
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel") }
            },
        )
    }
}
