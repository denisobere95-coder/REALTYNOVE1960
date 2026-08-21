package com.denis.realtynova.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.realtynova.core.data.manager.SessionManager
import com.denis.realtynova.core.domain.model.Property
import com.denis.realtynova.core.domain.model.UserRole
import com.denis.realtynova.core.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val properties: List<Property> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val marketTrends: List<Float> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val userRole: StateFlow<UserRole> = sessionManager.userRole
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserRole.BUYER)

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val properties = propertyRepository.getProperties()
                val trends = propertyRepository.getMarketTrends()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    properties = properties,
                    marketTrends = trends
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun toggleFavorite(propertyId: String) {
        val currentFavorites = _uiState.value.favoriteIds
        val newFavorites = if (currentFavorites.contains(propertyId)) {
            currentFavorites - propertyId
        } else {
            currentFavorites + propertyId
        }
        _uiState.value = _uiState.value.copy(favoriteIds = newFavorites)
    }

    fun refresh() {
        loadData()
    }
}
