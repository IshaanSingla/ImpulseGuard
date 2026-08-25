package com.example.impulseguard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.impulseguard.model.AppUiState
import com.example.impulseguard.ui.components.PrimaryButton
import com.example.impulseguard.ui.components.GhostButton
import com.example.impulseguard.ui.icons.ClockIcon
import com.example.impulseguard.ui.icons.PhoneIcon
import com.example.impulseguard.ui.icons.PauseScreenIcon
import com.example.impulseguard.ui.theme.Accent2_200
import com.example.impulseguard.ui.theme.Accent2_700
import com.example.impulseguard.ui.theme.Accent200
import com.example.impulseguard.ui.theme.Accent700
import com.example.impulseguard.ui.theme.ColorAccent
import com.example.impulseguard.ui.theme.ColorSurface
import com.example.impulseguard.ui.theme.ColorText
import com.example.impulseguard.ui.theme.FigtreeFamily
import com.example.impulseguard.ui.theme.HeadingLg
import com.example.impulseguard.ui.theme.HeadingSm
import com.example.impulseguard.ui.theme.Radius

@Composable
fun OnboardingScreen(state: AppUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 28.dp, vertical = 32.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            when (state.onboardStep) {
                0 -> OnboardStepContent(
                    iconBg = Accent200,
                    icon = { ClockIcon(Accent700) },
                    title = "A pause, not a lecture.",
                    titleStyle = HeadingLg.copy(fontSize = 34.sp),
                    body = "ImpulseGuard notices when you open Zomato, Swiggy, Amazon or Myntra — and shows you your real spending pattern before you scroll on. No blocking, no guilt. Just a moment to notice.",
                )
                1 -> OnboardStepContent(
                    iconBg = Accent2_200,
                    icon = { PhoneIcon(Accent2_700) },
                    title = "App awareness",
                    titleStyle = HeadingSm.copy(fontSize = 30.sp),
                    body = "Android needs to tell ImpulseGuard which app is in front — that's all this permission does. It never reads what's on your screen, only the app's name.",
                    note = "Used for: noticing when a watched app opens.\nNever used for: reading messages, screens, or contacts.",
                )
                else -> OnboardStepContent(
                    iconBg = Accent200,
                    icon = { PauseScreenIcon(Accent700) },
                    title = "The pause screen",
                    titleStyle = HeadingSm.copy(fontSize = 30.sp),
                    body = "This lets the pause screen appear briefly over a watched app. It's dismissible in one tap, always — this app never locks you out.",
                    note = "Used for: showing your stats for 3–5 seconds.\nNever used for: blocking purchases or apps.",
                )
            }
        }

        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
            ) {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .size(width = 22.dp, height = 6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(ColorAccent.copy(alpha = if (i <= state.onboardStep) 1f else 0.35f)),
                    )
                }
            }
            PrimaryButton(
                text = when (state.onboardStep) {
                    0 -> "Get started"
                    1 -> "Allow app awareness"
                    else -> "Allow the pause screen"
                },
                onClick = { state.onboardPrimary() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
            )
            if (state.onboardStep > 0) {
                GhostButton(
                    text = "Not now",
                    onClick = { state.onboardSkip() },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun OnboardStepContent(
    iconBg: Color,
    icon: @Composable () -> Unit,
    title: String,
    titleStyle: androidx.compose.ui.text.TextStyle,
    body: String,
    note: String? = null,
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(iconBg)
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        icon()
    }
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Text(title, style = titleStyle, color = ColorText)
        Text(
            body,
            fontFamily = FigtreeFamily,
            fontSize = 15.sp,
            color = ColorText.copy(alpha = 0.75f),
            modifier = Modifier.padding(top = 4.dp, bottom = if (note != null) 10.dp else 0.dp),
        )
        if (note != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(Radius.md))
                    .background(ColorSurface)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Text(note, fontFamily = FigtreeFamily, fontSize = 13.sp, color = ColorText.copy(alpha = 0.8f))
            }
        }
    }
}
