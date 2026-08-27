package com.denis.realtynova.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.realtynova.core.domain.model.Property
import com.denis.realtynova.core.domain.repository.PropertyRepository
import com.denis.realtynova.features.saved.SavedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PropertyDetailUiState(
    val property: Property? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val aiEvaluation: com.denis.realtynova.core.ai.PropertyEvaluation? = null,
    val error: String? = null
)

@HiltViewModel
class PropertyDetailViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val savedRepository: SavedRepository,
    private val propertyEvaluator: com.denis.realtynova.core.ai.PropertyEvaluator,
    private val analytics: com.google.firebase.analytics.FirebaseAnalytics
) : ViewModel() {

    private val _uiState = MutableStateFlow(PropertyDetailUiState())
    val uiState: StateFlow<PropertyDetailUiState> = _uiState.asStateFlow()

    fun loadProperty(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val property = propertyRepository.getPropertyById(id)
                val saved = savedRepository.observeSavedProperties().first()
                val isFavorite = saved.any { it.id == id }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    property = property,
                    isFavorite = isFavorite
                )

                // Log Creative Analytics Event
                property?.let { p ->
                    val bundle = android.os.Bundle().apply {
                        putString(com.google.firebase.analytics.FirebaseAnalytics.Param.ITEM_ID, p.id)
                        putString(com.google.firebase.analytics.FirebaseAnalytics.Param.ITEM_NAME, p.title)
                        putString(com.google.firebase.analytics.FirebaseAnalytics.Param.ITEM_CATEGORY, p.type)
                        putDouble(com.google.firebase.analytics.FirebaseAnalytics.Param.VALUE, p.price)
                        putString(com.google.firebase.analytics.FirebaseAnalytics.Param.CURRENCY, p.currency)
                    }
                    analytics.logEvent(com.google.firebase.analytics.FirebaseAnalytics.Event.VIEW_ITEM, bundle)
                    
                    val evaluation = propertyEvaluator.evaluateProperty(p)
                    _uiState.value = _uiState.value.copy(aiEvaluation = evaluation)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun toggleFavorite() {
        val currentProperty = _uiState.value.property ?: return
        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                savedRepository.remove(currentProperty.id)
            } else {
                savedRepository.save(currentProperty)
            }
            _uiState.value = _uiState.value.copy(isFavorite = !_uiState.value.isFavorite)
        }
    }
}
