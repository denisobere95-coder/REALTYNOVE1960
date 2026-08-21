package com.denis.realtynova.features.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.denis.realtynova.R
import com.denis.realtynova.core.designsystem.components.ButtonVariant
import com.denis.realtynova.core.designsystem.components.RealtyNovaButton
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(250)
        contentVisible = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "welcomeMotion")
    val imageScale by infiniteTransition.animateFloat(
        initialValue = 1.03f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "imageScale"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = R.drawable.img_51,
            contentDescription = null,
            modifier = Modifier.fillMaxSize().scale(imageScale),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.15f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.85f)
                    )
                )
            )
        )

        Column(
            modifier = Modifier.fillMaxSize().navigationBarsPadding().padding(24.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(1200)) + slideInVertically(tween(1000)) { it / 2 }
            ) {
                Column {
                    Text(
                        text = "The Future of\nKenyan Real Estate.",
                        color = Color.White,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 44.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Discover premium properties, verified land, and intelligent market insights in one seamless experience.",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 26.sp
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    RealtyNovaButton(
                        onClick = onNavigateToRegister,
                        variant = ButtonVariant.Premium,
                        modifier = Modifier.fillMaxWidth().height(62.dp)
                    ) {
                        Text("START EXPLORING", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    RealtyNovaButton(
                        onClick = onNavigateToLogin,
                        variant = ButtonVariant.Secondary,
                        modifier = Modifier.fillMaxWidth().height(62.dp)
                    ) {
                        Text("I ALREADY HAVE AN ACCOUNT", fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    }
                }
            }
        }
    }
}
