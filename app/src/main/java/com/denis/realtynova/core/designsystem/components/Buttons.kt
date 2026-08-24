package com.denis.realtynova.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.denis.realtynova.core.util.rememberHapticFeedback
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.RealtyNovaSpring

enum class ButtonVariant {
    Premium,
    Secondary,
    Outline
}

@Composable
fun RealtyNovaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Premium,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
    content: @Composable RowScope.() -> Unit
) {
    val haptic = rememberHapticFeedback()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = RealtyNovaSpring.Responsive,
        label = "ButtonScale"
    )

    val containerModifier = when (variant) {
        ButtonVariant.Premium -> {
            Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            DeepEmerald,
                            Color(0xFF0A5C52)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = ChampagneGold.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(18.dp)
                )
        }
        ButtonVariant.Secondary -> {
            Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
        }
        ButtonVariant.Outline -> {
            Modifier.border(
                width = 1.5.dp,
                color = DeepEmerald.copy(alpha = 0.45f),
                shape = RoundedCornerShape(18.dp)
            )
        }
    }

    val contentColor = when (variant) {
        ButtonVariant.Premium -> Color.White
        ButtonVariant.Secondary -> MaterialTheme.colorScheme.onSurfaceVariant
        ButtonVariant.Outline -> DeepEmerald
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(18.dp))
            .then(containerModifier)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = {
                    haptic()
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.ProvideTextStyle(
            value = MaterialTheme.typography.labelLarge.copy(
                color = contentColor,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        ) {
            Row(
                modifier = Modifier.padding(contentPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                content = content
            )
        }
    }
}
