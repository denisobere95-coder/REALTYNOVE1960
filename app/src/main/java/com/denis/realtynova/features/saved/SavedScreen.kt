package com.denis.realtynova.features.saved

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.realtynova.core.designsystem.components.PropertyCard
import com.denis.realtynova.core.domain.model.Property
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
    savedProperties: List<Property> = emptyList(),
    onPropertyClick: (String) -> Unit = {},
    onRemoveSaved: (String) -> Unit = {},
    onExploreProperties: () -> Unit = {},
    onSearch: () -> Unit = {},
    onNotifications: () -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf(SavedCategory.ALL) }

    val filteredProperties = remember(savedProperties, selectedCategory) {
        when (selectedCategory) {
            SavedCategory.ALL -> savedProperties
            SavedCategory.HOMES -> savedProperties.filter { it.type.contains("HOUSE", ignoreCase = true) }
            SavedCategory.APARTMENTS -> savedProperties.filter { it.type.contains("APARTMENT", ignoreCase = true) }
            SavedCategory.LAND -> savedProperties.filter { it.type.contains("LAND", ignoreCase = true) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                    SectionHeader(
                        title = if (selectedCategory == SavedCategory.ALL) "Your Collection" else selectedCategory.label,
                        count = filteredProperties.size
                    )
                }

                items(items = filteredProperties, key = { it.id }) { property ->
                    SavedPropertyItem(
                        property = property,
                        onClick = { onPropertyClick(property.id) },
                        onRemove = { onRemoveSaved(property.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Text(
                text = "$count saved ${if (count == 1) "property" else "properties"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Surface(
            modifier = Modifier.size(42.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Sort",
                    modifier = Modifier.size(19.dp),
                    tint = MaterialTheme.colorScheme.primary
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
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.13f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(start = 20.dp, end = 16.dp, top = 28.dp, bottom = 8.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Saved", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.width(9.dp))
                        Surface(shape = RoundedCornerShape(50.dp), color = MaterialTheme.colorScheme.primary) {
                            Text(
                                text = savedCount.toString(),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your private collection of properties.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
        shadowElevation = 3.dp
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
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        SavedCategory.entries.forEach { category ->
            val selected = category == selectedCategory
            Surface(
                modifier = Modifier.clickable { onCategorySelected(category) },
                shape = RoundedCornerShape(50.dp),
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(21.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Smart Property Watch", fontWeight = FontWeight.ExtraBold)
                Text(
                    text = "REALTYNOVA is watching your $propertyCount saved ${if (propertyCount == 1) "property" else "properties"} for important updates.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(19.dp))
        }
    }
}

@Composable
private fun SavedPropertyItem(property: Property, onClick: () -> Unit, onRemove: () -> Unit) {
    var removed by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = !removed,
        enter = fadeIn(tween(350)) + slideInVertically(tween(350)) { it / 4 }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
            Box {
                PropertyCard(
                    imageUrl = property.images.firstOrNull()?.url ?: "",
                    price = formatSavedPrice(property.price),
                    title = property.title,
                    location = property.location,
                    specs = "${property.bedrooms} Beds • ${property.bathrooms} Baths • ${property.areaSqFt} sqft",
                    type = property.type,
                    listingType = property.listingType,
                    isVerified = property.isVerified,
                    isPremium = property.isPremium,
                    onClick = onClick,
                    onFavoriteClick = { removed = true; onRemove() },
                    modifier = Modifier.fillMaxWidth()
                )
                Surface(
                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                    shape = RoundedCornerShape(50.dp),
                    color = Color.Black.copy(alpha = 0.65f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "SAVED", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.8.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { removed = true; onRemove() }) {
                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Remove", modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Remove", fontSize = 12.sp)
                }
            }
            Divider(
                modifier = Modifier.padding(top = 2.dp, bottom = 10.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
            )
        }
    }
}

@Composable
private fun EmptySavedState(onExplore: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp, vertical = 70.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(105.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(modifier = Modifier.size(70.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
        Text(text = "Build Your Collection", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Save homes, apartments and land you love. Your private property collection will appear here.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(onClick = onExplore, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
            Icon(imageVector = Icons.Default.AddHome, contentDescription = null, modifier = Modifier.size(19.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "EXPLORE PROPERTIES", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, letterSpacing = 1.sp)
        }
    }
}

private fun formatSavedPrice(price: Double): String = "KSh ${String.format(java.util.Locale.getDefault(), "%,.0f", price)}"
