package com.example.impulseguard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.impulseguard.model.AppUiState
import com.example.impulseguard.model.formatRupees
import com.example.impulseguard.ui.components.OrganicCard
import com.example.impulseguard.ui.icons.CheckIcon
import com.example.impulseguard.ui.icons.CloseIcon
import com.example.impulseguard.ui.icons.SavedIcon
import com.example.impulseguard.ui.theme.Accent2_500
import com.example.impulseguard.ui.theme.Accent2_200
import com.example.impulseguard.ui.theme.Accent2_800
import com.example.impulseguard.ui.theme.Accent200
import com.example.impulseguard.ui.theme.Accent700
import com.example.impulseguard.ui.theme.Accent800
import com.example.impulseguard.ui.theme.CaprasimoFamily
import com.example.impulseguard.ui.theme.ColorText
import com.example.impulseguard.ui.theme.FigtreeFamily
import com.example.impulseguard.ui.theme.HeadingSm
import com.example.impulseguard.ui.theme.Radius

@Composable
fun StreakTab(state: AppUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        Text("Your restraint streak", style = HeadingSm.copy(fontSize = 26.sp), color = ColorText, modifier = Modifier.padding(bottom = 4.dp))
        Text("The number that matters most — not the warnings.", fontFamily = FigtreeFamily, fontSize = 13.5.sp, color = ColorText.copy(alpha = 0.7f), modifier = Modifier.padding(bottom = 20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Radius.lg))
                .background(Accent2_500)
                .padding(vertical = 26.dp, horizontal = 22.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${state.streakCurrentRun}/10", fontFamily = CaprasimoFamily, fontSize = 52.sp, color = androidx.compose.ui.graphics.Color(0xFFFDFAF3))
                Text("opens with no purchase after", fontFamily = FigtreeFamily, fontSize = 13.5.sp, color = androidx.compose.ui.graphics.Color(0xFFFDFAF3).copy(alpha = 0.92f), modifier = Modifier.padding(top = 6.dp))
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 22.dp),
        ) {
            state.last10.forEach { won ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (won) Accent2_200 else Accent200),
                    contentAlignment = Alignment.Center,
                ) {
                    if (won) CheckIcon(Accent2_800) else CloseIcon(Accent800)
                }
            }
        }

        OrganicCard(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 4.dp)) {
                    SavedIcon(Accent700)
                    Text("Estimated saved this month", fontFamily = FigtreeFamily, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 14.5.sp, color = ColorText)
                }
                Text("₹${formatRupees(state.estSaved)}", fontFamily = CaprasimoFamily, fontSize = 24.sp, color = Accent700)
                Text(
                    "Based on your average impulse-buy size across no-purchase sessions.",
                    fontFamily = FigtreeFamily, fontSize = 12.sp, color = ColorText.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }

        Text(
            "Every win here counts more than any warning does — dopamine-seeking brains respond to reward, not guilt.",
            fontFamily = FigtreeFamily, fontSize = 12.5.sp, color = ColorText.copy(alpha = 0.6f),
        )
    }
}
