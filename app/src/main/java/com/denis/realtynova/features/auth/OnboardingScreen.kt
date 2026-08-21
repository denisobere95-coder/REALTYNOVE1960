package com.denis.realtynova.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    viewModel: AuthViewModel,
    onNavigateToAuth: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage(
            title = "Discover Luxury",
            description = "Browse the most exclusive properties in Kenya, from penthouses in Westlands to villas in Karen.",
            image = R.drawable.img_15
        ),
        OnboardingPage(
            title = "Verified Listings",
            description = "Every property is manually verified by our team to ensure your safety and peace of mind.",
            image = R.drawable.img_16
        ),
        OnboardingPage(
            title = "Seamless Moving",
            description = "Find your dream home and move in with ease. We handle the process from discovery to move-in.",
            image = R.drawable.img_17
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { index ->
            OnboardingContent(pages[index])
        }

        // Indicators & Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pager Indicator
            Row(
                Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pages.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) ChampagneGold else Color.White.copy(alpha = 0.3f)
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(if (pagerState.currentPage == iteration) 24.dp else 8.dp, 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skip Button
                if (pagerState.currentPage < 2) {
                    TextButton(onClick = {
                        viewModel.completeOnboarding()
                        onNavigateToAuth()
                    }) {
                        Text(
                            text = "SKIP",
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // Next / Get Started Button
                RealtyNovaButton(
                    onClick = {
                        if (pagerState.currentPage < 2) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            viewModel.completeOnboarding()
                            onNavigateToAuth()
                        }
                    },
                    variant = if (pagerState.currentPage == 2) ButtonVariant.Premium else ButtonVariant.Secondary,
                    modifier = Modifier
                        .width(if (pagerState.currentPage == 2) 200.dp else 120.dp)
                        .height(56.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage == 2) "GET STARTED" else "NEXT",
                        fontWeight = FontWeight.Bold
                    )
                    if (pagerState.currentPage < 2) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingContent(page: OnboardingPage) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = page.image,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            DeepEmerald.copy(alpha = 0.5f),
                            DeepEmerald.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(bottom = 150.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = page.title,
                color = Color.White,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = page.description,
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

private data class OnboardingPage(
    val title: String,
    val description: String,
    val image: Int
)
