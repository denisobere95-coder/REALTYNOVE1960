package com.denis.realtynova.features.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.realtynova.core.data.manager.StorageManager
import com.denis.realtynova.core.domain.model.Property
import com.denis.realtynova.core.domain.model.PropertyImage
import com.denis.realtynova.core.domain.model.PropertyImageType
import com.denis.realtynova.core.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PropertyPostViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val storageManager: StorageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PropertyPostUiState())
    val uiState: StateFlow<PropertyPostUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) { _uiState.value = _uiState.value.copy(title = title) }
    fun updateDescription(desc: String) { _uiState.value = _uiState.value.copy(description = desc) }
    fun updatePrice(price: Double) { _uiState.value = _uiState.value.copy(price = price) }
    fun updateLocation(loc: String) { _uiState.value = _uiState.value.copy(location = loc) }
    fun updateBedrooms(beds: Int) { _uiState.value = _uiState.value.copy(bedrooms = beds) }
    fun updateBathrooms(baths: Double) { _uiState.value = _uiState.value.copy(bathrooms = baths) }
    fun updateArea(area: Double) { _uiState.value = _uiState.value.copy(areaSqFt = area) }
    fun updateType(type: String) { _uiState.value = _uiState.value.copy(type = type) }

    fun addImage(uri: Uri) {
        val current = _uiState.value.selectedImages.toMutableList()
        current.add(uri)
        _uiState.value = _uiState.value.copy(selectedImages = current)
    }

    fun submitProperty() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val imageUrls = uploadImages(_uiState.value.selectedImages)
                val property = Property(
                    id = UUID.randomUUID().toString(),
                    title = _uiState.value.title,
                    description = _uiState.value.description,
                    price = _uiState.value.price,
                    location = _uiState.value.location,
                    address = _uiState.value.location,
                    bedrooms = _uiState.value.bedrooms,
                    bathrooms = _uiState.value.bathrooms,
                    areaSqFt = _uiState.value.areaSqFt,
                    images = imageUrls.mapIndexed { index, url -> 
                        PropertyImage(url, if (index == 0) PropertyImageType.HERO else PropertyImageType.EXTERIOR)
                    },
                    type = _uiState.value.type,
                    listingType = "Buy",
                    isVerified = false,
                    isPremium = false
                )
                
                val result = propertyRepository.saveProperty(property)
                
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(isSuccess = true, isLoading = false)
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = result.exceptionOrNull()?.message ?: "Failed to save property",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    private suspend fun uploadImages(uris: List<Uri>): List<String> {
        return uris.mapNotNull { uri ->
            storageManager.uploadImage(uri, "properties").getOrNull()
        }
    }
}

data class PropertyPostUiState(
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val location: String = "",
    val bedrooms: Int = 0,
    val bathrooms: Double = 0.0,
    val areaSqFt: Double = 0.0,
    val type: String = "Villa",
    val selectedImages: List<Uri> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
