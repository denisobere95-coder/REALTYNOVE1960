package com.denis.realtynova.core.designsystem.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import kotlin.math.cos
import kotlin.math.sin


/**
 * REALTYNOVA
 *
 * Premium architectural logo:
 *
 *        ✦
 *       /\
 *      /  \
 *     / ┌┐ \
 *    /  ││  \
 *   └───┴┴───┘
 *
 * House       = Real Estate
 * Door        = Access / Trust
 * Nova Spark  = Intelligence / Discovery
 * Glow        = Premium technology
 */
@Composable
fun RealtyNovaLogo(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    tint: Color = Color.White,
    animated: Boolean = true,
    glow: Boolean = true,
    premiumGradient: Boolean = false
) {

    val transition =
        rememberInfiniteTransition(
            label = "RealtyNovaLogoMotion"
        )

    /*
     * Subtle breathing animation.
     *
     * This is intentionally restrained. A logo should not bounce
     * aggressively because it damages brand perception.
     */
    val breathe by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LogoBreathing"
    )

    /*
     * Nova sparkle pulse.
     */
    val sparklePulse by transition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "NovaSparkle"
    )

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                if (animated) {
                    scaleX = breathe
                    scaleY = breathe
                }
            }
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            val w = size.toPx()
            val h = size.toPx()

            /*
             * =====================================================
             * BRAND GEOMETRY
             * =====================================================
             */

            val strokeWidth = w * 0.065f

            /*
             * Main architectural house.
             */
            val housePath = Path().apply {

                moveTo(
                    w * 0.14f,
                    h * 0.78f
                )

                lineTo(
                    w * 0.14f,
                    h * 0.46f
                )

                lineTo(
                    w * 0.50f,
                    h * 0.16f
                )

                lineTo(
                    w * 0.86f,
                    h * 0.46f
                )

                lineTo(
                    w * 0.86f,
                    h * 0.78f
                )

                lineTo(
                    w * 0.14f,
                    h * 0.78f
                )
            }

            /*
             * =====================================================
             * PREMIUM GLOW
             * =====================================================
             */

            if (glow) {

                drawPath(
                    path = housePath,
                    color = tint.copy(alpha = 0.10f),
                    style = Stroke(
                        width = strokeWidth * 3.2f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                drawCircle(
                    color = ChampagneGold.copy(
                        alpha = 0.08f
                    ),
                    radius = w * 0.20f,
                    center = Offset(
                        w * 0.76f,
                        h * 0.28f
                    )
                )
            }

            /*
             * =====================================================
             * HOUSE OUTLINE
             * =====================================================
             */

            val houseBrush =
                if (premiumGradient) {
                    Brush.linearGradient(
                        colors = listOf(
                            DeepEmerald,
                            tint,
                            ChampagneGold
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            tint,
                            tint
                        )
                    )
                }

            drawPath(
                path = housePath,
                brush = houseBrush,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            /*
             * =====================================================
             * PREMIUM FOUNDATION
             * =====================================================
             *
             * Represents stability, ownership and trust.
             */

            drawLine(
                brush = houseBrush,
                start = Offset(
                    w * 0.10f,
                    h * 0.83f
                ),
                end = Offset(
                    w * 0.90f,
                    h * 0.83f
                ),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            /*
             * =====================================================
             * CENTRAL DOOR
             * =====================================================
             */

            val doorLeft = w * 0.425f
            val doorTop = h * 0.53f
            val doorRight = w * 0.575f
            val doorBottom = h * 0.83f

            val doorPath = Path().apply {

                moveTo(
                    doorLeft,
                    doorBottom
                )

                lineTo(
                    doorLeft,
                    doorTop
                )

                lineTo(
                    doorRight,
                    doorTop
                )

                lineTo(
                    doorRight,
                    doorBottom
                )
            }

            drawPath(
                path = doorPath,
                brush = houseBrush,
                style = Stroke(
                    width = strokeWidth * 0.72f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            /*
             * Door handle.
             */
            drawCircle(
                color =
                    if (premiumGradient) {
                        ChampagneGold
                    } else {
                        tint
                    },

                radius = w * 0.018f,

                center = Offset(
                    w * 0.545f,
                    h * 0.68f
                )
            )

            /*
             * =====================================================
             * NOVA SPARK
             * =====================================================
             *
             * Four-point architectural sparkle rather than
             * a generic five-point star.
             */

            val centerX = w * 0.78f
            val centerY = h * 0.29f

            val outerRadius =
                w * 0.115f * sparklePulse

            val innerRadius =
                outerRadius * 0.22f

            val sparklePath =
                Path()

            for (i in 0 until 8) {

                val angle =
                    (-Math.PI / 2.0) +
                            (Math.PI / 4.0) * i

                val radius =
                    if (i % 2 == 0) {
                        outerRadius
                    } else {
                        innerRadius
                    }

                val x =
                    centerX +
                            cos(angle).toFloat() *
                            radius

                val y =
                    centerY +
                            sin(angle).toFloat() *
                            radius

                if (i == 0) {
                    sparklePath.moveTo(x, y)
                } else {
                    sparklePath.lineTo(x, y)
                }
            }

            sparklePath.close()

            /*
             * Spark glow.
             */
            if (glow) {

                drawPath(
                    path = sparklePath,
                    color = ChampagneGold.copy(
                        alpha = 0.20f
                    )
                )
            }

            /*
             * Spark core.
             */
            drawPath(
                path = sparklePath,
                color =
                    if (premiumGradient) {
                        ChampagneGold
                    } else {
                        tint
                    }
            )

            /*
             * =====================================================
             * INNER ARCHITECTURAL LINE
             * =====================================================
             *
             * Adds a subtle premium "N" / architectural feeling
             * without literally drawing the wordmark.
             */

            drawLine(
                color =
                    tint.copy(alpha = 0.28f),

                start = Offset(
                    w * 0.23f,
                    h * 0.70f
                ),

                end = Offset(
                    w * 0.34f,
                    h * 0.70f
                ),

                strokeWidth = w * 0.018f,
                cap = StrokeCap.Round
            )

            drawLine(
                color =
                    tint.copy(alpha = 0.28f),

                start = Offset(
                    w * 0.66f,
                    h * 0.70f
                ),

                end = Offset(
                    w * 0.77f,
                    h * 0.70f
                ),

                strokeWidth = w * 0.018f,
                cap = StrokeCap.Round
            )
        }
    }
}


/**
 * Premium REALTYNOVA logo preset.
 *
 * Ideal for:
 *
 * - Splash screen
 * - Welcome screen
 * - Authentication
 * - Premium property screens
 */
@Composable
fun RealtyNovaPremiumLogo(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    animated: Boolean = true
) {

    RealtyNovaLogo(
        modifier = modifier,
        size = size,
        tint = Color.White,
        animated = animated,
        glow = true,
        premiumGradient = true
    )
}
