package com.denis.realtynova.features.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.denis.realtynova.R
import com.denis.realtynova.core.designsystem.components.ButtonVariant
import com.denis.realtynova.core.designsystem.components.RealtyNovaButton
import com.denis.realtynova.core.designsystem.components.RealtyNovaTextField

@Composable
fun PhoneLoginScreen(
    onSendCodeAction: (String) -> Unit,
    onBackClickAction: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    val isValidPhone = phoneNumber.filter { it.isDigit() }.length >= 9

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = R.drawable.img_32,
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

        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            IconButton(onClick = onBackClickAction) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(text = "Continue with\nphone number", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            RealtyNovaTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = "Phone Number",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            RealtyNovaButton(
                onClick = { onSendCodeAction(phoneNumber) },
                enabled = isValidPhone && !isSending,
                modifier = Modifier.fillMaxWidth().height(58.dp)
            ) {
                Text("SEND CODE", fontWeight = FontWeight.Bold)
            }
        }
    }
}
