package com.denis.realtynova.features.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.realtynova.core.domain.model.RecentChat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed interface MessagesUiState {
    data object Loading : MessagesUiState
    data class Success(val chats: List<RecentChat>) : MessagesUiState
    data class Error(val message: String) : MessagesUiState
}

@HiltViewModel
class MessagesViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<MessagesUiState>(MessagesUiState.Loading)
    val uiState: StateFlow<MessagesUiState> = _uiState.asStateFlow()

    init {
        // Mock chats
        _uiState.value = MessagesUiState.Success(
            chats = listOf(
                RecentChat("1", "John Agent", null, "Hello, how can I help?", System.currentTimeMillis(), 1)
            )
        )
    }
}
