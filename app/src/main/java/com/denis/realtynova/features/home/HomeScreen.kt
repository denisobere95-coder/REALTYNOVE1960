package com.denis.realtynova.features.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import coil.compose.AsyncImage
import com.denis.realtynova.R
import com.denis.realtynova.core.designsystem.components.*
import com.denis.realtynova.core.designsystem.theme.*
import com.denis.realtynova.core.domain.model.Property
import com.denis.realtynova.core.domain.model.UserRole
import com.denis.realtynova.core.util.PriceFormatter
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

data class HomeCategory(val title: String, val iconRes: Int, val subtitle: String)

@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToAiAssistant: () -> Unit = {},
    onNavigateToSearch: (String?) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToMessages: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAdminDashboard: () -> Unit = {},
    onNavigateToAgentDashboard: () -> Unit = {},
    onNavigateToCountyExplorer: () -> Unit = {},
    onNavigateToMarketInsights: () -> Unit = {},
    onNavigateToMatchmaker: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val activity = context as? Activity
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (userRole == UserRole.ADMIN) {
                    SmallFloatingActionButton(
                        onClick = onNavigateToAdminDashboard,
                        containerColor = Color.Black,
                        contentColor = ChampagneGold
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, "Admin")
                    }
                }
                
                if (userRole == UserRole.AGENT || userRole == UserRole.ADMIN) {
                    SmallFloatingActionButton(
                        onClick = onNavigateToAgentDashboard,
                        containerColor = DeepEmerald,
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.Dashboard, "Agent")
                    }
                }

                NovaAiFab(onClick = onNavigateToAiAssistant)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            HomeScreenContent(
                uiState = uiState,
                onNavigateToDetail = onNavigateToDetail,
                onNavigateToAiAssistant = { 
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onNavigateToAiAssistant() 
                },
                onNavigateToSearch = onNavigateToSearch,
                onNavigateToNotifications = onNavigateToNotifications,
                onNavigateToMessages = onNavigateToMessages,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToCountyExplorer = onNavigateToCountyExplorer,
                onNavigateToMarketInsights = onNavigateToMarketInsights,
                onNavigateToMatchmaker = onNavigateToMatchmaker,
                onToggleFavorite = { id ->
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.toggleFavorite(id)
                },
                onRefresh = viewModel::refresh,
                onExitApp = { activity?.finish() }
            )
        }
    }
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToAiAssistant: () -> Unit,
    onNavigateToSearch: (String?) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCountyExplorer: () -> Unit,
    onNavigateToMarketInsights: () -> Unit,
    onNavigateToMatchmaker: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onRefresh: () -> Unit,
    onExitApp: () -> Unit
) {
    val scrollState = rememberLazyListState()
    var isRefreshing by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            confirmButton = {
                TextButton(onClick = { onExitApp() }) {
                    Text("EXIT", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("CANCEL")
                }
            },
            title = { Text("Exit REALTYNOVA?") },
            text = { Text("Are you sure you want to close the app?") }
        )
    }

    BackHandler {
        showExitDialog = true
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            onRefresh()
            delay(500.milliseconds)
            isRefreshing = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.isLoading) {
            HomeLoadingState()
        } else {
            LazyColumn(
                state = scrollState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(200.dp))
                }

                item {
                    PropertyOfTheDay(
                        property = uiState.featuredProperty,
                        onClick = onNavigateToDetail
                    )
                }

                item {
                    RealtyNovaSearchBar(
                        onSearchClick = { onNavigateToSearch(null) },
                        onAiClick = onNavigateToAiAssistant,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }

                item {
                    LuxuryCategoriesRow(onItemClick = onNavigateToSearch)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    val latestTrend = uiState.marketTrends.lastOrNull() ?: 0f
                    val trendText = if (latestTrend >= 0) "+${latestTrend}% market growth" else "${latestTrend}% market shift"
                    MarketPulseCard(
                        trends = trendText,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp).clickable { onNavigateToMarketInsights() }
                    )
                }

                item {
                    PremiumSectionHeader(
                        title = "Premium Collection",
                        subtitle = "Hand-picked luxury residences",
                        onSeeAll = { onNavigateToSearch(null) }
                    )
                    PremiumProperties(
                        properties = uiState.properties.filter { it.isPremium },
                        favoriteIds = uiState.favoriteIds,
                        onToggleFavorite = onToggleFavorite,
                        onClick = onNavigateToDetail
                    )
                }

                item {
                    PremiumSectionHeader(
                        title = "Curated Collections",
                        subtitle = "Specially selected for you",
                        onSeeAll = { onNavigateToSearch(null) }
                    )
                    CuratedCollections()
                }

                item {
                    PremiumSectionHeader(
                        title = "Matchmaker",
                        subtitle = "Find your perfect home",
                        onSeeAll = onNavigateToMatchmaker
                    )
                    MatchmakerHero(onClick = onNavigateToMatchmaker)
                }

                item {
                    PremiumSectionHeader(
                        title = "Exclusive Discovery",
                        subtitle = "Explore the best of Kenyan real estate",
                        onSeeAll = { onNavigateToSearch(null) }
                    )
                }

                itemsIndexed(
                    items = uiState.properties,
                    key = { _, property -> property.id }
                ) { index, property ->
                    AnimatedPropertyItem(
                        property = property,
                        isFavorite = uiState.favoriteIds.contains(property.id),
                        onToggleFavorite = { onToggleFavorite(property.id) },
                        index = index,
                        onClick = { onNavigateToDetail(property.id) }
                    )
                }
            }

            DynamicPremiumHeader(
                scrollState = scrollState,
                onSearch = { onNavigateToSearch(null) },
                onNotifications = onNavigateToNotifications,
                onMessages = onNavigateToMessages,
                onProfile = onNavigateToProfile
            )
        }
    }
}

@Composable
private fun MatchmakerHero(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("AI Property Matchmaker", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Swipe to find your next home", style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Default.Favorite, null, tint = DeepEmerald)
        }
    }
}

@Composable
fun LuxuryCategoriesRow(onItemClick: (String) -> Unit = {}) {
    val categories = listOf(
        HomeCategory("Apartment", R.drawable.img_1, "Urban Living"),
        HomeCategory("House", R.drawable.img_43, "Lush Mansionettes"),
        HomeCategory("Land", R.drawable.img_7, "Prime Plots"),
        HomeCategory("Commercial", R.drawable.img_33, "Business Hubs")
    )
    
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) { category ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(90.dp).clickable { onItemClick(category.title) }
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = category.iconRes,
                        contentDescription = category.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f)))
                    ))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun PropertyOfTheDay(property: Property?, onClick: (String) -> Unit) {
    if (property == null) return
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(32.dp))
            .clickable { onClick(property.id) }
    ) {
        AsyncImage(
            model = R.drawable.img_53,
            contentDescription = "Property of the Day",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = ChampagneGold
            ) {
                Text(
                    text = "FEATURED OF THE DAY",
                    style = RealtyNovaTextStyles.PremiumLabel,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = property.title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = ChampagneGold, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = property.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = PriceFormatter.formatPrice(property.price),
                style = RealtyNovaTextStyles.PropertyPrice.copy(fontSize = 20.sp),
                color = ChampagneGold
            )
        }
    }
}

@Composable
fun CuratedCollections() {
    val collections = listOf(
        "Coastal Living" to R.drawable.img_33,
        "Urban Penthouses" to R.drawable.img_1,
        "Safari Escapes" to R.drawable.img_51
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(collections) { item ->
            Box(
                modifier = Modifier
                    .width(240.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                AsyncImage(
                    model = item.second,
                    contentDescription = item.first,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                            )
                        )
                )
                Text(
                    text = item.first,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DynamicPremiumHeader(
    scrollState: LazyListState,
    onSearch: () -> Unit,
    onNotifications: () -> Unit,
    onMessages: () -> Unit,
    onProfile: () -> Unit
) {
    val scrollOffset = remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset.toFloat() } }
    val isScrolled by remember { derivedStateOf { scrollState.firstVisibleItemIndex > 0 || scrollOffset.value > 50 } }
    
    val headerHeight by animateDpAsState(
        targetValue = if (isScrolled) 100.dp else 220.dp,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "headerHeight"
    )
    
    val imageAlpha by animateFloatAsState(
        targetValue = if (isScrolled) 0f else 0.8f,
        label = "imageAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight)
    ) {
        AsyncImage(
            model = R.drawable.img_10,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = imageAlpha
                    translationY = -scrollOffset.value * 0.3f
                },
            contentScale = ContentScale.Crop
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = if (isScrolled) {
                            listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.background)
                        } else {
                            listOf(DeepEmerald.copy(alpha = 0.7f), Color.Transparent)
                        }
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "DISCOVER",
                        style = RealtyNovaTextStyles.HeroEyebrow,
                        color = if (isScrolled) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "REALTYNOVA",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isScrolled) MaterialTheme.colorScheme.onBackground else Color.White,
                        letterSpacing = 2.sp
                    )
                }
                Row {
                    HeaderSmallButton(icon = Icons.Default.ChatBubbleOutline, onClick = onMessages, isScrolled = isScrolled)
                    Spacer(modifier = Modifier.width(8.dp))
                    HeaderSmallButton(icon = Icons.Default.NotificationsNone, onClick = onNotifications, isScrolled = isScrolled)
                    Spacer(modifier = Modifier.width(8.dp))
                    HeaderSmallButton(icon = Icons.Default.Person, onClick = onProfile, isScrolled = isScrolled)
                }
            }
            
            if (isScrolled) {
                Spacer(modifier = Modifier.height(8.dp))
                CompactHeaderSearch(onClick = onSearch)
            }
        }
        
        if (isScrolled) {
            HorizontalDivider(
                modifier = Modifier.align(Alignment.BottomCenter),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun CompactHeaderSearch(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = DeepEmerald,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Search properties...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PremiumSectionHeader(title: String, subtitle: String, onSeeAll: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "EXPLORE ALL",
            style = RealtyNovaTextStyles.PremiumLabel,
            color = DeepEmerald,
            modifier = Modifier
                .clickable { onSeeAll() }
                .padding(bottom = 2.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PremiumProperties(
    properties: List<Property>,
    favoriteIds: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = properties, key = { it.id }) { property ->
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
                isFavorite = favoriteIds.contains(property.id),
                trustScore = property.trustScore,
                onClick = { onClick(property.id) },
                onFavoriteClick = { onToggleFavorite(property.id) },
                modifier = Modifier.width(320.dp)
            )
        }
    }
}

@Composable
fun AnimatedPropertyItem(
    property: Property,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    index: Int,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay((index * 50L).coerceAtMost(300L))
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 10 }
    ) {
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
            isFavorite = isFavorite,
            trustScore = property.trustScore,
            onClick = onClick,
            onFavoriteClick = onToggleFavorite,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
    }
}

@Composable
fun HeaderSmallButton(icon: ImageVector, onClick: () -> Unit, isScrolled: Boolean) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (isScrolled) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.2f))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isScrolled) MaterialTheme.colorScheme.onSurface else Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun HomeLoadingState() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 220.dp, bottom = 32.dp)
    ) {
        items(5) {
            PropertyCardShimmer()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    REALTYNOVATheme {
        HomeScreenContent(
            uiState = HomeUiState(
                properties = listOf(
                    Property(
                        id = "1",
                        title = "Luxury Villa in Karen",
                        description = "Elite villa with modern amenities",
                        price = 85000000.0,
                        location = "Karen, Nairobi",
                        address = "Karen Road",
                        bedrooms = 5,
                        bathrooms = 5.0,
                        areaSqFt = 6500.0,
                        images = emptyList(),
                        type = "Villa",
                        listingType = "Buy",
                        isPremium = true,
                        isVerified = true
                    )
                )
            ),
            onNavigateToDetail = {},
            onNavigateToAiAssistant = {},
            onNavigateToSearch = {},
            onNavigateToNotifications = {},
            onNavigateToMessages = {},
            onNavigateToProfile = {},
            onNavigateToCountyExplorer = {},
            onNavigateToMarketInsights = {},
            onNavigateToMatchmaker = {},
            onToggleFavorite = {},
            onRefresh = {},
            onExitApp = {}
        )
    }
}
