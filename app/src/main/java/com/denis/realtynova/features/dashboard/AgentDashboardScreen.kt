package com.denis.realtynova.features.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.denis.realtynova.core.designsystem.components.CreativeBackground
import com.denis.realtynova.core.designsystem.components.BackgroundVariant
import com.denis.realtynova.R
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.designsystem.theme.RealtyNovaTextStyles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentDashboardScreen(
    onBack: () -> Unit,
    onManageListings: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    CreativeBackground(
        imageRes = R.drawable.img_11,
        variant = BackgroundVariant.DARK,
        overlayAlpha = 0.9f
    ) {
        AgentDashboardContent(
            uiState = uiState,
            onBack = onBack,
            onManageListings = onManageListings
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentDashboardContent(
    uiState: DashboardUiState,
    onBack: () -> Unit,
    onManageListings: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Agent Dashboard", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
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
            item { 
                Text(
                    text = "Welcome back, Agent",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            item {
                CommissionCard(
                    amount = uiState.totalCommissions,
                    goal = uiState.commissionGoal
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatBox(modifier = Modifier.weight(1f), label = "Active Leads", value = "42", icon = Icons.Default.Groups, color = DeepEmerald)
                    StatBox(modifier = Modifier.weight(1f), label = "Pending Viewings", value = uiState.viewingRequestsCount.toString(), icon = Icons.Default.CalendarMonth, color = ChampagneGold)
                }
            }

            item { SectionHeader(title = "Portfolio Performance") }
            item { MarketPerformanceChart() }

            item { SectionHeader(title = "Recent Activity") }

            item {
                ActivityItem(
                    title = "New viewing request",
                    subtitle = "The Emerald Heights • Tomorrow, 10 AM",
                    time = "12m ago",
                    icon = Icons.Default.NotificationsActive
                )
            }

            item {
                ActivityItem(
                    title = "Commission paid",
                    subtitle = "Lavington Garden Residence",
                    time = "2h ago",
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF2E7D32)
                )
            }

            item {
                Button(
                    onClick = onManageListings,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEmerald)
                ) {
                    Text("MANAGE GLOBAL LISTINGS", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CommissionCard(amount: Double, goal: Double) {
    val progress = (amount / goal).toFloat().coerceIn(0f, 1f)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF151515)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = "TOTAL COMMISSIONS", color = ChampagneGold, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.2.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "KSh %,.0f".format(amount), color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = ChampagneGold,
                trackColor = Color.White.copy(alpha = 0.1f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${(progress * 100).toInt()}% of monthly goal reached", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun StatBox(modifier: Modifier, label: String, value: String, icon: ImageVector, color: Color) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ActivityItem(title: String, subtitle: String, time: String, icon: ImageVector, color: Color = DeepEmerald) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = color.copy(alpha = 0.1f)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(text = time, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun MarketPerformanceChart() {
    Surface(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Lead Conversion", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "+12% vs last month", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val points = listOf(0.4f, 0.3f, 0.6f, 0.5f, 0.8f, 0.7f, 0.9f)
                val width = size.width
                val height = size.height
                
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, height * (1 - points[0]))
                    points.forEachIndexed { index, point ->
                        val x = (width / (points.size - 1)) * index
                        val y = height * (1 - point)
                        lineTo(x, y)
                    }
                }
                
                drawPath(
                    path = path,
                    brush = Brush.verticalGradient(listOf(DeepEmerald, Color.Transparent)),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
        Text(text = "SEE ALL", color = DeepEmerald, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.clickable { })
    }
}

@Preview(showBackground = true)
@Composable
fun AgentDashboardScreenPreview() {
    com.denis.realtynova.core.designsystem.theme.REALTYNOVATheme {
        AgentDashboardContent(
            uiState = DashboardUiState(
                userRole = com.denis.realtynova.core.domain.model.UserRole.AGENT,
                totalCommissions = 2450000.0,
                viewingRequestsCount = 8
            ),
            onBack = {},
            onManageListings = {}
        )
    }
}
