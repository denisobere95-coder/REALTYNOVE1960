package com.denis.realtynova.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.denis.realtynova.core.designsystem.theme.MidnightEmerald
import com.denis.realtynova.core.designsystem.theme.MidnightNavy

enum class BackgroundVariant {
    EMERALD, NAVY, DARK
}

@Composable
fun CreativeBackground(
    imageRes: Int,
    variant: BackgroundVariant = BackgroundVariant.EMERALD,
    overlayAlpha: Float = 0.82f,
    content: @Composable () -> Unit
) {
    val baseColor = when (variant) {
        BackgroundVariant.EMERALD -> MidnightEmerald
        BackgroundVariant.NAVY -> MidnightNavy
        BackgroundVariant.DARK -> Color.Black
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            baseColor.copy(alpha = overlayAlpha),
                            baseColor.copy(alpha = overlayAlpha * 0.6f),
                            baseColor.copy(alpha = overlayAlpha * 0.95f)
                        )
                    )
                )
        )
        content()
    }
}
