package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// PYBattle OriginOS 7 Crystal Glass & Spatial Dark Palette
val PlaytBgObsidian = Color(0xFF060913)
val PlaytSurfaceDark = Color(0xFF101628)
val PlaytSurfaceElevated = Color(0xFF1A233D)
val PlaytCardBorder = Color(0xFF2E3C64)

// OriginOS 7 Liquid Crystal & Specular Glass Tokens
val CrystalGlassSurface = Color(0xCC12192E)
val CrystalGlassElevated = Color(0xD91B2542)
val CrystalSpecularHighlight = Color(0x59FFFFFF)
val CrystalSpecularSoft = Color(0x1FFFFFFF)
val CrystalSubtleEdge = Color(0x14FFFFFF)
val CrystalAuroraBlue = Color(0xFF38BDF8)
val CrystalAuroraViolet = Color(0xFF818CF8)
val CrystalAuroraRose = Color(0xFFFB7185)
val CrystalMint = Color(0xFF34D399)

val PlaytCrimson = Color(0xFFFF2A54)
val PlaytCrimsonDark = Color(0xFFBE123C)
val PlaytOrange = Color(0xFFFF8A00)
val PlaytCoinGold = Color(0xFFFFD60A)
val PlaytElectricBlue = Color(0xFF38BDF8)
val PlaytVerifiedGreen = Color(0xFF34D399)
val PlaytGradientPurple = Color(0xFF818CF8)
val PlaytGradientPink = Color(0xFFF472B6)

val PlaytTextPrimary = Color(0xFFF8FAFC)
val PlaytTextSecondary = Color(0xFFCBD5E1)
val PlaytTextMuted = Color(0xFF64748B)
val PlaytDanger = Color(0xFFFF3B5C)
val PlaytWarning = Color(0xFFFFB800)

fun crystalSpecularBorderBrush(
    accent: Color = CrystalAuroraBlue
): Brush = Brush.linearGradient(
    colors = listOf(
        CrystalSpecularHighlight,
        accent.copy(alpha = 0.42f),
        CrystalSubtleEdge,
        accent.copy(alpha = 0.25f)
    )
)

fun crystalSurfaceBrush(
    tint: Color = CrystalAuroraBlue
): Brush = Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.10f),
        tint.copy(alpha = 0.07f),
        CrystalGlassSurface
    )
)
