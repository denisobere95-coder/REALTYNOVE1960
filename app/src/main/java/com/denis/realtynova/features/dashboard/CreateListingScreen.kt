package com.denis.realtynova.features.dashboard

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.denis.realtynova.R
import com.denis.realtynova.core.designsystem.components.*
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.ChampagneGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: CreateListingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onSuccess()
    }

    CreativeBackground(
        imageRes = R.drawable.img_29,
        variant = BackgroundVariant.DARK,
        overlayAlpha = 0.88f
    ) {
        CreateListingContent(
            uiState = uiState,
            onBack = onBack,
            onNext = viewModel::nextStep,
            onPrevious = viewModel::previousStep,
            onUpdateCategory = viewModel::updateCategory,
            onUpdateBasicDetails = viewModel::updateBasicDetails,
            onUpdateLocation = viewModel::updateLocation,
            onUpdateSpecs = { bedrooms, bathrooms, builtArea, floors, floorNumber, isFurnished, landSize, lr, zoning, tenure ->
                viewModel.updateSpecs(bedrooms, bathrooms, builtArea, floors, floorNumber, isFurnished, landSize, lr, zoning, tenure)
            },
            onAddImage = viewModel::addImage,
            onRemoveImage = viewModel::removeImage,
            onToggleAmenity = viewModel::toggleAmenity,
            onAddDocument = viewModel::addVerificationDocument,
            onRemoveDocument = viewModel::removeVerificationDocument,
            onSubmit = viewModel::submit
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateListingContent(
    uiState: CreateListingUiState,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onUpdateCategory: (String) -> Unit,
    onUpdateBasicDetails: (String, String, String, Double) -> Unit,
    onUpdateLocation: (Double, Double, String, String) -> Unit,
    onUpdateSpecs: (Int, Double, Double, Int, Int, Boolean, Double, String, String, String) -> Unit,
    onAddImage: (Uri) -> Unit,
    onRemoveImage: (Uri) -> Unit,
    onToggleAmenity: (String) -> Unit,
    onAddDocument: (Uri) -> Unit,
    onRemoveDocument: (Uri) -> Unit,
    onSubmit: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Post Property", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            WizardBottomBar(
                currentStep = uiState.currentStep,
                isNextEnabled = isStepValid(uiState),
                onNext = if (uiState.currentStep == WizardStep.VERIFICATION) onSubmit else onNext,
                onPrevious = onPrevious,
                isLoading = uiState.isLoading
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            StepIndicator(currentStep = uiState.currentStep)
            Spacer(modifier = Modifier.height(32.dp))

            when (uiState.currentStep) {
                WizardStep.CATEGORY -> CategoryStep(uiState.category, onUpdateCategory)
                WizardStep.BASIC_DETAILS -> BasicDetailsStep(uiState, onUpdateBasicDetails)
                WizardStep.LOCATION -> LocationStep(uiState, onUpdateLocation)
                WizardStep.SPECS -> SpecsStep(uiState, onUpdateSpecs)
                WizardStep.MEDIA_AMENITIES -> MediaAmenitiesStep(uiState, onAddImage, onRemoveImage, onToggleAmenity)
                WizardStep.VERIFICATION -> VerificationStep(uiState, onAddDocument, onRemoveDocument)
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun StepIndicator(currentStep: WizardStep) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WizardStep.entries.forEachIndexed { index, step ->
            val isActive = step == currentStep
            val isCompleted = index < currentStep.ordinal
            
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) DeepEmerald else if (isCompleted) DeepEmerald.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.3f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, null, tint = DeepEmerald, modifier = Modifier.size(18.dp))
                } else {
                    Text(
                        (index + 1).toString(),
                        color = if (isActive) Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            if (index < WizardStep.entries.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(if (isCompleted) DeepEmerald else Color.LightGray.copy(alpha = 0.3f))
                )
            }
        }
    }
}

@Composable
fun WizardBottomBar(
    currentStep: WizardStep,
    isNextEnabled: Boolean,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 8.dp,
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (currentStep != WizardStep.CATEGORY) {
                RealtyNovaButton(
                    onClick = onPrevious,
                    variant = ButtonVariant.Secondary,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("BACK", fontWeight = FontWeight.Bold)
                }
            }
            
            RealtyNovaButton(
                onClick = onNext,
                enabled = isNextEnabled && !isLoading,
                isLoading = isLoading,
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    if (currentStep == WizardStep.VERIFICATION) "SUBMIT LISTING" else "CONTINUE",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CategoryStep(selected: String, onUpdate: (String) -> Unit) {
    Text("Select Property Type", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
    Spacer(modifier = Modifier.height(24.dp))
    
    val categoryOptions = listOf(
        "Apartment" to Icons.Default.Apartment,
        "Villa" to Icons.Default.Villa,
        "Townhouse" to Icons.Default.Home,
        "Land" to Icons.Default.Landscape,
        "Commercial" to Icons.Default.Business
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        categoryOptions.forEach { (label, icon) ->
            Surface(
                onClick = { onUpdate(label) },
                shape = RoundedCornerShape(16.dp),
                color = if (selected == label) DeepEmerald.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    if (selected == label) 2.dp else 1.dp,
                    if (selected == label) DeepEmerald else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, null, tint = if (selected == label) DeepEmerald else Color.Gray)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(label, fontWeight = FontWeight.Bold, color = if (selected == label) DeepEmerald else MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.weight(1f))
                    if (selected == label) {
                        Icon(Icons.Default.CheckCircle, null, tint = DeepEmerald)
                    }
                }
            }
        }
    }
}

@Composable
fun BasicDetailsStep(uiState: CreateListingUiState, onUpdate: (String, String, String, Double) -> Unit) {
    Text("Basic Details", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
    Spacer(modifier = Modifier.height(24.dp))
    
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        PostTextField(
            value = uiState.title,
            onValueChange = { onUpdate(it, uiState.description, uiState.listingType, uiState.price) },
            label = "Listing Title",
            placeholder = "e.g. Modern Penthouse with Garden"
        )
        
        SegmentedButton(
            options = listOf("Rent", "Buy"),
            selected = uiState.listingType,
            onSelected = { onUpdate(uiState.title, uiState.description, it, uiState.price) }
        )

        PostTextField(
            value = uiState.price.toString(),
            onValueChange = { onUpdate(uiState.title, uiState.description, uiState.listingType, it.toDoubleOrNull() ?: 0.0) },
            label = if (uiState.listingType == "Rent") "Monthly Rent (KSh)" else "Sale Price (KSh)",
            keyboardType = KeyboardType.Number,
            placeholder = "0.00"
        )
        
        PostTextField(
            value = uiState.description,
            onValueChange = { onUpdate(uiState.title, it, uiState.listingType, uiState.price) },
            label = "Description",
            singleLine = false,
            maxLines = 5,
            placeholder = "Describe the highlights of your property..."
        )
    }
}

@Composable
fun LocationStep(uiState: CreateListingUiState, onUpdate: (Double, Double, String, String) -> Unit) {
    Text("Area & Address", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
    Spacer(modifier = Modifier.height(24.dp))
    
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.LightGray.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Map, null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                Text("Map Disconnected", color = Color.Gray)
            }
        }
        
        PostTextField(
            value = uiState.county,
            onValueChange = { onUpdate(uiState.latitude, uiState.longitude, it, uiState.address) },
            label = "County / Area",
            placeholder = "e.g. Nairobi, Kiambu"
        )
        
        PostTextField(
            value = uiState.address,
            onValueChange = { onUpdate(uiState.latitude, uiState.longitude, uiState.county, it) },
            label = "Street Address / Landmark",
            placeholder = "e.g. 123 Mimosa Drive, Runda"
        )
    }
}

@Composable
fun SpecsStep(uiState: CreateListingUiState, onUpdate: (Int, Double, Double, Int, Int, Boolean, Double, String, String, String) -> Unit) {
    Text("Specifications", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
    Spacer(modifier = Modifier.height(24.dp))
    
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CounterField("Bedrooms", uiState.bedrooms, { onUpdate(it, uiState.bathrooms, uiState.builtArea, uiState.floors, uiState.floorNumber, uiState.isFurnished, uiState.landSizeAcres, uiState.lrNumber, uiState.zoning, uiState.tenure) }, Modifier.weight(1f))
            CounterField("Bathrooms", uiState.bathrooms.toInt(), { onUpdate(uiState.bedrooms, it.toDouble(), uiState.builtArea, uiState.floors, uiState.floorNumber, uiState.isFurnished, uiState.landSizeAcres, uiState.lrNumber, uiState.zoning, uiState.tenure) }, Modifier.weight(1f))
        }
        
        PostTextField(
            value = uiState.builtArea.toString(),
            onValueChange = { onUpdate(uiState.bedrooms, uiState.bathrooms, it.toDoubleOrNull() ?: 0.0, uiState.floors, uiState.floorNumber, uiState.isFurnished, uiState.landSizeAcres, uiState.lrNumber, uiState.zoning, uiState.tenure) },
            label = "Built Area (Sq Ft)",
            keyboardType = KeyboardType.Number
        )
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = uiState.isFurnished, onCheckedChange = { onUpdate(uiState.bedrooms, uiState.bathrooms, uiState.builtArea, uiState.floors, uiState.floorNumber, it, uiState.landSizeAcres, uiState.lrNumber, uiState.zoning, uiState.tenure) })
            Text("Furnished")
        }
    }
}

@Composable
fun MediaAmenitiesStep(
    uiState: CreateListingUiState,
    onAddImage: (Uri) -> Unit,
    onRemoveImage: (Uri) -> Unit,
    onToggleAmenity: (String) -> Unit
) {
    Text("Photos & Amenities", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
    Spacer(modifier = Modifier.height(24.dp))
    
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text("Property Images", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray.copy(alpha = 0.3f))
                    .clickable { /* Photo Picker Mock */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AddAPhoto, null, tint = Color.Gray)
            }
        }
        
        Text("Amenities", fontWeight = FontWeight.Bold)
        // Add amenity selection UI here if needed
    }
}

@Composable
fun VerificationStep(uiState: CreateListingUiState, onAddDoc: (Uri) -> Unit, onRemoveDoc: (Uri) -> Unit) {
    Text("Verification", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
    Spacer(modifier = Modifier.height(16.dp))
    Text("Upload ownership documents to get the VERIFIED badge.", color = Color.Gray)
    
    Spacer(modifier = Modifier.height(24.dp))
    
    Button(
        onClick = { /* File Picker Mock */ },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Icon(Icons.Default.UploadFile, null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("UPLOAD TITLE DEED / LEASE")
    }
}

@Composable
fun CounterField(label: String, value: Int, onUpdate: (Int) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (value > 0) onUpdate(value - 1) }) { Icon(Icons.Default.Remove, null) }
            Text(value.toString(), fontWeight = FontWeight.Bold)
            IconButton(onClick = { onUpdate(value + 1) }) { Icon(Icons.Default.Add, null) }
        }
    }
}

@Composable
fun SegmentedButton(options: List<String>, selected: String, onSelected: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(48.dp)) {
        options.forEachIndexed { index, option ->
            Surface(
                onClick = { onSelected(option) },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                color = if (selected == option) DeepEmerald else MaterialTheme.colorScheme.surface,
                shape = when(index) {
                    0 -> RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                    options.size - 1 -> RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                    else -> RoundedCornerShape(0.dp)
                },
                border = androidx.compose.foundation.BorderStroke(1.dp, if (selected == option) DeepEmerald else MaterialTheme.colorScheme.outlineVariant)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(option, color = if (selected == option) Color.White else Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PostTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            singleLine = singleLine,
            maxLines = maxLines,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType)
        )
    }
}

fun isStepValid(uiState: CreateListingUiState): Boolean {
    return when(uiState.currentStep) {
        WizardStep.CATEGORY -> uiState.category.isNotEmpty()
        WizardStep.BASIC_DETAILS -> uiState.title.isNotEmpty() && uiState.price > 0
        else -> true
    }
}
