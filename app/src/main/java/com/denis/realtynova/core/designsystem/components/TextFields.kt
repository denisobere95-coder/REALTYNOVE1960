package com.denis.realtynova.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.designsystem.theme.DeepEmerald

@Composable
fun RealtyNovaSearchBar(
    onSearchClick: () -> Unit,
    onAiClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onSearchClick),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 12.dp).size(22.dp)
            )
            
            Text(
                text = "Search properties...",
                modifier = Modifier.weight(1f).padding(start = 12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyLarge
            )
            
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clickable(onClick = onAiClick),
                shape = CircleShape,
                color = DeepEmerald
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Assistant",
                        tint = ChampagneGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RealtyNovaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    error: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val hasError = !error.isNullOrBlank()
    val isValid = value.isNotBlank() && !hasError

    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            hasError -> MaterialTheme.colorScheme.error
            isFocused -> DeepEmerald
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        },
        animationSpec = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing
        ),
        label = "InputBorderColor"
    )

    val containerColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
            hasError -> MaterialTheme.colorScheme.error.copy(alpha = 0.045f)
            isFocused -> DeepEmerald.copy(alpha = 0.045f)
            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f)
        },
        animationSpec = tween(250),
        label = "InputContainerColor"
    )

    val labelColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            hasError -> MaterialTheme.colorScheme.error
            isFocused -> DeepEmerald
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(250),
        label = "InputLabelColor"
    )

    val elevationOffset by animateDpAsState(
        targetValue = if (isFocused && enabled) 2.dp else 0.dp,
        animationSpec = tween<androidx.compose.ui.unit.Dp>(durationMillis = 250),
        label = "InputElevation"
    )

    val focusScale by animateFloatAsState(
        targetValue = if (isFocused && enabled) 1.002f else 1f,
        animationSpec = tween<Float>(durationMillis = 220),
        label = "InputScale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = focusScale
                scaleY = focusScale
            }
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(containerColor)
                .border(
                    width = if (isFocused || hasError) 1.5.dp else 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(elevationOffset)
        ) {

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = label
                    },
                enabled = enabled,
                label = {
                    Text(
                        text = label,
                        fontWeight = if (isFocused) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Normal
                        }
                    )
                },
                placeholder = placeholder?.let {
                    {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                .copy(alpha = 0.55f)
                        )
                    }
                },
                leadingIcon = leadingIcon,
                trailingIcon = {
                    when {
                        trailingIcon != null -> {
                            trailingIcon()
                        }

                        hasError -> {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Invalid $label",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        isValid -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "$label completed",
                                tint = DeepEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                singleLine = singleLine,
                isError = hasError,
                interactionSource = interactionSource,
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,

                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    errorBorderColor = Color.Transparent,

                    focusedLabelColor = labelColor,
                    unfocusedLabelColor = labelColor,
                    errorLabelColor = MaterialTheme.colorScheme.error,

                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,

                    focusedLeadingIconColor = DeepEmerald,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,

                    focusedTrailingIconColor = DeepEmerald,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        if (hasError) {
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.padding(
                    start = 8.dp,
                    end = 8.dp
                ),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(15.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
