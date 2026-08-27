package com.denis.realtynova.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.realtynova.core.domain.model.Property
import com.denis.realtynova.core.domain.model.SearchFilter
import com.denis.realtynova.core.domain.model.SortOrder
import com.denis.realtynova.core.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.FlowPreview
import kotlin.time.Duration.Companion.milliseconds
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        // Debounce search criteria changes to optimize network calls
        _uiState
            .debounce(400.milliseconds)
            .distinctUntilChanged { old, new -> old.filter == new.filter }
            .onEach { performSearch() }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(
            filter = _uiState.value.filter.copy(query = query)
        )
    }

    fun updateFilter(newFilter: SearchFilter) {
        _uiState.value = _uiState.value.copy(filter = newFilter)
    }

    fun toggleFilterSheet(visible: Boolean) {
        _uiState.value = _uiState.value.copy(isFilterSheetVisible = visible)
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(filter = SearchFilter())
    }

    private fun performSearch() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val results = propertyRepository.searchProperties(_uiState.value.filter)
                _uiState.value = _uiState.value.copy(
                    results = results,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    results = emptyList() // Or handle error state specifically
                )
            }
        }
    }
}

data class SearchUiState(
    val filter: SearchFilter = SearchFilter(),
    val results: List<Property> = emptyList(),
    val isLoading: Boolean = false,
    val isFilterSheetVisible: Boolean = false
)
