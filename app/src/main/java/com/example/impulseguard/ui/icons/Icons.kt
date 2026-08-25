package com.example.impulseguard.ui.icons

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Icon set: Material Outlined stand-ins for the design's Lucide-style (stroke-width 2.75) icons.
// Meaning-matched, not path-identical to the handoff's inline SVGs.

@Composable fun ClockIcon(tint: Color, size: Dp = 30.dp) = Icon(Icons.Outlined.Schedule, null, tint = tint, modifier = Modifier.size(size))
@Composable fun PhoneIcon(tint: Color, size: Dp = 30.dp) = Icon(Icons.Outlined.PhoneAndroid, null, tint = tint, modifier = Modifier.size(size))
@Composable fun PauseScreenIcon(tint: Color, size: Dp = 30.dp) = Icon(Icons.Outlined.CreditCard, null, tint = tint, modifier = Modifier.size(size))
@Composable fun StreakIcon(tint: Color, size: Dp = 22.dp) = Icon(Icons.Outlined.LocalFireDepartment, null, tint = tint, modifier = Modifier.size(size))
@Composable fun ChevronRightIcon(tint: Color, size: Dp = 18.dp) = Icon(Icons.Outlined.ChevronRight, null, tint = tint, modifier = Modifier.size(size))
@Composable fun CheckIcon(tint: Color, size: Dp = 16.dp) = Icon(Icons.Outlined.Check, null, tint = tint, modifier = Modifier.size(size))
@Composable fun CloseIcon(tint: Color, size: Dp = 14.dp) = Icon(Icons.Outlined.Close, null, tint = tint, modifier = Modifier.size(size))
@Composable fun HomeIcon(tint: Color, size: Dp = 21.dp) = Icon(Icons.Outlined.Home, null, tint = tint, modifier = Modifier.size(size))
@Composable fun AppsIcon(tint: Color, size: Dp = 21.dp) = Icon(Icons.Outlined.Apps, null, tint = tint, modifier = Modifier.size(size))
@Composable fun SettingsIcon(tint: Color, size: Dp = 21.dp) = Icon(Icons.Outlined.Settings, null, tint = tint, modifier = Modifier.size(size))
@Composable fun SavedIcon(tint: Color, size: Dp = 20.dp) = Icon(Icons.Outlined.Savings, null, tint = tint, modifier = Modifier.size(size))
