package com.denis.realtynova.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denis.realtynova.core.designsystem.components.CreativeBackground
import com.denis.realtynova.core.designsystem.components.BackgroundVariant
import com.denis.realtynova.R
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.ChampagneGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketInsightsScreen(
    onBack: () -> Unit
) {
    CreativeBackground(
        imageRes = R.drawable.img_52,
        variant = BackgroundVariant.DARK,
        overlayAlpha = 0.85f
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Market Insights", fontWeight = FontWeight.Bold, color = Color.White) },
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
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    MarketOverviewCard()
                }
                item {
                    PriceTrendCard()
                }
                item {
                    InvestmentHotspotCard(name = "Westlands", appreciation = "+12.4%")
                }
                item {
                    InvestmentHotspotCard(name = "Karen", appreciation = "+8.2%")
                }
            }
        }
    }
}

@Composable
private fun MarketOverviewCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = DeepEmerald
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("MARKET CAPITALIZATION", color = ChampagneGold, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.5.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("KSh 428.5B", color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.TrendingUp, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Up 4.2% from last month", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun PriceTrendCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Price Trends", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(24.dp))
            // Minimalist Chart placeholder
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))) {
                Text("Interactive Chart Data Loading...", modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun InvestmentHotspotCard(name: String, appreciation: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontWeight = FontWeight.Bold)
                Text(text = "Appreciation Rate", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(text = appreciation, color = Color(0xFF2E7D32), fontWeight = FontWeight.Black, fontSize = 16.sp)
        }
    }
}
