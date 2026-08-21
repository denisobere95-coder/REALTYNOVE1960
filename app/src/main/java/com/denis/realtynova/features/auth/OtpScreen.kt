package com.denis.realtynova.features.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.denis.realtynova.R
import com.denis.realtynova.core.designsystem.components.ButtonVariant
import com.denis.realtynova.core.designsystem.components.RealtyNovaButton
import kotlinx.coroutines.delay

@Composable
fun OtpScreen(
    phoneNumber: String,
    onVerify: (String) -> Unit,
    onResend: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    var otpCode by remember {
        mutableStateOf("")
    }

    var isFocused by remember {
        mutableStateOf(false)
    }

    var isVerifying by remember {
        mutableStateOf(false)
    }

    var verificationError by remember {
        mutableStateOf(false)
    }

    var resendSeconds by remember {
        mutableIntStateOf(30)
    }

    val focusRequester = remember {
        FocusRequester()
    }

    /*
     * Automatic keyboard focus.
     */
    LaunchedEffect(Unit) {
        delay(450)
        focusRequester.requestFocus()
    }

    /*
     * Resend countdown.
     */
    LaunchedEffect(resendSeconds) {
        if (resendSeconds > 0) {
            delay(1_000)
            resendSeconds--
        }
    }

    /*
     * Automatically verify when six digits are entered.
     *
     * Keep the actual backend verification in your ViewModel.
     */
    LaunchedEffect(otpCode) {
        verificationError = false

        if (otpCode.length == 6 && !isVerifying) {
            isVerifying = true

            /*
             * Replace this with ViewModel verification in production.
             */
            delay(250)

            onVerify(otpCode)

            isVerifying = false
        }
    }

    /*
     * Subtle animated background.
     */
    val infiniteTransition = rememberInfiniteTransition(
        label = "otpBackground"
    )

    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 5_500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AsyncImage(
            model = R.drawable.img_34,
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
         * Ambient glow.
         */
        Box(
            modifier = Modifier
                .size(300.dp)
                .scale(glowScale)
                .align(Alignment.TopEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFD7B76A).copy(alpha = 0.18f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        /*
         * Back button.
         */
        TextButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .padding(
                    top = 8.dp,
                    start = 8.dp
                )
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )

                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                Text(
                    text = "Back",
                    color = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.ime)
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 74.dp,
                    bottom = 28.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /*
             * Verification badge.
             */
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(
                        Color.White.copy(alpha = 0.10f),
                        RoundedCornerShape(24.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "SECURITY CHECK",
                color = Color.White.copy(alpha = 0.60f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Verify your number",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "We've sent a 6-digit verification code to",
                color = Color.White.copy(alpha = 0.70f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            /*
             * Masked phone number.
             */
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = Color(0xFFD7B76A),
                    modifier = Modifier.size(17.dp)
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = phoneNumber,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(
                modifier = Modifier.height(34.dp)
            )

            /*
             * OTP card.
             */
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                color = MaterialTheme.colorScheme.surface.copy(
                    alpha = 0.97f
                ),
                tonalElevation = 8.dp,
                shadowElevation = 14.dp
            ) {

                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "ENTER CODE",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp
                    )

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    /*
                     * Custom OTP cells.
                     */
                    OtpInputRow(
                        code = otpCode,
                        focused = isFocused,
                        hasError = verificationError,
                        onCodeChange = { newCode ->
                            if (
                                newCode.length <= 6 &&
                                newCode.all { it.isDigit() }
                            ) {
                                otpCode = newCode
                            }
                        },
                        focusRequester = focusRequester,
                        onFocusChanged = {
                            isFocused = it
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    /*
                     * Error message.
                     */
                    AnimatedVisibility(
                        visible = verificationError,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = "That code doesn't look right. Please try again.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    /*
                     * Verification state.
                     */
                    AnimatedContent(
                        targetState = isVerifying,
                        label = "verificationState"
                    ) { verifying ->

                        if (verifying) {
                            Text(
                                text = "VERIFYING...",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                letterSpacing = 1.2.sp
                            )
                        } else {
                            Text(
                                text = "${otpCode.length} / 6 digits entered",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(22.dp)
                    )

                    /*
                     * Verify button.
                     */
                    RealtyNovaButton(
                        onClick = {
                            if (otpCode.length == 6) {
                                isVerifying = true
                                onVerify(otpCode)
                            }
                        },
                        enabled = otpCode.length == 6 && !isVerifying,
                        variant = ButtonVariant.Premium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {

                        AnimatedContent(
                            targetState = isVerifying,
                            label = "buttonContent"
                        ) { verifying ->

                            if (verifying) {
                                Text(
                                    text = "VERIFYING...",
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.1.sp
                                )
                            } else {
                                Text(
                                    text = "VERIFY & CONTINUE",
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.1.sp
                                )
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    /*
                     * Resend section.
                     */
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Didn't receive the code?",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(
                            modifier = Modifier.width(5.dp)
                        )

                        if (resendSeconds > 0) {

                            Text(
                                text = "Resend in ${resendSeconds}s",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall
                            )

                        } else {

                            TextButton(
                                onClick = {
                                    resendSeconds = 30
                                    otpCode = ""
                                    verificationError = false
                                    onResend()
                                    focusRequester.requestFocus()
                                }
                            ) {

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )

                                    Spacer(
                                        modifier = Modifier.width(4.dp)
                                    )

                                    Text(
                                        text = "Resend code",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            /*
             * Security message.
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.45f),
                    modifier = Modifier.size(14.dp)
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = "Your verification is encrypted and secure",
                    color = Color.White.copy(alpha = 0.45f),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }
        }

        /*
         * Invisible input layer.
         *
         * It receives the actual keyboard input while the
         * six visible cells provide the premium visual UI.
         */
        BasicTextField(
            value = otpCode,
            onValueChange = { value ->
                if (
                    value.length <= 6 &&
                    value.all { it.isDigit() }
                ) {
                    otpCode = value
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            cursorBrush = SolidColor(Color.Transparent),
            textStyle = TextStyle(
                color = Color.Transparent
            ),
            modifier = Modifier
                .size(1.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused = it.isFocused
                }
        )
    }
}

@Composable
private fun OtpInputRow(
    code: String,
    focused: Boolean,
    hasError: Boolean,
    onCodeChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        repeat(6) { index ->

            val character =
                code.getOrNull(index)?.toString() ?: ""

            val isCurrent =
                focused && code.length == index

                    OtpCell(
                        character = character,
                        isCurrent = isCurrent,
                        hasError = hasError,
                        index = index,
                        modifier = Modifier.weight(1f)
                    )
        }
    }

    /*
     * Clicking any OTP cell restores keyboard focus.
     */
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .clickable {
                focusRequester.requestFocus()
                onFocusChanged(true)
            }
    )
}

@Composable
private fun OtpCell(
    character: String,
    isCurrent: Boolean,
    hasError: Boolean,
    index: Int,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = when {
            hasError ->
                MaterialTheme.colorScheme.error

            isCurrent ->
                MaterialTheme.colorScheme.primary

            character.isNotEmpty() ->
                MaterialTheme.colorScheme.primary.copy(
                    alpha = 0.45f
                )

            else ->
                MaterialTheme.colorScheme.outlineVariant
        },
        animationSpec = tween(180),
        label = "otpBorder"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            hasError ->
                MaterialTheme.colorScheme.error.copy(
                    alpha = 0.08f
                )

            isCurrent ->
                MaterialTheme.colorScheme.primary.copy(
                    alpha = 0.10f
                )

            character.isNotEmpty() ->
                MaterialTheme.colorScheme.primary.copy(
                    alpha = 0.06f
                )

            else ->
                MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.55f
                )
        },
        animationSpec = tween(180),
        label = "otpBackground"
    )

    val scale by animateFloatAsState(
        targetValue = if (character.isNotEmpty()) {
            1f
        } else {
            0.96f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "otpScale"
    )

    Box(
        modifier = modifier
            .height(62.dp)
            .scale(scale)
            .clip(RoundedCornerShape(17.dp))
            .background(backgroundColor)
            .border(
                width = if (isCurrent || hasError) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(17.dp)
            ),
        contentAlignment = Alignment.Center
    ) {

        AnimatedContent(
            targetState = character,
            transitionSpec = {
                (fadeIn(animationSpec = tween(120)) + scaleIn(animationSpec = tween(180)))
                    .togetherWith(fadeOut(animationSpec = tween(80)))
            },
            label = "otpCharacter"
        ) { value ->

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = if (hasError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
                textAlign = TextAlign.Center
            )
        }

        /*
         * Current input indicator.
         */
        AnimatedVisibility(
            visible = isCurrent && character.isEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
                    .width(18.dp)
                    .height(2.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(50)
                    )
            )
        }

        /*
         * Completed-cell indicator.
         */
        if (character.isNotEmpty() && !hasError) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(
                    alpha = 0.25f
                ),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(5.dp)
                    .size(11.dp)
            )
        }
    }
}

