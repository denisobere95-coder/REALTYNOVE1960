package com.denis.realtynova.features.booking

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.denis.realtynova.core.designsystem.components.RealtyNovaButton
import com.denis.realtynova.core.designsystem.components.ButtonVariant
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.domain.model.Property
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.ui.tooling.preview.Preview
import com.denis.realtynova.core.designsystem.theme.REALTYNOVATheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    propertyId: String,
    onBack: () -> Unit,
    onBookingConfirmed: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(propertyId) {
        viewModel.loadProperty(propertyId)
    }

    BookingScreenContent(
        uiState = uiState,
        propertyId = propertyId,
        onBack = onBack,
        onBookingConfirmed = onBookingConfirmed,
        onConfirmBooking = { id, date, time ->
            viewModel.confirmBooking(id, date, time)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreenContent(
    uiState: BookingUiState,
    propertyId: String,
    onBack: () -> Unit,
    onBookingConfirmed: () -> Unit,
    onConfirmBooking: (String, LocalDate, String) -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now().plusDays(1)) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val timeSlots = listOf("09:00 AM", "10:30 AM", "12:00 PM", "02:00 PM", "03:30 PM", "05:00 PM")

    LaunchedEffect(uiState.bookingSuccess) {
        if (uiState.bookingSuccess) {
            showSuccessDialog = true
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onBookingConfirmed()
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onBookingConfirmed()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEmerald)
                ) {
                    Text("DONE")
                }
            },
            icon = { Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(48.dp)) },
            title = { Text("Viewing Requested") },
            text = { Text("Your viewing request has been sent to the agent. They will contact you shortly to confirm.") },
            shape = RoundedCornerShape(28.dp)
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Book a Viewing", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Property Header
            uiState.property?.let { property ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HomeWork, contentDescription = null, tint = DeepEmerald)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = property.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = property.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Text("Select Date", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            // Simple Date Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(5) { dayOffset ->
                    val date = LocalDate.now().plusDays(dayOffset.toLong() + 1)
                    val isSelected = selectedDate == date

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedDate = date },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) DeepEmerald else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) DeepEmerald else MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = date.dayOfWeek.name.take(3),
                                fontSize = 10.sp,
                                color = if (isSelected) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = date.dayOfMonth.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Text("Select Preferred Time", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(timeSlots) { time ->
                    val isSelected = selectedTime == time
                    Surface(
                        modifier = Modifier.clickable { selectedTime = time },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) DeepEmerald.copy(alpha = 0.1f) else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) DeepEmerald else MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isSelected) DeepEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = time,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) DeepEmerald else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            RealtyNovaButton(
                onClick = {
                    onConfirmBooking(propertyId, selectedDate, selectedTime!!)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = selectedTime != null && !uiState.isProcessing,
                variant = ButtonVariant.Premium
            ) {
                if (uiState.isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("CONFIRM VIEWING", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private val sampleProperty = Property(
    id = "1",
    title = "Luxury Villa in Runda",
    location = "Runda, Nairobi",
    price = 85000000.0,
    bedrooms = 5,
    bathrooms = 4.5,
    areaSqFt = 4500.0,
    description = "A beautiful luxury villa...",
    address = "123 Runda Drive",
    type = "House",
    listingType = "Sale",
    images = emptyList()
)

@Preview(showBackground = true)
@Composable
fun BookingScreenPreview() {
    REALTYNOVATheme {
        BookingScreenContent(
            uiState = BookingUiState(
                property = sampleProperty,
                isLoading = false,
                isProcessing = false,
                bookingSuccess = false
            ),
            propertyId = "1",
            onBack = {},
            onBookingConfirmed = {},
            onConfirmBooking = { _, _, _ -> }
        )
    }
}
