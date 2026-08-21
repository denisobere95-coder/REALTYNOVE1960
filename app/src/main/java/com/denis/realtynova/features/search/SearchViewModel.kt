package com.denis.realtynova.features.search

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

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        performSearch()
    }

    fun onCategorySelected(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        performSearch()
    }

    fun onMaxPriceChanged(maxPrice: Double?) {
        _uiState.value = _uiState.value.copy(maxPrice = maxPrice)
        performSearch()
    }

    private fun performSearch() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val results = propertyRepository.searchProperties(
                query = _uiState.value.query,
                maxPrice = _uiState.value.maxPrice
            )
            
            val filteredResults = if (_uiState.value.selectedCategory != "All") {
                results.filter { it.type.contains(_uiState.value.selectedCategory, ignoreCase = true) }
            } else {
                results
            }

            _uiState.value = _uiState.value.copy(
                results = filteredResults,
                isLoading = false
            )
        }
    }
}

data class SearchUiState(
    val query: String = "",
    val selectedCategory: String = "All",
    val maxPrice: Double? = null,
    val results: List<Property> = emptyList(),
    val isLoading: Boolean = false
)
