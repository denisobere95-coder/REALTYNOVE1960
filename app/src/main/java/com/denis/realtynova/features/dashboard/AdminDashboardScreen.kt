package com.denis.realtynova.features.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.denis.realtynova.core.designsystem.components.CreativeBackground
import com.denis.realtynova.core.designsystem.components.BackgroundVariant
import com.denis.realtynova.R
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.designsystem.theme.RealtyNovaTextStyles
import com.denis.realtynova.core.designsystem.theme.REALTYNOVATheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit,
    onNavigateToModeration: () -> Unit = {}
) {
    CreativeBackground(
        imageRes = R.drawable.img_8,
        variant = BackgroundVariant.DARK,
        overlayAlpha = 0.92f
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("CORE CONTROL CENTER", fontWeight = FontWeight.Black, letterSpacing = 2.sp, fontSize = 14.sp) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item { SystemHealthHeader() }
                
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            label = "ACTIVE USERS",
                            value = "12,842",
                            trend = "+14%",
                            color = DeepEmerald
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            label = "ESCROW FUNDS",
                            value = "KSh 420M",
                            trend = "+8.2%",
                            color = ChampagneGold
                        )
                    }
                }

                item { AdminSectionHeader(title = "AI CONCIERGE HEALTH") }
                item { AiHealthMonitor() }

                item { AdminSectionHeader(title = "SYSTEM COMMANDS") }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CommandButton(
                            icon = Icons.Default.VerifiedUser,
                            title = "Approve Pending Properties",
                            count = "24",
                            onClick = onNavigateToModeration
                        )
                        CommandButton(icon = Icons.Default.BugReport, title = "View System Logs", color = Color(0xFFD64545))
                        CommandButton(icon = Icons.Default.CloudSync, title = "Sync Global Market Data")
                        CommandButton(icon = Icons.Default.SettingsSuggest, title = "Adjust AI Temperature")
                    }
                }
            }
        }
    }
}

@Composable
private fun SystemHealthHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF151515),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f, targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(animation = tween(1000), repeatMode = RepeatMode.Reverse),
                    label = "scale"
                )
                Box(modifier = Modifier.size(12.dp).scale(scale).background(Color(0xFF4CAF50), CircleShape))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "ALL SYSTEMS OPERATIONAL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(text = "Last global sync: 2 mins ago", color = Color.Gray, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, label: String, value: String, trend: String, color: Color) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, color = color, fontWeight = FontWeight.Black, fontSize = 10.sp, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = trend, color = Color(0xFF4CAF50), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AiHealthMonitor() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF151515),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Gemini 1.5 Pro (Nairobi Edge)", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(20.dp))
            LinearProgressIndicator(progress = { 0.92f }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape), color = DeepEmerald, trackColor = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Context Utilization: 92%", color = Color.Gray, fontSize = 10.sp)
                Text(text = "Latency: 240ms", color = Color.Gray, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun AdminSectionHeader(title: String) {
    Text(
        text = title,
        color = Color.Gray,
        fontWeight = FontWeight.Black,
        fontSize = 11.sp,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(top = 10.dp)
    )
}

@Composable
private fun CommandButton(
    icon: ImageVector,
    title: String,
    count: String? = null,
    color: Color = Color.White,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, color = color, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            if (count != null) {
                Surface(shape = CircleShape, color = DeepEmerald) {
                    Text(text = count, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun AdminDashboardScreenPreview() {
    REALTYNOVATheme {
        AdminDashboardScreen(
            onBack = {},
            onNavigateToModeration = {}
        )
    }
}
