package com.denis.realtynova.features.dashboard

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.tooling.preview.Preview
import com.denis.realtynova.core.designsystem.theme.REALTYNOVATheme
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: CreateListingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    CreateListingContent(
        uiState = uiState,
        availableAmenities = viewModel.availableAmenities,
        onBack = onBack,
        onSuccess = onSuccess,
        onPreviousStep = viewModel::previousStep,
        onNextStep = viewModel::nextStep,
        onSubmit = viewModel::submit,
        onUpdateCategory = viewModel::updateCategory,
        onUpdateBasicDetails = viewModel::updateBasicDetails,
        onUpdateLocation = viewModel::updateLocation,
        onUpdateSpecs = viewModel::updateSpecs,
        onAddImage = viewModel::addImage,
        onRemoveImage = viewModel::removeImage,
        onToggleAmenity = viewModel::toggleAmenity,
        onAddDoc = viewModel::addVerificationDocument,
        onRemoveDoc = viewModel::removeVerificationDocument
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingContent(
    uiState: CreateListingUiState,
    availableAmenities: List<String>,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    onPreviousStep: () -> Unit,
    onNextStep: () -> Unit,
    onSubmit: () -> Unit,
    onUpdateCategory: (String) -> Unit,
    onUpdateBasicDetails: (String, String, String, Double) -> Unit,
    onUpdateLocation: (Double, Double, String, String) -> Unit,
    onUpdateSpecs: (Int, Double, Double, Int, Int, Boolean, Double, String, String, String) -> Unit,
    onAddImage: (Uri) -> Unit,
    onRemoveImage: (Uri) -> Unit,
    onToggleAmenity: (String) -> Unit,
    onAddDoc: (Uri) -> Unit,
    onRemoveDoc: (Uri) -> Unit
) {
    val scrollState = rememberScrollState()

    BackHandler {
        if (uiState.currentStep != WizardStep.CATEGORY) {
            onPreviousStep()
        } else {
            onBack()
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Listing", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.currentStep != WizardStep.CATEGORY) onPreviousStep() else onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            WizardBottomBar(
                currentStep = uiState.currentStep,
                isLoading = uiState.isLoading,
                onNext = { 
                    if (uiState.currentStep == WizardStep.VERIFICATION) onSubmit() else onNextStep() 
                },
                onBack = { onPreviousStep() },
                isNextEnabled = isStepValid(uiState)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            StepIndicator(currentStep = uiState.currentStep)
            
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = uiState.currentStep,
                    transitionSpec = {
                        if (targetState.ordinal > initialState.ordinal) {
                            (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                        } else {
                            (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                        }.using(SizeTransform(clip = false))
                    },
                    label = "StepTransition"
                ) { step ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        when (step) {
                            WizardStep.CATEGORY -> CategoryStep(uiState.category, onUpdateCategory)
                            WizardStep.BASIC_DETAILS -> BasicDetailsStep(uiState, onUpdateBasicDetails)
                            WizardStep.LOCATION -> LocationStep(uiState, onUpdateLocation)
                            WizardStep.SPECS -> SpecsStep(uiState, onUpdateSpecs)
                            WizardStep.MEDIA_AMENITIES -> MediaAmenitiesStep(
                                uiState = uiState,
                                availableAmenities = availableAmenities,
                                onAddImage = onAddImage,
                                onRemoveImage = onRemoveImage,
                                onToggleAmenity = onToggleAmenity
                            )
                            WizardStep.VERIFICATION -> VerificationStep(
                                uiState = uiState,
                                onAddDoc = onAddDoc,
                                onRemoveDoc = onRemoveDoc
                            )
                        }
                        
                        if (uiState.error != null) {
                            Text(uiState.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                        
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StepIndicator(currentStep: WizardStep) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WizardStep.entries.forEach { step ->
            val isActive = step == currentStep
            val isCompleted = step.ordinal < currentStep.ordinal
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isActive -> ChampagneGold
                            isCompleted -> DeepEmerald
                            else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        }
                    )
            )
        }
    }
}

@Composable
fun WizardBottomBar(
    currentStep: WizardStep,
    isLoading: Boolean,
    onNext: () -> Unit,
    onBack: () -> Unit,
    isNextEnabled: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 8.dp,
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (currentStep != WizardStep.CATEGORY) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, DeepEmerald)
                ) {
                    Text("Back", color = DeepEmerald, fontWeight = FontWeight.Bold)
                }
            }
            
            Button(
                onClick = onNext,
                modifier = Modifier.weight(2f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepEmerald),
                enabled = isNextEnabled && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(
                        if (currentStep == WizardStep.VERIFICATION) "FINISH & PUBLISH" else "NEXT",
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun CategoryStep(selected: String, onSelect: (String) -> Unit) {
    Text("Select Property Type", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
    
    val categories = listOf(
        "House" to Icons.Default.Home,
        "Apartment" to Icons.Default.Apartment,
        "Land" to Icons.Default.Landscape,
        "Commercial" to Icons.Default.Business
    )
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        categories.forEach { (name, icon) ->
            val isSelected = selected == name
            Surface(
                onClick = { onSelect(name) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) DeepEmerald else MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, if (isSelected) DeepEmerald else MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = if (isSelected) 8.dp else 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else DeepEmerald,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (isSelected) {
                        Icon(Icons.Default.CheckCircle, null, tint = ChampagneGold)
                    }
                }
            }
        }
    }
}

@Composable
fun BasicDetailsStep(uiState: CreateListingUiState, onUpdate: (String, String, String, Double) -> Unit) {
    Text("Property Details", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
    
    var title by remember { mutableStateOf(uiState.title) }
    var desc by remember { mutableStateOf(uiState.description) }
    var price by remember { mutableStateOf(if (uiState.price == 0.0) "" else uiState.price.toString()) }
    var listingType by remember { mutableStateOf(uiState.listingType) }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        SegmentedButton(
            options = listOf("Buy", "Rent"),
            selectedOption = listingType,
            onOptionSelected = { 
                listingType = it
                onUpdate(title, desc, listingType, price.toDoubleOrNull() ?: 0.0)
            }
        )

        PostTextField(
            value = title,
            onValueChange = { 
                title = it
                onUpdate(title, desc, listingType, price.toDoubleOrNull() ?: 0.0)
            },
            label = "Property Title",
            placeholder = "e.g. Luxurious 5BR Villa in Runda"
        )
        
        PostTextField(
            value = desc,
            onValueChange = { 
                desc = it
                onUpdate(title, desc, listingType, price.toDoubleOrNull() ?: 0.0)
            },
            label = "Description",
            placeholder = "Detailed description of your property...",
            singleLine = false,
            minLines = 4
        )
        
        PostTextField(
            value = price,
            onValueChange = { 
                price = it
                onUpdate(title, desc, listingType, price.toDoubleOrNull() ?: 0.0)
            },
            label = "Price (KSh)",
            placeholder = "0.00",
            keyboardType = KeyboardType.Number
        )
    }
}

@Composable
fun LocationStep(uiState: CreateListingUiState, onUpdate: (Double, Double, String, String) -> Unit) {
    Text("Pin Location", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
    
    val nairobi = LatLng(uiState.latitude, uiState.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(nairobi, 12f)
    }

    var address by remember { mutableStateOf(uiState.address) }
    var county by remember { mutableStateOf(uiState.county) }

    val markerState = rememberMarkerState(position = nairobi)
    
    LaunchedEffect(uiState.latitude, uiState.longitude) {
        markerState.position = LatLng(uiState.latitude, uiState.longitude)
    }

    val mapProperties = remember { MapProperties(isMyLocationEnabled = false) }
    val mapUiSettings = remember { MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false) }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = mapUiSettings,
                onMapClick = { latLng ->
                    onUpdate(latLng.latitude, latLng.longitude, county, address)
                }
            ) {
                Marker(
                    state = markerState,
                    title = "Property Location",
                    draggable = true
                )
            }
        }
        
        PostTextField(
            value = county,
            onValueChange = { 
                county = it
                onUpdate(uiState.latitude, uiState.longitude, county, address)
            },
            label = "County / Area",
            placeholder = "e.g. Nairobi, Kiambu"
        )
        
        PostTextField(
            value = address,
            onValueChange = { 
                address = it
                onUpdate(uiState.latitude, uiState.longitude, county, address)
            },
            label = "Street Address / Landmark",
            placeholder = "e.g. 123 Mimosa Drive, Runda"
        )
    }
}

@Composable
fun SpecsStep(uiState: CreateListingUiState, onUpdate: (Int, Double, Double, Int, Int, Boolean, Double, String, String, String) -> Unit) {
    Text("Property Specs", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
    
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        when (uiState.category) {
            "House", "Apartment" -> {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CounterField(
                        label = "Bedrooms",
                        value = uiState.bedrooms,
                        onValueChange = { onUpdate(it, uiState.bathrooms, uiState.builtArea, uiState.floors, uiState.floorNumber, uiState.isFurnished, 0.0, "", "", "") },
                        modifier = Modifier.weight(1f)
                    )
                    CounterField(
                        label = "Bathrooms",
                        value = uiState.bathrooms.toInt(), // Simplified for UI
                        onValueChange = { onUpdate(uiState.bedrooms, it.toDouble(), uiState.builtArea, uiState.floors, uiState.floorNumber, uiState.isFurnished, 0.0, "", "", "") },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                PostTextField(
                    value = if (uiState.builtArea == 0.0) "" else uiState.builtArea.toString(),
                    onValueChange = { onUpdate(uiState.bedrooms, uiState.bathrooms, it.toDoubleOrNull() ?: 0.0, uiState.floors, uiState.floorNumber, uiState.isFurnished, 0.0, "", "", "") },
                    label = "Built Area (SqFt)",
                    keyboardType = KeyboardType.Number
                )
                
                if (uiState.category == "Apartment") {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        PostTextField(
                            value = uiState.floorNumber.toString(),
                            onValueChange = { onUpdate(uiState.bedrooms, uiState.bathrooms, uiState.builtArea, uiState.floors, it.toIntOrNull() ?: 0, uiState.isFurnished, 0.0, "", "", "") },
                            label = "Floor No.",
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Number
                        )
                        Row(modifier = Modifier.weight(1f).padding(top = 32.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = uiState.isFurnished, onCheckedChange = { onUpdate(uiState.bedrooms, uiState.bathrooms, uiState.builtArea, uiState.floors, uiState.floorNumber, it, 0.0, "", "", "") })
                            Text("Furnished")
                        }
                    }
                } else {
                    CounterField(
                        label = "Total Floors",
                        value = uiState.floors,
                        onValueChange = { onUpdate(uiState.bedrooms, uiState.bathrooms, uiState.builtArea, it, uiState.floorNumber, uiState.isFurnished, 0.0, "", "", "") }
                    )
                }
            }
            "Land" -> {
                PostTextField(
                    value = if (uiState.landSizeAcres == 0.0) "" else uiState.landSizeAcres.toString(),
                    onValueChange = { onUpdate(0, 0.0, 0.0, 0, 0, false, it.toDoubleOrNull() ?: 0.0, uiState.lrNumber, uiState.zoning, uiState.tenure) },
                    label = "Size (Acres)",
                    keyboardType = KeyboardType.Number
                )
                PostTextField(
                    value = uiState.lrNumber,
                    onValueChange = { onUpdate(0, 0.0, 0.0, 0, 0, false, uiState.landSizeAcres, it, uiState.zoning, uiState.tenure) },
                    label = "LR Number"
                )
                PostTextField(
                    value = uiState.zoning,
                    onValueChange = { onUpdate(0, 0.0, 0.0, 0, 0, false, uiState.landSizeAcres, uiState.lrNumber, it, uiState.tenure) },
                    label = "Zoning (e.g. Residential, Agricultural)"
                )
            }
            else -> {
                Text("Specs for Commercial properties will be added soon.")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MediaAmenitiesStep(
    uiState: CreateListingUiState,
    availableAmenities: List<String>,
    onAddImage: (Uri) -> Unit,
    onRemoveImage: (Uri) -> Unit,
    onToggleAmenity: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text("Media & Amenities", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
        
        val photoPicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(),
            onResult = { uris -> uris.forEach { onAddImage(it) } }
        )

        Text("Property Photos", fontWeight = FontWeight.Bold)
        
        // Custom Grid to avoid nested scroll issues
        val chunkedImages = (listOf(null) + uiState.selectedImages).chunked(3)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            chunkedImages.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { uri ->
                        Box(modifier = Modifier.weight(1f)) {
                            if (uri == null) {
                                Surface(
                                    onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                                    modifier = Modifier.aspectRatio(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.AddAPhoto, null, tint = DeepEmerald)
                                    }
                                }
                            } else {
                                Box {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = null,
                                        modifier = Modifier.aspectRatio(1f).clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { onRemoveImage(uri) },
                                        modifier = Modifier.align(Alignment.TopEnd).size(24.dp).padding(4.dp).background(Color.Black.copy(0.5f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }
                    // Fill empty spots in the last row
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Text("Amenities", fontWeight = FontWeight.Bold)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableAmenities.forEach { amenity ->
                val isSelected = uiState.selectedAmenities.contains(amenity)
                FilterChip(
                    selected = isSelected,
                    onClick = { onToggleAmenity(amenity) },
                    label = { Text(amenity) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DeepEmerald,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun VerificationStep(
    uiState: CreateListingUiState,
    onAddDoc: (Uri) -> Unit,
    onRemoveDoc: (Uri) -> Unit
) {
    Text("Verification Documents", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
    Text("Upload Title Deeds, Survey Maps or Ownership proof to get the 'Verified' badge.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    
    val docPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris -> uris.forEach { onAddDoc(it) } }
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = { docPicker.launch(arrayOf("application/pdf", "image/*")) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.UploadFile, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("SELECT DOCUMENTS", fontWeight = FontWeight.Bold)
        }
        
        uiState.verificationDocuments.forEach { uri ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Description, null, tint = DeepEmerald)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = uri.path?.substringAfterLast("/") ?: "Document",
                        maxLines = 1,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    IconButton(onClick = { onRemoveDoc(uri) }) {
                        Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
        
        if (uiState.verificationDocuments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("No documents uploaded yet", color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
fun CounterField(label: String, value: Int, onValueChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            IconButton(onClick = { if (value > 0) onValueChange(value - 1) }) {
                Icon(Icons.Default.Remove, null)
            }
            Text(
                text = value.toString(),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            IconButton(onClick = { onValueChange(value + 1) }) {
                Icon(Icons.Default.Add, null)
            }
        }
    }
}

@Composable
fun SegmentedButton(options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
    ) {
        options.forEach { option ->
            val isSelected = selectedOption == option
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) DeepEmerald else Color.Transparent)
                    .clickable { onOptionSelected(option) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
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
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, fontSize = 14.sp) },
            shape = RoundedCornerShape(12.dp),
            singleLine = singleLine,
            minLines = minLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DeepEmerald,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
    }
}

fun isStepValid(state: CreateListingUiState): Boolean {
    return when (state.currentStep) {
        WizardStep.CATEGORY -> state.category.isNotBlank()
        WizardStep.BASIC_DETAILS -> state.title.isNotBlank() && state.price > 0
        WizardStep.LOCATION -> state.county.isNotBlank() && state.address.isNotBlank()
        WizardStep.SPECS -> {
            if (state.category == "Land") state.landSizeAcres > 0 else state.builtArea > 0
        }
        WizardStep.MEDIA_AMENITIES -> state.selectedImages.isNotEmpty()
        WizardStep.VERIFICATION -> true // Optional
    }
}

@Preview(showBackground = true)
@Composable
fun CreateListingScreenPreview() {
    REALTYNOVATheme {
        CreateListingContent(
            uiState = CreateListingUiState(
                currentStep = WizardStep.CATEGORY,
                category = "House"
            ),
            availableAmenities = listOf("Swimming Pool", "Gym", "Security Guard"),
            onBack = {},
            onSuccess = {},
            onPreviousStep = {},
            onNextStep = {},
            onSubmit = {},
            onUpdateCategory = {},
            onUpdateBasicDetails = { _, _, _, _ -> },
            onUpdateLocation = { _, _, _, _ -> },
            onUpdateSpecs = { _, _, _, _, _, _, _, _, _, _ -> },
            onAddImage = {},
            onRemoveImage = {},
            onToggleAmenity = {},
            onAddDoc = {},
            onRemoveDoc = {}
        )
    }
}
