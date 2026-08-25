package com.denis.realtynova.features.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.denis.realtynova.R
import com.denis.realtynova.core.designsystem.components.ButtonVariant
import com.denis.realtynova.core.designsystem.components.RealtyNovaButton
import com.denis.realtynova.core.designsystem.components.RealtyNovaLogo
import com.denis.realtynova.core.designsystem.components.RealtyNovaTextField
import com.denis.realtynova.features.auth.components.SocialAuthSection
import kotlinx.coroutines.delay

@Composable
fun RegisterScreen(
    onRegisterSuccessAction: (String, String, String, String) -> Unit,
    onLoginClickAction: () -> Unit,
    onGoogleSignInAction: () -> Unit = {},
    onPhoneAuthClickAction: () -> Unit,
    onBackClickAction: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }

    val logoScale = remember {
        Animatable(0.82f)
    }

    LaunchedEffect(Unit) {
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 650,
                easing = FastOutSlowInEasing
            )
        )

        delay(100)
        showContent = true
    }

    val passwordScore = remember(password) {
        calculatePasswordScore(password)
    }

    val passwordProgress = passwordScore / 4f

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AsyncImage(
            model = R.drawable.img_30,
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
                            Color.Black.copy(alpha = 0.4f),
                            Color.Transparent,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        IconButton(
            onClick = onBackClickAction,
            modifier = Modifier
                .padding(
                    start = 12.dp,
                    top = 12.dp
                )
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go back",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.ime)
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 70.dp,
                    bottom = 28.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(74.dp)
                    .scale(logoScale.value)
                    .background(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                RealtyNovaLogo(
                    tint = Color.White,
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "REALTYNOVA",
                color = Color.White,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            Text(
                text = "Find a place worth calling home.",
                color = Color.White.copy(alpha = 0.70f),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(
                    animationSpec = tween(400)
                ) + slideInVertically(
                    initialOffsetY = { 45 },
                    animationSpec = tween(500)
                ),
                exit = fadeOut()
            ) {

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.surface.copy(
                        alpha = 0.98f
                    ),
                    tonalElevation = 8.dp,
                    shadowElevation = 14.dp
                ) {

                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {

                        Text(
                            text = "Create your account",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(7.dp)
                        )

                        Text(
                            text = "Join REALTYNOVA and start discovering verified properties.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(
                            modifier = Modifier.height(26.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            RegistrationStep(
                                number = "1",
                                title = "Account",
                                active = true
                            )

                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(
                                        MaterialTheme.colorScheme.outlineVariant
                                    )
                            )

                            RegistrationStep(
                                number = "2",
                                title = "Verify",
                                active = false
                            )

                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(
                                        MaterialTheme.colorScheme.outlineVariant
                                    )
                            )

                            RegistrationStep(
                                number = "3",
                                title = "Explore",
                                active = false
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(28.dp)
                        )

                        RealtyNovaTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Full name",
                            placeholder = "Enter your full name",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null
                                )
                            }
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        RealtyNovaTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = "Phone number",
                            placeholder = "+254 7XX XXX XXX",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null
                                )
                            },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                            )
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        RealtyNovaTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email address",
                            placeholder = "name@example.com",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null
                                )
                            }
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        RealtyNovaTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Create password",
                            placeholder = "Enter a strong password",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null
                                )
                            },
                            visualTransformation =
                                if (passwordVisible) {
                                    VisualTransformation.None
                                } else {
                                    PasswordVisualTransformation()
                                },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        passwordVisible = !passwordVisible
                                    }
                                ) {
                                    Icon(
                                        imageVector =
                                            if (passwordVisible) {
                                                Icons.Default.VisibilityOff
                                            } else {
                                                Icons.Default.Visibility
                                            },
                                        contentDescription =
                                            if (passwordVisible) {
                                                "Hide password"
                                            } else {
                                                "Show password"
                                            }
                                    )
                                }
                            }
                        )

                        AnimatedVisibility(
                            visible = password.isNotEmpty()
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = 10.dp
                                    )
                            ) {

                                LinearProgressIndicator(
                                    progress = {
                                        passwordProgress
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp)
                                )

                                Spacer(
                                    modifier = Modifier.height(6.dp)
                                )

                                Text(
                                    text = passwordStrengthLabel(
                                        passwordScore
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(22.dp)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(7.dp)
                        ) {

                            SecurityItem(
                                text = "Secure account protection",
                                enabled = passwordScore >= 2
                            )

                            SecurityItem(
                                text = "Verified property access",
                                enabled = true
                            )

                            SecurityItem(
                                text = "Your personal information stays private",
                                enabled = true
                            )
                        }

                        if (!errorMessage.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )

                        RealtyNovaButton(
                            onClick = { onRegisterSuccessAction(name, email, password, phoneNumber) },
                            variant = ButtonVariant.Premium,
                            isLoading = isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text(
                                text = "CREATE ACCOUNT",
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(
                                        MaterialTheme.colorScheme.outlineVariant
                                    )
                            )

                            Text(
                                text = "  OR SIGN UP WITH  ",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(
                                        MaterialTheme.colorScheme.outlineVariant
                                    )
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )

                        SocialAuthSection(
                            onGoogleClick = onGoogleSignInAction,
                            onFacebookClick = { },
                            onInstagramClick = { },
                            onPhoneClick = onPhoneAuthClickAction
                        )

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Already have an account? ",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = "Sign in",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        RealtyNovaButton(
                            onClick = onLoginClickAction,
                            variant = ButtonVariant.Secondary,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "SIGN IN",
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "SECURE • VERIFIED • PRIVATE",
                color = Color.White.copy(alpha = 0.42f),
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.6.sp
            )
        }
    }
}

@Composable
private fun RegistrationStep(
    number: String,
    title: String,
    active: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(30.dp)
                .background(
                    color = if (active) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(50)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = if (active) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SecurityItem(
    text: String,
    enabled: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    color = if (enabled) {
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.12f
                        )
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(50)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (enabled) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(13.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(
            modifier = Modifier.size(8.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun calculatePasswordScore(
    password: String
): Int {
    if (password.isEmpty()) return 0

    var score = 0

    if (password.length >= 8) score++
    if (password.any { it.isUpperCase() }) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++

    return score
}

private fun passwordStrengthLabel(
    score: Int
): String {
    return when (score) {
        0 -> ""
        1 -> "Weak password"
        2 -> "Fair password"
        3 -> "Strong password"
        else -> "Excellent password"
    }
}
