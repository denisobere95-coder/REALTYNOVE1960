package com.denis.realtynova.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.realtynova.core.domain.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class DashboardUiState(
    val userRole: UserRole = UserRole.BUYER,
    val totalCommissions: Double = 0.0,
    val commissionGoal: Double = 5000000.0,
    val viewingRequestsCount: Int = 0,
    val activeLeadsCount: Int = 0,
    val isLoading: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        // Mock data
        _uiState.value = _uiState.value.copy(
            totalCommissions = 2450000.0,
            viewingRequestsCount = 8,
            activeLeadsCount = 42
        )
    }
}
