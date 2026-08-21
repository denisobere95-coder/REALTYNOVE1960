
package com.denis.realtynova.core.designsystem.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

val RealtyNovaShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(28.dp)
)

/*
 * ============================================================
 * REALTYNOVA — PREMIUM DESIGN SYSTEM
 * ============================================================
 *
 * Architecture:
 *
 * REALTYNOVATheme
 *      ├── Brand Colors
 *      ├── Typography
 *      ├── Spacing
 *      ├── Motion
 *      └── System UI
 *
 * All screens should consume the design system rather than
 * defining arbitrary colors, spacing and animations locally.
 *
 * This keeps the entire application visually consistent.
 */


/* ============================================================
 * DARK COLOR SYSTEM
 * ============================================================ */

private val RealtyNovaDarkColorScheme = darkColorScheme(

    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,

    primaryContainer = Color(0xFF064E47),
    onPrimaryContainer = Color(0xFFA7F3E8),

    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,

    secondaryContainer = Color(0xFF173452),
    onSecondaryContainer = Color(0xFFD8E8FF),

    tertiary = ChampagneGold,
    onTertiary = Color(0xFF211A00),

    tertiaryContainer = Color(0xFF4A3B00),
    onTertiaryContainer = Color(0xFFFFE38A),

    error = md_theme_dark_error,
    onError = Color(0xFF601410),

    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFFFDAD6),

    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,

    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,

    surfaceVariant = Color(0xFF1C2927),
    onSurfaceVariant = Color(0xFFC1CCC9),

    outline = Color(0xFF899492),
    outlineVariant = Color(0xFF3D4947),

    scrim = Color.Black
)


/* ============================================================
 * LIGHT COLOR SYSTEM
 * ============================================================ */

private val RealtyNovaLightColorScheme = lightColorScheme(

    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,

    primaryContainer = Color(0xFFB2DFDB),
    onPrimaryContainer = Color(0xFF00201B),

    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,

    secondaryContainer = Color(0xFFD9E8FF),
    onSecondaryContainer = Color(0xFF001B3D),

    tertiary = ChampagneGold,
    onTertiary = Color(0xFFFFFFFF),

    tertiaryContainer = Color(0xFFFFE8A3),
    onTertiaryContainer = Color(0xFF241A00),

    error = md_theme_light_error,
    onError = Color.White,

    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,

    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,

    surfaceVariant = SoftBeige,
    onSurfaceVariant = Color(0xFF454746),

    outline = Color(0xFF737775),
    outlineVariant = Color(0xFFC3C8C5),

    scrim = Color.Black
)


/* ============================================================
 * REALTYNOVA THEME
 * ============================================================ */

@Composable
fun REALTYNOVATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),

    /*
     * Disabled by default.
     *
     * RealtyNova should maintain its own visual identity rather
     * than allowing Android dynamic colors to completely replace
     * the emerald / navy / champagne brand system.
     */
    dynamicColor: Boolean = false,

    content: @Composable () -> Unit
) {

    val context = LocalContext.current

    val colorScheme = when {

        dynamicColor &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {

            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        darkTheme -> RealtyNovaDarkColorScheme

        else -> RealtyNovaLightColorScheme
    }


    /*
     * ------------------------------------------------------------
     * SYSTEM UI
     * ------------------------------------------------------------
     */

    val view = LocalView.current

    if (!view.isInEditMode) {

        SideEffect {

            val window = (view.context as Activity).window

            /*
             * Modern edge-to-edge window behavior.
             */
            WindowCompat.setDecorFitsSystemWindows(
                window,
                false
            )

            /*
             * Transparent system bars allow the application's
             * surfaces and gradients to extend naturally behind
             * them.
             */
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT

            val controller =
                WindowCompat.getInsetsController(window, view)

            /*
             * Light icons on dark backgrounds.
             * Dark icons on light backgrounds.
             */
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }


    /*
     * ------------------------------------------------------------
     * GLOBAL DESIGN TOKENS
     * ------------------------------------------------------------
     */

    CompositionLocalProvider(

        /*
         * Spacing system created for RealtyNova.
         */
        LocalSpacing provides Spacing(),

        /*
         * Centralized animation/motion system.
         */
        LocalMotion provides RealtyNovaMotion(),

        ) {

        MaterialTheme(

            colorScheme = colorScheme,

            typography = RealtyNovaTypography,

            /*
             * Material 3 shape hierarchy.
             *
             * Small:
             * buttons / chips
             *
             * Medium:
             * inputs / standard cards
             *
             * Large:
             * premium cards / sheets
             */
            shapes = RealtyNovaShapes,

            content = content
        )
    }
}


/* ============================================================
 * SPACING ACCESSOR
 * ============================================================ */

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current


/* ============================================================
 * MOTION ACCESSOR
 * ============================================================ */

val MaterialTheme.motion: RealtyNovaMotion
    @Composable
    @ReadOnlyComposable
    get() = LocalMotion.current

