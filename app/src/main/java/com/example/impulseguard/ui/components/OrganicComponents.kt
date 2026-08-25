package com.example.impulseguard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.impulseguard.ui.theme.ColorAccent
import com.example.impulseguard.ui.theme.ColorBg
import com.example.impulseguard.ui.theme.ColorDivider
import com.example.impulseguard.ui.theme.ColorSurface
import com.example.impulseguard.ui.theme.ColorText
import com.example.impulseguard.ui.theme.FigtreeFamily
import com.example.impulseguard.ui.theme.CaprasimoFamily
import com.example.impulseguard.ui.theme.Neutral900
import com.example.impulseguard.ui.theme.Radius
import com.example.impulseguard.ui.theme.Space
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.pill))
            .background(ColorAccent)
            .clickable(onClick = onClick)
            .padding(vertical = Space.s2, horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = ColorBg, fontFamily = CaprasimoFamily, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.pill))
            .border(1.dp, ColorDivider, RoundedCornerShape(Radius.pill))
            .clickable(onClick = onClick)
            .padding(vertical = Space.s2, horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = ColorText, fontFamily = CaprasimoFamily, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.pill))
            .clickable(onClick = onClick)
            .padding(vertical = Space.s2, horizontal = Space.s1),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = ColorAccent, fontFamily = CaprasimoFamily, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

enum class TagStyle { Accent, Accent2, Neutral, Outline }

@Composable
fun OrganicTag(
    text: String,
    style: TagStyle,
    modifier: Modifier = Modifier,
) {
    val (bg, fg) = when (style) {
        TagStyle.Accent -> com.example.impulseguard.ui.theme.Accent100 to com.example.impulseguard.ui.theme.Accent800
        TagStyle.Accent2 -> com.example.impulseguard.ui.theme.Accent2_100 to com.example.impulseguard.ui.theme.Accent2_800
        TagStyle.Neutral -> com.example.impulseguard.ui.theme.Neutral100 to com.example.impulseguard.ui.theme.Neutral800
        TagStyle.Outline -> Color.Transparent to ColorAccent
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.pill))
            .then(if (style == TagStyle.Outline) Modifier.border(1.dp, ColorAccent, RoundedCornerShape(Radius.pill)) else Modifier)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 3.dp),
    ) {
        Text(text, color = fg, fontFamily = FigtreeFamily, fontSize = 11.sp)
    }
}

@Composable
fun OrganicCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    padding: PaddingValues = PaddingValues(Space.s3),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .shadow(1.dp, RoundedCornerShape(Radius.card), ambientColor = Neutral900.copy(alpha = 0.14f), spotColor = Neutral900.copy(alpha = 0.14f))
            .clip(RoundedCornerShape(Radius.card))
            .background(ColorSurface)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(padding),
    ) {
        content()
    }
}

@Composable
fun CardKicker(text: String) {
    Text(
        text.uppercase(),
        color = ColorAccent,
        fontFamily = FigtreeFamily,
        fontSize = 10.sp,
        letterSpacing = 1.sp,
    )
}

@Composable
fun IconBadge(
    initial: String,
    bg: Color,
    fg: Color,
    size: androidx.compose.ui.unit.Dp = 38.dp,
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(initial, color = fg, fontFamily = CaprasimoFamily, fontSize = fontSize)
    }
}

@Composable
fun OrganicSwitch(
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val trackColor = if (checked) ColorAccent else com.example.impulseguard.ui.theme.Neutral400
    Box(
        modifier = modifier
            .size(width = 44.dp, height = 26.dp)
            .clip(RoundedCornerShape(Radius.pill))
            .background(trackColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onToggle,
            )
            .padding(2.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.White),
        )
    }
}
