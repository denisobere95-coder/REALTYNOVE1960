package com.denis.realtynova.features.search

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import coil.compose.AsyncImage
import com.denis.realtynova.R
import com.denis.realtynova.core.designsystem.components.*
import com.denis.realtynova.core.designsystem.theme.*
import com.denis.realtynova.core.domain.model.*
import com.denis.realtynova.core.util.PriceFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    category: String? = null,
    location: String? = null,
    onPropertyClick: (String) -> Unit = {},
    onOpenMap: () -> Unit = {},
    onOpenAi: () -> Unit = {},
    onFiltersClick: () -> Unit = {},
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(category, location) {
        if (category != null || location != null) {
            viewModel.updateFilter(
                uiState.filter.copy(
                    propertyType = category ?: uiState.filter.propertyType,
                    query = location ?: uiState.filter.query
                )
            )
        }
    }

    if (uiState.isFilterSheetVisible) {
        FilterBottomSheet(
            filter = uiState.filter,
            onFilterChanged = { viewModel.updateFilter(it) },
            onDismiss = { viewModel.toggleFilterSheet(false) }
        )
    }

    CreativeBackground(
        imageRes = R.drawable.img_12,
        variant = BackgroundVariant.NAVY,
        overlayAlpha = 0.85f
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                SearchBottomDock(
                    onOpenMap = onOpenMap,
                    onOpenAi = onOpenAi
                )
            }
        ) { innerPadding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp, 18.dp, 16.dp, 28.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item { SearchHeader() }

                item {
                    SmartSearchBox(
                        value = uiState.filter.query,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        onAiClick = onOpenAi
                    )
                }

                if (uiState.filter.query.isEmpty() && uiState.filter == SearchFilter()) {
                    item { AiSearchHero(onClick = onOpenAi) }

                    item {
                        CategorySelector(
                            categories = listOf("All", "Apartment", "House", "Land", "Commercial"),
                            selected = uiState.filter.propertyType ?: "All",
                            onSelected = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            val type = if (it == "All") null else it
                            viewModel.updateFilter(uiState.filter.copy(propertyType = type))
                        }
                        )
                    }

                    item { 
                        QuickFilters(
                            onFiltersClick = { viewModel.toggleFilterSheet(true) },
                            onOpenMap = onOpenMap
                        ) 
                    }

                    item { 
                        DiscoveryHeader(onViewMap = onOpenMap) 
                    }
                }
                
                if (uiState.isLoading) {
                    items(3) { 
                        PropertyCardShimmer()
                    }
                } else if (uiState.results.isEmpty()) {
                    item {
                        EmptySearchState(onReset = { viewModel.clearFilters() })
                    }
                } else {
                    items(uiState.results) { property ->
                        PropertyCard(
                            imageUrl = property.images.firstOrNull()?.url ?: "",
                            price = PriceFormatter.formatPrice(property.price),
                            title = property.title,
                            location = property.location,
                            specs = "${property.bedrooms} Beds • ${property.bathrooms} Baths • ${property.areaSqFt} sqft",
                            type = property.type,
                            listingType = property.listingType,
                            isVerified = property.isVerified,
                            isPremium = property.isPremium,
                            trustScore = property.trustScore,
                            onClick = { onPropertyClick(property.id) },
                            onFavoriteClick = {},
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySearchState(onReset: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.LightGray.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No properties found",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Try adjusting your filters or search query",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onReset,
            colors = ButtonDefaults.buttonColors(containerColor = DeepEmerald)
        ) {
            Text("Reset Filters", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SearchHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "DISCOVER",
                style = RealtyNovaTextStyles.HeroEyebrow,
                color = ChampagneGold
            )
            Text(
                text = "Properties",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
        
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.NotificationsNone, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
private fun SmartSearchBox(
    value: String,
    onValueChange: (String) -> Unit,
    onAiClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = DeepEmerald)
            Spacer(modifier = Modifier.width(12.dp))
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search location, area, project...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            IconButton(onClick = onAiClick) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Search", tint = DeepEmerald)
            }
        }
    }
}

@Composable
private fun AiSearchHero(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightNavy)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(ChampagneGold.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = ChampagneGold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Try AI Search", color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = "Natural language property search", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            }
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White.copy(alpha = 0.3f))
        }
    }
}

@Composable
private fun CategorySelector(
    categories: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { category ->
            val isSelected = selected == category
            Surface(
                modifier = Modifier.clickable { onSelected(category) },
                shape = RoundedCornerShape(50.dp),
                color = if (isSelected) DeepEmerald else Color.White.copy(alpha = 0.1f),
                border = if (!isSelected) BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)) else null
            ) {
                Text(
                    text = category,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun QuickFilters(
    onFiltersClick: () -> Unit,
    onOpenMap: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
            onClick = onFiltersClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("Filters")
        }
        OutlinedButton(
            onClick = onOpenMap,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("Map View")
        }
    }
}

@Composable
private fun DiscoveryHeader(onViewMap: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(text = "Nearby Properties", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            text = "View Map", 
            color = ChampagneGold, 
            fontWeight = FontWeight.Bold, 
            fontSize = 12.sp,
            modifier = Modifier.clickable { onViewMap() }
        )
    }
}

@Composable
private fun SearchBottomDock(onOpenMap: () -> Unit, onOpenAi: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        color = MidnightEmerald,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onOpenMap) {
                Icon(Icons.Default.Map, contentDescription = "Map", tint = Color.White)
            }
            VerticalDivider(modifier = Modifier.height(24.dp), color = Color.White.copy(alpha = 0.2f))
            Button(
                onClick = onOpenAi,
                colors = ButtonDefaults.buttonColors(containerColor = DeepEmerald),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("AI ASSISTANT", fontWeight = FontWeight.Bold)
            }
        }
    }
}
