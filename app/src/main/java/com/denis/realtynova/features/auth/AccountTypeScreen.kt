package com.denis.realtynova.features.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.denis.realtynova.R
import com.denis.realtynova.core.designsystem.components.ButtonVariant
import com.denis.realtynova.core.designsystem.components.RealtyNovaButton
import com.denis.realtynova.core.domain.model.UserRole

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AccountTypeScreen(
    onTypeSelected: (UserRole) -> Unit
) {
    var selectedRole by remember {
        mutableStateOf<UserRole?>(null)
    }

    /*
     * Subtle background motion.
     *
     * It is deliberately slow so the screen feels premium,
     * rather than looking like an animated game interface.
     */
    val infiniteTransition = rememberInfiniteTransition(
        label = "backgroundMotion"
    )

    val glowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowOffset"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AsyncImage(
            model = R.drawable.img_31,
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
                            Color.Black.copy(alpha = 0.5f),
                            Color.Transparent,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        /*
         * Atmospheric background glow.
         */
        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(0.90f + (glowOffset * 0.12f))
                .align(Alignment.TopEnd)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFD7B76A).copy(alpha = 0.18f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 20.dp,
                    vertical = 28.dp
                )
        ) {

            /*
             * Onboarding progress.
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProgressDot(
                    active = true
                )

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(
                            Color.White.copy(alpha = 0.25f)
                        )
                )

                ProgressDot(
                    active = false
                )

                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(
                            Color.White.copy(alpha = 0.25f)
                        )
                )

                ProgressDot(
                    active = false
                )
            }

            Spacer(
                modifier = Modifier.height(34.dp)
            )

            /*
             * Brand eyebrow.
             */
            Text(
                text = "STEP 01 OF 03",
                color = Color.White.copy(alpha = 0.65f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Choose your\nREALTYNOVA journey.",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 42.sp
                )
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Tell us how you plan to use REALTYNOVA so we can personalize your experience.",
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            /*
             * Role cards.
             */
            RoleCard(
                role = UserRole.BUYER,
                title = "Buyer / Renter",
                subtitle = "Discover your next place",
                description = "Explore verified homes, apartments, land and commercial properties.",
                icon = Icons.Default.Home,
                isSelected = selectedRole == UserRole.BUYER,
                accent = Color(0xFF4FA98E),
                onSelect = {
                    selectedRole = UserRole.BUYER
                }
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            RoleCard(
                role = UserRole.AGENT,
                title = "Agent / Developer",
                subtitle = "Grow your property business",
                description = "List properties, manage leads, schedule viewings and track performance.",
                icon = Icons.Default.Business,
                isSelected = selectedRole == UserRole.AGENT,
                accent = Color(0xFFD7B76A),
                onSelect = {
                    selectedRole = UserRole.AGENT
                }
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            RoleCard(
                role = UserRole.LANDLORD,
                title = "Seller / Landlord",
                subtitle = "Turn your property into opportunity",
                description = "Market your property and connect with verified potential buyers or tenants.",
                icon = Icons.Default.Person,
                isSelected = selectedRole == UserRole.LANDLORD,
                accent = Color(0xFF6F8FD8),
                onSelect = {
                    selectedRole = UserRole.LANDLORD
                }
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            /*
             * Dynamic selection summary.
             */
            AnimatedContent(
                targetState = selectedRole,
                transitionSpec = {
                    fadeIn(
                        animationSpec = tween(220)
                    ) + scaleIn(
                        animationSpec = tween(220)
                    ) with fadeOut(
                        animationSpec = tween(150)
                    )
                },
                label = "selectionSummary"
            ) { role ->

                if (role != null) {

                    SelectedRoleSummary(
                        role = role
                    )
                } else {

                    Text(
                        text = "Select one option to continue.",
                        color = Color.White.copy(alpha = 0.55f),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            /*
             * Main CTA.
             */
            RealtyNovaButton(
                onClick = {
                    selectedRole?.let(onTypeSelected)
                },
                enabled = selectedRole != null,
                variant = ButtonVariant.Premium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "CONTINUE",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            Text(
                text = "You can update your preferences later.",
                color = Color.White.copy(alpha = 0.45f),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }
    }
}

@Composable
private fun RoleCard(
    role: UserRole,
    title: String,
    subtitle: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    accent: Color,
    onSelect: () -> Unit
) {
    val borderColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isSelected) {
            accent.copy(alpha = 0.85f)
        } else {
            Color.White.copy(alpha = 0.10f)
        },
        animationSpec = tween(250),
        label = "borderColor"
    )

    val backgroundColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isSelected) {
            Color.White.copy(alpha = 0.14f)
        } else {
            Color.White.copy(alpha = 0.07f)
        },
        animationSpec = tween(250),
        label = "backgroundColor"
    )

    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = spring(
            dampingRatio = 0.65f,
            stiffness = 450f
        ),
        label = "iconScale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(26.dp)
            )
            .clickable(
                onClick = onSelect
            ),
        shape = RoundedCornerShape(26.dp),
        color = backgroundColor
    ) {

        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /*
             * Role icon.
             */
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(
                        color = if (isSelected) {
                            accent.copy(alpha = 0.16f)
                        } else {
                            Color.White.copy(alpha = 0.08f)
                        },
                        shape = RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) {
                        accent
                    } else {
                        Color.White.copy(alpha = 0.75f)
                    },
                    modifier = Modifier
                        .size(29.dp)
                        .scale(iconScale)
                )
            }

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            /*
             * Text content.
             */
            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    color = if (isSelected) {
                        Color.White
                    } else {
                        Color.White.copy(alpha = 0.92f)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = subtitle,
                    color = if (isSelected) {
                        accent
                    } else {
                        Color.White.copy(alpha = 0.65f)
                    },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Text(
                    text = description,
                    color = Color.White.copy(alpha = 0.58f),
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            /*
             * Selection indicator.
             */
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .background(
                        color = if (isSelected) {
                            accent
                        } else {
                            Color.Transparent
                        },
                        shape = CircleShape
                    )
                    .border(
                        width = if (isSelected) 0.dp else 1.5.dp,
                        color = if (isSelected) {
                            Color.Transparent
                        } else {
                            Color.White.copy(alpha = 0.28f)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                AnimatedContent(
                    targetState = isSelected,
                    label = "checkAnimation"
                ) { selected ->

                    if (selected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectedRoleSummary(
    role: UserRole
) {
    val message = when (role) {
        UserRole.BUYER ->
            "Your REALTYNOVA experience will focus on discovering and securing properties."

        UserRole.AGENT ->
            "Your dashboard will focus on listings, leads, viewings and property performance."

        UserRole.LANDLORD ->
            "Your experience will focus on marketing, managing and transacting your properties."

        else ->
            "Your REALTYNOVA experience will be personalized to your account."
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.07f)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            color = Color.White.copy(alpha = 0.72f),
            style = MaterialTheme.typography.bodySmall,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun ProgressDot(
    active: Boolean
) {
    Box(
        modifier = Modifier
            .size(
                if (active) 11.dp else 8.dp
            )
            .background(
                color = if (active) {
                    Color(0xFFD7B76A)
                } else {
                    Color.White.copy(alpha = 0.35f)
                },
                shape = CircleShape
            )
    )
}

