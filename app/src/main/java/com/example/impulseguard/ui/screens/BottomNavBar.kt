package com.example.impulseguard.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.impulseguard.model.Tab
import com.example.impulseguard.ui.icons.AppsIcon
import com.example.impulseguard.ui.icons.HomeIcon
import com.example.impulseguard.ui.icons.SettingsIcon
import com.example.impulseguard.ui.icons.StreakIcon
import com.example.impulseguard.ui.theme.Accent700
import com.example.impulseguard.ui.theme.ColorDivider
import com.example.impulseguard.ui.theme.ColorText
import com.example.impulseguard.ui.theme.FigtreeFamily

@Composable
fun BottomNavBar(currentTab: Tab, onTabSelected: (Tab) -> Unit) {
    Column(modifier = Modifier.navigationBarsPadding()) {
        HorizontalDivider(color = ColorDivider, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 10.dp, start = 6.dp, end = 6.dp),
        ) {
            NavItem(Tab.HOME, "Home", currentTab, onTabSelected) { HomeIcon(it) }
            NavItem(Tab.STREAK, "Streak", currentTab, onTabSelected) { StreakIcon(it, size = 21.dp) }
            NavItem(Tab.APPS, "Apps", currentTab, onTabSelected) { AppsIcon(it) }
            NavItem(Tab.SETTINGS, "Settings", currentTab, onTabSelected) { SettingsIcon(it) }
        }
    }
}

@Composable
private fun RowScope.NavItem(
    tab: Tab,
    label: String,
    currentTab: Tab,
    onTabSelected: (Tab) -> Unit,
    icon: @Composable (Color) -> Unit,
) {
    val color = if (tab == currentTab) Accent700 else ColorText.copy(alpha = 0.45f)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .weight(1f)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) { onTabSelected(tab) },
    ) {
        icon(color)
        Text(label, fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 10.5.sp, color = color)
    }
}
