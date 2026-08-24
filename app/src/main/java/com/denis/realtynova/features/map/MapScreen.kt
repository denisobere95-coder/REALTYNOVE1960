package com.denis.realtynova.features.map

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.denis.realtynova.core.designsystem.components.PropertyCard
import com.denis.realtynova.core.domain.model.Property
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.designsystem.theme.RealtyNovaTextStyles
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import java.util.Locale

private val NairobiCenter = LatLng(-1.286389, 36.817223)
private val SchoolLocation = LatLng(-1.3093, 36.8125) // Strathmore University example

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToList: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilters by remember { mutableStateOf(false) }
    var mapReady by remember { mutableStateOf(false) }
    var isSatellite by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    val initialLocation = remember(uiState.properties) {
        uiState.properties.firstOrNull()?.let { LatLng(it.latitude, it.longitude) } ?: NairobiCenter
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 11f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = false,
                mapType = if (isSatellite) MapType.SATELLITE else MapType.NORMAL
            ),
            uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false, compassEnabled = false, mapToolbarEnabled = false),
            onMapLoaded = { mapReady = true },
            onMapClick = { latLng ->
                viewModel.onPropertySelected(null)
                scope.launch {
                    cameraPositionState.animate(
                        com.google.android.gms.maps.CameraUpdateFactory.newLatLng(latLng),
                        1000
                    )
                }
            }
        ) {
            // School Marker
            Marker(
                state = rememberMarkerState(position = SchoolLocation),
                title = "Strathmore University",
                snippet = "Academic Hotspot",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )

            uiState.properties.forEach { property ->
                val markerState = remember(property.id) {
                    MarkerState(position = LatLng(property.latitude, property.longitude))
                }
                Marker(
                    state = markerState,
                    title = property.title,
                    snippet = "KSh %,.0f".format(property.price),
                    onClick = {
                        viewModel.onPropertySelected(property)
                        false
                    }
                )
            }
        }

        // Luxury UI Overlays
        MapGradientOverlay()

        MapTopBar(
            onBack = onNavigateBack,
            onSearch = onNavigateToSearch,
            onFilter = { showFilters = true }
        )

        MapCategoryRow(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 120.dp)
        )

        MapControls(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp, bottom = 100.dp),
            isSatellite = isSatellite,
            onToggleSatellite = { isSatellite = !isSatellite },
            onZoomIn = {
                cameraPositionState.move(com.google.android.gms.maps.CameraUpdateFactory.zoomIn())
            },
            onZoomOut = {
                cameraPositionState.move(com.google.android.gms.maps.CameraUpdateFactory.zoomOut())
            },
            onMyLocation = {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(SchoolLocation, 16f)
            }
        )

        AnimatedVisibility(
            visible = uiState.selectedProperty != null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp),
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            uiState.selectedProperty?.let { property ->
                SelectedPropertySheet(
                    property = property,
                    onClose = { viewModel.onPropertySelected(null) },
                    onOpen = { onNavigateToDetail(property.id) },
                    onDirectionsClick = {
                        val gmmIntentUri = Uri.parse("google.navigation:q=${property.latitude},${property.longitude}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                    },
                    onCopyCoords = {
                        val coords = "${property.latitude}, ${property.longitude}"
                        clipboardManager.setText(AnnotatedString(coords))
                        Toast.makeText(context, "Coordinates copied: $coords", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        if (!mapReady) MapLoadingOverlay()
    }

    if (showFilters) {
        ModalBottomSheet(
            onDismissRequest = { showFilters = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            MapFilterSheet(onDismiss = { showFilters = false })
        }
    }
}

@Composable
private fun MapGradientOverlay() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                )
            )
    )
}

@Composable
private fun MapTopBar(onBack: () -> Unit, onSearch: () -> Unit, onFilter: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlassIconButton(icon = Icons.AutoMirrored.Filled.ArrowBack, onClick = onBack)
        Spacer(modifier = Modifier.width(12.dp))
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            color = Color.Black.copy(alpha = 0.45f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onSearch)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Search area...", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        GlassIconButton(icon = Icons.Default.Tune, onClick = onFilter)
    }
}

@Composable
private fun MapCategoryRow(modifier: Modifier = Modifier) {
    val categories = listOf("All", "Buy", "Rent", "Land", "Gated Communities", "Luxury")
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        categories.forEachIndexed { index, category ->
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = if (index == 0) DeepEmerald else Color.Black.copy(alpha = 0.55f),
                border = if (index != 0) androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)) else null,
                modifier = Modifier.clickable { }
            ) {
                Text(
                    text = category,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun SelectedPropertySheet(
    property: Property,
    onClose: () -> Unit,
    onOpen: () -> Unit,
    onDirectionsClick: () -> Unit,
    onCopyCoords: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                PropertyCard(
                    imageUrl = property.images.firstOrNull()?.url ?: "",
                    price = if (property.currency == "USD") "$%,.0f".format(property.price) else "KSh ${String.format(Locale.getDefault(), "%,.0f", property.price)}",
                    title = property.title,
                    location = property.location,
                    specs = "${property.bedrooms} Beds • ${property.bathrooms} Baths • ${property.areaSqFt} sqft",
                    type = property.type,
                    listingType = property.listingType,
                    isVerified = property.isVerified,
                    isPremium = property.isPremium,
                    onClick = onOpen,
                    onFavoriteClick = {},
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                    IconButton(
                        onClick = onCopyCoords,
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy Coordinates", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onOpen,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEmerald)
                ) {
                    Text("VIEW DETAILS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onDirectionsClick,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Icon(imageVector = Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("DIRECTIONS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun GlassIconButton(icon: ImageVector, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(48.dp).clickable(onClick = onClick),
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun MapControls(
    modifier: Modifier,
    isSatellite: Boolean,
    onToggleSatellite: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onMyLocation: () -> Unit
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
            modifier = Modifier.size(48.dp).clickable(onClick = onToggleSatellite),
            shape = CircleShape,
            color = if (isSatellite) ChampagneGold else Color.Black.copy(alpha = 0.5f),
            border = if (!isSatellite) androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)) else null
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isSatellite) Icons.Default.Map else Icons.Default.Satellite,
                    contentDescription = "Toggle Satellite",
                    tint = if (isSatellite) Color.Black else Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        
        GlassIconButton(icon = Icons.Default.Add, onClick = onZoomIn)
        GlassIconButton(icon = Icons.Default.Remove, onClick = onZoomOut)
        
        Surface(
            modifier = Modifier.size(48.dp).clickable(onClick = onMyLocation),
            shape = CircleShape,
            color = DeepEmerald,
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.School, contentDescription = "Jump to School", tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
private fun MapLoadingOverlay() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.Black.copy(alpha = 0.8f)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp), strokeWidth = 3.dp, color = ChampagneGold)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Preparing Map...", color = Color.White, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun MapFilterSheet(onDismiss: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
        Text(text = "Map Filters", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(24.dp))
        // Placeholder for filter options
        Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Text("APPLY FILTERS", fontWeight = FontWeight.Bold)
        }
    }
}
