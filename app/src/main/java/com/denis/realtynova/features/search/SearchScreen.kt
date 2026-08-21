
package com.denis.realtynova.features.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Villa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.denis.realtynova.core.util.PriceFormatter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.denis.realtynova.core.designsystem.components.PropertyCard
import com.denis.realtynova.core.domain.model.Property

private val NovaGold = Color(0xFFD6B36A)
private val NovaGoldLight = Color(0xFFF2D9A2)
private val NovaDark = Color(0xFF10151C)
private val NovaDarkSoft = Color(0xFF191F27)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onPropertyClick: (String) -> Unit = {},
    onOpenMap: () -> Unit = {},
    onOpenAi: () -> Unit = {},
    onFiltersClick: () -> Unit = {},
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF7F7F5),
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
                    value = uiState.query,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    onAiClick = onOpenAi
                )
            }

            if (uiState.query.isEmpty()) {
                item { AiSearchHero(onClick = onOpenAi) }

                item {
                    CategorySelector(
                        categories = listOf("All", "Apartments", "Houses", "Land", "Commercial", "Luxury"),
                        selected = uiState.selectedCategory,
                        onSelected = { viewModel.onCategorySelected(it) }
                    )
                }

                item { QuickFilters(onFiltersClick = onFiltersClick) }

                item { DiscoveryHeader() }

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


/*
 * ================================================================
 * HEADER
 * ================================================================
 */

@Composable
private fun SearchHeader() {

    Row(
        modifier = Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text = "Discover",

                fontSize = 13.sp,

                fontWeight =
                    FontWeight.Medium,

                color =
                    Color(0xFF777777)
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            Text(
                text = "Your next address.",

                fontSize = 27.sp,

                fontWeight =
                    FontWeight.ExtraBold,

                color =
                    Color(0xFF15171A)
            )
        }

        Surface(
            shape = CircleShape,

            color = Color.White,

            shadowElevation = 4.dp
        ) {

            IconButton(
                onClick = {}
            ) {

                Icon(
                    imageVector =
                        Icons.Default.FavoriteBorder,

                    contentDescription =
                        "Saved properties",

                    tint =
                        Color(0xFF1A1A1A)
                )
            }
        }
    }
}


/*
 * ================================================================
 * SMART SEARCH
 * ================================================================
 */

@Composable
private fun SmartSearchBox(
    value: String,
    onValueChange: (String) -> Unit,
    onAiClick: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape =
                        RoundedCornerShape(24.dp)
                ),

        shape =
            RoundedCornerShape(24.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            )
    ) {

        Column(
            modifier =
                Modifier.padding(10.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Search,

                    contentDescription =
                        null,

                    tint =
                        Color(0xFF777777),

                    modifier =
                        Modifier
                            .padding(
                                start = 8.dp
                            )
                            .size(21.dp)
                )

                TextField(
                    value = value,

                    onValueChange =
                        onValueChange,

                    modifier =
                        Modifier.weight(1f),

                    singleLine = true,

                    placeholder = {
                        Text(
                            "Search location, property or lifestyle..."
                        )
                    },

                    colors =
                        TextFieldDefaults.colors(
                            focusedContainerColor =
                                Color.Transparent,

                            unfocusedContainerColor =
                                Color.Transparent,

                            focusedIndicatorColor =
                                Color.Transparent,

                            unfocusedIndicatorColor =
                                Color.Transparent
                        )
                )

                /*
                 * Voice search
                 */

                IconButton(
                    onClick = {}
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.KeyboardVoice,

                        contentDescription =
                            "Voice search",

                        tint =
                            Color(0xFF777777)
                    )
                }
            }

            /*
             * AI Search button
             */

            Surface(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable(
                            onClick = onAiClick
                        ),

                shape =
                    RoundedCornerShape(17.dp),

                color =
                    NovaDark
            ) {

                Row(
                    modifier =
                        Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 12.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    AiPulseIcon()

                    Spacer(
                        modifier =
                            Modifier.width(10.dp)
                    )

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                "SEARCH WITH AI",

                            color =
                                NovaGoldLight,

                            fontSize = 9.sp,

                            fontWeight =
                                FontWeight.ExtraBold,

                            letterSpacing =
                                1.2.sp
                        )

                        Spacer(
                            modifier =
                                Modifier.height(2.dp)
                        )

                        Text(
                            text =
                                "“Find me a quiet 3-bed near Nairobi under KSh 15M”",

                            color =
                                Color.White.copy(
                                    alpha = 0.65f
                                ),

                            fontSize = 10.sp,

                            maxLines = 1,

                            overflow =
                                TextOverflow.Ellipsis
                        )
                    }

                    Icon(
                        imageVector =
                            Icons.Default.AutoAwesome,

                        contentDescription =
                            null,

                        tint =
                            NovaGoldLight
                    )
                }
            }
        }
    }
}


/*
 * ================================================================
 * AI SEARCH HERO
 * ================================================================
 */

@Composable
private fun AiSearchHero(
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                ),

        shape =
            RoundedCornerShape(27.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    NovaDark
            )
    ) {

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors =
                                listOf(
                                    NovaDark,
                                    NovaDarkSoft,
                                    Color(0xFF252018)
                                )
                        )
                    )
                    .padding(20.dp)
        ) {

            Column {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    AiPulseIcon(
                        large = true
                    )

                    Spacer(
                        modifier =
                            Modifier.width(10.dp)
                    )

                    Column {

                        Text(
                            text =
                                "REAL TYNOVA INTELLIGENCE",

                            color =
                                NovaGoldLight,

                            fontSize = 9.sp,

                            fontWeight =
                                FontWeight.ExtraBold,

                            letterSpacing =
                                1.3.sp
                        )

                        Text(
                            text =
                                "Search beyond keywords.",

                            color =
                                Color.White,

                            fontSize = 18.sp,

                            fontWeight =
                                FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                Text(
                    text =
                        "Describe the lifestyle, location, budget " +
                                "and property you want. REALTYNOVA AI " +
                                "will turn your idea into matching homes.",

                    color =
                        Color.White.copy(
                            alpha = 0.65f
                        ),

                    fontSize = 12.sp,

                    lineHeight = 18.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                Surface(
                    shape =
                        RoundedCornerShape(14.dp),

                    color =
                        Color.White.copy(
                            alpha = 0.08f
                        )
                ) {

                    Row(
                        modifier =
                            Modifier.padding(
                                horizontal = 13.dp,
                                vertical = 10.dp
                            ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text =
                                "Try: “Modern family home with garden”",

                            color =
                                Color.White.copy(
                                    alpha = 0.75f
                                ),

                            fontSize = 10.sp,

                            modifier =
                                Modifier.weight(1f)
                        )

                        Icon(
                            imageVector =
                                Icons.Default.AutoAwesome,

                            contentDescription =
                                null,

                            tint =
                                NovaGoldLight,

                            modifier =
                                Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}


/*
 * ================================================================
 * CATEGORY SELECTOR
 * ================================================================
 */

@Composable
private fun CategorySelector(
    categories: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {

    Column {

        Text(
            text = "Explore",

            fontSize = 18.sp,

            fontWeight =
                FontWeight.ExtraBold
        )

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        Row(
            modifier =
                Modifier.horizontalScroll(
                    rememberScrollState()
                ),

            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            categories.forEach { category ->

                val isSelected =
                    selected == category

                Surface(
                    modifier =
                        Modifier.clickable {
                            onSelected(
                                category
                            )
                        },

                    shape =
                        RoundedCornerShape(
                            50.dp
                        ),

                    color =
                        if (isSelected)
                            NovaDark
                        else
                            Color.White,

                    border =
                        if (!isSelected)
                            androidx.compose.foundation
                                .BorderStroke(
                                    1.dp,
                                    Color(0xFFE6E6E6)
                                )
                        else
                            null
                ) {

                    Text(
                        text = category,

                        color =
                            if (isSelected)
                                Color.White
                            else
                                Color(0xFF555555),

                        fontSize = 11.sp,

                        fontWeight =
                            FontWeight.Bold,

                        modifier =
                            Modifier.padding(
                                horizontal = 15.dp,
                                vertical = 10.dp
                            )
                    )
                }
            }
        }
    }
}


/*
 * ================================================================
 * QUICK FILTERS
 * ================================================================
 */

@Composable
private fun QuickFilters(
    onFiltersClick: () -> Unit
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {

        FilterChip(
            icon =
                Icons.Default.LocationOn,

            text = "Location",
            modifier = Modifier.weight(1f)
        )

        FilterChip(
            icon =
                Icons.Default.Tune,

            text = "Budget",
            modifier = Modifier.weight(1f)
        )

        Surface(
            modifier =
                Modifier
                    .weight(1f)
                    .clickable(
                        onClick =
                            onFiltersClick
                    ),

            shape =
                RoundedCornerShape(14.dp),

            color =
                Color.White,

            border =
                androidx.compose.foundation
                    .BorderStroke(
                        1.dp,
                        Color(0xFFE4E4E4)
                    )
        ) {

            Row(
                modifier =
                    Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 11.dp
                    ),

                horizontalArrangement =
                    Arrangement.Center,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.FilterList,

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(15.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(5.dp)
                )

                Text(
                    text = "All Filters",

                    fontSize = 10.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}


/*
 * ================================================================
 * FILTER CHIP
 * ================================================================
 */

@Composable
private fun FilterChip(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier,

        shape =
            RoundedCornerShape(14.dp),

        color =
            Color.White,

        border =
            androidx.compose.foundation
                .BorderStroke(
                    1.dp,
                    Color(0xFFE4E4E4)
                )
    ) {

        Row(
            modifier =
                Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 11.dp
                ),

            horizontalArrangement =
                Arrangement.Center,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,

                contentDescription =
                    null,

                modifier =
                    Modifier.size(15.dp),

                tint =
                    Color(0xFF555555)
            )

            Spacer(
                modifier =
                    Modifier.width(5.dp)
            )

            Text(
                text = text,

                fontSize = 10.sp,

                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}


/*
 * ================================================================
 * RECENT SEARCHES
 * ================================================================
 */

@Composable
private fun RecentSearchHeader() {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text =
                "Recent searches",

            fontSize = 17.sp,

            fontWeight =
                FontWeight.ExtraBold,

            modifier =
                Modifier.weight(1f)
        )

        Text(
            text =
                "Clear",

            color =
                Color(0xFF777777),

            fontSize = 10.sp,

            fontWeight =
                FontWeight.Bold
        )
    }
}


@Composable
private fun RecentSearchRow(
    search: String,
    onClick: () -> Unit
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(15.dp)
                )
                .background(
                    Color.White
                )
                .clickable(
                    onClick = onClick
                )
                .padding(13.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Surface(
            shape =
                CircleShape,

            color =
                Color(0xFFF3F3F3)
        ) {

            Icon(
                imageVector =
                    Icons.Default.History,

                contentDescription =
                    null,

                modifier =
                    Modifier
                        .padding(8.dp)
                        .size(16.dp),

                tint =
                    Color(0xFF777777)
            )
        }

        Spacer(
            modifier =
                Modifier.width(11.dp)
        )

        Text(
            text = search,

            fontSize = 12.sp,

            color =
                Color(0xFF444444),

            modifier =
                Modifier.weight(1f),

            maxLines = 1,

            overflow =
                TextOverflow.Ellipsis
        )

        Icon(
            imageVector =
                Icons.Default.Close,

            contentDescription =
                "Remove",

            tint =
                Color(0xFFAAAAAA),

            modifier =
                Modifier.size(16.dp)
        )
    }
}


/*
 * ================================================================
 * DISCOVERY
 * ================================================================
 */

@Composable
private fun DiscoveryHeader() {

    Column {

        Text(
            text =
                "Curated for you",

            fontSize = 20.sp,

            fontWeight =
                FontWeight.ExtraBold
        )

        Spacer(
            modifier =
                Modifier.height(3.dp)
        )

        Text(
            text =
                "Properties worth discovering",

            fontSize = 11.sp,

            color =
                Color(0xFF777777)
        )
    }
}


/*
 * ================================================================
 * FEATURED PROPERTY
 * ================================================================
 */

@Composable
private fun FeaturedPropertyCard(
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClick
                ),

        shape =
            RoundedCornerShape(27.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )
    ) {

        Column {

            Box {

                AsyncImage(
                    model =
                        "https://images.unsplash.com/photo-1600607687920-4e2a09cf159d",

                    contentDescription =
                        "Featured property",

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 27.dp,
                                    topEnd = 27.dp
                                )
                            ),

                    contentScale =
                        ContentScale.Crop
                )

                Surface(
                    modifier =
                        Modifier
                            .align(
                                Alignment.TopStart
                            )
                            .padding(12.dp),

                    shape =
                        RoundedCornerShape(
                            50.dp
                        ),

                    color =
                        Color.Black.copy(
                            alpha = 0.65f
                        )
                ) {

                    Row(
                        modifier =
                            Modifier.padding(
                                horizontal = 9.dp,
                                vertical = 6.dp
                            ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.AutoAwesome,

                            contentDescription =
                                null,

                            tint =
                                NovaGoldLight,

                            modifier =
                                Modifier.size(13.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(4.dp)
                        )

                        Text(
                            text =
                                "AI PICK",

                            color =
                                Color.White,

                            fontSize = 8.sp,

                            fontWeight =
                                FontWeight.ExtraBold
                        )
                    }
                }

                Surface(
                    modifier =
                        Modifier
                            .align(
                                Alignment.TopEnd
                            )
                            .padding(12.dp),

                    shape =
                        CircleShape,

                    color =
                        Color.White.copy(
                            alpha = 0.92f
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.FavoriteBorder,

                        contentDescription =
                            "Save",

                        modifier =
                            Modifier
                                .padding(9.dp)
                                .size(18.dp)
                    )
                }
            }

            Column(
                modifier =
                    Modifier.padding(15.dp)
            ) {

                Text(
                    text =
                        "Contemporary Villa",

                    fontSize = 18.sp,

                    fontWeight =
                        FontWeight.ExtraBold
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.LocationOn,

                        contentDescription =
                            null,

                        tint =
                            Color(0xFF888888),

                        modifier =
                            Modifier.size(14.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(3.dp)
                    )

                    Text(
                        text =
                            "Karen, Nairobi",

                        fontSize = 10.sp,

                        color =
                            Color(0xFF777777)
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text =
                        "KSh 38,500,000",

                    color =
                        Color(0xFF9A762F),

                    fontSize = 18.sp,

                    fontWeight =
                        FontWeight.ExtraBold
                )

                Spacer(
                    modifier =
                        Modifier.height(11.dp)
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            16.dp
                        )
                ) {

                    PropertyMiniSpec(
                        "4",
                        "Beds"
                    )

                    PropertyMiniSpec(
                        "4",
                        "Baths"
                    )

                    PropertyMiniSpec(
                        "4,200",
                        "sqft"
                    )

                    Spacer(
                        modifier =
                            Modifier.weight(1f)
                    )

                    Text(
                        text =
                            "VIEW →",

                        color =
                            NovaGold,

                        fontSize = 10.sp,

                        fontWeight =
                            FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}


@Composable
private fun PropertyMiniSpec(
    value: String,
    label: String
) {

    Column {

        Text(
            text = value,

            fontSize = 11.sp,

            fontWeight =
                FontWeight.ExtraBold
        )

        Text(
            text = label,

            fontSize = 9.sp,

            color =
                Color(0xFF888888)
        )
    }
}


/*
 * ================================================================
 * AI PULSE ICON
 * ================================================================
 */

@Composable
private fun AiPulseIcon(
    large: Boolean = false
) {

    val transition =
        rememberInfiniteTransition(
            label = "aiPulse"
        )

    val scale by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,

        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        1300,
                        easing =
                            FastOutSlowInEasing
                    ),

                repeatMode =
                    RepeatMode.Reverse
            ),

        label = "aiScale"
    )

    Box(
        modifier =
            Modifier
                .size(
                    if (large)
                        42.dp
                    else
                        30.dp
                )
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .background(
                    NovaGold.copy(
                        alpha = 0.13f
                    ),
                    CircleShape
                )
                .border(
                    1.dp,
                    NovaGold.copy(
                        alpha = 0.45f
                    ),
                    CircleShape
                ),

        contentAlignment =
            Alignment.Center
    ) {

        Icon(
            imageVector =
                Icons.Default.AutoAwesome,

            contentDescription =
                null,

            tint =
                NovaGoldLight,

            modifier =
                Modifier.size(
                    if (large)
                        19.dp
                    else
                        14.dp
                )
        )
    }
}


/*
 * ================================================================
 * BOTTOM DOCK
 * ================================================================
 */

@Composable
private fun SearchBottomDock(
    onOpenMap: () -> Unit,
    onOpenAi: () -> Unit
) {

    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),

        color =
            Color.White,

        shadowElevation =
            14.dp
    ) {

        Row(
            modifier =
                Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 10.dp
                ),

            horizontalArrangement =
                Arrangement.spacedBy(9.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            OutlinedButton(
                onClick =
                    onOpenMap,

                modifier =
                    Modifier.weight(1f),

                shape =
                    RoundedCornerShape(16.dp)
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Map,

                    contentDescription =
                        null,

                    modifier =
                        Modifier.size(17.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(6.dp)
                )

                Text(
                    text = "MAP",

                    fontSize = 10.sp,

                    fontWeight =
                        FontWeight.ExtraBold
                )
            }

            Button(
                onClick =
                    onOpenAi,

                modifier =
                    Modifier.weight(1.35f),

                shape =
                    RoundedCornerShape(16.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            NovaDark
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.AutoAwesome,

                    contentDescription =
                        null,

                    tint =
                        NovaGoldLight,

                    modifier =
                        Modifier.size(17.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(6.dp)
                )

                Text(
                    text =
                        "ASK AI",

                    color =
                        Color.White,

                    fontSize = 10.sp,

                    fontWeight =
                        FontWeight.ExtraBold
                )
            }
        }
    }
}
