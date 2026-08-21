package com.denis.realtynova.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════════
// REALTYNOVA — PREMIUM DESIGN SYSTEM COLOR TOKENS
// ═══════════════════════════════════════════════════════════════════════
//
// Design language:
// • Deep Emerald  → trust, property, growth
// • Champagne Gold → luxury, premium listings
// • Professional Navy → intelligence, finance, trust
// • Warm Ivory → architectural warmth
// • Graphite → sophisticated typography
//
// These are design tokens. Screens should consume these values rather
// than introducing arbitrary colors.
// ═══════════════════════════════════════════════════════════════════════


// ───────────────────────────────────────────────────────────────────────
// BRAND — CORE IDENTITY
// ───────────────────────────────────────────────────────────────────────

/** Primary RealtyNova identity color. */
val DeepEmerald = Color(0xFF063D35)

/** Richer emerald used for premium gradients and active states. */
val RoyalEmerald = Color(0xFF075E54)

/** Darkest brand tone for cinematic backgrounds. */
val MidnightEmerald = Color(0xFF021F1B)

/** Sophisticated navy used for finance, intelligence and trust. */
val ProfessionalNavy = Color(0xFF08294A)

/** Deep navy for dark surfaces and premium overlays. */
val MidnightNavy = Color(0xFF04182D)


// ───────────────────────────────────────────────────────────────────────
// LUXURY — GOLD SYSTEM
// ───────────────────────────────────────────────────────────────────────

/** Main luxury accent. */
val ChampagneGold = Color(0xFFD4AF37)

/** Brighter highlight used sparingly for premium interactions. */
val ChampagneGoldLight = Color(0xFFE7CB67)

/** Deeper gold used for contrast and pressed states. */
val ChampagneGoldDark = Color(0xFFA98216)

/** Extremely subtle gold surface. */
val ChampagneGoldSoft = Color(0xFFF6EFD5)


// ───────────────────────────────────────────────────────────────────────
// ARCHITECTURAL NEUTRALS
// ───────────────────────────────────────────────────────────────────────

/** Primary light application background. */
val WarmWhite = Color(0xFFFFFDFC)

/** Architectural ivory. */
val SoftBeige = Color(0xFFF4F0E6)

/** Slightly darker beige for cards and secondary surfaces. */
val Sandstone = Color(0xFFE8E0D0)

/** Neutral border tone. */
val StoneGray = Color(0xFFD5D0C7)

/** Premium light text color. */
val Graphite = Color(0xFF252525)

/** Secondary text. */
val SlateGray = Color(0xFF667085)

/** Muted UI elements. */
val MistGray = Color(0xFF98A2B3)


// ───────────────────────────────────────────────────────────────────────
// DARK ARCHITECTURAL SURFACES
// ───────────────────────────────────────────────────────────────────────

/** Main dark background. */
val Obsidian = Color(0xFF07100E)

/** Primary dark surface. */
val DeepSurface = Color(0xFF0B1815)

/** Elevated dark surface. */
val ElevatedSurface = Color(0xFF10221E)

/** Dark border. */
val DarkBorder = Color(0xFF203832)

/** Primary dark-theme text. */
val DarkText = Color(0xFFF3F1EB)

/** Secondary dark-theme text. */
val DarkMutedText = Color(0xFFB8C4BF)


// ───────────────────────────────────────────────────────────────────────
// STATUS / SEMANTIC COLORS
// ───────────────────────────────────────────────────────────────────────

/** Verified listings, accounts and documents. */
val VerifiedBlue = Color(0xFF2787D9)

/** Successful transactions and confirmations. */
val SuccessGreen = Color(0xFF2E9B62)

/** Attention / incomplete actions. */
val WarningAmber = Color(0xFFE0A21A)

/** Destructive actions and validation errors. */
val DangerRed = Color(0xFFD64545)

/** Informational state. */
val InfoCyan = Color(0xFF2C9CB5)


// ───────────────────────────────────────────────────────────────────────
// TRANSPARENT / OVERLAY TOKENS
// ───────────────────────────────────────────────────────────────────────

val GlassWhite = Color.White.copy(alpha = 0.78f)

val GlassDark = Color.Black.copy(alpha = 0.32f)

val OverlayDark = Color.Black.copy(alpha = 0.55f)

val OverlayHeavy = Color.Black.copy(alpha = 0.72f)


// ═══════════════════════════════════════════════════════════════════════
// LIGHT COLOR SCHEME
// ═══════════════════════════════════════════════════════════════════════

val md_theme_light_primary = DeepEmerald
val md_theme_light_onPrimary = Color.White

val md_theme_light_primaryContainer = Color(0xFFD3E9E3)
val md_theme_light_onPrimaryContainer = Color(0xFF00201B)

val md_theme_light_secondary = ProfessionalNavy
val md_theme_light_onSecondary = Color.White

val md_theme_light_secondaryContainer = Color(0xFFD8E5F5)
val md_theme_light_onSecondaryContainer = Color(0xFF071D35)

val md_theme_light_tertiary = ChampagneGoldDark
val md_theme_light_onTertiary = Color.White

val md_theme_light_tertiaryContainer = ChampagneGoldSoft
val md_theme_light_onTertiaryContainer = Color(0xFF3B2F00)

val md_theme_light_error = DangerRed
val md_theme_light_onError = Color.White

val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onErrorContainer = Color(0xFF410002)

val md_theme_light_background = WarmWhite
val md_theme_light_onBackground = Graphite

val md_theme_light_surface = Color.White
val md_theme_light_onSurface = Graphite

val md_theme_light_surfaceVariant = SoftBeige
val md_theme_light_onSurfaceVariant = SlateGray

val md_theme_light_outline = StoneGray
val md_theme_light_outlineVariant = Color(0xFFE4DED4)

val md_theme_light_scrim = Color.Black


// ═══════════════════════════════════════════════════════════════════════
// DARK COLOR SCHEME
// ═══════════════════════════════════════════════════════════════════════

val md_theme_dark_primary = Color(0xFF72D6C6)
val md_theme_dark_onPrimary = Color(0xFF003731)

val md_theme_dark_primaryContainer = Color(0xFF075046)
val md_theme_dark_onPrimaryContainer = Color(0xFF9AF2E2)

val md_theme_dark_secondary = Color(0xFFB7CBE3)
val md_theme_dark_onSecondary = Color(0xFF0D2944)

val md_theme_dark_secondaryContainer = Color(0xFF243D59)
val md_theme_dark_onSecondaryContainer = Color(0xFFD5E7FF)

val md_theme_dark_tertiary = ChampagneGoldLight
val md_theme_dark_onTertiary = Color(0xFF382C00)

val md_theme_dark_tertiaryContainer = Color(0xFF554500)
val md_theme_dark_onTertiaryContainer = Color(0xFFFFE58D)

val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_onError = Color(0xFF690005)

val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)

val md_theme_dark_background = Obsidian
val md_theme_dark_onBackground = DarkText

val md_theme_dark_surface = DeepSurface
val md_theme_dark_onSurface = DarkText

val md_theme_dark_surfaceVariant = ElevatedSurface
val md_theme_dark_onSurfaceVariant = DarkMutedText

val md_theme_dark_outline = Color(0xFF71847E)
val md_theme_dark_outlineVariant = DarkBorder

val md_theme_dark_scrim = Color.Black