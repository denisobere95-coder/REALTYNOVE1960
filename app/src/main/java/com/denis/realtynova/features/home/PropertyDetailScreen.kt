package com.denis.realtynova.features.home

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import coil.compose.AsyncImage
import com.denis.realtynova.R
import com.denis.realtynova.core.designsystem.components.RealtyNovaButton
import com.denis.realtynova.core.designsystem.components.ButtonVariant
import com.denis.realtynova.core.domain.model.NearbyAmenity
import com.denis.realtynova.core.domain.model.Property
import com.denis.realtynova.core.domain.model.PropertyImage
import com.denis.realtynova.core.domain.model.PropertyImageType
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.RealtyNovaTextStyles
import com.denis.realtynova.core.util.PriceFormatter
import java.util.Locale

@Composable
fun PropertyDetailScreen(
    id: String,
    onBack: () -> Unit,
    onNavigateToBooking: (String) -> Unit = {},
    onNavigateToPayment: (String, Double) -> Unit = { _, _ -> },
    onNavigateToVirtualTour: (String) -> Unit = {},
    viewModel: PropertyDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(id) {
        viewModel.loadProperty(id)
    }

    Scaffold(
        bottomBar = {
            if (uiState.property != null) {
                PropertyActionDock(
                    isFavorite = uiState.isFavorite,
                    onFavoriteClick = { viewModel.toggleFavorite() },
                    onBookingClick = { onNavigateToBooking(id) },
                    onReserveClick = { onNavigateToPayment(id, uiState.property!!.price * 0.01) },
                    onCallClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:+254700000000")
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DeepEmerald)
                }
            } else if (uiState.property != null) {
                PropertyDetailContent(
                    property = uiState.property!!,
                    aiEvaluation = uiState.aiEvaluation,
                    onBack = onBack,
                    onVrClick = { onNavigateToVirtualTour(id) }
                )
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun PropertyDetailContent(
    property: Property,
    aiEvaluation: com.denis.realtynova.core.ai.PropertyEvaluation?,
    onBack: () -> Unit,
    onVrClick: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            PropertyImageHeader(
                property = property,
                onBack = onBack,
                onVrClick = onVrClick
            )
        }
        item {
            PropertyMainInfo(property = property)
        }
        if (aiEvaluation != null) {
            item {
                AiPropertyAnalysis(evaluation = aiEvaluation)
            }
        }
        item {
            ClassifiedGallery(images = property.images)
        }
        item {
            PropertySpecs(property = property)
        }
        item {
            PropertyDescription(property = property)
        }
        item {
            PropertyAmenities(property = property)
        }
        item {
            NeighborhoodIntelligence(property = property)
        }
        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
fun PropertyImageHeader(
    property: Property,
    onBack: () -> Unit,
    onVrClick: () -> Unit
) {
    val context = LocalContext.current
    val shimmerTranslate = rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Box(modifier = Modifier.fillMaxWidth().height(440.dp)) {
        val imageUrl = property.images.firstOrNull()?.url ?: ""
        val imageModel = remember(imageUrl) {
            if (imageUrl.startsWith("res:///drawable/")) {
                val resName = imageUrl.substringAfterLast("/")
                context.resources.getIdentifier(resName, "drawable", context.packageName).let {
                    if (it != 0) it else R.drawable.img_53
                }
            } else if (imageUrl.isEmpty()) {
                R.drawable.img_53
            } else {
                imageUrl
            }
        }

        AsyncImage(
            model = imageModel,
            contentDescription = property.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent, Color.Black.copy(alpha = 0.75f))
            )
        ))

        // Luxury Shimmer Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.04f), Color.Transparent),
                        start = androidx.compose.ui.geometry.Offset(shimmerTranslate.value - 250f, shimmerTranslate.value - 250f),
                        end = androidx.compose.ui.geometry.Offset(shimmerTranslate.value, shimmerTranslate.value)
                    )
                )
        )

        IconButton(
            onClick = onBack,
            modifier = Modifier.statusBarsPadding().padding(16.dp).size(44.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.4f))
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Row(
            modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.clickable { onVrClick() },
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ViewInAr, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("360° TOUR", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (property.isPremium) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ChampagneGold
                ) {
                    Text(
                        text = "PREMIUM",
                        style = RealtyNovaTextStyles.PremiumLabel,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ClassifiedGallery(images: List<PropertyImage>) {
    if (images.size <= 1) return

    val context = LocalContext.current

    Column(modifier = Modifier.padding(top = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PROPERTY GALLERY",
                style = RealtyNovaTextStyles.HeroEyebrow,
                color = DeepEmerald
            )
            Text(
                text = "${images.size} PHOTOS",
                style = RealtyNovaTextStyles.PremiumLabel,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(18.dp))
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(images) { image ->
                val imageUrl = image.url
                val imageModel = remember(imageUrl) {
                    if (imageUrl.startsWith("res:///drawable/")) {
                        val resName = imageUrl.substringAfterLast("/")
                        context.resources.getIdentifier(resName, "drawable", context.packageName).let {
                            if (it != 0) it else R.drawable.img_14
                        }
                    } else {
                        imageUrl
                    }
                }

                Box(
                    modifier = Modifier
                        .size(240.dp, 160.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = image.altText,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    Surface(
                        modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.55f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = image.type.name.uppercase(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AiPropertyAnalysis(evaluation: com.denis.realtynova.core.ai.PropertyEvaluation) {
    Column(modifier = Modifier.padding(24.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFFF3E5F5), // Light Purple for AI
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCE93D8))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF7B1FA2),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Nova AI Evaluation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF7B1FA2)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF7B1FA2)
                    ) {
                        Text(
                            text = "${evaluation.luxuryScore}/100",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = evaluation.analysis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4A148C),
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
fun NeighborhoodIntelligence(property: Property) {
    Column(modifier = Modifier.padding(24.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = null,
                        tint = DeepEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Neighborhood Intelligence",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = DeepEmerald
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = property.neighborhoodInfo ?: "Exclusive insights into the local lifestyle and amenities.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
                
                if (property.nearbyAmenities.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    property.nearbyAmenities.forEachIndexed { index, amenity ->
                        AmenityRow(amenity)
                        if (index < property.nearbyAmenities.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 14.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AmenityRow(amenity: NearbyAmenity) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                val icon = when(amenity.type) {
                    "School" -> Icons.Default.School
                    "Hospital" -> Icons.Default.LocalHospital
                    "Mall" -> Icons.Default.LocalMall
                    "Office" -> Icons.Default.Business
                    else -> Icons.Default.LocationOn
                }
                Icon(imageVector = icon, contentDescription = null, tint = DeepEmerald, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = amenity.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = amenity.type, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            text = "${amenity.distanceKm} km",
            fontWeight = FontWeight.ExtraBold,
            color = DeepEmerald,
            fontSize = 12.sp
        )
    }
}

@Composable
fun PropertyMainInfo(property: Property) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
        Text(
            text = property.location.uppercase(),
            style = RealtyNovaTextStyles.HeroEyebrow,
            color = DeepEmerald
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = property.title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = PriceFormatter.formatPrice(property.price),
                style = RealtyNovaTextStyles.PropertyPriceLarge,
                color = DeepEmerald
            )
            if (property.listingType.equals("Rent", true)) {
                Text(
                    text = " / month",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                )
            }
        }
        
        if (property.yieldPercentage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFE8F5E9),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA5D6A7))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Estimated Yield: ${property.yieldPercentage}%",
                            color = Color(0xFF1B5E20),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Surface(
                    shape = RoundedCornerShape(50),
                    color = ChampagneGold.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ChampagneGold)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Investment Score: 94/100",
                            color = Color(0xFF634D00),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PropertySpecs(property: Property) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SpecItem(icon = Icons.Default.Bed, value = property.bedrooms.toString(), label = "Bedrooms")
            SpecItem(icon = Icons.Default.Bathtub, value = property.bathrooms.toString(), label = "Bathrooms")
            SpecItem(icon = Icons.Default.SquareFoot, value = String.format(LocalConfiguration.current.locales[0], "%,.0f", property.areaSqFt), label = "SqFt")
        }
    }
}

@Composable
private fun SpecItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = DeepEmerald, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun PropertyDescription(property: Property) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(text = "The Property", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = property.description,
            style = RealtyNovaTextStyles.AIMessage,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun PropertyAmenities(property: Property) {
    if (property.amenities.isEmpty()) return
    
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(text = "Amenities", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(16.dp))
        
        property.amenities.chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                rowItems.forEach { amenity ->
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = DeepEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = amenity, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun PropertyActionDock(
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onBookingClick: () -> Unit,
    onReserveClick: () -> Unit,
    onCallClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
        tonalElevation = 8.dp,
        shadowElevation = 24.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp).border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.clickable { onCallClick() }) {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = "Call Agent", tint = DeepEmerald)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = if (isFavorite) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isFavorite) Color(0xFFFFCDD2) else MaterialTheme.colorScheme.outlineVariant)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.clickable { onFavoriteClick() }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFD32F2F) else DeepEmerald
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            
            OutlinedButton(
                onClick = onBookingClick,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DeepEmerald)
            ) {
                Text(text = "BOOK", fontWeight = FontWeight.Bold, color = DeepEmerald)
            }

            Spacer(modifier = Modifier.width(12.dp))

            RealtyNovaButton(
                onClick = onReserveClick,
                modifier = Modifier.weight(1.5f).height(56.dp),
                variant = ButtonVariant.Premium
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "RESERVE", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }
    }
}
