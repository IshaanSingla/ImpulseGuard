package com.example.impulseguard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.impulseguard.viewmodel.ImpulseViewModel
import com.example.impulseguard.model.ColorRole
import com.example.impulseguard.model.LastPurchase
import com.example.impulseguard.model.formatRupees
import com.example.impulseguard.model.tagLabel
import com.example.impulseguard.ui.components.IconBadge
import com.example.impulseguard.ui.components.PrimaryButton
import com.example.impulseguard.ui.components.SecondaryButton
import com.example.impulseguard.ui.theme.Accent2_100
import com.example.impulseguard.ui.theme.Accent2_200
import com.example.impulseguard.ui.theme.Accent2_800
import com.example.impulseguard.ui.theme.Accent200
import com.example.impulseguard.ui.theme.Accent300
import com.example.impulseguard.ui.theme.Accent800
import com.example.impulseguard.ui.theme.ColorAccent
import com.example.impulseguard.ui.theme.ColorBg
import com.example.impulseguard.ui.theme.ColorText
import com.example.impulseguard.ui.theme.FigtreeFamily
import com.example.impulseguard.ui.theme.Neutral900
import com.example.impulseguard.ui.theme.Radius
import kotlinx.coroutines.delay

/** Fed from the in-app [ImpulseViewModel] (the "Simulate" hook). */
@Composable
fun InterceptionOverlay(state: ImpulseViewModel) {
    val overlay = state.overlay ?: return
    val app = state.watchedApps.find { it.id == overlay.appId } ?: return

    InterceptionOverlayContent(
        appName = app.name,
        initial = app.initial,
        colorRole = app.colorRole,
        opensThisMonth = overlay.opensThisMonth,
        spentThisMonth = overlay.spentThisMonth,
        lastPurchase = overlay.lastPurchase,
        streakCurrentRun = state.streakCurrentRun,
        onContinuePlanned = { state.overlayContinue("planned") },
        onContinueBrowsing = { state.overlayContinue("browsing") },
    )
}

/** Stateless content — also hosted directly inside the real system overlay window
 * by [com.example.impulseguard.service.OverlayController], with no [ImpulseViewModel] involved. */
@Composable
fun InterceptionOverlayContent(
    appName: String,
    initial: String,
    colorRole: ColorRole,
    opensThisMonth: Int,
    spentThisMonth: Int,
    lastPurchase: LastPurchase?,
    streakCurrentRun: Int,
    onContinuePlanned: () -> Unit,
    onContinueBrowsing: () -> Unit,
) {
    val (iconBg, iconFg) = when (colorRole) {
        ColorRole.ACCENT -> Accent200 to Accent800
        ColorRole.ACCENT2 -> Accent2_200 to Accent2_800
    }
    var countdown by remember { mutableIntStateOf(3) }

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000)
            countdown -= 1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral900.copy(alpha = 0.78f))
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Radius.lg))
                .background(ColorBg)
                .padding(horizontal = 24.dp, vertical = 26.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 14.dp)) {
                IconBadge(initial, iconBg, iconFg, size = 36.dp, fontSize = 13.sp)
                Text("$appName — just a moment", fontFamily = FigtreeFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ColorText)
            }

            Text(
                "You've opened $appName $opensThisMonth times this month, spent ₹${formatRupees(spentThisMonth)}.",
                fontFamily = FigtreeFamily, fontSize = 14.5.sp, color = ColorText, lineHeight = 21.sp,
                modifier = Modifier.padding(bottom = 6.dp),
            )
            lastPurchase?.let { lp ->
                val daysLabel = if (lp.daysAgo == 1) "1 day ago" else "${lp.daysAgo} days ago"
                Text(
                    "Last impulse buy here: ₹${formatRupees(lp.amount)}, $daysLabel — tagged \"${tagLabel(lp.tag).lowercase()}\" the next day.",
                    fontFamily = FigtreeFamily, fontSize = 13.sp, color = ColorText.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.md))
                    .background(Accent2_100)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .padding(bottom = 18.dp),
            ) {
                Text(
                    "$streakCurrentRun of your last 10 opens ended with no purchase — nice run.",
                    fontFamily = FigtreeFamily, fontSize = 12.5.sp, color = Accent2_800,
                )
            }

            if (countdown > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(34.dp), color = ColorAccent, trackColor = Accent300, strokeWidth = 3.dp)
                    Text("Just breathe for ${countdown}s…", fontFamily = FigtreeFamily, fontSize = 13.sp, color = ColorText.copy(alpha = 0.65f))
                }
            } else {
                Column {
                    PrimaryButton(
                        text = "I'm here to buy something planned",
                        onClick = onContinuePlanned,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    )
                    SecondaryButton(
                        text = "Just browsing, let me through",
                        onClick = onContinueBrowsing,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
