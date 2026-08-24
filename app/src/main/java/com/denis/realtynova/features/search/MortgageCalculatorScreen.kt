package com.denis.realtynova.features.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.ChampagneGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MortgageCalculatorScreen(
    onBack: () -> Unit
) {
    var amount by remember { mutableStateOf(5000000f) }
    var interest by remember { mutableStateOf(12f) }
    var years by remember { mutableStateOf(20f) }

    val monthlyPayment = remember(amount, interest, years) {
        val r = (interest / 100) / 12
        val n = years * 12
        if (r == 0f) amount / n else (amount * r * Math.pow((1 + r).toDouble(), n.toDouble()) / (Math.pow((1 + r).toDouble(), n.toDouble()) - 1)).toFloat()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mortgage Calculator", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = DeepEmerald,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("ESTIMATED MONTHLY PAYMENT", color = ChampagneGold.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("KSh %,.0f".format(monthlyPayment), color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total Interest: KSh %,.0f".format((monthlyPayment * years * 12) - amount), color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
            }

            CalculatorSlider(
                label = "Property Amount",
                value = amount,
                onValueChange = { amount = it },
                valueRange = 1000000f..100000000f,
                prefix = "KSh "
            )

            CalculatorSlider(
                label = "Interest Rate",
                value = interest,
                onValueChange = { interest = it },
                valueRange = 5f..25f,
                suffix = "%"
            )

            CalculatorSlider(
                label = "Loan Term",
                value = years,
                onValueChange = { years = it },
                valueRange = 1f..30f,
                suffix = " Years"
            )
        }
    }
}

@Composable
private fun CalculatorSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    prefix: String = "",
    suffix: String = ""
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "$prefix%,.0f$suffix".format(value), fontWeight = FontWeight.Black, color = DeepEmerald)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(thumbColor = DeepEmerald, activeTrackColor = DeepEmerald)
        )
    }
}
