
package com.denis.realtynova.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * REALTYNOVA DESIGN SYSTEM
 *
 * Premium 4dp-based spacing system.
 *
 * The spacing scale is intentionally semantic:
 * - Micro spacing → icon/text relationships
 * - Component spacing → fields, buttons, badges
 * - Section spacing → major content groups
 * - Hero spacing → premium/immersive layouts
 *
 * Keep screen-level layouts on this scale instead of
 * scattering arbitrary dp values throughout the application.
 */
@Immutable
data class Spacing(
    // ─────────────────────────────────────────────
    // FOUNDATION
    // ─────────────────────────────────────────────

    /** No spacing. Useful for overriding defaults. */
    val none: Dp = 0.dp,

    /** 2dp optical adjustment. */
    val hairline: Dp = 2.dp,

    /** 4dp — icon/text micro relationship. */
    val micro: Dp = 4.dp,

    /** 8dp — compact internal spacing. */
    val extraSmall: Dp = 8.dp,

    /** 12dp — dense component spacing. */
    val small: Dp = 12.dp,

    /** 16dp — primary component spacing. */
    val medium: Dp = 16.dp,

    /** 20dp — comfortable component spacing. */
    val mediumLarge: Dp = 20.dp,

    /** 24dp — standard card/content padding. */
    val large: Dp = 24.dp,

    /** 28dp — visual breathing room. */
    val largePlus: Dp = 28.dp,

    /** 32dp — major component separation. */
    val extraLarge: Dp = 32.dp,

    /** 40dp — strong section separation. */
    val huge: Dp = 40.dp,

    /** 48dp — screen-level spacing. */
    val xxLarge: Dp = 48.dp,

    /** 56dp — large hero separation. */
    val xxxLarge: Dp = 56.dp,

    /** 64dp — major layout separation. */
    val giant: Dp = 64.dp,

    /** 80dp — cinematic/hero spacing. */
    val massive: Dp = 80.dp,

    /** 96dp — immersive premium layouts. */
    val extreme: Dp = 96.dp,

    /** 120dp — hero-level breathing room. */
    val cinematic: Dp = 120.dp,

    // ─────────────────────────────────────────────
    // SEMANTIC SPACING
    // ─────────────────────────────────────────────

    /** Screen horizontal padding. */
    val screenHorizontal: Dp = 20.dp,

    /** Screen vertical padding. */
    val screenVertical: Dp = 24.dp,

    /** Standard card padding. */
    val card: Dp = 20.dp,

    /** Large premium card padding. */
    val cardLarge: Dp = 24.dp,

    /** Compact list item spacing. */
    val listItem: Dp = 12.dp,

    /** Space between major sections. */
    val section: Dp = 32.dp,

    /** Large section separation. */
    val sectionLarge: Dp = 48.dp,

    /** Hero section separation. */
    val hero: Dp = 64.dp,

    /** Form field separation. */
    val formField: Dp = 16.dp,

    /** Form section separation. */
    val formSection: Dp = 24.dp,

    /** Button internal horizontal spacing. */
    val buttonHorizontal: Dp = 24.dp,

    /** Button internal vertical spacing. */
    val buttonVertical: Dp = 12.dp,

    /** Icon-to-text spacing. */
    val iconText: Dp = 8.dp,

    /** Badge-to-badge spacing. */
    val badge: Dp = 6.dp,

    /** Property card content spacing. */
    val propertyCard: Dp = 16.dp,

    /** Property metadata spacing. */
    val propertyMeta: Dp = 8.dp,

    /** Bottom navigation content padding. */
    val bottomBar: Dp = 16.dp,

    /** Floating action button offset. */
    val floatingAction: Dp = 20.dp,

    /** Search component internal padding. */
    val search: Dp = 16.dp,

    /** AI chat message spacing. */
    val chatMessage: Dp = 12.dp,

    /** Empty-state content spacing. */
    val emptyState: Dp = 24.dp
)

/**
 * Global RealtyNova spacing provider.
 *
 * Access through:
 *
 * MaterialTheme.spacing
 *
 * after providing LocalSpacing inside RealtyNovaTheme.
 */
val LocalSpacing = staticCompositionLocalOf {
    Spacing()
}

