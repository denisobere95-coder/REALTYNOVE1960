package com.denis.realtynova.features.dashboard

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.realtynova.core.domain.model.*
import com.denis.realtynova.core.domain.repository.PropertyRepository
import com.denis.realtynova.core.util.CloudinaryManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

enum class WizardStep {
    CATEGORY,
    BASIC_DETAILS,
    LOCATION,
    SPECS,
    MEDIA_AMENITIES,
    VERIFICATION
}

data class CreateListingUiState(
    val currentStep: WizardStep = WizardStep.CATEGORY,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,

    // Step 1: Category
    val category: String = "", // House, Apartment, Land, Commercial

    // Step 2: Basic Details
    val title: String = "",
    val description: String = "",
    val listingType: String = "Buy", // Buy, Rent
    val price: Double = 0.0,
    val currency: String = "KSh",

    // Step 3: Location
    val latitude: Double = -1.2921, // Nairobi default
    val longitude: Double = 36.8219,
    val county: String = "",
    val address: String = "",

    // Step 4: Specs
    // House/Apartment
    val bedrooms: Int = 0,
    val bathrooms: Double = 0.0,
    val builtArea: Double = 0.0,
    val floors: Int = 1,
    // Apartment specific
    val floorNumber: Int = 0,
    val isFurnished: Boolean = false,
    // Land specific
    val landSizeAcres: Double = 0.0,
    val lrNumber: String = "",
    val zoning: String = "",
    val tenure: String = "",

    // Step 5: Media & Amenities
    val selectedImages: List<Uri> = emptyList(),
    val selectedAmenities: List<String> = emptyList(),

    // Step 6: Verification
    val verificationDocuments: List<Uri> = emptyList()
)

@HiltViewModel
class CreateListingViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val cloudinaryManager: CloudinaryManager,
    private val contentResolver: ContentResolver
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateListingUiState())
    val uiState: StateFlow<CreateListingUiState> = _uiState.asStateFlow()

    val availableAmenities = listOf(
        "Swimming Pool", "Gym", "High-speed Internet", "Borehole", 
        "Backup Generator", "CCTV", "Garden", "Elevator", 
        "Security Guard", "Parking", "Balcony", "Club House"
    )

    fun nextStep() {
        val next = when (_uiState.value.currentStep) {
            WizardStep.CATEGORY -> WizardStep.BASIC_DETAILS
            WizardStep.BASIC_DETAILS -> WizardStep.LOCATION
            WizardStep.LOCATION -> WizardStep.SPECS
            WizardStep.SPECS -> WizardStep.MEDIA_AMENITIES
            WizardStep.MEDIA_AMENITIES -> WizardStep.VERIFICATION
            WizardStep.VERIFICATION -> WizardStep.VERIFICATION
        }
        _uiState.value = _uiState.value.copy(currentStep = next)
    }

    fun previousStep() {
        val prev = when (_uiState.value.currentStep) {
            WizardStep.CATEGORY -> WizardStep.CATEGORY
            WizardStep.BASIC_DETAILS -> WizardStep.CATEGORY
            WizardStep.LOCATION -> WizardStep.BASIC_DETAILS
            WizardStep.SPECS -> WizardStep.LOCATION
            WizardStep.MEDIA_AMENITIES -> WizardStep.SPECS
            WizardStep.VERIFICATION -> WizardStep.MEDIA_AMENITIES
        }
        _uiState.value = _uiState.value.copy(currentStep = prev)
    }

    fun updateCategory(category: String) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun updateBasicDetails(title: String, description: String, listingType: String, price: Double) {
        _uiState.value = _uiState.value.copy(
            title = title,
            description = description,
            listingType = listingType,
            price = price
        )
    }

    fun updateLocation(lat: Double, lng: Double, county: String, address: String) {
        _uiState.value = _uiState.value.copy(
            latitude = lat,
            longitude = lng,
            county = county,
            address = address
        )
    }

    fun updateSpecs(
        bedrooms: Int = _uiState.value.bedrooms,
        bathrooms: Double = _uiState.value.bathrooms,
        builtArea: Double = _uiState.value.builtArea,
        floors: Int = _uiState.value.floors,
        floorNumber: Int = _uiState.value.floorNumber,
        isFurnished: Boolean = _uiState.value.isFurnished,
        landSizeAcres: Double = _uiState.value.landSizeAcres,
        lrNumber: String = _uiState.value.lrNumber,
        zoning: String = _uiState.value.zoning,
        tenure: String = _uiState.value.tenure
    ) {
        _uiState.value = _uiState.value.copy(
            bedrooms = bedrooms,
            bathrooms = bathrooms,
            builtArea = builtArea,
            floors = floors,
            floorNumber = floorNumber,
            isFurnished = isFurnished,
            landSizeAcres = landSizeAcres,
            lrNumber = lrNumber,
            zoning = zoning,
            tenure = tenure
        )
    }

    fun addImage(uri: Uri) {
        _uiState.value = _uiState.value.copy(
            selectedImages = _uiState.value.selectedImages + uri
        )
    }

    fun removeImage(uri: Uri) {
        _uiState.value = _uiState.value.copy(
            selectedImages = _uiState.value.selectedImages - uri
        )
    }

    fun toggleAmenity(amenity: String) {
        val current = _uiState.value.selectedAmenities
        val next = if (current.contains(amenity)) current - amenity else current + amenity
        _uiState.value = _uiState.value.copy(selectedAmenities = next)
    }

    fun addVerificationDocument(uri: Uri) {
        _uiState.value = _uiState.value.copy(
            verificationDocuments = _uiState.value.verificationDocuments + uri
        )
    }

    fun removeVerificationDocument(uri: Uri) {
        _uiState.value = _uiState.value.copy(
            verificationDocuments = _uiState.value.verificationDocuments - uri
        )
    }

    fun submit() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val state = _uiState.value
                val propertyId = UUID.randomUUID().toString()

                // 1. Parallel Upload Images to Cloudinary (Optimal for Mobile)
                val imageUrls = withContext(Dispatchers.IO) {
                    state.selectedImages.map { uri ->
                        async {
                            val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                                ?: throw Exception("Failed to read image")
                            cloudinaryManager.uploadImage(bytes)
                                ?: throw Exception("Image upload to Cloudinary failed")
                        }
                    }.awaitAll()
                }

                // 2. Parallel Upload Documents
                val docUrls = withContext(Dispatchers.IO) {
                    state.verificationDocuments.mapIndexed { index, uri ->
                        async {
                            val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                                ?: throw Exception("Failed to read document")
                            val path = "properties/$propertyId/doc_$index.pdf"
                            propertyRepository.uploadFile("verification-docs", path, bytes)
                        }
                    }.awaitAll()
                }

                // 3. Construct Property Object
                val images = imageUrls.mapIndexed { i, url -> 
                    PropertyImage(url, if (i == 0) PropertyImageType.HERO else PropertyImageType.EXTERIOR) 
                }
                
                val property = Property(
                    id = propertyId,
                    title = state.title,
                    description = state.description,
                    price = state.price,
                    currency = state.currency,
                    location = "${state.address}, ${state.county}",
                    address = state.address,
                    bedrooms = state.bedrooms,
                    bathrooms = state.bathrooms,
                    areaSqFt = if (state.category == "Land") 0.0 else state.builtArea,
                    type = state.category,
                    listingType = state.listingType,
                    latitude = state.latitude,
                    longitude = state.longitude,
                    images = images,
                    amenities = state.selectedAmenities,
                    houseDetails = if (state.category == "House") HouseDetails(
                        bedrooms = state.bedrooms,
                        bathrooms = state.bathrooms,
                        parkingSpaces = 0,
                        floors = state.floors,
                        builtAreaSqFt = state.builtArea,
                        landAreaSqFt = 0.0,
                        yearBuilt = null,
                        tenureType = null
                    ) else null,
                    apartmentDetails = if (state.category == "Apartment") ApartmentDetails(
                        bedrooms = state.bedrooms,
                        bathrooms = state.bathrooms,
                        floorNumber = state.floorNumber,
                        totalFloors = state.floors,
                        isFurnished = state.isFurnished,
                        serviceCharge = null
                    ) else null,
                    landDetails = if (state.category == "Land") LandDetails(
                        sizeAcres = state.landSizeAcres,
                        parcelNumber = state.lrNumber,
                        zoningType = state.zoning,
                        topography = null,
                        soilType = null,
                        tenureType = state.tenure,
                        isServiced = true
                    ) else null
                )

                // 4. Save to Repository
                val result = propertyRepository.saveProperty(property)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(isSuccess = true, isLoading = false)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, 
                        error = result.exceptionOrNull()?.message ?: "Unknown error"
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
