package com.denis.realtynova.features.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.denis.realtynova.R
import com.denis.realtynova.core.designsystem.components.RealtyNovaLogo
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.RealtyNovaTextStyles
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    viewModel: AuthViewModel,
    onNavigateToMain: () -> Unit,
    onNavigateToWelcome: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()

    val revealProgress = remember { Animatable(0f) }
    
    val logoScale by animateFloatAsState(
        targetValue = if (revealProgress.value > 0.8f) 1f else 0.85f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow),
        label = "logoScale"
    )

    val shimmerTranslate = rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val rotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(3000, easing = LinearEasing)
            )
        }
        
        revealProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1200, easing = FastOutSlowInEasing)
        )
        
        delay(800)
        
        when {
            currentUser != null -> onNavigateToMain()
            !isOnboardingCompleted -> onNavigateToOnboarding()
            else -> onNavigateToWelcome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AsyncImage(
            model = R.drawable.img_51,
            contentDescription = null,
            modifier = Modifier.fillMaxSize().alpha(0.2f),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DeepEmerald,
                            Color(0xFF021F1B)
                        )
                    )
                )
        )
        // Architectural Background Rings
        Canvas(modifier = Modifier.size(400.dp).alpha(0.15f * revealProgress.value)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            drawCircle(
                color = ChampagneGold,
                radius = (size.minDimension / 2) * 0.8f,
                style = Stroke(width = 1.dp.toPx()),
                center = center
            )
            drawCircle(
                color = Color.White,
                radius = (size.minDimension / 2) * 0.6f,
                style = Stroke(width = 0.5.dp.toPx()),
                center = center
            )
        }

        // Shimmer Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        start = Offset(shimmerTranslate.value - 500f, shimmerTranslate.value - 500f),
                        end = Offset(shimmerTranslate.value, shimmerTranslate.value)
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            RealtyNovaLogo(
                tint = Color.White,
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale)
                    .alpha(revealProgress.value)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "REALTYNOVA",
                style = MaterialTheme.typography.displaySmall.copy(
                    color = Color.White,
                    letterSpacing = 8.sp,
                    fontWeight = FontWeight.Light
                ),
                modifier = Modifier.alpha(revealProgress.value.coerceIn(0f, 1f))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "DISCOVER • VERIFY • MOVE",
                style = RealtyNovaTextStyles.HeroEyebrow.copy(
                    color = ChampagneGold.copy(alpha = 0.8f)
                ),
                modifier = Modifier.alpha(revealProgress.value.coerceIn(0f, 1f))
            )
        }

        Text(
            text = "PREMIUM KENYAN REAL ESTATE",
            style = RealtyNovaTextStyles.PremiumLabel.copy(
                color = Color.White.copy(alpha = 0.4f),
                letterSpacing = 2.sp
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .alpha(revealProgress.value)
        )
    }
}
