package com.example.impulseguard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.impulseguard.viewmodel.ImpulseViewModel
import com.example.impulseguard.model.ColorRole
import com.example.impulseguard.model.PurchaseTag
import com.example.impulseguard.model.Tab
import com.example.impulseguard.model.formatRupees
import com.example.impulseguard.model.tagLabel
import com.example.impulseguard.ui.components.CardKicker
import com.example.impulseguard.ui.components.IconBadge
import com.example.impulseguard.ui.components.OrganicCard
import com.example.impulseguard.ui.components.OrganicTag
import com.example.impulseguard.ui.components.TagStyle
import com.example.impulseguard.ui.icons.ChevronRightIcon
import com.example.impulseguard.ui.icons.StreakIcon
import com.example.impulseguard.ui.theme.Accent2_200
import com.example.impulseguard.ui.theme.Accent2_700
import com.example.impulseguard.ui.theme.Accent2_800
import com.example.impulseguard.ui.theme.Accent200
import com.example.impulseguard.ui.theme.Accent800
import com.example.impulseguard.ui.theme.ColorAccent
import com.example.impulseguard.ui.theme.ColorAccent2
import com.example.impulseguard.ui.theme.ColorDivider
import com.example.impulseguard.ui.theme.ColorSurface
import com.example.impulseguard.ui.theme.ColorText
import com.example.impulseguard.ui.theme.FigtreeFamily
import com.example.impulseguard.ui.theme.HeadingSm
import com.example.impulseguard.ui.theme.Radius

private fun roleColors(role: ColorRole) = when (role) {
    ColorRole.ACCENT -> Accent200 to Accent800
    ColorRole.ACCENT2 -> Accent2_200 to Accent2_800
}

private fun tagStyle(tag: PurchaseTag) = when (tag) {
    PurchaseTag.IMPULSE -> TagStyle.Accent
    PurchaseTag.PLANNED -> TagStyle.Accent2
    PurchaseTag.REGRET -> TagStyle.Neutral
}

@Composable
fun HomeTab(state: ImpulseViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
            ) {
                Column {
                    Text("IMPULSEGUARD", fontFamily = FigtreeFamily, fontSize = 12.sp, letterSpacing = 1.sp, color = com.example.impulseguard.ui.theme.Accent700)
                    Text("This month", style = HeadingSm.copy(fontSize = 26.sp), color = ColorText)
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(ColorAccent)
                        .padding(1.dp),
                ) {
                    Box(modifier = Modifier.padding(8.dp)) {
                        Text("IG", fontFamily = com.example.impulseguard.ui.theme.CaprasimoFamily, fontSize = 15.sp, color = com.example.impulseguard.ui.theme.ColorBg)
                    }
                }
            }
        }

        state.reinforceMsg?.let { msg ->
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Radius.md))
                        .background(Accent2_200.copy(alpha = 0.6f))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                        .padding(bottom = 16.dp),
                ) {
                    Text(msg, fontFamily = FigtreeFamily, fontSize = 13.5.sp, color = Accent2_800)
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
            ) {
                OrganicCard(modifier = Modifier.weight(1f)) {
                    Column {
                        CardKicker("Opens")
                        Text("${state.totalOpensMonth}", fontFamily = com.example.impulseguard.ui.theme.CaprasimoFamily, fontSize = 26.sp, color = ColorText)
                    }
                }
                OrganicCard(modifier = Modifier.weight(1f)) {
                    Column {
                        CardKicker("Spent")
                        Text("₹${formatRupees(state.totalSpentMonth)}", fontFamily = com.example.impulseguard.ui.theme.CaprasimoFamily, fontSize = 26.sp, color = ColorText)
                    }
                }
            }
        }

        item {
            OrganicCard(
                onClick = { state.tab = Tab.STREAK },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Accent2_200)
                            .padding(11.dp),
                    ) {
                        StreakIcon(Accent2_700)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${state.streakCurrentRun} of your last 10 opens — no purchase", fontFamily = FigtreeFamily, fontSize = 14.5.sp, color = ColorText, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text("Tap to see your streak", fontFamily = FigtreeFamily, fontSize = 12.5.sp, color = ColorText.copy(alpha = 0.65f))
                    }
                    ChevronRightIcon(ColorText.copy(alpha = 0.4f))
                }
            }
        }

        item {
            Text("Recent activity", style = HeadingSm.copy(fontSize = 16.sp), color = ColorText, modifier = Modifier.padding(bottom = 4.dp))
            Text("Real-time log — this is what feeds every pause screen.", fontFamily = FigtreeFamily, fontSize = 12.5.sp, color = ColorText.copy(alpha = 0.6f), modifier = Modifier.padding(bottom = 10.dp))
        }

        items(state.purchaseLog) { entry ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.md))
                    .background(ColorSurface)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .padding(bottom = 0.dp),
            ) {
                IconBadge(entry.initial, Accent200, Accent800, size = 30.dp, fontSize = 12.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text("${entry.appName} · ₹${formatRupees(entry.amount)}", fontFamily = FigtreeFamily, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold, fontSize = 13.5.sp, color = ColorText)
                    Text(entry.timeAgo, fontFamily = FigtreeFamily, fontSize = 11.5.sp, color = ColorText.copy(alpha = 0.6f))
                }
                OrganicTag(tagLabel(entry.tag), tagStyle(entry.tag))
            }
            Box(modifier = Modifier.padding(top = 8.dp))
        }

        item {
            Text("Try it — simulate opening an app", style = HeadingSm.copy(fontSize = 16.sp), color = ColorText, modifier = Modifier.padding(top = 14.dp, bottom = 4.dp))
            Text("This app polls in the background; here you can trigger that moment yourself.", fontFamily = FigtreeFamily, fontSize = 12.5.sp, color = ColorText.copy(alpha = 0.6f), modifier = Modifier.padding(bottom = 10.dp))
        }

        items(state.watchedApps.filter { it.enabled }) { app ->
            val (bg, fg) = roleColors(app.colorRole)
            val today = state.opensToday[app.id] ?: 0
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.md))
                    .background(ColorSurface)
                    .clickable { state.simulateOpen(app.id) }
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .padding(bottom = 0.dp),
            ) {
                IconBadge(app.initial, bg, fg, size = 34.dp, fontSize = 13.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text("Open ${app.name}", fontFamily = FigtreeFamily, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold, fontSize = 13.5.sp, color = ColorText)
                    Text("$today today · pauses at ${state.escalationThreshold}", fontFamily = FigtreeFamily, fontSize = 11.5.sp, color = ColorText.copy(alpha = 0.6f))
                }
                Text("Simulate", fontFamily = FigtreeFamily, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 11.sp, color = com.example.impulseguard.ui.theme.Accent700)
            }
            Box(modifier = Modifier.padding(top = 8.dp))
        }
    }
}
