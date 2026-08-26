package com.example.impulseguard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.impulseguard.viewmodel.ImpulseViewModel
import com.example.impulseguard.model.PurchaseTag
import com.example.impulseguard.ui.components.GhostButton
import com.example.impulseguard.ui.components.PrimaryButton
import com.example.impulseguard.ui.theme.ColorAccent
import com.example.impulseguard.ui.theme.ColorBg
import com.example.impulseguard.ui.theme.ColorDivider
import com.example.impulseguard.ui.theme.ColorSurface
import com.example.impulseguard.ui.theme.ColorText
import com.example.impulseguard.ui.theme.FigtreeFamily
import com.example.impulseguard.ui.theme.HeadingSm
import com.example.impulseguard.ui.theme.Radius

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseLogSheet(state: ImpulseViewModel, sheetState: SheetState) {
    val mockAppName = state.watchedApps.find { it.id == state.mockApp?.appId }?.name ?: ""

    ModalBottomSheet(
        onDismissRequest = { state.skipLog() },
        sheetState = sheetState,
        containerColor = ColorBg,
        dragHandle = null,
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 8.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ColorDivider),
            )
            Text("Planned, or impulse?", style = HeadingSm.copy(fontSize = 19.sp), color = ColorText, modifier = Modifier.padding(bottom = 4.dp))
            Text("Payment confirmation caught for $mockAppName.", fontFamily = FigtreeFamily, fontSize = 13.sp, color = ColorText.copy(alpha = 0.65f), modifier = Modifier.padding(bottom = 16.dp))

            Text("Amount", fontFamily = FigtreeFamily, fontSize = 12.sp, color = ColorText.copy(alpha = 0.7f), modifier = Modifier.padding(bottom = 5.dp))
            OutlinedTextField(
                value = state.logAmount,
                onValueChange = { state.logAmount = it.filter { c -> c.isDigit() } },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(Radius.pill),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ColorSurface,
                    unfocusedContainerColor = ColorSurface,
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorDivider,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.pill))
                    .border(1.dp, ColorDivider, RoundedCornerShape(Radius.pill))
                    .padding(bottom = 18.dp),
            ) {
                SegOption(
                    text = "Planned",
                    selected = state.logTag == PurchaseTag.PLANNED,
                    onClick = { state.logTag = PurchaseTag.PLANNED },
                    modifier = Modifier.weight(1f),
                )
                SegOption(
                    text = "Impulse",
                    selected = state.logTag == PurchaseTag.IMPULSE,
                    onClick = { state.logTag = PurchaseTag.IMPULSE },
                    modifier = Modifier.weight(1f),
                )
            }

            PrimaryButton(
                text = "Log it",
                onClick = { state.submitLog() },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            )
            GhostButton(
                text = "Skip logging",
                onClick = { state.skipLog() },
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            )
        }
    }
}

@Composable
private fun SegOption(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(if (selected) ColorAccent else androidx.compose.ui.graphics.Color.Transparent)
            .padding(vertical = 9.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, fontFamily = FigtreeFamily, fontSize = 13.sp, color = if (selected) ColorBg else ColorText)
    }
}
