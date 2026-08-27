package com.denis.realtynova.features.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.denis.realtynova.core.designsystem.components.CreativeBackground
import com.denis.realtynova.core.designsystem.components.BackgroundVariant
import com.denis.realtynova.R
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.designsystem.theme.REALTYNOVATheme
import com.denis.realtynova.core.domain.model.Property

@Composable
fun AdminModerationScreen(
    viewModel: ModerationViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    CreativeBackground(
        imageRes = R.drawable.img_30,
        variant = BackgroundVariant.DARK,
        overlayAlpha = 0.9f
    ) {
        AdminModerationContent(
            uiState = uiState,
            onNavigateToDetail = onNavigateToDetail,
            onBack = onBack,
            onApproveListing = { viewModel.approveListing(it) },
            onRejectListing = { viewModel.rejectListing(it) },
            onClearMessage = { viewModel.clearMessage() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminModerationContent(
    uiState: ModerationUiState,
    onNavigateToDetail: (String) -> Unit,
    onBack: () -> Unit,
    onApproveListing: (String) -> Unit,
    onRejectListing: (String) -> Unit,
    onClearMessage: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessage()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Moderation Queue", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading && uiState.pendingListings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DeepEmerald)
            }
        } else if (uiState.pendingListings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No pending listings", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.pendingListings) { property ->
                    ModerationItem(
                        property = property,
                        onView = { onNavigateToDetail(property.id) },
                        onApprove = { onApproveListing(property.id) },
                        onReject = { onRejectListing(property.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ModerationItem(
    property: Property,
    onView: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = property.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    Text(
                        text = "${property.currency} ${property.price}",
                        color = ChampagneGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
                IconButton(onClick = onView) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = "View Details", tint = Color.White.copy(alpha = 0.7f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEmerald),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("APPROVE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("REJECT", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminModerationScreenPreview() {
    val sampleProperties = listOf(
        Property(
            id = "1",
            title = "Modern Apartment in Kilimani",
            description = "A stunning modern apartment...",
            price = 15000000.0,
            location = "Kilimani, Nairobi",
            address = "Lenana Road",
            bedrooms = 3,
            bathrooms = 2.0,
            areaSqFt = 1200.0,
            images = emptyList(),
            type = "Apartment",
            listingType = "Sale"
        ),
        Property(
            id = "2",
            title = "Luxury Villa in Runda",
            description = "Spacious villa with a garden...",
            price = 85000000.0,
            location = "Runda, Nairobi",
            address = "Runda Drive",
            bedrooms = 5,
            bathrooms = 4.0,
            areaSqFt = 4500.0,
            images = emptyList(),
            type = "House",
            listingType = "Sale"
        )
    )

    REALTYNOVATheme {
        AdminModerationContent(
            uiState = ModerationUiState(
                pendingListings = sampleProperties,
                isLoading = false
            ),
            onNavigateToDetail = {},
            onBack = {},
            onApproveListing = {},
            onRejectListing = {},
            onClearMessage = {}
        )
    }
}
