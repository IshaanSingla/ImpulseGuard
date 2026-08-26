package com.example.impulseguard.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.impulseguard.permissions.PermissionsHelper
import com.example.impulseguard.viewmodel.ImpulseViewModel
import com.example.impulseguard.model.formatRupees
import com.example.impulseguard.ui.components.OrganicCard
import com.example.impulseguard.ui.components.OrganicSwitch
import com.example.impulseguard.ui.components.OrganicTag
import com.example.impulseguard.ui.components.SecondaryButton
import com.example.impulseguard.ui.components.TagStyle
import com.example.impulseguard.ui.icons.ChevronRightIcon
import com.example.impulseguard.ui.theme.ColorAccent
import com.example.impulseguard.ui.theme.ColorText
import com.example.impulseguard.ui.theme.Accent700
import com.example.impulseguard.ui.theme.FigtreeFamily
import com.example.impulseguard.ui.theme.HeadingSm
import com.example.impulseguard.ui.theme.Neutral300
import com.example.impulseguard.ui.theme.Radius

@Composable
private fun sliderColors() = SliderDefaults.colors(
    thumbColor = ColorAccent,
    activeTrackColor = ColorAccent,
    inactiveTrackColor = Neutral300,
    activeTickColor = androidx.compose.ui.graphics.Color.Transparent,
    inactiveTickColor = androidx.compose.ui.graphics.Color.Transparent,
)

@Composable
fun SettingsTab(state: ImpulseViewModel) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        Text("Settings", style = HeadingSm.copy(fontSize = 26.sp), color = ColorText, modifier = Modifier.padding(bottom = 4.dp))
        Text("You set the thresholds — nothing here is hardcoded against you.", fontFamily = FigtreeFamily, fontSize = 13.5.sp, color = ColorText.copy(alpha = 0.7f), modifier = Modifier.padding(bottom = 18.dp))

        OrganicCard(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
            Column {
                SettingRow("Pause after", "${state.escalationThreshold} opens/day")
                Slider(
                    value = state.escalationThreshold.toFloat(),
                    onValueChange = { state.updateEscalationThreshold(it.toInt()) },
                    valueRange = 1f..6f,
                    steps = 4,
                    colors = sliderColors(),
                )
                Text(
                    "First ${state.escalationThreshold - 1} opens each day pass through with no pause.",
                    fontFamily = FigtreeFamily, fontSize = 12.sp, color = ColorText.copy(alpha = 0.6f),
                )
            }
        }

        OrganicCard(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
            Column {
                SettingRow("Food delivery weekly limit", "₹${formatRupees(state.foodLimit)}")
                Slider(
                    value = state.foodLimit.toFloat(),
                    onValueChange = { state.updateFoodLimit((it / 100).toInt() * 100) },
                    valueRange = 500f..6000f,
                    colors = sliderColors(),
                )
            }
        }

        OrganicCard(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
            Column {
                SettingRow("Shopping weekly limit", "₹${formatRupees(state.shopLimit)}")
                Slider(
                    value = state.shopLimit.toFloat(),
                    onValueChange = { state.updateShopLimit((it / 100).toInt() * 100) },
                    valueRange = 500f..8000f,
                    colors = sliderColors(),
                )
            }
        }

        OrganicCard(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Smarter tracking", fontFamily = FigtreeFamily, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 14.5.sp, color = ColorText)
                    Text("Auto-detect payment confirmations instead of manual logging.", fontFamily = FigtreeFamily, fontSize = 12.sp, color = ColorText.copy(alpha = 0.65f))
                }
                OrganicSwitch(
                    checked = state.smarterTracking,
                    onToggle = {
                        if (!state.smarterTracking && !PermissionsHelper.hasNotificationListenerAccess(context)) {
                            context.startActivity(PermissionsHelper.notificationListenerIntent())
                        }
                        state.toggleSmarterTracking()
                    },
                )
            }
        }

        Text("Permissions", style = HeadingSm.copy(fontSize = 15.sp), color = ColorText, modifier = Modifier.padding(vertical = 10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 20.dp)) {
            state.permissions.forEach { perm ->
                OrganicCard(padding = androidx.compose.foundation.layout.PaddingValues(0.dp), modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (!perm.granted) {
                                        val intent = when (perm.id) {
                                            "usage" -> PermissionsHelper.usageAccessIntent(context)
                                            "overlay" -> PermissionsHelper.overlayPermissionIntent(context)
                                            else -> PermissionsHelper.notificationListenerIntent()
                                        }
                                        context.startActivity(intent)
                                    }
                                    state.togglePermissionWhy(perm.id)
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                        ) {
                            OrganicTag(if (perm.granted) "On" else "Off", if (perm.granted) TagStyle.Accent2 else TagStyle.Neutral)
                            Text(perm.name, fontFamily = FigtreeFamily, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold, fontSize = 13.5.sp, color = ColorText, modifier = Modifier.weight(1f))
                            ChevronRightIcon(
                                ColorText.copy(alpha = 0.5f),
                                size = 16.dp,
                            )
                        }
                        if (perm.whyOpen) {
                            Text(
                                perm.why,
                                fontFamily = FigtreeFamily, fontSize = 12.5.sp, color = ColorText.copy(alpha = 0.7f),
                                modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                            )
                        }
                    }
                }
            }
        }

        SecondaryButton(
            text = if (state.pauseAll) "Resume monitoring" else "Pause all monitoring today",
            onClick = { state.togglePauseAll() },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SettingRow(label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
    ) {
        Text(label, fontFamily = FigtreeFamily, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 14.5.sp, color = ColorText)
        Text(value, fontFamily = FigtreeFamily, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 14.5.sp, color = Accent700)
    }
}
