package com.denis.realtynova.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.realtynova.core.domain.model.Property
import com.denis.realtynova.core.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ModerationUiState(
    val pendingListings: List<Property> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class ModerationViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModerationUiState())
    val uiState: StateFlow<ModerationUiState> = _uiState.asStateFlow()

    init {
        loadPendingListings()
    }

    fun loadPendingListings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val listings = propertyRepository.getPendingListings()
            _uiState.update { it.copy(pendingListings = listings, isLoading = false) }
        }
    }

    fun approveListing(id: String) {
        viewModelScope.launch {
            val success = propertyRepository.updateListingStatus(id, "active")
            if (success) {
                _uiState.update { it.copy(message = "Listing approved successfully") }
                loadPendingListings()
            } else {
                _uiState.update { it.copy(message = "Failed to approve listing") }
            }
        }
    }

    fun rejectListing(id: String) {
        viewModelScope.launch {
            val success = propertyRepository.updateListingStatus(id, "rejected")
            if (success) {
                _uiState.update { it.copy(message = "Listing rejected") }
                loadPendingListings()
            } else {
                _uiState.update { it.copy(message = "Failed to reject listing") }
            }
        }
    }
    
    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
