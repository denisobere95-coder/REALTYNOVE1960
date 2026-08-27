package com.denis.realtynova.features.saved

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.denis.realtynova.core.designsystem.components.CreativeBackground
import com.denis.realtynova.core.designsystem.components.BackgroundVariant
import com.denis.realtynova.R
import com.denis.realtynova.core.designsystem.components.PropertyCard
import com.denis.realtynova.core.designsystem.theme.*
import com.denis.realtynova.core.domain.model.Property
import com.denis.realtynova.core.util.PriceFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface SavedRepository {
    fun observeSavedProperties(): Flow<List<Property>>
    suspend fun remove(propertyId: String)
    suspend fun save(property: Property)
}

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val savedRepository: SavedRepository
) : ViewModel() {

    val savedProperties = savedRepository.observeSavedProperties()

    fun removeSaved(propertyId: String) {
        viewModelScope.launch {
            savedRepository.remove(propertyId)
        }
    }
}

@Composable
fun SavedScreen(
    onPropertyClick: (String) -> Unit = {},
    onCompareClick: (String, String) -> Unit = { _, _ -> },
    onExploreProperties: () -> Unit = {},
    onSearch: () -> Unit = {},
    onNotifications: () -> Unit = {},
    viewModel: SavedViewModel = hiltViewModel()
) {
    val savedProperties by viewModel.savedProperties.collectAsState(initial = emptyList())
    var selectedCategory by remember { mutableStateOf(SavedCategory.ALL) }

    val filteredProperties = remember(savedProperties, selectedCategory) {
        when (selectedCategory) {
            SavedCategory.ALL -> savedProperties
            SavedCategory.HOMES -> savedProperties.filter { it.type.contains("HOUSE", ignoreCase = true) || it.type.contains("VILLA", ignoreCase = true) }
            SavedCategory.APARTMENTS -> savedProperties.filter { it.type.contains("APARTMENT", ignoreCase = true) }
            SavedCategory.LAND -> savedProperties.filter { it.type.contains("LAND", ignoreCase = true) }
        }
    }

    CreativeBackground(
        imageRes = R.drawable.img_43,
        variant = BackgroundVariant.EMERALD,
        overlayAlpha = 0.85f
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 110.dp)
            ) {
                item {
                    SavedHeader(
                        savedCount = savedProperties.size,
                        onSearch = onSearch,
                        onNotifications = onNotifications
                    )
                }

                item {
                    SavedCategoryBar(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )
                }

                if (savedProperties.size >= 2) {
                    item {
                        ComparePromotionCard(
                            p1 = savedProperties[0],
                            p2 = savedProperties[1],
                            onClick = { onCompareClick(savedProperties[0].id, savedProperties[1].id) }
                        )
                    }
                }

                if (savedProperties.isNotEmpty()) {
                    item {
                        SmartSavedInsight(propertyCount = savedProperties.size)
                    }
                }

                if (filteredProperties.isEmpty()) {
                    item {
                        EmptySavedState(onExplore = onExploreProperties)
                    }
                } else {
                    item {
                        SavedSectionHeader(
                            title = if (selectedCategory == SavedCategory.ALL) "Your Collection" else selectedCategory.label,
                            count = filteredProperties.size
                        )
                    }

                    items(items = filteredProperties, key = { it.id }) { property ->
                        SavedPropertyItem(
                            property = property,
                            onClick = { onPropertyClick(property.id) },
                            onRemove = { viewModel.removeSaved(property.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparePromotionCard(p1: Property, p2: Property, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp).clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightEmerald),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = ChampagneGold) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.Balance, contentDescription = null, tint = Color.Black, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Property Compare", color = Color.White, fontWeight = FontWeight.ExtraBold)
                Text(text = "Compare ${p1.title} vs ${p2.title}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun SavedSectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text(
                text = "$count saved ${if (count == 1) "property" else "properties"}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
        Surface(
            modifier = Modifier.size(42.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Sort",
                    modifier = Modifier.size(19.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun SavedHeader(savedCount: Int, onSearch: () -> Unit, onNotifications: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DeepEmerald.copy(alpha = 0.4f),
                        Color.Transparent
                    )
                )
            )
            .padding(start = 20.dp, end = 16.dp, top = 28.dp, bottom = 8.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Saved", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Spacer(modifier = Modifier.width(12.dp))
                        Surface(shape = RoundedCornerShape(50.dp), color = ChampagneGold) {
                            Text(
                                text = savedCount.toString(),
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your private portfolio of elite properties.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                HeaderIconButton(icon = Icons.Default.Search, onClick = onSearch)
                Spacer(modifier = Modifier.width(7.dp))
                HeaderIconButton(icon = Icons.Default.NotificationsNone, onClick = onNotifications)
            }
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun HeaderIconButton(icon: ImageVector, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(44.dp).clickable(onClick = onClick),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private enum class SavedCategory(val label: String, val icon: ImageVector) {
    ALL("All", Icons.Default.Favorite),
    HOMES("Homes", Icons.Default.HomeWork),
    APARTMENTS("Apartments", Icons.Default.Apartment),
    LAND("Land", Icons.Default.Landscape)
}

@Composable
private fun SavedCategoryBar(selectedCategory: SavedCategory, onCategorySelected: (SavedCategory) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SavedCategory.entries.forEach { category ->
            val selected = category == selectedCategory
            Surface(
                modifier = Modifier.clickable { onCategorySelected(category) },
                shape = RoundedCornerShape(50.dp),
                color = if (selected) DeepEmerald else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = if (!selected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) else null
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = category.label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SmartSavedInsight(propertyCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = DeepEmerald) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(21.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Smart Property Watch", fontWeight = FontWeight.ExtraBold)
                Text(
                    text = "Watching $propertyCount properties for price shifts.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = DeepEmerald, modifier = Modifier.size(19.dp))
        }
    }
}

@Composable
private fun SavedPropertyItem(property: Property, onClick: () -> Unit, onRemove: () -> Unit) {
    var removed by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = !removed,
        exit = fadeOut(tween(300))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
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
                isFavorite = true,
                trustScore = property.trustScore,
                onClick = onClick,
                onFavoriteClick = { removed = true; onRemove() },
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { removed = true; onRemove() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "REMOVE FROM SAVED", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun EmptySavedState(onExplore: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp, vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.05f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(56.dp))
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Portfolio Empty", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = Color.White)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Save the properties you are interested in and they will appear here in your private collection.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onExplore,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepEmerald)
        ) {
            Text(text = "EXPLORE PROPERTIES", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
    }
}
