package com.denis.realtynova.core.designsystem.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * RealtyNova Motion System
 *
 * A centralized motion language for:
 * - Navigation
 * - Cards
 * - Buttons
 * - Property images
 * - Bottom navigation
 * - Dialogs / sheets
 * - AI interactions
 * - Loading states
 * - Premium hero transitions
 *
 * Keep motion semantic. Screens should consume these tokens
 * instead of inventing arbitrary animation timings.
 */
@Immutable
data class RealtyNovaMotion(
    // ─────────────────────────────────────────────
    // Duration Tokens
    // ─────────────────────────────────────────────

    /** Tiny state changes: icons, toggles, opacity. */
    val micro: Int = 120,

    /** Button presses, favorite actions, small interactions. */
    val interaction: Int = 180,

    /** Normal content transitions. */
    val standard: Int = 280,

    /** Screen-level transitions. */
    val navigation: Int = 360,

    /** Large visual transitions. */
    val dramatic: Int = 450,

    /** Hero/property-detail animations. */
    val hero: Int = 600,

    /** Splash / brand entrance. */
    val cinematic: Int = 900,

    /** Long ambient animations. */
    val ambient: Int = 1800,

    // ─────────────────────────────────────────────
    // Stagger Tokens
    // ─────────────────────────────────────────────

    /** Delay between list items entering the screen. */
    val stagger: Int = 45,

    /** Larger delay used for hero content. */
    val heroStagger: Int = 90,

    // ─────────────────────────────────────────────
    // Motion Distance
    // ─────────────────────────────────────────────

    /** Small movement used for micro interactions. */
    val microOffset: Float = 4f,

    /** Standard content movement. */
    val standardOffset: Float = 20f,

    /** Large entrance movement. */
    val dramaticOffset: Float = 48f,

    /** Hero movement. */
    val heroOffset: Float = 80f
)

val LocalMotion = staticCompositionLocalOf {
    RealtyNovaMotion()
}


/**
 * RealtyNova spring library.
 *
 * These are semantic springs rather than screen-specific animations.
 */
object RealtyNovaSpring {

    /**
     * Soft, luxurious movement.
     *
     * Best for:
     * - Property cards
     * - Images
     * - Bottom navigation
     * - Favorite animations
     */
    val Gentle = spring<Float>(
        dampingRatio = 0.72f,
        stiffness = 220f
    )

    /**
     * Default interactive spring.
     *
     * Best for:
     * - Buttons
     * - Toggles
     * - Selection states
     * - Chips
     */
    val Responsive = spring<Float>(
        dampingRatio = 0.82f,
        stiffness = 500f
    )

    /**
     * Fast and controlled.
     *
     * Best for:
     * - Icon buttons
     * - Press states
     * - Small UI transitions
     */
    val Tight = spring<Float>(
        dampingRatio = 1f,
        stiffness = 850f
    )

    /**
     * Premium expressive spring.
     *
     * Slight overshoot gives hero elements
     * a polished, cinematic entrance.
     */
    val Premium = spring<Float>(
        dampingRatio = 0.68f,
        stiffness = 320f
    )

    /**
     * Very soft spring for large surfaces.
     *
     * Best for:
     * - Bottom sheets
     * - Property detail panels
     * - Search overlays
     */
    val Soft = spring<Float>(
        dampingRatio = 0.86f,
        stiffness = 180f
    )

    /**
     * Snappy spring for navigation and controls.
     */
    val Snappy = spring<Float>(
        dampingRatio = 0.92f,
        stiffness = 700f
    )
}


/**
 * RealtyNova easing curves.
 *
 * Use these for tween-based animations where a spring
 * is not appropriate.
 */
object RealtyNovaEasing {

    /**
     * Elegant ease-out.
     *
     * Excellent for elements entering the screen.
     */
    val Enter = CubicBezierEasing(
        0.16f,
        1f,
        0.3f,
        1f
    )

    /**
     * Smooth exit.
     */
    val Exit = CubicBezierEasing(
        0.7f,
        0f,
        0.84f,
        0f
    )

    /**
     * Premium material-style movement.
     */
    val Standard = CubicBezierEasing(
        0.2f,
        0f,
        0f,
        1f
    )

    /**
     * Cinematic hero movement.
     */
    val Cinematic = CubicBezierEasing(
        0.12f,
        0.8f,
        0.18f,
        1f
    )

    /**
     * Very smooth ambient motion.
     */
    val Ambient = CubicBezierEasing(
        0.37f,
        0f,
        0.63f,
        1f
    )
}


/**
 * Semantic motion categories.
 *
 * Useful when you want screens to communicate intent
 * instead of directly choosing arbitrary durations.
 */
enum class RealtyNovaMotionType {
    Micro,
    Interaction,
    Standard,
    Navigation,
    Dramatic,
    Hero,
    Cinematic,
    Ambient
}


/**
 * Resolves a semantic motion type to its duration.
 */
fun RealtyNovaMotion.duration(
    type: RealtyNovaMotionType
): Int {
    return when (type) {
        RealtyNovaMotionType.Micro -> micro
        RealtyNovaMotionType.Interaction -> interaction
        RealtyNovaMotionType.Standard -> standard
        RealtyNovaMotionType.Navigation -> navigation
        RealtyNovaMotionType.Dramatic -> dramatic
        RealtyNovaMotionType.Hero -> hero
        RealtyNovaMotionType.Cinematic -> cinematic
        RealtyNovaMotionType.Ambient -> ambient
    }
}