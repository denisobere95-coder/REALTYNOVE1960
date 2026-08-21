package com.denis.realtynova.features.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.realtynova.core.domain.model.Property
import com.denis.realtynova.core.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaymentUiState(
    val property: Property? = null,
    val isLoading: Boolean = false,
    val isProcessing: Boolean = false,
    val paymentSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    fun loadProperty(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val property = propertyRepository.getPropertyById(id)
                _uiState.value = _uiState.value.copy(isLoading = false, property = property)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Failed to load property")
            }
        }
    }

    fun processMpesaPayment(phone: String, amount: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true)
            delay(3000) // Mock STK push wait
            _uiState.value = _uiState.value.copy(isProcessing = false, paymentSuccess = true)
        }
    }

    fun processCardPayment(number: String, expiry: String, cvc: String, amount: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true)
            delay(2000) // Mock gateway processing
            _uiState.value = _uiState.value.copy(isProcessing = false, paymentSuccess = true)
        }
    }
}
