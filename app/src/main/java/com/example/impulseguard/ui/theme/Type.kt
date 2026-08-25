package com.example.impulseguard.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.impulseguard.R

val FigtreeFamily = FontFamily(
    Font(R.font.figtree_regular, FontWeight.Normal),
    Font(R.font.figtree_semibold, FontWeight.SemiBold),
    Font(R.font.figtree_bold, FontWeight.Bold),
)

val CaprasimoFamily = FontFamily(
    Font(R.font.caprasimo_regular, FontWeight.Normal),
)

// h1 42 / h2 32 / h3 25 / h4 20, line-height 1.12, letter-spacing -0.015em, weight 400 (Caprasimo)
val HeadingXL = TextStyle(fontFamily = CaprasimoFamily, fontWeight = FontWeight.Normal, fontSize = 42.sp, lineHeight = 47.sp, letterSpacing = (-0.015).sp)
val HeadingLg = TextStyle(fontFamily = CaprasimoFamily, fontWeight = FontWeight.Normal, fontSize = 32.sp, lineHeight = 36.sp, letterSpacing = (-0.015).sp)
val HeadingMd = TextStyle(fontFamily = CaprasimoFamily, fontWeight = FontWeight.Normal, fontSize = 25.sp, lineHeight = 28.sp, letterSpacing = (-0.015).sp)
val HeadingSm = TextStyle(fontFamily = CaprasimoFamily, fontWeight = FontWeight.Normal, fontSize = 20.sp, lineHeight = 22.sp, letterSpacing = (-0.015).sp)

val Typography = Typography(
    headlineLarge = HeadingXL,
    headlineMedium = HeadingLg,
    headlineSmall = HeadingMd,
    titleLarge = HeadingSm,
    titleMedium = TextStyle(fontFamily = FigtreeFamily, fontWeight = FontWeight.Bold, fontSize = 17.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = FigtreeFamily, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 23.sp),
    bodyMedium = TextStyle(fontFamily = FigtreeFamily, fontWeight = FontWeight.Normal, fontSize = 13.5.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = FigtreeFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 17.sp),
    labelLarge = TextStyle(fontFamily = FigtreeFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 17.sp),
    labelMedium = TextStyle(fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 15.sp),
    labelSmall = TextStyle(fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 10.5.sp, lineHeight = 13.sp, letterSpacing = 0.02.sp),
)
