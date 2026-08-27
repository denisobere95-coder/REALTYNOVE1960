package com.denis.realtynova.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.domain.model.Property
import com.denis.realtynova.core.util.PriceFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyComparisonScreen(
    id1: String,
    id2: String,
    onBack: () -> Unit,
    viewModel: PropertyComparisonViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(id1, id2) {
        viewModel.loadProperties(id1, id2)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Property Comparison", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = DeepEmerald)
            } else if (uiState.property1 != null && uiState.property2 != null) {
                ComparisonTable(p1 = uiState.property1!!, p2 = uiState.property2!!)
            } else if (uiState.error != null) {
                Text(text = uiState.error!!, modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun ComparisonTable(p1: Property, p2: Property) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
        // Headers (Images)
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            ComparisonImageHeader(property = p1, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(16.dp))
            ComparisonImageHeader(property = p2, modifier = Modifier.weight(1f))
        }
        
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.5f))

        // Attribute Rows
        ComparisonRow(label = "Price", value1 = PriceFormatter.formatPrice(p1.price), value2 = PriceFormatter.formatPrice(p2.price))
        ComparisonRow(label = "Location", value1 = p1.location, value2 = p2.location)
        ComparisonRow(label = "Type", value1 = p1.type, value2 = p2.type)
        ComparisonRow(label = "Bedrooms", value1 = p1.bedrooms.toString(), value2 = p2.bedrooms.toString())
        ComparisonRow(label = "Bathrooms", value1 = p1.bathrooms.toString(), value2 = p2.bathrooms.toString())
        ComparisonRow(label = "Area", value1 = "${p1.areaSqFt.toInt()} sqft", value2 = "${p2.areaSqFt.toInt()} sqft")
        ComparisonRow(label = "Verified", value1 = if(p1.isVerified) "Yes" else "No", value2 = if(p2.isVerified) "Yes" else "No")
        ComparisonRow(label = "Investment Score", value1 = "92/100", value2 = "88/100")
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ComparisonRow(label: String, value1: String, value2: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value1,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = DeepEmerald
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = value2,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = DeepEmerald
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
    }
}

@Composable
fun ComparisonImageHeader(property: Property, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = property.images.firstOrNull()?.url ?: "",
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = property.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}
