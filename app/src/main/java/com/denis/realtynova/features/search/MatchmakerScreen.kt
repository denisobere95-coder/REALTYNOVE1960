package com.denis.realtynova.features.search

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.denis.realtynova.core.designsystem.components.CreativeBackground
import com.denis.realtynova.core.designsystem.components.BackgroundVariant
import com.denis.realtynova.R
import com.denis.realtynova.core.domain.model.Property
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.features.home.HomeViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchmakerScreen(
    onBack: () -> Unit,
    onPropertyClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val properties = uiState.properties.filter { it.isPremium }.shuffled()
    
    var currentIndex by remember { mutableIntStateOf(0) }
    
    CreativeBackground(
        imageRes = R.drawable.img_23,
        variant = BackgroundVariant.NAVY,
        overlayAlpha = 0.9f
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("AI Matchmaker", fontWeight = FontWeight.Bold, color = Color.White)
                            Text("SWIPE TO DISCOVER", style = MaterialTheme.typography.labelSmall, color = ChampagneGold)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                if (currentIndex < properties.size) {
                    val property = properties[currentIndex]
                    
                    MatchCard(
                        property = property,
                        onSwipeLeft = { currentIndex++ },
                        onSwipeRight = { 
                            viewModel.toggleFavorite(property.id)
                            currentIndex++
                        },
                        onInfoClick = { onPropertyClick(property.id) }
                    )
                    
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 48.dp),
                        horizontalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        MatchActionCircle(
                            icon = Icons.Default.Close,
                            color = Color(0xFFD64545),
                            onClick = { currentIndex++ }
                        )
                        MatchActionCircle(
                            icon = Icons.Default.Favorite,
                            color = DeepEmerald,
                            onClick = { 
                                viewModel.toggleFavorite(property.id)
                                currentIndex++
                            }
                        )
                    }
                } else {
                    MatchmakerEmptyState(onRefresh = { currentIndex = 0 })
                }
            }
        }
    }
}

@Composable
private fun MatchCard(
    property: Property,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onInfoClick: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val rotation = (offsetX / 20).coerceIn(-15f, 15f)
    val alphaValue = (1 - (kotlin.math.abs(offsetX) / 1000)).coerceIn(0.5f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .aspectRatio(0.7f)
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .graphicsLayer { rotationZ = rotation }
            .alpha(alphaValue)
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    offsetX += delta
                },
                onDragStopped = {
                    if (offsetX > 300) onSwipeRight()
                    else if (offsetX < -300) onSwipeLeft()
                    offsetX = 0f
                }
            )
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(32.dp))
    ) {
        AsyncImage(
            model = property.images.firstOrNull()?.url ?: R.drawable.img_14,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)))
        ))
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Surface(shape = RoundedCornerShape(8.dp), color = ChampagneGold) {
                Text(
                    text = property.type.uppercase(),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = property.title, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text(text = property.location, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "KSh %,.0f".format(property.price), color = ChampagneGold, fontWeight = FontWeight.Black, fontSize = 20.sp)
        }
        
        IconButton(
            onClick = onInfoClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            Icon(Icons.Default.Info, null, tint = Color.White)
        }
    }
}

@Composable
private fun MatchActionCircle(icon: ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(64.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
private fun MatchmakerEmptyState(onRefresh: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Favorite, null, tint = Color.LightGray.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("No more matches for now.", color = Color.White.copy(alpha = 0.7f))
        TextButton(onClick = onRefresh) {
            Text("START OVER", color = ChampagneGold, fontWeight = FontWeight.Bold)
        }
    }
}
