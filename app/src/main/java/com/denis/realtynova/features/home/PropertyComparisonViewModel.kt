package com.denis.realtynova.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.realtynova.core.domain.model.Property
import com.denis.realtynova.core.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ComparisonUiState(
    val property1: Property? = null,
    val property2: Property? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PropertyComparisonViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComparisonUiState())
    val uiState: StateFlow<ComparisonUiState> = _uiState.asStateFlow()

    fun loadProperties(id1: String, id2: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val p1 = propertyRepository.getPropertyById(id1)
                val p2 = propertyRepository.getPropertyById(id2)
                _uiState.value = _uiState.value.copy(
                    property1 = p1,
                    property2 = p2,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load properties"
                )
            }
        }
    }
}
