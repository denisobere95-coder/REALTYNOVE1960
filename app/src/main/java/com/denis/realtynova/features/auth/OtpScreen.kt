package com.denis.realtynova.features.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
    onNavigateBack: () -> Unit,
    isVerifying: Boolean = false,
    errorMessage: String? = null
) {
    var otpCode by rememberSaveable { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    var secondsRemaining by rememberSaveable { mutableIntStateOf(30) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(450)
        focusRequester.requestFocus()
    }

    LaunchedEffect(secondsRemaining) {
        if (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
        }
    }

    LaunchedEffect(otpCode) {
        if (otpCode.length == 6 && !isVerifying) {
            onVerify(otpCode)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(16.dp).statusBarsPadding()
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.ime)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Verify your number",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Enter the 6-digit code sent to\n$phoneNumber",
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 14.dp
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    OtpInputRow(
                        code = otpCode,
                        focused = isFocused,
                        hasError = !errorMessage.isNullOrBlank(),
                        onCodeChange = { if (it.length <= 6 && it.all { it.isDigit() }) otpCode = it },
                        focusRequester = focusRequester,
                        onFocusChanged = { isFocused = it }
                    )

                    if (!errorMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (secondsRemaining > 0) {
                        Text(text = "Resend code in ${secondsRemaining}s", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        TextButton(onClick = { secondsRemaining = 30; onResend() }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Resend code", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    RealtyNovaButton(
                        onClick = { onVerify(otpCode) },
                        enabled = otpCode.length == 6 && !isVerifying,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        if (isVerifying) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("VERIFY & CONTINUE", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        BasicTextField(
            value = otpCode,
            onValueChange = { if (it.length <= 6 && it.all { it.isDigit() }) otpCode = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.size(1.dp).focusRequester(focusRequester).onFocusChanged { isFocused = it.isFocused }
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
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(6) { index ->
            val character = code.getOrNull(index)?.toString() ?: ""
            val isCurrent = focused && code.length == index
            OtpCell(character = character, isCurrent = isCurrent, hasError = hasError, modifier = Modifier.weight(1f))
        }
    }
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).clickable { focusRequester.requestFocus(); onFocusChanged(true) })
}

@Composable
private fun OtpCell(
    character: String,
    isCurrent: Boolean,
    hasError: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = when {
            hasError -> MaterialTheme.colorScheme.error
            isCurrent -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outlineVariant
        },
        label = "otpBorder"
    )

    Box(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (hasError) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = character,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = if (hasError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
        if (isCurrent && character.isEmpty()) {
            Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp).width(16.dp).height(2.dp).background(MaterialTheme.colorScheme.primary))
        }
    }
}
