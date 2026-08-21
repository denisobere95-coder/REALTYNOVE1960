
package com.denis.realtynova.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.designsystem.theme.DeepEmerald


/*
 * ================================================================
 * REALTYNOVA MOTION LOADING SYSTEM
 * ================================================================
 *
 * The loading state should communicate:
 *
 * "The property experience is loading"
 *
 * rather than:
 *
 * "The application is frozen."
 */


/*
 * ================================================================
 * PREMIUM SHIMMER
 * ================================================================
 */

@Composable
fun ShimmerModifier(
    showShimmer: Boolean = true,
    targetValue: Float = 1200f
): Modifier {

    if (!showShimmer) {
        return Modifier
    }

    val transition =
        rememberInfiniteTransition(
            label = "RealtyNovaShimmer"
        )

    val shimmerPosition by
    transition.animateFloat(
        initialValue = -targetValue,
        targetValue = targetValue,

        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 1450,
                        easing = LinearEasing
                    ),

                repeatMode =
                    RepeatMode.Restart
            ),

        label = "ShimmerPosition"
    )

    val baseColor =
        MaterialTheme
            .colorScheme
            .surfaceVariant
            .copy(alpha = 0.72f)

    val highlightColor =
        MaterialTheme
            .colorScheme
            .surface
            .copy(alpha = 0.98f)

    val goldHighlight =
        ChampagneGold.copy(
            alpha = 0.055f
        )

    val brush =
        Brush.linearGradient(
            colors =
                listOf(
                    baseColor,
                    baseColor,
                    highlightColor,
                    goldHighlight,
                    baseColor,
                    baseColor
                ),

            start =
                androidx.compose.ui.geometry.Offset(
                    x = shimmerPosition - 300f,
                    y = shimmerPosition - 300f
                ),

            end =
                androidx.compose.ui.geometry.Offset(
                    x = shimmerPosition + 300f,
                    y = shimmerPosition + 300f
                )
        )

    return Modifier.background(brush)
}


/*
 * ================================================================
 * GENERIC LOADING SKELETON
 * ================================================================
 */

@Composable
fun LoadingSkeleton(
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 200.dp,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp)
) {

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(height)
                .clip(shape)
                .background(
                    MaterialTheme
                        .colorScheme
                        .surfaceVariant
                        .copy(alpha = 0.42f)
                )
                .border(
                    width = 1.dp,
                    color =
                        Color.White.copy(
                            alpha = 0.07f
                        ),
                    shape = shape
                )
                .then(
                    ShimmerModifier()
                )
    )
}


/*
 * ================================================================
 * PROPERTY CARD SKELETON
 * ================================================================
 *
 * Use this while property API/database results are loading.
 *
 * It mirrors the eventual UI rather than showing a generic block.
 */

@Composable
fun PropertyCardSkeleton(
    modifier: Modifier = Modifier
) {

    Surface(
        modifier =
            modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(24.dp),

        color =
            MaterialTheme
                .colorScheme
                .surface
                .copy(alpha = 0.96f),

        tonalElevation = 3.dp
    ) {

        Column {

            /*
             * Property image
             */

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 24.dp,
                                topEnd = 24.dp
                            )
                        )
                        .then(
                            ShimmerModifier()
                        )
            )

            Column(
                modifier =
                    Modifier.padding(16.dp),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                /*
                 * Price
                 */

                LoadingSkeleton(
                    modifier =
                        Modifier.width(145.dp),

                    height = 25.dp,

                    shape =
                        RoundedCornerShape(8.dp)
                )

                /*
                 * Title
                 */

                LoadingSkeleton(
                    modifier =
                        Modifier.fillMaxWidth(0.78f),

                    height = 20.dp,

                    shape =
                        RoundedCornerShape(7.dp)
                )

                /*
                 * Location
                 */

                LoadingSkeleton(
                    modifier =
                        Modifier.fillMaxWidth(0.58f),

                    height = 16.dp,

                    shape =
                        RoundedCornerShape(7.dp)
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    repeat(3) {

                        LoadingSkeleton(
                            modifier =
                                Modifier.width(70.dp),

                            height = 28.dp,

                            shape =
                                RoundedCornerShape(50.dp)
                        )
                    }
                }
            }
        }
    }
}


/*
 * ================================================================
 * FULL PROPERTY FEED LOADING
 * ================================================================
 */

@Composable
fun PropertyFeedLoading(
    modifier: Modifier = Modifier,
    itemCount: Int = 3
) {

    Column(
        modifier =
            modifier.fillMaxWidth(),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        repeat(itemCount.coerceIn(1, 8)) {

            PropertyCardSkeleton()
        }
    }
}


/*
 * ================================================================
 * PREMIUM ERROR STATE
 * ================================================================
 */

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {

    val transition =
        rememberInfiniteTransition(
            label = "ErrorPulse"
        )

    val pulse by
    transition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.08f,

        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 1500,
                        easing =
                            FastOutSlowInEasing
                    ),

                repeatMode =
                    RepeatMode.Reverse
            ),

        label = "ErrorPulseScale"
    )

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(28.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        /*
         * --------------------------------------------------------
         * Error visual
         * --------------------------------------------------------
         */

        Box(
            modifier =
                Modifier
                    .size(104.dp)
                    .graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                    }
                    .clip(CircleShape)
                    .background(
                        MaterialTheme
                            .colorScheme
                            .error
                            .copy(alpha = 0.08f)
                    )
                    .border(
                        width = 1.dp,
                        color =
                            MaterialTheme
                                .colorScheme
                                .error
                                .copy(alpha = 0.18f),
                        shape = CircleShape
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            Box(
                modifier =
                    Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme
                                .colorScheme
                                .error
                                .copy(alpha = 0.12f)
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.CloudOff,

                    contentDescription =
                        "Connection error",

                    tint =
                        MaterialTheme
                            .colorScheme
                            .error,

                    modifier =
                        Modifier.size(32.dp)
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(28.dp)
        )

        /*
         * --------------------------------------------------------
         * Headline
         * --------------------------------------------------------
         */

        Text(
            text =
                "We couldn't load that",

            style =
                MaterialTheme
                    .typography
                    .headlineSmall,

            fontWeight =
                FontWeight.Bold,

            textAlign =
                TextAlign.Center
        )

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        /*
         * --------------------------------------------------------
         * Description
         * --------------------------------------------------------
         */

        Text(
            text =
                message.ifBlank {
                    "Something interrupted your RealtyNova experience."
                },

            style =
                MaterialTheme
                    .typography
                    .bodyLarge,

            color =
                MaterialTheme
                    .colorScheme
                    .onSurface
                    .copy(alpha = 0.62f),

            textAlign =
                TextAlign.Center
        )

        Spacer(
            modifier =
                Modifier.height(28.dp)
        )

        /*
         * --------------------------------------------------------
         * Retry button
         * --------------------------------------------------------
         */

        RealtyNovaButton(
            onClick = onRetry,
            variant =
                ButtonVariant.Premium
        ) {

            Icon(
                imageVector =
                    Icons.Default.Refresh,

                contentDescription =
                    null,

                modifier =
                    Modifier.size(18.dp)
            )

            Spacer(
                modifier =
                    Modifier.width(9.dp)
            )

            Text(
                text =
                    "TRY AGAIN",

                fontWeight =
                    FontWeight.Bold,

                letterSpacing =
                    0.8.sp
            )
        }
    }
}


/*
 * ================================================================
 * COMPACT ERROR STATE
 * ================================================================
 *
 * Useful inside property lists, cards or smaller containers.
 */

@Composable
fun CompactErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(24.dp)
                )
                .background(
                    MaterialTheme
                        .colorScheme
                        .error
                        .copy(alpha = 0.055f)
                )
                .border(
                    width = 1.dp,
                    color =
                        MaterialTheme
                            .colorScheme
                            .error
                            .copy(alpha = 0.12f),
                    shape =
                        RoundedCornerShape(24.dp)
                )
                .padding(22.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector =
                Icons.Default.Warning,

            contentDescription =
                "Error",

            tint =
                MaterialTheme
                    .colorScheme
                    .error,

            modifier =
                Modifier.size(28.dp)
        )

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        Text(
            text =
                "Unable to load",

            style =
                MaterialTheme
                    .typography
                    .titleMedium,

            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(5.dp)
        )

        Text(
            text =
                message,

            style =
                MaterialTheme
                    .typography
                    .bodyMedium,

            color =
                MaterialTheme
                    .colorScheme
                    .onSurface
                    .copy(alpha = 0.6f),

            textAlign =
                TextAlign.Center
        )

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        RealtyNovaButton(
            onClick = onRetry,
            variant =
                ButtonVariant.Secondary
        ) {

            Icon(
                imageVector =
                    Icons.Default.Refresh,

                contentDescription =
                    null,

                modifier =
                    Modifier.size(16.dp)
            )

            Spacer(
                modifier =
                    Modifier.width(7.dp)
            )

            Text(
                text = "RETRY",
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}
