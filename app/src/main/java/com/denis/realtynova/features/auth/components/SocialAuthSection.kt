package com.denis.realtynova.features.auth.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SocialAuthSection(
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit,
    onInstagramClick: () -> Unit,
    onPhoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            SocialButton(
                icon = Icons.Default.Phone,
                contentDescription = "Phone",
                containerColor = Color(0xFF4CAF50),
                onClick = onPhoneClick
            )
            SocialButton(
                text = "G",
                contentDescription = "Google",
                containerColor = Color(0xFFDB4437),
                onClick = onGoogleClick
            )
            SocialButton(
                text = "f",
                contentDescription = "Facebook",
                containerColor = Color(0xFF4267B2),
                onClick = onFacebookClick
            )
        }
    }
}

@Composable
private fun SocialButton(
    icon: ImageVector? = null,
    text: String? = null,
    contentDescription: String,
    containerColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(56.dp),
        shape = androidx.compose.foundation.shape.CircleShape,
        color = containerColor.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, containerColor.copy(alpha = 0.2f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(24.dp),
                    tint = containerColor
                )
            } else if (text != null) {
                Text(
                    text = text,
                    color = containerColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}
